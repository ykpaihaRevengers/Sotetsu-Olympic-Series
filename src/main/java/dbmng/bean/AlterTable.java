package dbmng.bean;

import java.util.ArrayList;
import java.util.List;

import dbmng.bean.CreateTable.ColumnTypes;
import home.dao.MainDAO;
import home.tool.ScenarioUtil;

public class AlterTable extends ExecuteUpdate {
	private String action;
	private String replaceColumn;
	private String replacedValue;
	private List<String> addColumnList;

	public static final String RENAME_TO = " RENAME TO ";
	public static final String RENAME_COLUMN = " RENAME COLUMN ";
	public static final String DROP_COLUMN = " DROP COLUMN ";
	public static final String ADD = " ADD ";

	public AlterTable(String tableName, String action, String replaceColumn) {
		super(MainDAO.ALTER_TABLE, tableName);
		this.action = action;
		this.replaceColumn = replaceColumn;
		this.addColumnList = new ArrayList<>();
	}

	public String getAction() {
		return action;
	}

	public String getReplacedValue() {
		return replacedValue;
	}

	public void setReplacedValue(String replacedValue) {
		this.replacedValue = replacedValue;
	}

	public String getReplaceColumn() {
		return replaceColumn;
	}

	public List<String> getAddColumnList() {
		return addColumnList;
	}

	public void setAddColumnList(List<String> addColumnList) {
		this.addColumnList = addColumnList;
	}

	public void setAddColumnList(String columnName, ColumnTypes types, int size, boolean notNull, String defaultValue) {

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
			this.addColumnList.add(inputColumn);
		}
	}

}
