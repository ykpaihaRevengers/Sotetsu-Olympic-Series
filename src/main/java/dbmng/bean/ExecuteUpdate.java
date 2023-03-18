package dbmng.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import home.dao.MainDAO;
import home.dao.MainDAO.Operator;
import home.dao.err.DAOException;
import home.tool.ScenarioUtil;

public class ExecuteUpdate {

	private String tableName;
	private String executionType;
	private List<Column> columns;
	private List<Filter> filters;

	public ExecuteUpdate(String executionType, String tableName) {
		super();
		this.executionType = executionType;
		this.tableName = tableName.toLowerCase();
		this.filters = new ArrayList<>();
	}

	public ExecuteUpdate(String executionType, String tableName, MainDAO dao) throws DAOException {
		super();
		this.executionType = executionType;
		this.tableName = tableName;
		this.columns = dao.showColumns(tableName);
		this.filters = new ArrayList<>();
	}

	public ExecuteUpdate(String tableName, MainDAO dao) throws DAOException {
		super();
		this.tableName = tableName;
		this.columns = dao.showColumns(tableName);
		this.filters = new ArrayList<>();
	}

	public class Filter {
		private String filterColumn;
		private String operator;
		private Object value;
		private List<Object> inValueList;

		private boolean or;

		public Filter(String filterColumn, String operator, Object value) {
			super();
			this.filterColumn = filterColumn;
			this.operator = operator;
			this.value = value;
		}

		public Filter(String filterColumn) {
			super();
			this.filterColumn = filterColumn;
			this.operator = MainDAO.IN;
			this.inValueList = new ArrayList<>();
		}

		public String getFilterColumn() {
			return filterColumn;
		}

		public String getOperator() {
			return operator;
		}

		public Object getValue() {
			return value;
		}

		public boolean isOr() {
			return or;
		}

		public void setOr(boolean or) {
			this.or = or;
		}

		public boolean checkNullOrEmpty() {
			return Optional.ofNullable(this.filterColumn).isPresent() && Optional.ofNullable(this.operator).isPresent() && Optional.ofNullable(this.value).isPresent()
					&& !(this.filterColumn.isEmpty() || this.operator.isEmpty());
		}

		public Column getColumnAttribute(List<Column> columns) {
			return columns.stream().filter(c -> c.getColumnName().equals(this.filterColumn)).findFirst().orElse(null);
		}

		public String checkIsNull() {
			if (this.value == null) {
				return this.filterColumn + " IS NULL";
			}
			if (this.value instanceof String && ((String) this.value).isEmpty()) {
				return this.filterColumn + " = ''";
			}
			return this.filterColumn + " " + this.operator.replaceAll("%", "") + " ?";
		}

		public List<Object> getInValueList() {
			return inValueList;
		}

		public void setInValue(Object inValue) {
			this.inValueList.add(inValue);
		}

	}

	public String getExecutionType() {
		return executionType;
	}

	public void setExecutionType(String executionType) {
		this.executionType = executionType;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public String getTableName() {
		return tableName;
	}

	public List<Filter> getFilters() {
		return filters;
	}

	public void setFilters(String filterColumn, String operator, String filterValue) {
		if (ScenarioUtil.checkStringValue(filterColumn, operator)) {
			Object placeHolder = MainDAO.createPraceHolder(filterColumn, filterValue, columns);
			this.filters.add(new Filter(filterColumn, operator, checkOpelationFlug(operator, placeHolder)));
		}
	}

	public void setFilters(String filterColumn, Operator operator, String filterValue) {
		setFilters(filterColumn, operator.toString(), filterValue);
	}

	public void setFilters(String filterColumn, List<String> filterContainValues) {
		if (ScenarioUtil.checkStringValue(filterColumn) && ScenarioUtil.checkList(filterContainValues)) {
			Filter filter = new Filter(filterColumn);
			filterContainValues.stream().map(filterContainValue -> filterContainValue.trim()).map(filterContainValue -> MainDAO.createPraceHolder(filterColumn, filterContainValue, columns))
					.forEachOrdered(filter::setInValue);
			this.filters.add(filter);
		}
	}

	/**
	 * カラム名からカラムの要素を取得
	 * @param columns
	 * @param inputcolumnName
	 * @return
	 */
	public Column getColumnAttribute(List<Column> columns, String inputcolumnName) {
		return columns.stream().filter(c -> c.getColumnName().equals(inputcolumnName)).findFirst().orElse(null);
	}

	private static Object checkOpelationFlug(String opelationFlug, Object placeholder) {
		if (opelationFlug.equals("LIKE")) {
			//あいまい検索
			placeholder = "%" + placeholder + "%";
		} else if (opelationFlug.equals("LIKE%")) {
			//前方一致
			placeholder = placeholder + "%";
		} else if (opelationFlug.equals("%LIKE")) {
			//後方一致
			placeholder = "%" + placeholder;
		}
		return placeholder;
	}

}
