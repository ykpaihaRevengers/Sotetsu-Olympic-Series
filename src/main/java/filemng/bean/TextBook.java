package filemng.bean;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import home.servlet.MainServlet;
import home.tool.FileUtils;
import home.tool.ScenarioUtil;
import usr.УкраїнаRevengers.bean.ManuscriptScedule;
import usr.УкраїнаRevengers.bean.ScheduleTag;

public class TextBook {
	private static final String SPLITARRAY = "/splitArray/";
	private static final String TAIL = "Yahoo Japan Corporation. All Rights Reserved.";

	private String fileName;
	private Path fileFullPath;
	private List<String> fileText;
	private List<List<String>> fileTextSeveral;

	private String ordinalCode;

	protected enum TextConnectionType {
		ahead, behind
	}

	public TextBook(Path fileFullPath) {
		super();
		this.fileName = fileFullPath.getFileName().toString();
		this.fileFullPath = fileFullPath;

		List<String> fileTexts = new ArrayList<>();

		//ファイルの読み込み
		try {
			Files.lines(fileFullPath, StandardCharsets.UTF_8).filter(str -> ScenarioUtil.checkObjectValue(str)).filter(str -> !(str.isBlank() || str.startsWith("-")))
					.map(f -> f.trim()).forEach(fileTexts::add);
		} catch (Exception xxx) {
			try {
				Files.lines(fileFullPath, Charset.forName("Shift_JIS")).filter(str -> ScenarioUtil.checkObjectValue(str)).filter(str -> !(str.isBlank() || str.startsWith("-")))
						.map(f -> f.trim()).forEach(fileTexts::add);
			} catch (IOException e) {

				e.printStackTrace();

				fileTexts.add("ファイルが見つからないため、新規で作成します。");
			}
		}
		fileTexts = ScenarioUtil.mappingArrayList(fileTexts, text -> text.replace("(", "（"));
		fileTexts = ScenarioUtil.mappingArrayList(fileTexts, text -> text.replace(")", "）"));
		if (ScenarioUtil.filteringArrayList(fileTexts, text -> text.endsWith(TAIL)).size() > 1) {
			String fileListConnect = String.join(SPLITARRAY, fileTexts);
			String splitChar = TAIL + SPLITARRAY;
			List<String> dividedFileListConnect = ScenarioUtil.split(fileListConnect, splitChar);
			this.fileTextSeveral = ScenarioUtil.mappingArrayList(dividedFileListConnect, text -> ScenarioUtil.split(text, SPLITARRAY));
		}

		this.fileText = fileTexts;
		String hashCodeToString = Integer.toString(fileText.hashCode());
		this.ordinalCode = hashCodeToString.substring(hashCodeToString.length() - 5);
		System.out.println(ordinalCode);

	}

	public TextBook(List<String> fileText, Path fileFullPath) {
		super();
		this.fileName = fileFullPath.getFileName().toString();
		this.fileFullPath = fileFullPath;
		this.fileText = ScenarioUtil.filteringArrayList(fileText, line -> ScenarioUtil.checkStringValue(line) &&
				!line.startsWith("Copyright (C)") && !line.isBlank());
		String hashCodeToString = Integer.toString(this.fileText.hashCode());
		this.ordinalCode = hashCodeToString.substring(hashCodeToString.length() - 5);
	}

	public String getFileName() {
		return fileName;
	}

	public List<String> getFileText() {
		return fileText;
	}

	public Path getFileFullPath() {
		return fileFullPath;
	}

	public void setFileText(List<String> fileText) {
		this.fileText = fileText;
	}

