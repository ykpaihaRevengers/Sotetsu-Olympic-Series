package dbmng.servlet;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dbmng.bean.AlterTable;
import dbmng.bean.Column;
import dbmng.bean.CreateTable;
import dbmng.bean.CreateTable.ColumnTypes;
import dbmng.bean.Delete;
import dbmng.bean.DropTable;
import dbmng.bean.ExecuteQuery;
import dbmng.bean.ExecuteUpdate;
import dbmng.bean.Insert;
import dbmng.bean.SelectedData;
import dbmng.bean.TruncateTable;
import dbmng.bean.Update;
import filemng.bean.FileEdition;
import filemng.bean.FileList;
import filemng.bean.TextBook;
import home.dao.MainDAO;
import home.dao.err.DAOException;
import home.servlet.MainServlet;
import home.tool.ScenarioUtil;

/**
 * Servlet implementation class DataBaseServlet
 */
@WebServlet("/DataBaseServlet")
public class DataBaseServlet extends MainServlet {
	private static final long serialVersionUID = 1L;

	public static final String DB_EXECUTE = "db_execute";

	private static final String DB_EXECUTE_MSG = "db_execute_msg";

	public static final String DATABASE_NAME = "database_name";

	private static final String CHECK_SQL_JSP = "dbmng/checkSQL.jsp";

	private static final String SELECT_JSP = "select.jsp";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DataBaseServlet() {
		super();
	}

