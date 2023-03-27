package usr.УкраїнаRevengers.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import dbmng.bean.Column;
import home.tool.ScenarioUtil;

public class NameJudging implements NumberingNameList {

	public enum CodeHeader {
		TY, MG, SO, YF, SI, TJ, 五輪

	}

	public enum Athletes {
		新綱島, 新横浜, 羽沢横浜国大, 西谷
	}

	public enum Direction {
		lA, lB, eA, eB;

		public Direction exchangeAB(Direction aORb) {
			if (aORb.equals(lA)) {
				return Direction.lB;
			} else if (aORb.equals(lB)) {
				return Direction.lA;
			} else if (aORb.equals(eA)) {
				return Direction.eB;
			} else {
				return Direction.eA;
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
		Laview, TJраїна, S_TRAIN, 川越特急, 特急, 快速急行, ゑふраїна, 通勤特急, TY特急, 急行, 快速, TY急行, 準急, 区間準急, 各駅停車, OlympicLine, Olympic特急, 普通
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
			if (contentContains(nameList, false, TJ22, SO10)) {
				return CodeHeader.TY + "o";
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

		if (contentContains(nameList, false, SYH)) {
			if (contentNotContains(nameList, false, SO08)) {
				return CodeHeader.MG + "h";
			}
			if (contentNotContains(nameList, false, SO10)) {
				return CodeHeader.MG + "y";
			}
			return CodeHeader.MG + "o";
		} else if (contentContains(nameList, false, N19, N17)) {
			return CodeHeader.MG + "n";
		} else if (contentContains(nameList, false, I27, I25)) {
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
			if (contentContains(nameList, false, SI39)) {
				return CodeHeader.SI + "b";
			}

			return CodeHeader.SI + "k";
		}
		if (contentContains(nameList, false, SI05, SI37, SI39)) {
			return CodeHeader.SI + "r";
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
			return "eA";
		} else {
			return "eB";
		}

	}

	private static boolean checkIsLocal(List<String> nameList) {
		if (contentContains(nameList, true, TY01, TY02, TY07) || contentContains(nameList, true, Y21, Y24)) {
			if (contentContains(nameList, true, SI10) && contentNotContains(nameList, true, SI07)) {
				return false;
			}
			return true;
		}

		if (contentNotContains(nameList, true, TY02)) {
			return false;
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

	public static boolean checkDirection(List<String> nameList) {

		if (!nameList.isEmpty()) {
			String firstObjectName = nameList.get(0);
			String lastObjectName = nameList.get(nameList.size() - 1);

			String[] dirALastStops = { MM06, Y24, SO18, SO37 };
			if (Arrays.asList(dirALastStops).stream().anyMatch(sta -> sta.equals(lastObjectName))) {
				return true;
			} else if (Arrays.asList(dirALastStops).stream().anyMatch(sta -> sta.equals(firstObjectName))) {
				return false;
			}
			String[] dirBLastStops = { SR29, SR26, I27, SI36, TJ39, SI39, SI41 };
			if (Arrays.asList(dirBLastStops).stream().anyMatch(sta -> sta.equals(lastObjectName))) {
				return false;
			} else if (Arrays.asList(dirBLastStops).stream().anyMatch(sta -> sta.equals(firstObjectName))) {
				return true;
			}

			if (contentContains(nameList, true, TY01, TY03)) {
				return judgeDirection(nameList, TY01, TY03);
			}

			if (contentContains(nameList, true, TY01, BKLO)) {
				return judgeDirection(nameList, BKLO, TY01);
			}

			if (contentContains(nameList, true, MG01, MG03)) {
				return judgeDirection(nameList, MG01, MG03);
			}

			if (contentContains(nameList, true, SH01, SH03)) {
				return judgeDirection(nameList, SH03, SH01);
			}

			if (contentContains(nameList, true, SO01, SO08)) {
				return judgeDirection(nameList, SO01, SO08);
			}

			if (contentContains(nameList, true, SO08, SO10)) {
				return judgeDirection(nameList, SO08, SO10);
			}

			if (contentContains(nameList, true, YF06, BKLO)) {
				return judgeDirection(nameList, YF06, BKLO);
			}

			if (contentContains(nameList, true, BKLO, Y18)) {
				return judgeDirection(nameList, BKLO, Y18);
			}

			if (contentContains(nameList, true, N07, N08)) {
				return judgeDirection(nameList, N08, N07);
			}

			if (contentContains(nameList, true, I04, I09)) {
				return judgeDirection(nameList, I09, I04);
			}

			if (contentContains(nameList, true, BKLO, TJ13)) {
				return judgeDirection(nameList, TJ13, BKLO);
			}

			if (contentContains(nameList, true, BKLO, TJ10)) {
				return judgeDirection(nameList, TJ10, BKLO);
			}

			if (contentContains(nameList, true, BKLO, SI10)) {
				return judgeDirection(nameList, SI10, BKLO);
			}

			if (contentContains(nameList, true, BKLO, SI17)) {
				return judgeDirection(nameList, SI17, BKLO);
			}

			if (contentContains(nameList, true, SJK, Tokyo2020_Olympic_Gateway)) {
				return judgeDirection(nameList, SJK, Tokyo2020_Olympic_Gateway);
			}
		}
		return false;
	}

	//名前で方向を決定
	private static Boolean judgeDirection(List<String> nameList, String dirBside, String dirAside) {
		if (nameList.contains(dirBside) && nameList.contains(dirAside)) {
			if (nameList.indexOf(dirBside) < nameList.indexOf(dirAside)) {
				return true;
			}
			return false;
		}
		return null;
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

			if (deployCode.startsWith(CodeHeader.TY.toString() + "t")) {
				headersTY.add(CodeHeader.TJ);
			}

			if (deployCode.startsWith(CodeHeader.TY.toString() + "s")) {
				headersTY.add(CodeHeader.SI);
			}

			if (deployCode.startsWith(CodeHeader.TY.toString() + "o")) {
				headersTY.add(CodeHeader.MG);
				headersTY.add(CodeHeader.SO);
			}

			return headersTY;
		}

		if (deployCode.startsWith(CodeHeader.MG.toString())) {
			List<CodeHeader> headersMG = new ArrayList<>();
			headersMG.add(CodeHeader.MG);
			if (deployCode.startsWith(CodeHeader.MG.toString() + "o")) {
				headersMG.add(CodeHeader.SO);
			}
			return headersMG;
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
		if (codeHeader.equals(CodeHeader.MG) && deployCode.startsWith(CodeHeader.五輪.toString()) && name.equals(COTHGI)) {
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

		if (!deployCode.contains("e")) {
			return Shubetsu.各駅停車;
		}

		if (codeHeader.equals(CodeHeader.TY)) {
			if (!scheduleNames.contains(TY05)) {
				if (scheduleNames.contains(F14)) {
					return Shubetsu.TY特急;
				}
				return Shubetsu.ゑふраїна;
			} else {
				if (scheduleNames.contains(TY02) || scheduleNames.contains(TY12)) {
					return Shubetsu.各駅停車;
				}
				if (!scheduleNames.contains(F14)) {
					return Shubetsu.急行;
				}
				return Shubetsu.TY急行;
			}
		}
		if (codeHeader.equals(CodeHeader.MG)) {
			if (scheduleNames.contains(MG01) && !scheduleNames.contains(MG07)) {
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

			if (contentContains(scheduleNames, true, SO01, SO09)) {
				return Shubetsu.快速;
			}

			if (!scheduleNames.contains(SO11) && !scheduleNames.contains(SO31) && scheduleNames.contains(SO10) && (contentContains(scheduleNames, false, SO18, SO37))) {
				if (contentNotContains(scheduleNames, false, SO09)) {
					return Shubetsu.特急;
				}
				return Shubetsu.通勤特急;
			}
			return Shubetsu.各駅停車;
		}

		if (codeHeader.equals(CodeHeader.TJ)) {
			if (deployCode.contains(CodeHeader.TY.toString())) {
				if (scheduleNames.contains(TJ15)) {
					return Shubetsu.各駅停車;
				} else if (!contentContains(scheduleNames, false, TJ14)) {
					return Shubetsu.ゑふраїна;
				} else {
					return Shubetsu.急行;
				}
			}
			if (deployCode.contains(CodeHeader.YF.toString()) && scheduleNames.contains(TJ12)) {
				return Shubetsu.各駅停車;
			}
			if (deployCode.contains(CodeHeader.YF.toString()) && scheduleNames.contains(TJ47)) {
				return Shubetsu.普通;
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

			if (contentContains(scheduleNames, false, SI07, SI39)) {
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
			if (name.equals(SR29)) {
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
			if (name.equals(TJ39)) {
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
