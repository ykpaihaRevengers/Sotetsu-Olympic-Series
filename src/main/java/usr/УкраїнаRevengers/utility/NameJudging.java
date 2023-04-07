package usr.УкраїнаRevengers.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import dbmng.bean.Column;
import home.tool.ScenarioUtil;

public class NameJudging implements NumberingNameList, DirectionsList {

	public enum CodeHeader {
		TY, MG, SO, YF, SI, TJ, 五輪
	}

	public enum Athletes {
		新綱島, 新横浜, 羽沢横浜国大, 西谷
	}

	public enum Direction {
		A, B;

		public Direction exchangeAB(Direction aORb) {
			if (aORb.equals(A)) {
				return Direction.B;
			} else {
				return Direction.A;
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
		Laview, TJраїна, S_TRAIN, 川越特急, 特急, 快速急行, ゑふраїна, 準特急, 通勤特急, TY特急, オリンピックライン特急, 急行, 通勤急行, 相特急行, 快速, TY急行, 準急, 区間準急, 各駅停車, 普通, オリンピックライン
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
			if (contentContains(nameList, false, TJ13, TJ30) && contentNotContains(nameList, SO08)) {
				return CodeHeader.TY + "t";
			}
			if (contentContains(nameList, false, SH01, SO10)) {
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
			if (contentNotContains(nameList, SO08)) {
				return CodeHeader.MG + "h";
			}
			if (contentNotContains(nameList, SO10)) {
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
		if (contentContains(nameList, false, SI10, SI39, SI17) && contentNotContains(nameList, YF06)) {
			if (contentContains(nameList, false, SI26, SI36) && contentNotContains(nameList, SI10)) {
				return CodeHeader.SI + "X";
			}
			if (contentContains(nameList, false, SI39)) {
				return CodeHeader.SI + "b";
			}
			if (contentContains(nameList, true, SI26, SI27, SI28)) {
				return CodeHeader.SI + "d";
			}

			return CodeHeader.SI + "k";
		}
		if (contentContains(nameList, false, SI05, SI37, SI39)) {
			return CodeHeader.SI + "r";
		}

		if (contentContains(nameList, false, TJ10, TJ22, TJ26, TJ30, TJ33) && contentNotContains(nameList, YF06)) {
			if (contentContains(nameList, false, TJ39, TJ47)) {
				return CodeHeader.TJ + "d";
			}
			return CodeHeader.TJ + "k";
		}
		throw new NullPointerException("該当なし");
	}

	public static String judgeFooter(List<String> nameList) {

		if (checkDirection(nameList)) {
			return "A";
		} else {
			return "B";
		}

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

	private static boolean contentNotContains(List<String> nameList, String... judgeValues) {
		return Arrays.asList(judgeValues).stream().allMatch(judgeValue -> !nameList.contains(judgeValue));
	}

	public static List<CodeHeader> judgeAttribute(String deployCode, List<String> scheduleNames) {

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
				if (contentContains(scheduleNames, false, TJ13, TJ22)) {
					headersTY.add(CodeHeader.TJ);
				}
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
			if (contentNotContains(scheduleNames, SO09, SO11)) {
				return Shubetsu.オリンピックライン特急;
			}
			return Shubetsu.オリンピックライン;
		}

		if (codeHeader.equals(CodeHeader.TY)) {
			if (contentNotContains(scheduleNames, TY02, TY04, TY06, TY10)) {
				if (contentNotContains(scheduleNames, TY03, F15)) {
					return Shubetsu.S_TRAIN;
				}
				if (contentNotContains(scheduleNames, TY05, TY08, TY09)) {
					if (contentNotContains(scheduleNames, TY13, F14, YF04)) {
						return Shubetsu.ゑふраїна;
					} else if (contentContains(scheduleNames, true, F14) && contentNotContains(scheduleNames, TY13)) {
						return Shubetsu.TY特急;
					} else {
						return Shubetsu.通勤特急;
					}
				} else {
					if (contentNotContains(scheduleNames, F14, YF04)) {
						return Shubetsu.急行;
					} else if (contentNotContains(scheduleNames, F14)) {
						return Shubetsu.通勤急行;
					} else {
						return Shubetsu.TY急行;
					}
				}
			} else {
				return Shubetsu.各駅停車;
			}
		}

		if (codeHeader.equals(CodeHeader.MG)) {
			if (scheduleNames.contains(MG01) && !scheduleNames.contains(MG07)) {
				if (contentNotContains(scheduleNames, MG09, SH02)) {
					return Shubetsu.快速急行;
				}
				return Shubetsu.急行;
			} else {
				return Shubetsu.各駅停車;
			}
		}

		if (codeHeader.equals(CodeHeader.YF)) {
			if (contentNotContains(scheduleNames, Y15)) {
				return Shubetsu.S_TRAIN;
			}
			return Shubetsu.各駅停車;
		}

		if (codeHeader.equals(CodeHeader.SO)) {
			if (contentNotContains(scheduleNames, SO01)) {
				if (contentNotContains(scheduleNames, SO09)) {
					return Shubetsu.特急;
				} else if (contentNotContains(scheduleNames, SO11, SO31)) {
					return Shubetsu.通勤特急;
				}
				return Shubetsu.各駅停車;
			} else {
				if (contentNotContains(scheduleNames, SO02, SO07, SO11, SO37)) {
					if (contentNotContains(scheduleNames, SO09)) {
						return Shubetsu.特急;
					} else {
						return Shubetsu.通勤特急;
					}
				} else if (contentNotContains(scheduleNames, SO02, SO07)) {
					if (contentNotContains(scheduleNames, SO05)) {
						return Shubetsu.通勤急行;
					} else {
						return Shubetsu.快速;
					}
				}
				return Shubetsu.各駅停車;
			}
		}

		if (codeHeader.equals(CodeHeader.TJ)) {

			if (deployCode.startsWith(CodeHeader.TY.toString()) && contentNotContains(scheduleNames, TJ12, TJ14, TJ18)) {
				return Shubetsu.ゑふраїна;
			}

			if (contentContains(scheduleNames, true, BKLO, TJ18) && contentNotContains(scheduleNames, TJ13, TJ14)) {
				return Shubetsu.TJраїна;
			}

			if (contentContains(scheduleNames, true, BKLO, TJ13) && contentNotContains(scheduleNames, TJ12, TJ14, TJ18)) {
				if (contentNotContains(scheduleNames, TJ11, TJ23, TJ24, TJ25, TJ27, TJ28)) {
					return Shubetsu.川越特急;
				}
				return Shubetsu.快速急行;
			}

			if (contentContains(scheduleNames, true, TJ12, TJ13, TJ14) && contentNotContains(scheduleNames, TJ02, TJ03)) {
				if (contentNotContains(scheduleNames, TJ07)) {
					return Shubetsu.急行;
				}
				if (contentContains(scheduleNames, false, BKLO)) {
					return Shubetsu.準急;
				}
			}
			if (deployCode.startsWith("TJd")) {
				return Shubetsu.普通;
			}
			return Shubetsu.各駅停車;
		}

		if (codeHeader.equals(CodeHeader.SI)) {

			if (deployCode.startsWith(CodeHeader.SI + "X")) {
				return Shubetsu.Laview;
			}

			if (contentNotContains(scheduleNames, SI06)) {
				if (contentNotContains(scheduleNames, SI18)) {
					return Shubetsu.快速急行;
				}
				if (contentContains(scheduleNames, true, SI11, SI12)) {
					return Shubetsu.通勤急行;
				}
				return Shubetsu.急行;
			}
			if (deployCode.contains(CodeHeader.TY.toString()) && !scheduleNames.contains(SI16)) {
				return Shubetsu.ゑふраїна;
			}

			if (contentContains(scheduleNames, true, SI06, SI16) && contentNotContains(scheduleNames, SI02, SI03, SI07, SI08)) {
				if (scheduleNames.contains(SI12)) {
					return Shubetsu.準急;
				} else {
					return Shubetsu.快速;
				}
			}
			if (contentContains(scheduleNames, true, SI07, SI08) && contentNotContains(scheduleNames, SI02, SI03)) {
				return Shubetsu.区間準急;
			}

			if (contentContains(scheduleNames, false, SI07, SI39)) {
				return Shubetsu.各駅停車;
			}

			if (deployCode.startsWith("SId")) {
				return Shubetsu.普通;
			}

			if (deployCode.contains(CodeHeader.YF.toString()) && contentContains(scheduleNames, true, SI12) && contentNotContains(scheduleNames, SI11, SI13)) {
				return Shubetsu.S_TRAIN;
			}

			if (deployCode.contains(CodeHeader.TY.toString()) && contentContains(scheduleNames, true, SI10) && contentNotContains(scheduleNames, SI13)) {
				return Shubetsu.S_TRAIN;
			}

			return Shubetsu.各駅停車;

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

	public static String judgeStandardSortName(CodeHeader codeHeader, String tableName) {

		if (tableName.endsWith("a")) {
			switch (codeHeader) {
			case TY:
				return TY01 + _DEP;
			case MG:
				return COTHGI + _DEP;
			case SO:
				return SO08 + _DEP;
			case TJ:
				return TJ11 + _DEP;
			case SI:
				return SI17 + _DEP;
			case YF:
				return BKLO + _DEP;
			case 五輪:
				return SJK + _DEP;
			}
		} else if (tableName.endsWith("b")) {
			switch (codeHeader) {
			case TY:
				return MM06 + _DEP;
			case MG:
				return COTHGI + _DEP;
			case SO:
				return SO10 + _DEP;
			case TJ:
				return TJ11 + _DEP;
			case SI:
				return SI01 + _DEP;
			case YF:
				return BKLO + _DEP;
			case 五輪:
				return Tokyo2020_Olympic_Gateway + _DEP;
			}
		}
		return null;
	}

	public static String judgeValueToNull(CodeHeader codeHeader, String tableName, String ikisaki, String columnKey, String previousKey, String previousValue) {

		if (ScenarioUtil.anyMatch(previousValue, EXCEL_NULL, TERMINUS)) {
			return EXCEL_NULL;
		}
		if ((ikisaki + _DEP).equals(columnKey)) {
			return TERMINUS;
		}
		if (tableName.endsWith("a")) {
			switch (codeHeader) {
			case TY:
				if (ScenarioUtil.anyMatch(ikisaki, SO37, SO18, SO14, SO08) && columnKey.equals(TY14 + _ARR)) {
					return EXCEL_NULL;
				}
				if (ScenarioUtil.anyMatch(ikisaki, Y24, Y22) && columnKey.equals(YF07 + _ARR)) {
					return EXCEL_NULL;
				}
				return PASS;

			case MG:
				if (columnKey.equals(I27 + _DEP) && !previousValue.equals(EXCEL_NULL)) {
					return NON_VIA;
				}
				if (previousValue.equals(NON_VIA)) {
					return NON_VIA;
				}
				return PASS;

			case SO:
				if (ScenarioUtil.anyMatch(ikisaki, SO18, SO14, SO17) && columnKey.equals(SO31 + _ARR)) {
					return NON_VIA;
				}
				if (previousValue.equals(NON_VIA) && !columnKey.equals(SO11 + _ARR)) {
					return NON_VIA;
				}
				if (ScenarioUtil.noneMatch(previousValue, NON_VIA, EXCEL_NULL) && columnKey.equals(SO11 + _ARR)) {
					return EXCEL_NULL;
				}
				return PASS;

			case TJ:
				if (!ikisaki.equals(BKLO) && columnKey.equals(TJ10 + _ARR)) {
					return EXCEL_NULL;
				}
				return PASS;

			case SI:
				if (!ikisaki.equals(BKLO) && columnKey.equals(SI05 + _ARR)) {
					return EXCEL_NULL;
				}
				if (ikisaki.equals(BKLO) && (columnKey.contains(SI38) || columnKey.contains(SI37))) {
					return NON_VIA;
				}
				if (columnKey.equals(SI41 + _DEP) && !previousValue.equals(EXCEL_NULL)) {
					return NON_VIA;
				}
				if (columnKey.equals(SI40 + _ARR) && previousValue.equals(NON_VIA)) {
					return NON_VIA;
				}
				if (columnKey.equals(SI40 + _DEP) && previousValue.equals(NON_VIA)) {
					return NON_VIA;
				}
				if (columnKey.equals(SI39 + _DEP) && !previousValue.equals(EXCEL_NULL)) {
					return NON_VIA;
				}
				if (ikisaki.equals(BKLO) && ScenarioUtil.anyMatch(columnKey, SI38 + _ARR, SI38 + _DEP, SI37 + _ARR, SI37 + _DEP)) {
					return NON_VIA;
				}
				return PASS;

			case YF:
				return PASS;

			case 五輪:
				return PASS;
			}
		} else if (tableName.endsWith("b")) {
			switch (codeHeader) {
			case TY:
				if (ScenarioUtil.anyMatch(ikisaki, SI10, SI12, SI15, SI17, SI19, SI41, SI23, SI26, SI36) && columnKey.equals(YF05 + _ARR)) {
					return EXCEL_NULL;
				}
				return PASS;
			case MG:
				if (columnKey.equals(SH01 + _ARR) && !previousValue.equals(EXCEL_NULL)) {
					return EXCEL_NULL;
				}
				if (ScenarioUtil.anyMatch(ikisaki, YF01, YF05, YF06, YF09, F13, TY01, TJ14, TJ22, TJ30, TJ33) && columnKey.equals(MG07 + _ARR)) {
					return EXCEL_NULL;
				}
				if (columnKey.equals(I04 + _ARR) && !previousValue.equals(EXCEL_NULL)) {
					return NON_VIA;
				}
				if (previousValue.equals(NON_VIA)) {
					return NON_VIA;
				}
				if (ScenarioUtil.noneMatch(previousValue, NON_VIA, EXCEL_NULL) && columnKey.equals(N04 + _ARR)) {
					return EXCEL_NULL;
				}

				return PASS;
			case SO:
				if (columnKey.equals(SO37 + _DEP) && !previousValue.equals(EXCEL_NULL)) {
					return NON_VIA;
				}
				if (previousValue.equals(NON_VIA)) {
					return NON_VIA;
				}
				if (!ikisaki.equals(SO01) && columnKey.equals(SO07 + _ARR)) {
					return EXCEL_NULL;
				}
				return PASS;

			case TJ:
				return PASS;

			case SI:
				if (columnKey.equals(SI37 + _ARR) && !previousValue.equals(EXCEL_NULL)) {
					return NON_VIA;
				}
				if (columnKey.equals(SI39 + _ARR) && !previousValue.equals(EXCEL_NULL)) {
					return NON_VIA;
				}
				if (columnKey.equals(SI40 + _ARR) && !previousValue.equals(EXCEL_NULL)) {
					return NON_VIA;
				}
				if (columnKey.equals(SI07 + _ARR) && !previousValue.equals(NON_VIA)) {
					return EXCEL_NULL;
				}
				if (columnKey.equals(SI19 + _ARR) && !previousValue.equals(NON_VIA)) {
					return EXCEL_NULL;
				}
				if (ScenarioUtil.noneMatch(columnKey, SI06 + _ARR, SI07 + _ARR, SI19 + _ARR) && previousValue.equals(NON_VIA)) {
					return NON_VIA;
				}
				return PASS;

			case YF:
				return PASS;

			case 五輪:
				return PASS;
			}
		}
		throw new NullPointerException("該当なし");
	}

}
