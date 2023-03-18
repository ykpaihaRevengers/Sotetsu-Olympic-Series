package usr.УкраїнаRevengers.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import dbmng.bean.Column;
import home.tool.ScenarioUtil;

public class NameJudging {

	static final String Tokyo2020_Olympic_Gateway = "羽沢横浜国大";
	static final String SJK = "新宿";
	static final String BKLO = "池袋";
	static final String COTH = "武蔵小杉";

	static final String TY01 = "渋谷";
	static final String TY05 = "学芸大学";
	static final String TY07 = "自由が丘";
	static final String TY16 = "菊名";
	static final String MM06 = "元町・中華街";
	static final String Y21 = "月島";
	static final String Y24 = "新木場";
	static final String F13 = "新宿三丁目";
	static final String F12 = "東新宿";

	static final String YF06 = "小竹向原";
	static final String YF07 = "千川";
	static final String YF08 = "要町";

	static final String MG01 = "目黒";
	static final String MG13 = "日吉（神奈川県）";
	static final String NI03 = "白金高輪";

	static final String N19 = "赤羽岩淵";
	static final String N17 = "王子神谷";
	static final String SO18 = "海老名（相鉄・小田急）";

	static final String I27 = "西高島平";
	static final String I25 = "高島平";
	static final String SO37 = "湘南台";

	static final String TJ30 = "森林公園（埼玉県）";
	static final String TJ28 = "高坂";
	static final String TJ07 = "上板橋";
	static final String TJ10 = "成増";
	static final String TJ12 = "朝霞";
	static final String TJ13 = "朝霞台";
	static final String TJ14 = "志木";
	static final String TJ22 = "川越市";
	static final String TJ33 = "小川町（埼玉県）";

	static final String SI06 = "練馬";
	static final String SI10 = "石神井公園";
	static final String SI12 = "保谷";
	static final String SI16 = "秋津";

	static final String SI39 = "豊島園（西武線）";
	static final String SI17 = "所沢";
	static final String SI26 = "飯能";
	static final String SI36 = "西武秩父";
	static final String SI41 = "西武球場前";

	static final String SO01 = "横浜";
	static final String SO09 = "鶴ケ峰";
	static final String SO10 = "二俣川";
	static final String SO11 = "希望ケ丘";

	//JudgeIsLocal
	static final String TY02 = "代官山";
	static final String MG09 = "奥沢";
	static final String SO03 = "西横浜";
	static final String SI07 = "中村橋";

	static final String TJ04 = "大山（東京都）";

	public enum CodeHeader {
		TY, MG, SO, YF, SI, TJ, 五輪

	}

	public enum Athletes {
		新綱島, 新横浜, 羽沢横浜国大, 西谷
	}

	public enum Direction {
		lA, lB, єA, єB;

		public Direction exchangeAB(Direction aORb) {
			if (aORb.equals(lA)) {
				return Direction.lB;
			} else if (aORb.equals(lB)) {
				return Direction.lA;
			} else if (aORb.equals(єA)) {
				return Direction.єB;
			} else {
				return Direction.єA;
			}
		}

		public char getDirKey(Direction dir) {
			return dir.toString().charAt(1);
		}
	}

	public enum Terminus {
		isTerminusA, isTerminusB, Ordinary
	}

	public enum Shubetsu {
		Laview, TJраїна, S_TRAIN, 川越特急, 特急, 快速急行, ゑふраїна, TY特急, 急行, 快速, TY急行, 準急, 区間準急, 各駅停車, OlympicLine, Olympic特急
	}

	public enum FPAIPAColumnname {
		code, shubetsu, ikisaki;

		public static List<String> getBaseColumnName() {
			return ScenarioUtil.mappingArrayList(Arrays.asList(FPAIPAColumnname.values()), value -> value.toString());
		}

		public static List<String> getScheduleColumnName(List<Column> columns) {
			return columns.stream().map(column -> column.getColumnName()).filter(str -> !FPAIPAColumnname.getBaseColumnName().contains(str))
					.collect(Collectors.toList());

		}
	}

