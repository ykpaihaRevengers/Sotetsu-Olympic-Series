package home.dao.err;

public class DAOException extends Exception {
	public static final String ERROR_INTERNAL_JSP = "errInternal.jsp";

	public DAOException(String message) {
		super(message);
	}
}