	public List<FileEdition> editTextbook(HttpServletRequest request) {
		List<FileEdition> fileEditions = new ArrayList<>();

		//キーワードで行を絞る
		if (MainServlet.checkRequestParameter(request, "filter")) {
			String filterValue = MainServlet.createStreamFromTextArea(request, "filter").map(text -> text.replaceAll("、", "/")).map(text -> text.replaceAll(",", "/")).collect(Collectors.joining("/"));
			fileText = filterByKeyWord(filterValue);
			fileEditions.add(new FileEdition(1, "contains", filterValue, "filter"));
		}

		//キーワードで行を除外する
		if (MainServlet.checkRequestParameter(request, "filter_remove")) {
			String filterValue = MainServlet.createStreamFromTextArea(request, "filter_remove").map(text -> text.replaceAll("、", "/")).map(text -> text.replaceAll(",", "/"))
					.collect(Collectors.joining("/"));
			fileText = exceptByKeyWord(filterValue);
			fileEditions.add(new FileEdition(1, "contains", filterValue, "filter_remove"));
		}
		// フィルター
		for (int i = 1; i <= 10; i++) {
			if (MainServlet.checkRequestParameter(request, "pattern" + i, "filterValue" + i, "function" + i)) {
				String pattern = MainServlet.getRequestParameter(request, "pattern" + i);
				String filterValue = MainServlet.getRequestParameter(request, "filterValue" + i);
				String function = MainServlet.getRequestParameter(request, "function" + i);
				String functionChar = MainServlet.getRequestParameter(request, "functionChar" + i);

				//編集パターンを記録
				fileEditions.add(new FileEdition(1, pattern, filterValue, function, functionChar));
				fileText = filterProcess(pattern, filterValue, function, functionChar);
			}

		}
		//文字の置換
		for (int i = 1; i <= 10; i++) {
			if (MainServlet.checkRequestParameter(request, "before" + i)) {
				String regex = MainServlet.getRequestParameter(request, "before" + i);
				String replacement = MainServlet.getRequestParameter(request, "after" + i);
				fileEditions.add(new FileEdition(1, "contains", regex, "replaceAll", replacement));
				fileText = ScenarioUtil.mappingArrayList(fileText, value -> value.replaceAll(regex, replacement));
			}
		}

		return fileEditions;

	}

	public List<String> editTextbookByOrdered(HttpServletRequest request, List<FileEdition> fileEditions) {
		for (FileEdition edition : fileEditions) {
			if (edition.getPattern().equals("contains") && edition.getFunction().equals("filter")) {
				fileText = filterByKeyWord(edition.getFilterValue());
			} else if (edition.getPattern().equals("contains") && edition.getFunction().equals("filter_remove")) {
				fileText = exceptByKeyWord(edition.getFilterValue());
			} else if (edition.getPattern().equals("contains") && edition.getFunction().equals("replaceAll")) {
				fileText = ScenarioUtil.mappingArrayList(fileText, value -> value.replaceAll(edition.getFilterValue(), edition.getReplacedValue()));
			} else {
				fileText = filterProcess(edition.getPattern(), edition.getFilterValue(), edition.getFunction(), edition.getReplacedValue());
			}
		}

		return fileText;
	}

	private List<String> filterProcess(String pattern, String filterValue, String function, String functionChar) {
		//ファイル編集のパターンをチェック
		fileText = ScenarioUtil.mappingArrayList(fileText, value -> charProcess(value, pattern, filterValue, function, functionChar));

		String trimming = "";
		//前の文字と結合するの場合
		if (function.equals("ahead")) {
			trimming = FileUtils.SLASH + FileUtils.APPEND;
		}
		//後の文字と結合するの場合
		if (function.equals("behind")) {
			trimming = FileUtils.APPEND + FileUtils.SLASH;
		}

		fileText = ScenarioUtil.split(fileText.stream().collect(Collectors.joining(FileUtils.SLASH)).replaceAll(trimming, ""), FileUtils.SLASH);
		if (ScenarioUtil.checkStringValue(functionChar)) {
			fileText = ScenarioUtil.mappingArrayList(fileText, v -> v.replaceAll(filterValue, functionChar));
		}

		//除外
		if (function.equals("!")) {
			if (pattern.equals("equals")) {
				fileText = ScenarioUtil.filteringArrayList(fileText, a -> !a.equals(filterValue));
			} else if (pattern.equals("startswith")) {
				fileText = ScenarioUtil.filteringArrayList(fileText, a -> !a.startsWith(filterValue));
			} else if (pattern.equals("endswith")) {
				fileText = ScenarioUtil.filteringArrayList(fileText, a -> !a.endsWith(filterValue));
			} else if (pattern.equals("contains")) {
				fileText = ScenarioUtil.filteringArrayList(fileText, a -> !a.contains(filterValue));
			}
		}
		//指定
		if (function.equals("match")) {
			if (pattern.equals("equals")) {
				fileText = ScenarioUtil.filteringArrayList(fileText, a -> a.equals(filterValue));
			} else if (pattern.equals("startswith")) {
				fileText = ScenarioUtil.filteringArrayList(fileText, a -> a.startsWith(filterValue));
			} else if (pattern.equals("endswith")) {
				fileText = ScenarioUtil.filteringArrayList(fileText, a -> a.endsWith(filterValue));
			} else if (pattern.equals("contains")) {
				fileText = ScenarioUtil.filteringArrayList(fileText, a -> a.contains(filterValue));
			}
		}
		return ScenarioUtil.mappingArrayList(fileText, line -> line.trim());
	}

