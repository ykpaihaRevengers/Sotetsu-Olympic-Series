package usr.УкраїнаRevengers.bean;

import home.tool.TimeCalcUtil;

public class ScheduleTag {
	private String name;
	private String arrive;
	private String departure;

	private Integer waitMoment;
	private Integer spending;
	private Integer totalMinute;

	private Integer comparingArrive;
	private Integer comparingDeparture;

	public String getName() {
		return name;
	}

	public String getArrive() {
		return arrive;
	}

	public String getDeparture() {
		return departure;
	}

	public ScheduleTag(String name, String arrive, String departure) {
		super();
		this.name = name;
		this.arrive = arrive;
		this.departure = departure;
		this.waitMoment = TimeCalcUtil.culcExtenttoInteger(arrive, departure);
	}

	public ScheduleTag(String name, String arrive) {
		super();
		this.name = name;
		this.arrive = arrive;
	}

	public ScheduleTag(ScheduleTag originScheduleTag, ScheduleTag editScheduleTag) {
		this(editScheduleTag.getName(), editScheduleTag.getArrive(), editScheduleTag.getDeparture());
		this.spending = editScheduleTag.getSpending();
		this.totalMinute = editScheduleTag.getTotalMinute();
		this.comparingArrive = TimeCalcUtil.culcExtenttoInteger(originScheduleTag.getArrive(), this.arrive);
		this.comparingDeparture = TimeCalcUtil.culcExtenttoInteger(originScheduleTag.getDeparture(), this.departure);
	}

	public ScheduleTag(String name) {
		this.name = name;
	}

	public Integer getWaitMoment() {
		return waitMoment;
	}

	public Integer getSpending() {
		return spending;
	}

	public void setSpending(Integer spending) {
		this.spending = spending;
	}

	public Integer getTotalMinute() {
		return totalMinute;
	}

	public void setTotalMinute(Integer totalMinute) {
		this.totalMinute = totalMinute;
	}

	public Integer getComparingArrive() {
		return comparingArrive;
	}

	public void setComparingArrive(Integer comparingArrive) {
		this.comparingArrive = comparingArrive;
	}

	public Integer getComparingDeparture() {
		return comparingDeparture;
	}

	public void setComparingDeparture(Integer comparingDeparture) {
		this.comparingDeparture = comparingDeparture;
	}

}
