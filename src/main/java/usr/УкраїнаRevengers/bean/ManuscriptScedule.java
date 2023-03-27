package usr.УкраїнаRevengers.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import home.dao.MainDAO.CurrentDateTimeStamp;
import home.tool.ScenarioUtil;
import usr.УкраїнаRevengers.utility.NameJudging;

public class ManuscriptScedule {
	private String code;
	private List<ScheduleTag> scheduleTags;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setCode(String code, List<String> nameList) {
		String header = NameJudging.judgeHeader(nameList);
		String footer = NameJudging.judgeFooter(nameList);

		this.code = header + code + footer;
	}

	public List<ScheduleTag> getScheduleTags() {
		return scheduleTags;
	}

	public void setScheduleTags(List<ScheduleTag> scheduleTags) {
		this.scheduleTags = scheduleTags;
	}

	public List<List<String>> createInsertValues() {
		List<List<String>> insertValues = new ArrayList<>();

		for (ScheduleTag tag : this.scheduleTags) {
			List<String> insertValue = new ArrayList<>();
			insertValue.add(this.code);
			insertValue.add(tag.getName());
			insertValue.add(tag.getArrive());
			insertValue.add(tag.getDeparture());
			insertValue.add(CurrentDateTimeStamp.CURRENT_TIMESTAMP.toString());
			insertValues.add(insertValue);
			System.out.println("this.code : " + this.code);
			System.out.println("tag.getName() : " + tag.getName());
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
