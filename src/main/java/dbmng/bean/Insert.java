package dbmng.bean;

import java.util.ArrayList;
import java.util.List;

import home.dao.MainDAO;
import home.dao.err.DAOException;
import home.tool.ScenarioUtil;

public class Insert extends ExecuteUpdate {

	private List<Column> insertColumns;
	private List<List<String>> insertValues;

	public Insert(String tableName, MainDAO dao) throws DAOException {
		super(MainDAO.INSERT_INTO, tableName);
		this.insertColumns = ScenarioUtil.filteringArrayList(dao.showColumns(tableName), c -> !c.isSerial());
		this.insertValues = new ArrayList<>();
	}

	public Insert(String tableName, MainDAO dao, List<String> insertFilter) throws DAOException {
		super(MainDAO.INSERT_INTO, tableName);
		this.insertColumns = ScenarioUtil.filteringArrayList(dao.showColumns(tableName), c -> !c.isSerial() && insertFilter.contains(c.getColumnName()));
		this.insertValues = new ArrayList<>();
	}

	public List<Column> getInsertColumns() {
		return insertColumns;
	}

	public List<List<String>> getInsertValues() {
		return insertValues;
	}

	public void setInsertValue(List<String> insertValue) {
		this.insertValues.add(insertValue);
	}

}
