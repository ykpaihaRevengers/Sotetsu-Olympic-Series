package dbmng.bean;

import home.dao.MainDAO;

public class DropTable extends ExecuteUpdate {

	public DropTable(String tableName) {
		super(MainDAO.DROP_TABLE_IF_EXISTS, tableName);
	}

}
