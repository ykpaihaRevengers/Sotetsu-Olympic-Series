package dbmng.bean;

import home.dao.MainDAO;
import home.dao.err.DAOException;

public class Delete extends ExecuteUpdate {

	public Delete(String tableName, MainDAO dao) throws DAOException {
		super(MainDAO.DELETE_FROM, tableName, dao);
	}

}