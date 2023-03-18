package filemng.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileEdition {

	private int code;
	private String pattern;
	private String filterValue;
	private String function;
	private String replacedValue;

	public int getCode() {
		return code;
	}

	public String getPattern() {
		return pattern;
	}

	public String getFilterValue() {
		return filterValue;
	}

	public String getFunction() {
		return function;
	}

	public String getReplacedValue() {
		return replacedValue;
	}

	public FileEdition(int code, String pattern, String filterValue, String function, String replacedValue) {
		super();
		this.code = code;
		this.pattern = Optional.ofNullable(pattern).orElse("");
		this.filterValue = Optional.ofNullable(filterValue).orElse("");
		this.function = Optional.ofNullable(function).orElse("");
		this.replacedValue = Optional.ofNullable(replacedValue).orElse("");
	}

	public FileEdition(int code, String pattern, String filterValue, String function) {
		super();
		this.code = code;
		this.pattern = Optional.ofNullable(pattern).orElse("");
		this.filterValue = Optional.ofNullable(filterValue).orElse("");
		this.function = Optional.ofNullable(function).orElse("");
	}

	public List<String> createInsertList(int codeNum) {
		List<String> insertValues = new ArrayList<>();
		this.code = codeNum;
		insertValues.add(Integer.toString(code));
		insertValues.add(Optional.ofNullable(pattern).orElse(""));
		insertValues.add(Optional.ofNullable(filterValue).orElse(""));
		insertValues.add(Optional.ofNullable(function).orElse(""));
		insertValues.add(Optional.ofNullable(replacedValue).orElse(""));
		return insertValues;
	}

}
