package usr.УкраїнаRevengers.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import home.dao.MainDAO.CurrentDateTimeStamp;
import home.tool.ScenarioUtil;
import home.tool.TimeCalcUtil;
import usr.УкраїнаRevengers.utility.NameJudging;
import usr.УкраїнаRevengers.utility.NameJudging.Shubetsu;

public class ManuscriptScedule {
	private String code;
	private Shubetsu orthodoxShubetsu;
	private String ikisaki;
	private List<ScheduleTag> scheduleTags;

	public ManuscriptScedule(List<ScheduleTag> scheduleTags, String ordinalCode) {
		this.scheduleTags = scheduleTags;
		List<String> mappingArrayList = ScenarioUtil.mappingArrayList(scheduleTags, scheduleTag -> scheduleTag.getName().trim());
		String header = NameJudging.judgeHeader(mappingArrayList);
		String footer = NameJudging.judgeFooter(mappingArrayList);
		this.code = header + ordinalCode + footer;
	}

	public ManuscriptScedule(Map<String, Object> selectedIkisaki, List<Map<String, Object>> selectedScheduleData) {
		this.code = (String) selectedIkisaki.get("deployed_code");
		this.orthodoxShubetsu = Shubetsu.valueOf((String) selectedIkisaki.get("shubetsu"));
		this.ikisaki = (String) selectedIkisaki.get("ikisaki");
		this.scheduleTags = new ArrayList<ScheduleTag>();
		String culcSpendingValue = null;
		String shihatsuTime = "";
		for (Map<String, Object> selectedSchedule : selectedScheduleData) {
			String gotName = (String) selectedSchedule.get("name");
			String gotArr = (String) selectedSchedule.get("arr");
			String gotDep = (String) selectedSchedule.get("dep");
			ScheduleTag scheduleTag = new ScheduleTag(gotName, gotArr, gotDep);
			if (culcSpendingValue != null) {
				scheduleTag.setSpending(TimeCalcUtil.culcExtenttoInteger(culcSpendingValue, gotArr));
				scheduleTag.setTotalMinute(TimeCalcUtil.culcExtenttoInteger(shihatsuTime, gotArr));
			} else {
				scheduleTag.setSpending(0);
				shihatsuTime = gotDep;
				scheduleTag.setTotalMinute(TimeCalcUtil.culcExtenttoInteger(shihatsuTime, gotDep));
			}
			culcSpendingValue = gotDep;

			this.scheduleTags.add(scheduleTag);
		}

	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<ScheduleTag> getScheduleTags() {
		return scheduleTags;
	}

	public void setScheduleTags(List<ScheduleTag> scheduleTags) {
		this.scheduleTags = scheduleTags;
	}

	public Shubetsu getOrthodoxShubetsu() {
		return orthodoxShubetsu;
	}

	public String getIkisaki() {
		return ikisaki;
	}

	public List<List<String>> createInsertValues() {
		List<List<String>> insertValues = new ArrayList<>();
		int num = 1;
		for (ScheduleTag tag : this.scheduleTags) {
			List<String> insertValue = new ArrayList<>();
			insertValue.add(Integer.toString(num++));
			insertValue.add(this.code);
			insertValue.add(tag.getName());
			insertValue.add(tag.getArrive());
			insertValue.add(tag.getDeparture());
			insertValue.add(CurrentDateTimeStamp.CURRENT_TIMESTAMP.toString());
			insertValues.add(insertValue);
		}
		return insertValues;

	}

	public enum DirectionBaseColumns {
		code, shubetsu, ikisaki;

		public static List<String> getColumnNames() {
			return ScenarioUtil.mappingArrayList(Arrays.asList(DirectionBaseColumns.values()), a -> a.toString());
		}
	}

}
