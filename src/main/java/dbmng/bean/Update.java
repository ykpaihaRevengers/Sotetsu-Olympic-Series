package dbmng.bean;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import home.dao.MainDAO;
import home.dao.MainDAO.Operator;
import home.dao.err.DAOException;
import home.tool.ScenarioUtil;

public class Update extends ExecuteUpdate {
	private List<UpdateSetting> setting;

	public List<UpdateSetting> getSetting() {
		return setting;
	}

	public void setSetting(String setColumn, String culc, String settingValue) {
		if (ScenarioUtil.checkStringValue(setColumn, culc) || ScenarioUtil.mappingArrayList(super.getColumns(), column -> column.getColumnName()).contains(setColumn)) {
			Object value = MainDAO.createPraceHolder(setColumn, culc, settingValue, super.getColumns());
			Object theoryValue = MainDAO.createPraceHolder(setColumn, settingValue, super.getColumns());
			this.setting.add(new UpdateSetting(setColumn, culc, value, theoryValue));
		}
	}

	public void setSetting(String setColumn, Operator operator, Object settingValue) {
		String settingValueToString = null;
		if (settingValue instanceof Integer) {
			settingValueToString = Integer.toString((Integer) settingValue);
		} else if (settingValue instanceof Date) {
			settingValueToString = settingValue.toString();
		} else if (settingValue != null) {
			settingValueToString = (String) settingValue;
		}
		this.setSetting(setColumn, operator.toString(), settingValueToString);
	}

	public Update(String tableName, MainDAO dao) throws DAOException {
		super(MainDAO.UPDATE, tableName, dao);
		this.setting = new ArrayList<>();
	}

	public Update(String tableName) {
		super(MainDAO.UPDATE, tableName);
	}

	public class UpdateSetting {
		private String setColumn;
		private String calc;
		private Object value;
		private Object theoryValue;

		private boolean isInterval;

		public UpdateSetting(String setColumn, String operator, Object value, Object theoryValue) {
			super();
			this.setColumn = setColumn;
			this.calc = operator;
			this.value = value;
			this.theoryValue = theoryValue;
		}

		public String getSetColumn() {
			return setColumn;
		}

		public String getCalc() {
			return calc;
		}

		public Object getValue() {
			return value;
		}

		public void setInterval(boolean isInterval) {
			this.isInterval = isInterval;
		}

		public boolean isInterval() {
			return isInterval;
		}

		public boolean checkNullOrEmpty() {
			return ScenarioUtil.checkStringValue(this.setColumn, this.calc) && ScenarioUtil.checkObjectValue(this.value);
		}

		public Object getTheoryValue() {
			return theoryValue;
		}

	}

}
