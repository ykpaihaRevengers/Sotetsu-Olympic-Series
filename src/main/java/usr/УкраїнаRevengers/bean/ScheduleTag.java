package usr.УкраїнаRevengers.bean;

public class ScheduleTag {
	private String name;
	private String arrive;
	private String departure;

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
	}

	public ScheduleTag(String name, String arrive) {
		super();
		this.name = name;
		this.arrive = arrive;
	}

}