	public static String judgeHeader(List<String> nameList) {
		if (contentContains(nameList, true, TY01, TY07)) {
			if (contentContains(nameList, false, SI10)) {
				return CodeHeader.TY + "s";
			}
			if (contentContains(nameList, false, TJ22, TJ30)) {
				return CodeHeader.TY + "t";
			}
			return CodeHeader.TY + "f";
		}

		if (contentContains(nameList, true, Tokyo2020_Olympic_Gateway, SJK)) {
			return CodeHeader.五輪.toString();
		}

		if (contentContains(nameList, true, SO01, SO10)) {
			return CodeHeader.SO + "k";
		}
		if (contentContains(nameList, true, SO37)) {
			return CodeHeader.SO + "z";
		}

		if (contentContains(nameList, false, N19, N17, SO18)) {
			return CodeHeader.MG + "n";
		} else if (contentContains(nameList, false, I27, I25, SO37)) {
			return CodeHeader.MG + "i";
		}
		if (contentContains(nameList, true, Y21)) {
			if (contentContains(nameList, true, SI10)) {
				return CodeHeader.YF + "s";
			}
			if (contentContains(nameList, true, TJ22)) {
				return CodeHeader.YF + "t";
			}
			return CodeHeader.YF + "y";
		}
		if (contentContains(nameList, false, SI10, SI39, SI17) && contentNotContains(nameList, true, YF06)) {
			if (contentContains(nameList, false, SI26, SI36) && contentNotContains(nameList, true, SI10)) {
				return CodeHeader.SI + "X";
			}
			return CodeHeader.SI + "k";
		}
		if (contentContains(nameList, false, TJ10, TJ22, TJ30) && contentNotContains(nameList, true, YF06)) {
			return CodeHeader.TJ + "k";
		}
		return null;
	}

	public static String judgeFooter(List<String> nameList) {
		boolean isLocal = checkIsLocal(nameList);
		boolean directionA = checkDirection(nameList);

		if (isLocal && directionA) {
			return "lA";
		} else if (isLocal) {
			return "lB";
		} else if (directionA) {
			return "єA";
		} else {
			return "єB";
		}

	}

	private static boolean checkIsLocal(List<String> nameList) {
		if (contentContains(nameList, true, TY01, TY02, TY07) || contentContains(nameList, true, Y21, Y24)) {
			if (contentContains(nameList, true, SI10) && contentNotContains(nameList, true, SI07)) {
				return false;
			}
			return true;
		}

		if (contentContains(nameList, false, SI07, SI39)) {
			return true;
		}
		if (contentContains(nameList, false, TJ04, MG09, SO03)) {
			return true;
		}

		if (contentContains(nameList, false, NI03) && contentNotContains(nameList, false, MG01)) {
			return true;
		}
		if (contentContains(nameList, true, SO09, Tokyo2020_Olympic_Gateway)) {
			return true;
		}

		return false;
	}

	private static boolean checkDirection(List<String> nameList) {
		String firstObjectName = nameList.get(0);
		String lastObjectName = nameList.get(nameList.size() - 1);

		String[] dirALastStops = { MM06, Y24, SO18, SO37 };
		if (Arrays.asList(dirALastStops).stream().anyMatch(sta -> sta.equals(firstObjectName))) {
			return true;
		} else if (Arrays.asList(dirALastStops).stream().anyMatch(sta -> sta.equals(firstObjectName))) {
			return false;
		}

		if (firstObjectName.equals(BKLO)) {
			if (contentContains(nameList, false, Y21, TY01, Tokyo2020_Olympic_Gateway)) {
				return true;
			} else {
				return false;
			}
		} else if (lastObjectName.equals(BKLO)) {
			if (contentContains(nameList, false, Y21, TY01, Tokyo2020_Olympic_Gateway)) {
				return false;
			} else {
				return true;
			}
		}

		if (firstObjectName.equals(F13)) {
			return true;
		} else if (lastObjectName.equals(F13)) {
			return false;
		}

		if (lastObjectName.equals(TY16)) {
			return true;
		} else if (firstObjectName.equals(TY16)) {
			return false;
		}
		if (contentContains(nameList, true, SO10)) {
			if (firstObjectName.equals(SO01)) {
				return true;
			} else if (lastObjectName.equals(SO01)) {
				return false;
			}
		}

		if (lastObjectName.equals(MG13)) {
			return true;
		} else if (firstObjectName.equals(MG13)) {
			return false;
		}

		if (lastObjectName.equals(COTH)) {
			return true;
		} else if (firstObjectName.equals(COTH)) {
			return false;
		}

		if (lastObjectName.equals(NI03)) {
			return true;
		} else if (firstObjectName.equals(NI03)) {
			return false;
		}
		if (contentContains(nameList, true, Tokyo2020_Olympic_Gateway)) {
			if (firstObjectName.equals(SJK)) {
				return true;
			} else if (lastObjectName.equals(SJK)) {
				return false;
			}
		}

		return false;
	}

