package filemng.bean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import dbmng.bean.ExecuteQuery;
import home.dao.MainDAO;
import home.dao.err.DAOException;
import home.tool.ScenarioUtil;

public class FileList {

	private List<TextBook> bookList;
	private Path dirPath;
	private List<FileEdition> fileEditions;

	public static final String TABLE_NAME_FILE_EDITION = "file_edition";

	private static final String DEFAULT_EDITION_CODE = "1";

	public FileList(Path dirPath, List<TextBook> bookList) {
		super();
		this.bookList = bookList;
		this.dirPath = dirPath;
	}

	public FileList(String dirPathName) {
		this(Paths.get(dirPathName));
	}

	public FileList(Path dirPath) {
		super();

		List<TextBook> bookList = new ArrayList<>();

		try {
			if (Files.isDirectory(dirPath)) {
				Files.list(dirPath).forEach(new Consumer<Path>() {
					@Override
					public void accept(Path filePath) {
						if (!Files.isDirectory(filePath)) {
							TextBook textBook = new TextBook(filePath);
							//ファイルを分割した場合
							if (ScenarioUtil.checkList(textBook.getFileTextSeveral())) {
								textBook.getFileTextSeveral().stream().forEachOrdered(fileList -> bookList.add(new TextBook(fileList, filePath)));
							} else {
								bookList.add(textBook);
							}
						}
					}
				});
				this.dirPath = dirPath;
			} else {
				TextBook textBook = new TextBook(dirPath);
				//ファイルを分割した場合
				if (ScenarioUtil.checkList(textBook.getFileTextSeveral())) {
					textBook.getFileTextSeveral().stream().forEachOrdered(fileList -> bookList.add(new TextBook(fileList, dirPath)));
				} else {
					bookList.add(textBook);
				}

				this.dirPath = dirPath.getParent();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		this.bookList = bookList;
	}

	public List<TextBook> getBookList() {
		return bookList;
	}

	public void setBookList(List<TextBook> bookList) {
		this.bookList = bookList;
	}

	public Path getDirPath() {
		return dirPath;
	}

	public List<FileEdition> getFileEditions() {
		return fileEditions;
	}

	public void setFileEditions(List<FileEdition> fileEditions) {
		this.fileEditions = fileEditions;
	}

	public void setFileEditions(MainDAO dao, String code) throws DAOException {
		ExecuteQuery select = new ExecuteQuery(TABLE_NAME_FILE_EDITION, dao);
		select.setFilter("code", MainDAO.EQUALS, code);
		this.fileEditions = ScenarioUtil.mappingArrayList(dao.select(select), data -> new FileEdition((Integer) data.get("code"), (String) data.get("pattern"),
				(String) data.get("filter_value"), (String) data.get("function"), (String) data.get("replaced_value")));
	}

	public void editFile(MainDAO dao, String code) throws DAOException {
		//編集方法の読み込み
		setFileEditions(dao, code);
		//登録した編集方法でファイルを編集
		this.bookList.stream().forEachOrdered(book -> book.editTextbookByOrdered(this.fileEditions));
	}

	public void editFile(MainDAO dao) throws DAOException {
		editFile(dao, DEFAULT_EDITION_CODE);
	}

	public void editFile(List<FileEdition> fileEditions) throws DAOException {
		//編集方法の読み込み
		setFileEditions(fileEditions);
		//登録した編集方法でファイルを編集
		this.bookList.stream().forEachOrdered(book -> book.editTextbookByOrdered(this.fileEditions));
	}

}