	private List<String> filterByKeyWord(String filterValue) {
		return ScenarioUtil.filteringArrayList(fileText, line -> ScenarioUtil.split(filterValue, "/").stream().anyMatch(keyword -> line.contains(keyword)));
	}

	private List<String> exceptByKeyWord(String filterValue) {
		return ScenarioUtil.filteringArrayList(fileText, line -> ScenarioUtil.split(filterValue, "/").stream().allMatch(keyword -> !line.contains(keyword)));

	}

	/**
	 * 
	 * ファイルの文字を条件に合わせて置換する
	 * @param value
	 * @param pattern
	 * @param filterValue
	 * @param function
	 * @param functionChar
	 * @return
	 */
	private String charProcess(String value, String pattern, String filterValue, String function, String functionChar) {
		if (appendingMatrix(pattern, value, filterValue)) {
			if (function.equals("ahead")) {
				value = new StringBuilder(FileUtils.APPEND).append(value).toString();
			} else if (function.equals("behind")) {
				value = new StringBuilder(value).append(FileUtils.APPEND).toString();
			}
			if (function.equals("replaceall") && ScenarioUtil.checkStringValue(functionChar)) {
				value = value.replaceAll(filterValue, functionChar);
			}
			if (function.equals("remove")) {
				value = value.replaceAll(filterValue, "");
			}
		}
		return value;
	}

	/**
	 * @param pattern
	 * @param value
	 * @param appendingValue
	 * @return
	 */
	private boolean appendingMatrix(String pattern, String value, String appendingValue) {
		boolean equals = pattern.equals("equals") && value.equals(appendingValue);
		boolean startswith = pattern.equals("startswith") && value.startsWith(appendingValue);
		boolean endswith = pattern.equals("endswith") && value.endsWith(appendingValue);
		boolean contains = pattern.equals("contains") && value.contains(appendingValue);

		return equals || startswith || endswith || contains;
	}

	public String getOrdinalCode() {
		return ordinalCode;
	}

	public List<List<String>> getFileTextSeveral() {
		return fileTextSeveral;
	}

	public ManuscriptScedule arrangeTextBook() {

		boolean firstText = true;
		ManuscriptScedule manuSchedule = new ManuscriptScedule();
		List<ScheduleTag> scheduleTags = new ArrayList<>();
		for (String text : this.getFileText()) {
			String[] manuscriptArray = text.split(",");
			if (manuscriptArray.length == 2) {
				if (firstText) {
					scheduleTags.add(new ScheduleTag(manuscriptArray[1], null, (manuscriptArray[0])));
					firstText = false;
				} else {
					scheduleTags.add(new ScheduleTag(manuscriptArray[1], (manuscriptArray[0])));
				}
			}
			if (manuscriptArray.length == 3) {
				scheduleTags.add(new ScheduleTag(manuscriptArray[2], (manuscriptArray[0]), (manuscriptArray[1])));
			}
		}

		manuSchedule.setScheduleTags(scheduleTags);
		manuSchedule.setCode(this.getOrdinalCode(), ScenarioUtil.mappingArrayList(scheduleTags, scheduleTag -> scheduleTag.getName().trim()));

		return manuSchedule;
	}
}
