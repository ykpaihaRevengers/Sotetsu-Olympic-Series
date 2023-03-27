
package dbmng.bean;

import java.util.ArrayList;
import java.util.List;

import home.dao.MainDAO;
import home.dao.MainDAO.Operator;
import home.dao.err.DAOException;
import home.tool.ScenarioUtil;

public class ExecuteQuery {
	private String tableName;
	private List<String> selectColumnList;
	private List<Column> columns;
	private List<Column> wholeColumns;
	private String countColumn;
	private List<Filters> filter;
	private String orderByColumn;
	private String groupByColumn;

	private boolean desc;
	private boolean isNullsFirst;

	public class Filters {
		private String filterColumn;
		private String operator;
		private Object placeholder;

		public Filters(String filterColumn, String operator, Object placeholder) {
			super();

			this.filterColumn = filterColumn;
			this.operator = operator;
			this.placeholder = placeholder;

		}

		public String getFilterColumn() {
			return filterColumn;
		}

		public String getOperator() {
			return operator;
		}

		public Object getPlaceholder() {
			return placeholder;
		}
	}

	public enum Sorting {
		DESC, NULLS_FIRST, DESC_NULLS_LAST
	}

	public ExecuteQuery(String tableName, MainDAO dao) throws DAOException {
		super();
		this.tableName = tableName.toLowerCase();
		this.columns = dao.showColumns(tableName.toLowerCase());
		List<Column> serialColumn = ScenarioUtil.filteringArrayList(columns, c -> c.isSerial());
		if (!serialColumn.isEmpty()) {
			this.orderByColumn = serialColumn.get(0).getColumnName();
		}
		this.filter = new ArrayList<>();
	}

	public ExecuteQuery(String tableName, MainDAO dao, List<String> selectColumnList) throws DAOException {
		this(tableName, dao);
		if (ScenarioUtil.checkList(selectColumnList)) {
			this.wholeColumns = columns;
			this.columns = ScenarioUtil.filteringArrayList(columns, a -> selectColumnList.contains(a.getColumnName()));
			this.selectColumnList = selectColumnList;
			if (!selectColumnList.contains(this.orderByColumn)) {
				this.orderByColumn = null;
			}
		}
	}

	public ExecuteQuery() {
	}

	public String getTableName() {
		return tableName;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public List<Filters> getFilter() {
		return filter;
	}

	public String getOrderByColumn() {
		return orderByColumn;
	}

	public boolean isDesc() {
		return desc;
	}

	public boolean isNullsFirst() {
		return isNullsFirst;
	}

	public List<String> getSelectColumnList() {
		return selectColumnList;
	}

	public void setFilter(String filterColumn, String opelationFlug, String filterValue) {
		List<Column> wholeColumns = this.wholeColumns;
		if (!ScenarioUtil.checkList(wholeColumns)) {
			wholeColumns = this.columns;
		}
		Object placeholder = null;
		for (Column column : wholeColumns) {
			if (column.getColumnName().equals(filterColumn)) {
				placeholder = MainDAO.createPraceHolder(filterValue, column);
				if (column.getDataType() != null && column.getDataType().equals("integer") && opelationFlug.contains("LIKE")) {
					filterColumn = "CAST(" + filterColumn + " AS TEXT)";
				}
				placeholder = checkOpelationFlug(opelationFlug, placeholder);
			}
		}
		this.filter.add(new ExecuteQuery().new Filters(filterColumn, opelationFlug.replaceAll("%", ""), placeholder));
	}

	public void setFilter(String filterColumn, Operator operator, String filterValue) {
		setFilter(filterColumn, operator.toString(), filterValue);
	}

	public void setOrderByColumn(String orderByColumn) {
		this.orderByColumn = orderByColumn;
	}

	public void setOrderByColumn(String orderByColumn, boolean desc) {
		if (ScenarioUtil.checkStringValue(orderByColumn)) {
			this.orderByColumn = orderByColumn;
			this.desc = desc;
		}
	}

	public void setOrderByColumn(String orderByColumn, Sorting sorting) {
		if (ScenarioUtil.checkStringValue(orderByColumn)) {
			this.orderByColumn = orderByColumn;
			this.isNullsFirst = sorting.equals(Sorting.NULLS_FIRST);
			this.desc = sorting.equals(Sorting.DESC);
			if (this.isNullsFirst && this.desc) {
			}
		}
	}

	private Object checkOpelationFlug(String opelationFlug, Object placeholder) {
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
		System.out.println("placeholder " + placeholder);
		return placeholder;
	}

	public String getCountColumn() {
		return countColumn;
	}

	public void setCountColumn(String countColumn) {
		this.countColumn = countColumn;
	}

	public String getGroupByColumn() {
		return groupByColumn;
	}

	public void setGroupByColumn(String groupByColumn) {
		if (ScenarioUtil.checkStringValue(groupByColumn)) {
			this.groupByColumn = groupByColumn;
		}
	}

}
