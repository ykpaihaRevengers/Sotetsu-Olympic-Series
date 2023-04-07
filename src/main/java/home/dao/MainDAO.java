package home.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import dbmng.bean.AlterTable;
import dbmng.bean.Column;
import dbmng.bean.CreateTable;
import dbmng.bean.CreateTemporaryTable;
import dbmng.bean.Delete;
import dbmng.bean.ExecuteQuery;
import dbmng.bean.ExecuteUpdate.Filter;
import dbmng.bean.Insert;
import dbmng.bean.Update;
import dbmng.bean.Update.UpdateSetting;
import home.dao.err.DAOException;
import home.tool.ScenarioUtil;

public class MainDAO implements AutoCloseable {
	private String url = "jdbc:postgresql:";

	private String user = "java";
	private String pass = "Zzzz1111";
	private String dataBaseName;

	private String sql;
	private List<Object> placeHolders;
	private String errMsg;
	Connection connection = null;

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public List<Object> getPrepareSetting() {
		return placeHolders;
	}

	public void setPrepareSetting(List<Object> prepareSetting) {
		this.placeHolders = prepareSetting;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public void setTableCatalog(String tableCatalog) {
	}

	public static final String CREATE_TABLE = "CREATE TABLE ";
	public static final String ALTER_TABLE = "ALTER TABLE ";
	public static final String DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS ";
	public static final String TRUNCATE_TABLE = "TRUNCATE TABLE ";
	public static final String NOT_NULL = " NOT NULL";
	public static final String PRIMARY_KEY = "PRIMARY KEY";
	public static final String DEFAULT = " DEFAULT ";

	public static final String SELECT = "SELECT ";
	public static final String COUNT_FULL = "COUNT(*) ";
	public static final String COUNT = "COUNT";
	public static final String AS = " AS ";
	public static final String FROM = " FROM ";
	public static final String WHERE = " WHERE ";
	public static final String AND = " AND ";
	public static final String EQUALS = "=";
	public static final String IN = " IN ";
	public static final String LIKE = "LIKE";
	public static final String UPDATE_NULL = " = NULL ";

	public static final String ORDER_BY = " ORDER BY ";
	public static final String GROUP_BY = " GROUP BY ";

	public static final String DESC = " DESC";

	public static final String INSERT_INTO = "INSERT INTO ";
	public static final String VALUES = " VALUES ";

	public static final String UPDATE = "UPDATE ";
	public static final String SET = " SET ";

	public static final String DELETE_FROM = "DELETE FROM ";
	public static final String WITH_UR = ";";

	public static final String CREATE_TEMPORARY_TABLE = "CREATE TEMPORARY TABLE ";

	public static final String NULLS_FIRST = " NULLS FIRST";
	public static final String NULLS_LAST = " NULLS LAST";

	public enum CurrentDateTimeStamp {
		CURRENT_TIMESTAMP, CURRENT_DATE, CURRENT_TIME;

		static boolean contains(String value) {
			return ScenarioUtil.mappingArrayList(Arrays.asList(CurrentDateTimeStamp.values()), a -> a.toString()).contains(value);
		}
	}

	public enum Operator {
		EQUALS("="), LIKE("LIKE"), STARTSWITH("LIKE%"), ENDSWITH("%LIKE"), NOT_EQUALS("!="), gT(">"), lT("<"), gE(">="), lE("<="), BETWEEN("BETWEEN"), ADD("+"), SUB("-"), MULT("*"), DIV("/");

		private String operator; // フィールドの定義

		private Operator(String operator) {
			this.operator = operator;
		}

		public String toString() {
			return operator;
		}

	}

	public MainDAO(String dataBaseName) throws DAOException {
		try {
			// JDBCドライバの登録
			Class.forName("org.postgresql.Driver");
			this.dataBaseName = dataBaseName;
			// データベースの接続
			this.connection = DriverManager.getConnection(url + dataBaseName, user, pass);
			this.placeHolders = new ArrayList<>();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new DAOException("ドライバの登録に失敗しました");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DAOException("データベースの接続に失敗しました");

		}
	}

	public String executeUpdate(String sql, List<Object> preceHolders) {

		try (PreparedStatement st = connection.prepareStatement(sql);) {
			if (sql.contains("?")) {
				setPraceHolders(st, preceHolders);
			}
			//SQLの実行
			return Integer.toString(st.executeUpdate());

		} catch (SQLException e) {
			e.printStackTrace();
			return e.getMessage();

		}

	}

	public ResultSet executeQuery(String sql, List<Object> preceHolders) throws SQLException {
		PreparedStatement st = connection.prepareStatement(sql);
		if (sql.contains("?")) {
			setPraceHolders(st, preceHolders);
		}
		//SQLの実行
		return st.executeQuery();
	}

	public String executeUpdateList(List<String> sqls) {

		int count = 1;
		try {
			// トランザクションの開始
			connection.setAutoCommit(false);
			//SQLの実行
			for (String sql : sqls) {
				connection.prepareStatement(sql).executeUpdate();
				count++;
			}
			connection.commit();
			return null;
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return count + " " + e.getMessage();
		} finally {
			// オートコミット有効化
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void setPraceHolders(PreparedStatement st, List<Object> praceHolders) throws SQLException {
		if (!(praceHolders == null || praceHolders.isEmpty())) {
			int i = 0;
			for (Object praceHolder : praceHolders) {
				if (praceHolder instanceof Integer) {
					st.setInt(++i, (Integer) praceHolder);
				} else if (praceHolder instanceof String) {
					st.setString(++i, (String) praceHolder);
				} else if (praceHolder instanceof Date) {
					st.setDate(++i, (Date) praceHolder);
				} else if (praceHolder instanceof Time) {
					st.setTime(++i, (Time) praceHolder);
				} else if (praceHolder instanceof Timestamp) {
					st.setTimestamp(++i, (Timestamp) praceHolder);
				}
			}
		}
	}

	private String getExecuteMessage(String... messages) {
		String outputMessage = "";
		try {
			for (String execute : Arrays.asList(messages)) {
				Integer.parseInt(execute);
			}
			return "SUCCESS";
		} catch (Exception e) {
			return outputMessage;
		}
	}

	/**
	 * プレイスホルダーの型をチェックして、要素をデータ型に変換する
	 * @param value
	 * @param dataType
	 * @return
	 */
	public static Object createPraceHolder(String value, Column column) {
		if (column == null) {
			return column;
		}
		String dataType = column.getDataType();
		if (ScenarioUtil.checkStringValue(value)) {
			value = value.trim();
		}
		if (dataType.equals("integer")) {
			return (Integer) Integer.parseInt(value);
		} else if (dataType.equals("date")) {
			return Date.valueOf(value);
		} else if (dataType.equals("time without time zone")) {
			return ScenarioUtil.timeValueOf(value);
		} else if (dataType.equals("timestamp")) {
			return Timestamp.valueOf(value);
		} else {
			return value;
		}
	}

	/**
	 * カラムリストとカラム名からデータ型を判断し、要素を判断されたデータ型に変換する
	 * 
	 * @param columnName
	 * @param value
	 * @param columns
	 * @return
	 */
	public static Object createPraceHolder(String columnName, String value, List<Column> columns) {
		Object praceHolder = null;
		if (ScenarioUtil.checkStringValue(columnName, value)) {
			praceHolder = createPraceHolder(value, columns.stream().filter(c -> c.getColumnName().equals(columnName)).findFirst().orElse(null));
		}
		return praceHolder;
	}

	public static Object createPraceHolder(String columnName, String calc, String value, List<Column> columns) {
		Object praceHolder = null;
		if (ScenarioUtil.checkStringValue(calc)) {
			praceHolder = createPraceHolder(columnName, value, columns);
			if (calc.equals("+") && praceHolder instanceof Time) {
				String[] element = value.split(":");
				if (element.length == 3) {
					return Time.valueOf(LocalTime.of(0, 0).minusHours(Integer.parseInt(element[0])).minusMinutes(Integer.parseInt(element[1])).minusSeconds(Integer.parseInt(element[2])));
				} else if (element.length == 2) {
					return Time.valueOf(LocalTime.of(0, 0).minusHours(Integer.parseInt(element[0])).minusMinutes(Integer.parseInt(element[1])));
				} else {
					return Time.valueOf(LocalTime.of(0, 0).minusHours(Integer.parseInt(value) / 60).minusMinutes(Integer.parseInt(value) % 60));
				}
			}
		}
		return praceHolder;
	}

	public String createTable(CreateTable table) {
		String tableName = table.getTableName();
		String primaryKey = table.getPrimaryKey();

		String primarySql = "";
		if (ScenarioUtil.checkStringValue(primaryKey)) {
			primarySql = ", " + PRIMARY_KEY + "(" + primaryKey + ")";
		}
		// SQL文の作成
		String sql = new StringBuffer(CREATE_TABLE).append(tableName).append("( ").append(String.join(", ", table.getColumnAttr())).append(primarySql).append(" );").toString();
		//SQLの実行
		String message = executeUpdate(sql, this.placeHolders);

		message = message + executeUpdate(new StringBuffer(ALTER_TABLE).append(tableName).append(" OWNER TO ").append(this.user).append(WITH_UR).toString(), this.placeHolders);
		if (message.contains("ERROR")) {
			return tableName + "テーブルを作成できませんでした。";

		}
		return tableName + "テーブルを作成しました。";
	}

	public String createTemporaryTable(CreateTemporaryTable temporaryTable) {
		//一時テーブルの作成
		String sql1 = new StringBuffer(CREATE_TEMPORARY_TABLE).append(temporaryTable.getTemporarytableName()).append(AS).append(SELECT).append("*").append(FROM).append(temporaryTable.getTableName())
				.append(WHERE)
				.append(temporaryTable.getKeyColumn()).append("= ?;").toString();
		this.placeHolders.clear();
		this.placeHolders.add(temporaryTable.getKeyValue());
		String exe1 = executeUpdate(sql1, placeHolders);
		//一時テーブルの中身を更新
		String sql2 = new StringBuffer(UPDATE).append(temporaryTable.getTemporarytableName()).append(SET).append(temporaryTable.getKeyColumn()).append("= ?;").toString();
		this.placeHolders.clear();
		this.placeHolders.add(temporaryTable.getChangeValue());
		String exe2 = executeUpdate(sql2, placeHolders);
		//元のテーブルに新しいレコードとして複製
		String sql3 = new StringBuffer(INSERT_INTO).append(temporaryTable.getTableName()).append(" ").append(SELECT).append("*").append(FROM).append(temporaryTable.getTemporarytableName()).toString();
		String exe3 = executeUpdate(sql3, placeHolders);

		return getExecuteMessage(exe1, exe2, exe3);

	}

	public String alterTable(AlterTable alter) {
		String sql = new StringBuffer(ALTER_TABLE).append(alter.getTableName()).append(alter.getAction()).append(alter.getReplacedValue()).toString();
		if (alter.getAction().equals(AlterTable.RENAME_COLUMN)) {
			sql = sql.replaceAll(alter.getAction(), AlterTable.RENAME_COLUMN + alter.getReplaceColumn() + " TO ");
		}
		if (alter.getAction().equals(AlterTable.DROP_COLUMN)) {
			sql = new StringBuffer(sql).append(alter.getReplaceColumn()).toString();
		}
		if (alter.getAction().equals(AlterTable.ADD)) {
			sql = new StringBuffer(sql).append(
					ScenarioUtil.filteringArrayList(alter.getAddColumnList(), line -> !line.contains(NOT_NULL) || line.contains(DEFAULT)).stream().collect(Collectors.joining("," + AlterTable.ADD)))
					.toString();
		}
		sql = new StringBuffer(sql).append(WITH_UR).toString();

		//SQLの実行
		String checkValue = executeUpdate(sql, this.placeHolders);
		if (!checkValue.contains("ERROR") && !ScenarioUtil.filteringArrayList(alter.getAddColumnList(), line -> !line.contains(NOT_NULL) || line.contains(DEFAULT)).isEmpty()) {
			return String.join("、", ScenarioUtil.filteringArrayList(alter.getAddColumnList(), line -> !line.contains(NOT_NULL) || line.contains(DEFAULT)));
		}
		if (checkValue.equals("0")) {
			return alter.getReplacedValue();
		}

		return checkValue;
	}

	public String dropTable(String tableName) {
		// SQL文の作成
		String sql = new StringBuffer(DROP_TABLE_IF_EXISTS).append(tableName).append(WITH_UR).toString();
		return executeUpdate(sql, this.placeHolders);
	}

	public String truncateTable(String tableName) {
		String sql = new StringBuffer(TRUNCATE_TABLE).append(tableName).append(" RESTART IDENTITY").append(WITH_UR).toString();
		return executeUpdate(sql, this.placeHolders);
	}

	public String insert(Insert insert) {
		String insertSql = new StringBuffer(INSERT_INTO).append(insert.getTableName().toLowerCase())
				.append(" (").append(ScenarioUtil.mappingJoining(insert.getInsertColumns(), column -> column.getColumnName(), ", ")).append(")").append(VALUES).toString();
		this.placeHolders = new ArrayList<>();
		for (List<String> insertValue : insert.getInsertValues()) {
			String parameterSql = "(";
			int i = 0;
			for (Column column : insert.getInsertColumns()) {
				String element = insertValue.get(i++);
				if (ScenarioUtil.checkStringValue(element)) {
					if (CurrentDateTimeStamp.contains(element)) {
						parameterSql = parameterSql + element + ", ";
					} else {
						parameterSql = parameterSql + "?, ";
						this.placeHolders.add(createPraceHolder(element, column));
					}
				} else {
					parameterSql = parameterSql + "NULL, ";
				}
			}
			insertSql = insertSql + parameterSql.trim().replaceAll(",$", "), ");
		}
		return executeUpdate(insertSql.trim().replaceAll(",$", " ") + WITH_UR, placeHolders);
	}

	public String update(Update update) {
		String updateSql = new StringBuffer(UPDATE).append(update.getTableName().toLowerCase()).append(SET).toString();
		List<String> settingSQLs = new ArrayList<>();
		this.placeHolders.clear();
		if (ScenarioUtil.checkList(update.getSetting())) {

			for (UpdateSetting setting : update.getSetting()) {
				String settingSQL = "";
				if (Optional.ofNullable(setting.getCalc()).isPresent() && setting.getCalc().equals("=")) {
					if (ScenarioUtil.checkObjectValue(setting.getValue())) {
						settingSQL = setting.getSetColumn() + " = ? ";
					} else {
						settingSQL = setting.getSetColumn() + UPDATE_NULL;
					}

				} else if (ScenarioUtil.checkStringValue(setting.getCalc()) && !setting.getCalc().equals("=")) {
					if (setting.getCalc().equals("+") && setting.getValue() instanceof Time) {
						settingSQL = setting.getSetColumn() + " = " + setting.getSetColumn() + " - ? ";
					} else {
						settingSQL = setting.getSetColumn() + " = " + setting.getSetColumn() + " " + setting.getCalc() + " ? ";
					}
				}
				if (!settingSQL.isEmpty()) {
					settingSQLs.add(settingSQL);
				}
			}

			update.getSetting().stream().map(f -> f.getValue()).forEach(this.placeHolders::add);

		}
		updateSql = updateSql + String.join(", ", settingSQLs);

		List<Filter> filters = update.getFilters();
		if (filters != null && !filters.isEmpty()) {
			updateSql = updateSql + WHERE

					+ String.join(MainDAO.AND, ScenarioUtil.mappingArrayList(filters, filter -> judgeUpdateOperator(filter)));

			for (Filter filter : filters) {
				if (ScenarioUtil.checkList(filter.getInValueList())) {
					filter.getInValueList().stream().forEachOrdered(this.placeHolders::add);
				} else {
					this.placeHolders.add(filter.getValue());
				}
			}
		}

		return getExecuteMessage(executeUpdate(updateSql + WITH_UR, placeHolders));
	}

	private String judgeUpdateOperator(Filter filter) {

		if (ScenarioUtil.checkList(filter.getInValueList())) {
			String sql = filter.getFilterColumn() + " " + IN + "( ";
			for (int i = 0; i < filter.getInValueList().size(); i++) {
				sql = sql + "? ,";
			}
			return sql.replaceAll(",$", ") ");
		}
		return filter.getFilterColumn() + " " + filter.getOperator().replaceAll("%", "") + " ?";
	}

	public String delete(Delete delete) {
		this.placeHolders.clear();
		String deleteSql = new StringBuffer(DELETE_FROM).append(delete.getTableName().toLowerCase()).append(WHERE).toString();
		List<Filter> filters = delete.getFilters();
		if (filters != null && !filters.isEmpty()) {
			deleteSql = deleteSql + ScenarioUtil.mappingJoining(filters, filter -> filter.checkIsNull(), MainDAO.AND);
			filters.stream().map(f -> f.getValue()).forEach(this.placeHolders::add);
		}
		return executeUpdate(deleteSql + WITH_UR, placeHolders);
	}

	public Map<String, Integer> showTableList() throws DAOException {
		// SQL文の作成
		String sql = new StringBuffer(SELECT)
				.append("relname").append(AS).append("table_name").append(FROM).append("pg_stat_user_tables")
				.append(ORDER_BY).append("relname").append(WITH_UR).toString();

		//テーブル一覧の取得
		return selectTableList(sql);
	}

	public Map<String, Integer> showTableList(String filterValue, Operator operator) throws DAOException {
		this.placeHolders.clear();
		String operatorToString = operator.toString();
		if (operator.equals(Operator.STARTSWITH)) {
			operatorToString = Operator.LIKE.toString();
			filterValue = filterValue + "%";
		} else if (operator.equals(Operator.ENDSWITH)) {
			operatorToString = Operator.LIKE.toString();
			filterValue = "%" + filterValue;
		} else if (operator.equals(Operator.LIKE)) {
			filterValue = "%" + filterValue + "%";
		}
		// SQL文の作成
		String sql = new StringBuffer(SELECT)
				.append("relname").append(AS).append("table_name").append(FROM).append("pg_stat_user_tables")
				.append(WHERE).append("relname ").append(operatorToString).append(" ?")
				.append(ORDER_BY).append("relname").append(WITH_UR).toString();
		this.placeHolders.add(filterValue);

		//テーブル一覧の取得
		return selectTableList(sql);
	}

	private Map<String, Integer> selectTableList(String sql) throws DAOException {
		//SQLの実行
		try (ResultSet rs = executeQuery(sql, this.placeHolders);) {
			List<String> tableList = new ArrayList<String>();
			while (rs.next()) {
				tableList.add(rs.getString("table_name"));
			}
			return tableList.stream().collect(Collectors.toMap(tablename -> tablename, tableName -> {
				try (ResultSet rscount = executeQuery(new StringBuffer(SELECT).append(COUNT_FULL).append(FROM).append(tableName).toString(), this.placeHolders);) {
					while (rscount.next()) {
						return rscount.getInt(1);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return -1;
			}, (u, v) -> v, LinkedHashMap::new));
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DAOException("テーブルのカラム表示に失敗しました");
		}
	}

	public List<Column> showColumns(String tableName) throws DAOException {
		// SQL文の作成
		String sql = new StringBuffer(SELECT)
				.append("ordinal_position,column_name,data_type,character_maximum_length,is_nullable,column_default")
				.append(FROM).append("information_schema.columns")
				.append(WHERE).append("table_catalog= ? ")
				.append(AND).append("table_name= ? ")
				.append(ORDER_BY).append("ordinal_position").append(WITH_UR).toString();

		//プレースホルダの設定
		this.placeHolders.clear();
		this.placeHolders.add(dataBaseName);
		this.placeHolders.add(tableName.toLowerCase());

		//SQLの実行
		try (ResultSet rs = executeQuery(sql, this.placeHolders);) {
			List<Column> list = new ArrayList<Column>();

			while (rs.next()) {
				Column column = new Column(rs.getString("column_name"), rs.getString("data_type"), 0);
				column.setOrdinalPosition(rs.getInt("ordinal_position"));

				if (!(rs.getString("character_maximum_length") == null)) {
					column.setVarcharLength(rs.getInt("character_maximum_length"));
				}
				boolean isNullable = true;
				if (rs.getString("is_nullable").equals("NO")) {
					isNullable = false;
				}
				column.setNullable(isNullable);
				column.setDefaultValue(rs.getString("column_default"));
				list.add(column);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DAOException("テーブルのカラム表示に失敗しました");
		}
	}

	public List<Map<String, Object>> select(ExecuteQuery select) throws DAOException {

		List<Object> placeHolders = new ArrayList<>();
		// SQL文の作成
		String sql = new StringBuffer(SELECT).append("*").append(FROM).append(select.getTableName().toLowerCase()).toString();
		if (ScenarioUtil.checkList(select.getSelectColumnList())) {
			sql = sql.replace(" * ", " " + select.getSelectColumnList().stream().map(Object::toString).collect(Collectors.joining(", ")) + " ");
		}

		if (ScenarioUtil.checkList(select.getFilter())) {
			sql = new StringBuffer(sql).append(WHERE).toString()
					+ String.join(AND, select.getFilter().stream().map(where -> where.getFilterColumn() + " " + where.getOperator() + " ?").collect(Collectors.toList()));
			select.getFilter().stream().map(f -> f.getPlaceholder()).forEachOrdered(placeHolders::add);
		}

		if (ScenarioUtil.checkStringValue(select.getOrderByColumn())) {//ORDER BYの設定
			sql = new StringBuffer(sql).append(ORDER_BY).append(select.getOrderByColumn()).toString();
			if (select.isDesc()) {//降順の場合
				sql = sql + DESC;
				if (!select.isNullsFirst()) {
					sql = sql + NULLS_LAST;

				}
			} else if (select.isNullsFirst()) {//NULL 値を最初に表示させる場合
				sql = sql + NULLS_FIRST;
			}
		}

		if (ScenarioUtil.checkStringValue(select.getGroupByColumn())) {//GROUP BYの設定
			sql = new StringBuffer(sql).append(GROUP_BY).append(select.getGroupByColumn()).toString();
		}

		sql = sql + WITH_UR;
		//SQLの実行
		try (ResultSet rs = executeQuery(sql, placeHolders);) {
			List<Map<String, Object>> selectedRecords = new ArrayList<>();
			while (rs.next()) {
				Map<String, Object> record = new LinkedHashMap<>();
				for (Column column : select.getColumns()) {
					Object element;
					if (column.getDataType().equals("integer")) {
						element = rs.getInt(column.getColumnName());
					} else if (column.getDataType().equals("date")) {
						element = rs.getDate(column.getColumnName());
					} else if (column.getDataType().equals("timestamp without time zone")) {
						element = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(rs.getTimestamp(column.getColumnName()));
					} else {
						element = rs.getString(column.getColumnName());
						if (column.getDataType().equals("time without time zone") && element != null) {
							element = ((String) element).replaceAll(":00$", "");
						}
					}
					record.put(column.getColumnName(), element);
				}
				selectedRecords.add(record);
			}
			return selectedRecords;

		} catch (SQLException e) {
			e.printStackTrace();
			throw new DAOException("テーブルの選択に失敗しました");
		}

	}

	@Override
	public void close() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