	/**
	 * @throws IOException 
	 * @throws ServletException 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		// 初期処理
		super.doGet(request, response);

		//モデルのDAO作成
		try (MainDAO dao = new MainDAO(getRequestParameter(request, DATABASE_NAME));) {
			session.setAttribute(DATABASE_NAME, MainServlet.getRequestParameter(request, DATABASE_NAME));
			request.setAttribute("table_names", dao.showTableList());
			if (action == null || action.length() == 0) {
				//Listをリクエストスコープに入れてJSPへフォワードする
				MainServlet.gotoPage(request, response, "home.jsp");
			} else {
				request.setAttribute("action", action);
				switch (action) {
				case "executeUpdate":
					MainServlet.gotoPage(request, response, executeUpdate(request, session, dao, response));
					break;

				case "showColumns":
					MainServlet.gotoPage(request, response, showColumns(request, session, dao, response));
					break;

				case "executeUpdatefromList":
					MainServlet.gotoPage(request, response, executeUpdatefromList(request, session, dao, response));
					break;

				case "select":
					MainServlet.gotoPage(request, response, select(request, session, dao, response));
					break;

				case "insert":
					MainServlet.gotoPage(request, response, insert(request, session, dao, response));
					break;

				case "update":
					MainServlet.gotoPage(request, response, update(request, session, dao, response));
					break;

				case "delete":
					MainServlet.gotoPage(request, response, delete(request, session, dao, response));
					break;

				case "createTableA":
					if (ScenarioUtil.checkObjectValue(createTable(request, session, dao, response, "A"))) {
						MainServlet.gotoPage(request, response, CHECK_SQL_JSP);
					}
					break;

				case "createTableB":
					if (ScenarioUtil.checkObjectValue(createTable(request, session, dao, response, "B"))) {
						MainServlet.gotoPage(request, response, CHECK_SQL_JSP);
					}
					break;

				case "create_table":
					MainServlet.gotoPage(request, response, "dbmng/createTable.jsp");
					break;

				case "drop_table":
					DropTable dropTable = new DropTable(request.getParameter("table_name"));
					session.setAttribute(DB_EXECUTE, dropTable);
					MainServlet.gotoPage(request, response, CHECK_SQL_JSP);
					break;

				case "alter_table":
					AlterTable alterTable = new AlterTable(getRequestParameter(request, "table_name"), super.getRequestParameter(request, "alterAction"),
							super.getRequestParameter(request, "columnName"));
					alterTable = addColumnList(request, 10, alterTable);
					session.setAttribute(DB_EXECUTE, alterTable);
					MainServlet.gotoPage(request, response, CHECK_SQL_JSP);
					break;

				case "truncate_table":
					TruncateTable truncateTable = new TruncateTable(request.getParameter("table_name"));
					session.setAttribute(DB_EXECUTE, truncateTable);
					MainServlet.gotoPage(request, response, CHECK_SQL_JSP);
					break;

				case "readSQL":
					List<String> sqls = Arrays.asList(((TextBook) session.getAttribute("text_book")).getFileText().stream().collect(Collectors.joining(" ")).replaceAll("  ", " ").split(";"));
					session.setAttribute("sqls", sqls);
					MainServlet.gotoPage(request, response, page);
					break;

				case "individualProcessing":
					request.setAttribute(DB_EXECUTE_MSG, individualProcessing(request, session, dao, response));
					MainServlet.gotoPage(request, response, page);
					break;

				case "quit":
					MainServlet.gotoPage(request, response, quit(request, session));
					break;

				default:
					break;
				}
			}
		} catch (DAOException e) {
			e.printStackTrace();
			request.setAttribute("message", "内部エラーが発生しました");
			request.setAttribute("err_detail", e.getMessage());
			MainServlet.gotoPage(request, response, DAOException.ERROR_INTERNAL_JSP);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private String executeUpdate(HttpServletRequest request, HttpSession session, MainDAO dao, HttpServletResponse response) throws DAOException {
		ExecuteUpdate executeUpdate = (ExecuteUpdate) session.getAttribute(DB_EXECUTE);
		String tableName = executeUpdate.getTableName();
		String executedpage = "home.jsp";
		String message = "";
		if (executeUpdate instanceof CreateTable) {
			CreateTable createTable = (CreateTable) executeUpdate;
			message = dao.createTable(createTable);
			request.setAttribute(DB_EXECUTE_MSG, tableName + " テーブルの作成が完了しました。");
		}
		if (executeUpdate instanceof DropTable) {
			message = dao.dropTable(tableName);
			request.setAttribute(DB_EXECUTE_MSG, tableName + " テーブルの削除が完了しました。");
			return executedpage;
		}
		if (executeUpdate instanceof TruncateTable) {
			message = dao.truncateTable(tableName);
			request.setAttribute(DB_EXECUTE_MSG, tableName + " テーブルをリセットしました。");
			return executedpage;
		}
		if (executeUpdate instanceof Insert) {
			message = dao.insert((Insert) executeUpdate);
			request.setAttribute(DB_EXECUTE_MSG, tableName + " のデータの挿入が" + message + "件完了しました。");
			return select(request, session, dao, response);
		}

		if (executeUpdate instanceof Update) {
			message = dao.update((Update) executeUpdate);
			request.setAttribute(DB_EXECUTE_MSG, tableName + " のデータの更新が" + message + "件完了しました。");
			return select(request, session, dao, response);
		}

		if (executeUpdate instanceof Delete) {
			message = dao.delete((Delete) executeUpdate);
			request.setAttribute(DB_EXECUTE_MSG, tableName + " のデータの削除が" + message + "件完了しました。");
			session.removeAttribute("type");
			return select(request, session, dao, response);
		}

		if (executeUpdate instanceof AlterTable) {
			AlterTable alterTable = (AlterTable) executeUpdate;
			alterTable.setReplacedValue(super.getRequestParameter(request, "renamedName"));
			message = dao.alterTable(alterTable);

			String executeMessage = " テーブル名を" + tableName + "から" + message + "に変更しました。";
			if (alterTable.getAction().contains("RENAME COLUMN")) {
				executeMessage = " カラム名を" + alterTable.getReplaceColumn() + "から" + message + "に変更しました。";
			}
			if (alterTable.getAction().contains("DROP COLUMN")) {
				executeMessage = alterTable.getReplaceColumn() + "カラムを" + tableName + "テーブルから削除しました。";
			}
			if (alterTable.getAction().contains("ADD")) {
				executeMessage = tableName + "テーブルにカラム（" + message + "）を追加しました。";
			}
			request.setAttribute(DB_EXECUTE_MSG, executeMessage);
			request.setAttribute("table_names", dao.showTableList());
			request.setAttribute("table_attr_list", dao.showColumns(tableName));
			return "showTableAttribute.jsp";
		}
		//Listをリクエストスコープに入れてJSPへフォワードする
		request.setAttribute("table_names", dao.showTableList());

		//SQL実行に使用したsessionの無効化
		session.removeAttribute(DB_EXECUTE);
		try {
			if (message.contains("ERROR") || message.contains("error")) {
				Integer.parseInt(message);
			}
		} catch (NumberFormatException e) {
			executedpage = DAOException.ERROR_INTERNAL_JSP;
			throw new DAOException(message);
		}
		return executedpage;
	}

	private String select(HttpServletRequest request, HttpSession session, MainDAO dao, HttpServletResponse response) throws DAOException {
		try {
			String selectTableName = getRequestParameter(request, "table_name");
			String selectedColumn = getRequestParameter(request, "select_columns");
			Optional.ofNullable(selectedColumn).ifPresent(x -> x.replaceAll("\t", ","));
			List<String> selectedColumnList = new ArrayList<>();
			if (super.checkRequestParameter(request, "select_columns")) {
				selectedColumnList = ScenarioUtil.split(selectedColumn, ",");
			}

			ExecuteQuery select = new ExecuteQuery(selectTableName, dao, selectedColumnList);

			//条件を指定
			int filterCount = 1;
			String filterColumn = request.getParameter("filter_column" + filterCount);
			String opelationFlug = request.getParameter("opel" + filterCount);
			String filterValue = request.getParameter("filter_value" + filterCount);
			List<String> filterMessages = new ArrayList<>();

			//条件の取得

			while (ScenarioUtil.checkStringValue(filterColumn, filterValue)) {
				//条件式の決定
				select.setFilter(filterColumn, opelationFlug, filterValue);
				filterMessages.add(filterColumn + " " + opelationFlug + " " + filterValue);
				filterCount++;
				//次のカラム名とカラムの値を取得
				filterColumn = request.getParameter("filter_column" + filterCount);
				opelationFlug = request.getParameter("opel" + filterCount);
				filterValue = request.getParameter("filter_value" + filterCount);
			}

			//カラムによる並び替えを指定
			select.setOrderByColumn(getRequestParameter(request, "order_by"), getRequestParameter(request, "desc").equals("DESC"));

			//SQLを実行
			List<Map<String, Object>> dbDataList = dao.select(select);

			List<Column> columns = select.getColumns();

			//sessionスコープへの代入
			if (super.checkRequestParameter(request, "table_name")) {
				session.setAttribute("table_name", selectTableName);
			}

			session.setAttribute("selected_data", new SelectedData(columns, dbDataList));
			session.setAttribute("columns", columns);
			if (getRequestParameter(request, "action").equals("insert")) {
				session.setAttribute("insert_columns", ScenarioUtil.filteringArrayList(columns, column -> !column.isSerial()));
			}
			session.setAttribute("dbDataList", dbDataList);

			//requestスコープへの代入
			request.setAttribute("filter_msg", filterMessages);

			if (checkRequestParameter(request, "page")) {
				return getRequestParameter(request, "page");
			}
			return "select.jsp";
		} catch (DAOException e) {
			throw new DAOException(e.getMessage());
		}

	}

	private String insert(HttpServletRequest request, HttpSession session, MainDAO dao, HttpServletResponse response) throws DAOException {
		String page = CHECK_SQL_JSP;
		if (super.checkRequestParameter(request, "gotoPage") && request.getParameter("gotoPage").equals("dbmng/insert.jsp")) {
			session.setAttribute("serial", "SERIAL");
			select(request, session, dao, response);
			page = "dbmng/insert.jsp";
		}
		if (super.checkRequestParameter(request, "page")) {

			Insert insert = new Insert((String) session.getAttribute("table_name"), dao);

			List<String> insertValue = new ArrayList<>();
			for (int i = 0; i < insert.getInsertColumns().size(); i++) {
				insertValue.add(super.getRequestParameter(request, "insert_value" + (i + 1)));
			}

			if (ScenarioUtil.checkList(insertValue)) {
				insert.setInsertValue(insertValue);
			}

			if (super.checkRequestParameter(request, "insert_list")) {
				super.createStreamFromTextArea(request, "insert_list").map(xy -> ScenarioUtil.split(xy, getRequestParameter(request, "split_char"))).forEach(insert::setInsertValue);
			}
			session.setAttribute(DB_EXECUTE, insert);
		}
		request.setAttribute("page", SELECT_JSP);
		return page;
	}

	private String update(HttpServletRequest request, HttpSession session, MainDAO dao, HttpServletResponse response) throws DAOException {

		if (super.checkRequestParameter(request, "gotoPage") && request.getParameter("gotoPage").equals("dbmng/update.jsp")) {
			select(request, session, dao, response);
			return "dbmng/update.jsp";
		}

		if (super.checkRequestParameter(request, "type") && super.getRequestParameter(request, "type").equals("update_form")) {

			Update update = new Update((String) session.getAttribute("table_name"), dao);

			//値の更新
			IntStream.rangeClosed(1, 5).forEachOrdered(
					i -> update.setSetting(super.getRequestParameter(request, "setColumn" + i), super.getRequestParameter(request, "culc" + i), super.getRequestParameter(request, "value" + i)));

			//複数カラムを一括更新
			if (super.checkRequestParameter(request, "batch")) {
				super.createStreamFromTextArea(request, "batch")
						.forEachOrdered(s -> update.setSetting(s, super.getRequestParameter(request, "culcbatch"), super.getRequestParameter(request, "batchvalue")));
			}

			//更新条件を指定
			IntStream.rangeClosed(1, 3).forEachOrdered(i -> update.setFilters(super.getRequestParameter(request, "filterColumn" + i), super.getRequestParameter(request, "opel" + i),
					request.getParameter("filterValue" + i)));

			session.setAttribute("type", "update_form");
			session.setAttribute(DB_EXECUTE, update);
			request.setAttribute("page", SELECT_JSP);
		}

		return CHECK_SQL_JSP;
	}

	private String delete(HttpServletRequest request, HttpSession session, MainDAO dao, HttpServletResponse response) throws DAOException {
		if (super.checkRequestParameter(request, "gotoPage") && request.getParameter("gotoPage").equals("dbmng/delete.jsp")) {
			select(request, session, dao, response);
			return "dbmng/delete.jsp";
		}

		Delete delete = new Delete((String) session.getAttribute("table_name"), dao);

		//表から選択して削除
		if (super.getRequestParameter(request, "type").equals("select_record")) {
			int i = 0;
			for (Column column : delete.getColumns()) {
				delete.setFilters(column.getColumnName(), MainDAO.EQUALS, request.getParameter("filterValue" + (++i)));
			}
			session.setAttribute("type", "select_record");
		}

		//条件式から削除
		if (super.getRequestParameter(request, "type").equals("filter_by_column")) {
			IntStream.rangeClosed(1, 3).forEachOrdered(i -> delete.setFilters(super.getRequestParameter(request, "filterColumn" + i), super.getRequestParameter(request, "opel" + i),
					request.getParameter("filterValue" + i)));
			session.setAttribute("type", "filter_by_column");
		}
		session.setAttribute(DB_EXECUTE, delete);
		request.setAttribute("page", SELECT_JSP);

		return CHECK_SQL_JSP;
	}

	private Object createTable(HttpServletRequest request, HttpSession session, MainDAO dao, HttpServletResponse response, String createTablePattern) {
		if (super.checkRequestParameter(request, "table_name")) {

			CreateTable createTable = new CreateTable(super.getRequestParameter(request, "table_name"), super.getRequestParameter(request, "primary_key"));
			if (createTablePattern.equals("A")) {
				if (super.checkRequestParameter(request, "columns_list")) {
					//	テキストボックスで入力された配列をストリームで処理
					super.createStreamFromTextArea(request, "columns_list").map(xy -> xy.replaceAll(request.getParameter("split_char"), " ")).forEach(createTable::setColumnAttr);
				}
			} else if (createTablePattern.equals("B")) {
				createTable = createColumnList(request, 10, createTable);
			}
			session.setAttribute(DB_EXECUTE, createTable);
		}
		return session.getAttribute(DB_EXECUTE);
	}

	private CreateTable createColumnList(HttpServletRequest request, int size, CreateTable createTable) {
		for (int i = 1; i < size; i++) {
			if (super.checkRequestParameter(request, "column_name" + i, "data_type" + i)) {
				String column_name = super.getRequestParameter(request, "column_name" + i);
				ColumnTypes data_type = ColumnTypes.valueOf(super.getRequestParameter(request, "data_type" + i));
				String max_length = super.getRequestParameter(request, "max_length" + i);
				int charLength = 0;
				if (!max_length.isEmpty()) {
					charLength = Integer.parseInt(max_length);
				}
				boolean not_null = super.checkRequestParameter(request, "not_null" + i);
				String default_value = super.getRequestParameter(request, "default_value" + i);

				createTable.setColumnAttr(column_name, data_type, charLength, not_null, default_value);
			}
		}
		return createTable;
	}

	private AlterTable addColumnList(HttpServletRequest request, int size, AlterTable alterTable) {
		for (int i = 1; i < size; i++) {
			if (super.checkRequestParameter(request, "column_name" + i, "data_type" + i)) {
				String column_name = super.getRequestParameter(request, "column_name" + i);
				ColumnTypes data_type = ColumnTypes.valueOf(super.getRequestParameter(request, "data_type" + i));
				String max_length = super.getRequestParameter(request, "max_length" + i);
				int charLength = 0;
				if (!max_length.isEmpty()) {
					charLength = Integer.parseInt(max_length);
				}
				boolean not_null = super.checkRequestParameter(request, "not_null" + i);
				String default_value = super.getRequestParameter(request, "default_value" + i);

				alterTable.setAddColumnList(column_name, data_type, charLength, not_null, default_value);
			}
		}
		return alterTable;
	}

	private String executeUpdatefromList(HttpServletRequest request, HttpSession session, MainDAO dao, HttpServletResponse response) throws DAOException {
		String message = dao.executeUpdateList((List<String>) session.getAttribute("sqls"));
		String executedpage = "showTables.jsp";
		if (message != null) {
			executedpage = DAOException.ERROR_INTERNAL_JSP;
			throw new DAOException(message);
		} else {
			request.setAttribute(DB_EXECUTE_MSG, " SQLの実行が完了しました。");
			//Listをリクエストスコープに入れてJSPへフォワードする
			request.setAttribute("table_names", dao.showTableList());
		}
		return executedpage;
	}

	private String showColumns(HttpServletRequest request, HttpSession session, MainDAO dao, HttpServletResponse response) throws DAOException {
		List<Column> columnList = dao.showColumns(request.getParameter("table_name"));
		session.setAttribute("table_name", request.getParameter("table_name"));
		request.setAttribute("table_attr_list", columnList);

		return "showTableAttribute.jsp";
	}

	private String quit(HttpServletRequest request, HttpSession session) {
		ExecuteUpdate quit = (ExecuteUpdate) session.getAttribute(DB_EXECUTE);
		if (quit instanceof CreateTable) {
			return "dbmng/createTable.jsp";
		} else if (quit instanceof Insert) {
			return "dbmng/insert.jsp";
		} else if (quit != null) {
			request.setAttribute(DB_EXECUTE_MSG, quit.getExecutionType() + "を取り消しました。");
		}
		if (checkRequestParameter(request, "page")) {
			return getRequestParameter(request, "page");
		}
		return "showTables.jsp";
	}

	private String individualProcessing(HttpServletRequest request, HttpSession session, MainDAO dao, HttpServletResponse response) throws DAOException {

		FileList fileList = (FileList) session.getAttribute("file_list");

		if (checkSessionAttribute(session, "file_list") && getRequestParameter(request, "process").equals("uplord_file_edition")) {

			List<FileEdition> fileEditions = fileList.getFileEditions();
			try {
				ResultSet rs = dao.executeQuery(MainDAO.SELECT + "MAX(code)" + MainDAO.FROM + getRequestParameter(request, "table_name"), null);
				Integer codeNum = 1;
				while (rs.next()) {
					codeNum = codeNum + rs.getInt(1);
				}
				Insert insert = new Insert("file_edition", dao);
				if (ScenarioUtil.checkList(fileEditions)) {
					for (FileEdition fileEdition : fileEditions) {
						insert.setInsertValue(fileEdition.createInsertList(codeNum));
					}
				}
				return "条件処理を" + dao.insert(insert) + "件登録しました。";

			} catch (SQLException e) {
				e.printStackTrace();
				return e + "　が原因で処理に失敗しました。";
			}
		}
		if (checkRequestParameter(request, "file_edition_code") && getRequestParameter(request, "process").equals("download_file_edition")) {

			fileList.setFileEditions(dao, getRequestParameter(request, "file_edition_code"));
			session.setAttribute("file_list", fileList);

			return "登録した編集方法を読み込みました。";
		}
		if (getRequestParameter(request, "process").endsWith("quickly")) {

			//登録した編集方法でファイルを編集
			fileList.editFile(dao);
			session.setAttribute("file_list", fileList);
			List<TextBook> bookList = fileList.getBookList();

			if (getRequestParameter(request, "process").equals("insert_quickly")) {
				Insert insert = new Insert("schedule_manuscript", dao);
				ScenarioUtil.mappingArrayList(bookList, book -> book.arrangeTextBook())
						.stream().map(schedule -> schedule.createInsertValues()).flatMap(list -> list.stream()).forEachOrdered(insert::setInsertValue);
				String result = dao.insert(insert);
				request.setAttribute("execute_msg", "DBへの反映が" + result + "件反映されました。");
			} else if (getRequestParameter(request, "process").equals("edit_quickly")) {
				request.setAttribute("disable", "disable");
			}

			return "デフォルトでファイルを編集しました。";
		}

		return "";
	}

}
