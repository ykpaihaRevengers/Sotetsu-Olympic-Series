package dbmng.bean;

import home.dao.MainDAO;
import home.dao.err.DAOException;

public class CreateTemporaryTable extends ExecuteUpdate {

	private static final String TMP = "TMP_";
	private String temporarytableName;
	private String keyColumn;
	private Object keyValue;
	private Object changeValue;

	public CreateTemporaryTable(String tableName, MainDAO dao, String keyColumn, String keyValue, String changeValue) throws DAOException {
		super(MainDAO.CREATE_TEMPORARY_TABLE, tableName, dao);
		this.temporarytableName = TMP + tableName;
		this.keyColumn = keyColumn;
		this.keyValue = MainDAO.createPraceHolder(keyColumn, keyValue, super.getColumns());
		this.changeValue = MainDAO.createPraceHolder(keyColumn, changeValue, super.getColumns());
	}

	public String getTemporarytableName() {
		return temporarytableName;
	}

	public String getKeyColumn() {
		return keyColumn;
	}

	public Object getKeyValue() {
		return keyValue;
	}

	public Object getChangeValue() {
		return changeValue;
	}

}
