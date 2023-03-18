package dbmng.bean;

import java.util.ArrayList;
import java.util.List;

import home.dao.MainDAO;
import home.tool.ScenarioUtil;

public class CreateTable extends ExecuteUpdate {

	private String primaryKey;
	private List<String> columnAttr;

	public enum ColumnTypes {

		SERIAL, INTEGER, TEXT, VARCHAR, CHAR, DATE, TIME, TIMESTAMP;

		String getVARCHAR(int i) {
			return VARCHAR.toString() + "(" + i + ")";
		}

		String getCHAR(int i) {
			return CHAR.toString() + "(" + i + ")";
		}
	}

	public CreateTable(String tableName, String primaryKey) {
		super(MainDAO.CREATE_TABLE, tableName);
		this.primaryKey = primaryKey;
		this.columnAttr = new ArrayList<>();
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public List<String> getColumnAttr() {
		return columnAttr;
	}

	public void setColumnAttr(String columnName, ColumnTypes types, int size, boolean notNull, String defaultValue) {
		if (ScenarioUtil.checkStringValue(columnName)) {
			String inputColumn = columnName + " " + types.toString();
			if (types.equals(ColumnTypes.VARCHAR) || types.equals(ColumnTypes.CHAR)) {
				inputColumn = inputColumn + "(" + size + ")";
			}
			if (notNull) {
				inputColumn = inputColumn + MainDAO.NOT_NULL;
			}
			if (ScenarioUtil.checkStringValue(defaultValue)) {
				inputColumn = inputColumn + MainDAO.DEFAULT;
				if (types.equals(ColumnTypes.CHAR) || types.equals(ColumnTypes.VARCHAR) || types.equals(ColumnTypes.TEXT)) {
					inputColumn = inputColumn + " '" + defaultValue + "'";
				} else {
					inputColumn = inputColumn + " " + defaultValue;
				}
			}
			this.columnAttr.add(inputColumn);
		}
	}

	public void setColumnAttr(String inputColumn) {
		this.columnAttr.add(inputColumn);
	}

}
