package dbmng.bean;

import home.dao.MainDAO;

public class TruncateTable extends ExecuteUpdate {

	public TruncateTable(String tableName) {
		super(MainDAO.TRUNCATE_TABLE, tableName);
	}

}