	private static boolean contentContains(List<String> nameList, boolean allMatch, String... judgeValues) {
		if (allMatch) {
			return Arrays.asList(judgeValues).stream().allMatch(judgeValue -> nameList.contains(judgeValue));
		}
		return Arrays.asList(judgeValues).stream().anyMatch(judgeValue -> nameList.contains(judgeValue));
	}

	private static boolean contentNotContains(List<String> nameList, boolean allMatch, String... judgeValues) {
		if (allMatch) {
			return Arrays.asList(judgeValues).stream().allMatch(judgeValue -> !nameList.contains(judgeValue));
		}
		return Arrays.asList(judgeValues).stream().anyMatch(judgeValue -> !nameList.contains(judgeValue));
	}

	public static List<CodeHeader> judgeAttribute(String deployCode) {

		if (deployCode.startsWith(CodeHeader.TY.toString())) {
			List<CodeHeader> headersTY = new ArrayList<>();
			headersTY.add(CodeHeader.TY);

			if (deployCode.startsWith(CodeHeader.TY.toString() + "s")) {
				headersTY.add(CodeHeader.SI);
			}

			if (deployCode.startsWith(CodeHeader.TY.toString() + "t")) {
				headersTY.add(CodeHeader.TJ);
			}
			return headersTY;
		}

		if (deployCode.startsWith(CodeHeader.MG.toString())) {
			return Arrays.asList(CodeHeader.MG);
		}
		if (deployCode.startsWith(CodeHeader.YF.toString())) {
			List<CodeHeader> headersYF = new ArrayList<>();
			headersYF.add(CodeHeader.YF);
			headersYF.add(CodeHeader.TY);

			if (deployCode.startsWith(CodeHeader.YF.toString() + "s")) {
				headersYF.add(CodeHeader.SI);
			}

			if (deployCode.startsWith(CodeHeader.YF.toString() + "t")) {
				headersYF.add(CodeHeader.TJ);
			}
			return headersYF;
		}

		if (deployCode.startsWith(CodeHeader.SI.toString())) {
			return Arrays.asList(CodeHeader.SI);
		}

		if (deployCode.startsWith(CodeHeader.TJ.toString())) {
			return Arrays.asList(CodeHeader.TJ);
		}

		if (deployCode.startsWith(CodeHeader.SO.toString())) {
			return Arrays.asList(CodeHeader.SO);
		}

		if (deployCode.startsWith(CodeHeader.五輪.toString())) {
			return Arrays.asList(CodeHeader.五輪, CodeHeader.SO, CodeHeader.MG);
		}

		return null;
	}

	public static boolean judgeSchedule(CodeHeader codeHeader, String deployCode, List<String> nameList, String name) {

		if (codeHeader.equals(CodeHeader.SI) && deployCode.startsWith(CodeHeader.TY.toString()) && name.equals(BKLO)) {
			return false;
		}
		if (codeHeader.equals(CodeHeader.SI) && deployCode.startsWith(CodeHeader.YF.toString()) && name.equals(BKLO)) {
			return false;
		}
		if (codeHeader.equals(CodeHeader.TJ) && deployCode.startsWith(CodeHeader.TY.toString()) && name.equals(BKLO)) {
			return false;
		}
		if (codeHeader.equals(CodeHeader.TJ) && deployCode.startsWith(CodeHeader.YF.toString()) && name.equals(BKLO)) {
			return false;
		}
		if (codeHeader.equals(CodeHeader.TY) && deployCode.startsWith(CodeHeader.YF.toString()) && name.equals(BKLO)) {
			return false;
		}
		if (codeHeader.equals(CodeHeader.TY) && deployCode.startsWith(CodeHeader.YF.toString()) && name.equals(YF07)) {
			return false;
		}
		if (codeHeader.equals(CodeHeader.TY) && deployCode.startsWith(CodeHeader.YF.toString()) && name.equals(YF08)) {
			return false;
		}
		if (codeHeader.equals(CodeHeader.MG) && deployCode.startsWith(CodeHeader.五輪.toString()) && name.equals(COTH)) {
			return false;
		}
		return nameList.contains(name);

	}

