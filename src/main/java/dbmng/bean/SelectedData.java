package dbmng.bean;

import java.util.List;
import java.util.Map;

public class SelectedData {

	private List<Column> columns;
	private List<Map<String, Object>> dbDataList;

	public SelectedData(List<Column> columns, List<Map<String, Object>> dbDataList) {
		super();
		this.columns = columns;
		this.dbDataList = dbDataList;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public List<Map<String, Object>> getDbDataList() {
		return dbDataList;
	}

}
