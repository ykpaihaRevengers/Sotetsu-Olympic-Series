package usr.УкраїнаRevengers.bean;

import java.util.ArrayList;
import java.util.List;

import home.tool.ScenarioUtil;

public class SceduleComparing {

	private ManuscriptScedule origin;
	private ManuscriptScedule edit;
	private ManuscriptScedule newSchedule;

	public enum NameDiff {
		OnlyinOrigin, Merged, OnlyinEdited;
	}

	public SceduleComparing(ManuscriptScedule origin, ManuscriptScedule edit) {
		this.origin = origin;
		this.edit = edit;

		List<ScheduleTag> newScheduleTags = new ArrayList<ScheduleTag>();

		for (ScheduleTag originSceduleTag : origin.getScheduleTags()) {
			for (ScheduleTag editSceduleTag : edit.getScheduleTags()) {
				if (originSceduleTag.getName().equals(editSceduleTag.getName())) {
					newScheduleTags.add(new ScheduleTag(originSceduleTag, editSceduleTag));
				}
			}
			if (ScenarioUtil.contentNotContains(ScenarioUtil.mappingArrayList(edit.getScheduleTags(), sceduletag -> sceduletag.getName()), originSceduleTag.getName())) {
				newScheduleTags.add(new ScheduleTag(originSceduleTag.getName()));
			}
		}
		this.newSchedule = new ManuscriptScedule(newScheduleTags, edit.getCode().substring(3, 9));
	}

	public SceduleComparing(ManuscriptScedule origin, ManuscriptScedule edit, String from, String to) {
		this.origin = origin;
		this.edit = edit;

		List<ScheduleTag> newScheduleTags = new ArrayList<ScheduleTag>();
		boolean count = false;
		for (ScheduleTag originSceduleTag : origin.getScheduleTags()) {
			if (originSceduleTag.getName().equals(from)) {
				count = true;
			}
			if (count) {
				for (ScheduleTag editSceduleTag : edit.getScheduleTags()) {
					if (originSceduleTag.getName().equals(editSceduleTag.getName())) {
						newScheduleTags.add(new ScheduleTag(originSceduleTag, editSceduleTag));
					}
				}
			} else {
				newScheduleTags.add(new ScheduleTag(originSceduleTag.getName()));
			}
			if (originSceduleTag.getName().equals(to)) {
				count = false;
			}
		}

		this.newSchedule = new ManuscriptScedule(newScheduleTags, edit.getCode().substring(3, 9));
	}

	public ManuscriptScedule getOrigin() {
		return origin;
	}

	public ManuscriptScedule getEdit() {
		return edit;
	}

	public ManuscriptScedule getNewSchedule() {
		return newSchedule;
	}

}