	public static Shubetsu judgeShubetsu(CodeHeader codeHeader, String deployCode, List<String> scheduleNames) {
		if (deployCode.contains(CodeHeader.五輪.toString())) {
			if (scheduleNames.contains(SO09)) {
				return Shubetsu.OlympicLine;
			} else {
				if (deployCode.endsWith(Direction.lA.toString()) || deployCode.endsWith(Direction.lB.toString())) {
					return Shubetsu.OlympicLine;
				} else {
					return Shubetsu.Olympic特急;
				}
			}
		}

		if (!deployCode.contains("є")) {
			return Shubetsu.各駅停車;
		}

		if (codeHeader.equals(CodeHeader.TY)) {
			if (!scheduleNames.contains(TY05)) {
				if (scheduleNames.contains(F12)) {
					return Shubetsu.TY特急;
				}
				return Shubetsu.ゑふраїна;
			} else {
				if (scheduleNames.contains(TY02)) {
					return Shubetsu.各駅停車;
				}
				if (scheduleNames.contains(BKLO) && !scheduleNames.contains(F12)) {
					return Shubetsu.急行;
				}
				return Shubetsu.TY急行;
			}
		}
		if (codeHeader.equals(CodeHeader.MG)) {
			if (scheduleNames.contains(MG01) && !scheduleNames.contains(MG09)) {
				return Shubetsu.急行;
			} else {
				return Shubetsu.各駅停車;
			}
		}

		if (codeHeader.equals(CodeHeader.YF)) {
			return Shubetsu.各駅停車;
		}

		if (codeHeader.equals(CodeHeader.SO)) {
			if (scheduleNames.contains(SO03)) {
				return Shubetsu.各駅停車;
			}

			if (scheduleNames.contains(SO09)) {
				return Shubetsu.快速;
			}

			if (scheduleNames.contains(SO11)) {
				return Shubetsu.急行;
			}
			if (!scheduleNames.contains(SO11) && scheduleNames.contains(SO10)) {
				return Shubetsu.特急;
			}
			return Shubetsu.各駅停車;
		}

		if (codeHeader.equals(CodeHeader.TJ)) {
			if (deployCode.contains(CodeHeader.TY.toString())) {
				if (scheduleNames.contains(TJ12)) {
					return Shubetsu.各駅停車;
				} else {
					return Shubetsu.ゑふраїна;
				}
			}
			if (deployCode.contains(CodeHeader.YF.toString()) && scheduleNames.contains(TJ12)) {
				return Shubetsu.各駅停車;
			}

			if (!scheduleNames.contains(TJ12)) {
				if (scheduleNames.contains(TJ13) && !scheduleNames.contains(TJ14)) {
					if (scheduleNames.contains(TJ28)) {
						return Shubetsu.快速急行;
					}
					return Shubetsu.川越特急;
				}
				if (!scheduleNames.contains(TJ13) && !scheduleNames.contains(TJ14)) {
					return Shubetsu.TJраїна;
				}
			} else {
				if (scheduleNames.contains(TJ07)) {
					return Shubetsu.準急;
				} else {
					return Shubetsu.急行;
				}
			}

		}

		if (codeHeader.equals(CodeHeader.SI)) {
			if (deployCode.contains(CodeHeader.TY.toString()) && !scheduleNames.contains(SI16)) {
				return Shubetsu.ゑふраїна;
			}

			if (scheduleNames.contains(SI07) || scheduleNames.contains(SI39)) {
				return Shubetsu.各駅停車;
			}
			if (scheduleNames.contains(SI06)) {

				if (scheduleNames.contains(SI12)) {
					return Shubetsu.準急;
				} else {
					return Shubetsu.快速;
				}
			} else {
				if (!scheduleNames.contains(BKLO)) {
					return Shubetsu.S_TRAIN;
				}

				if (scheduleNames.contains(SI10)) {
					return Shubetsu.急行;
				} else {
					return Shubetsu.Laview;
				}
			}

		}
		return null;
	}

	public static Terminus judgeEnding(String name, CodeHeader header, Direction aORb) {

		switch (header) {
		case TY:
			if (name.equals(MM06)) {
				return Terminus.isTerminusA;
			}
			break;

		case MG:
			if (name.equals(I27)) {
				return Terminus.isTerminusB;
			}
			break;

		case YF:
			if (name.equals(Y24)) {
				return Terminus.isTerminusA;
			}
			break;

		case SO:
			if (name.equals(SO01)) {
				return Terminus.isTerminusB;
			}
			if (name.equals(SO18)) {
				return Terminus.isTerminusA;
			}
			if (name.equals(SO37)) {
				return Terminus.isTerminusA;
			}
			break;

		case TJ:
			if (name.equals(BKLO)) {
				return Terminus.isTerminusA;
			}
			if (name.equals(TJ33)) {
				return Terminus.isTerminusB;
			}
			break;

		case SI:
			if (name.equals(BKLO)) {
				return Terminus.isTerminusA;
			}
			if (name.equals(SI39)) {
				return Terminus.isTerminusB;
			}
			if (name.equals(SI41)) {
				return Terminus.isTerminusB;
			}
			break;

		default:
			return Terminus.Ordinary;

		}

		return Terminus.Ordinary;

	}

}
