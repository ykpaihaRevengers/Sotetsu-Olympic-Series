package dbmng.bean;

import java.util.Optional;

public class Column {
	private int ordinalPosition;
	private String columnName;
	private boolean isNullable;
	private boolean isSerial;
	private String dataType;
	private String defaultValue;
	private String insertValue;

	private int varcharLength;

	public Column(String columnName, String dataType, int varcharLength) {
		super();
		this.columnName = columnName;
		this.varcharLength = varcharLength;
		this.dataType = dataType;
		String type = this.getDataType().replace("(n)", "(" + varcharLength + ")");
		this.dataType = type;

	}

	public Column(String columnName, String dataType) {
		this.columnName = columnName;
		this.dataType = dataType;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public int getVarcharLength() {
		return varcharLength;
	}

	public void setVarcharLength(int varcharLength) {
		this.varcharLength = varcharLength;

	}

	public int getOrdinalPosition() {
		return ordinalPosition;
	}

	public void setOrdinalPosition(int ordinalPosition) {
		this.ordinalPosition = ordinalPosition;
	}

	public boolean getIsNullable() {
		return isNullable;
	}

	public void setNullable(boolean isNullable) {
		this.isNullable = isNullable;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		this.isSerial = Optional.ofNullable(defaultValue).orElse("").startsWith("nextval(") && Optional.ofNullable(defaultValue).orElse("").endsWith("::regclass)");
	}

	public String getInsertValue() {
		return insertValue;
	}

	public void setInsertValue(String insertValue) {
		this.insertValue = insertValue;
	}

	public boolean isSerial() {
		return isSerial;
	}

}
