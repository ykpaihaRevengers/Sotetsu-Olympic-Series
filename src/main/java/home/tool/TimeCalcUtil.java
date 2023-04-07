package home.tool;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalTime;

public class TimeCalcUtil {

	/**
	 * LocalTime.ofを使用して、String型からTime型に変換する
	 * @param value
	 * @return
	 */
	public static Time timeValueOf(String value) {
		if (ScenarioUtil.checkStringValue(value)) {
			String[] element = value.split(":");
			if (element.length == 3) {
				return Time.valueOf(LocalTime.of(Integer.parseInt(element[0]), Integer.parseInt(element[1]), Integer.parseInt(element[2])));
			} else if (element.length == 2) {
				return Time.valueOf(LocalTime.of(Integer.parseInt(element[0]), Integer.parseInt(element[1])));
			} else {
				return Time.valueOf(LocalTime.of(Integer.parseInt(value) / 60, Integer.parseInt(value) % 60, 0));
			}
		}
		return null;
	}

	private static long TimeToMillsecound(Time t) {
		/*
		 * Timeをミリ秒に変換
		 */
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String sTime = sdf.format(t);
		int hh = Integer.parseInt(sTime.substring(0, 2));
		int mm = Integer.parseInt(sTime.substring(3, 5));
		int ss = Integer.parseInt(sTime.substring(6, 8));
		long ltime = (((hh * 60 + mm) * 60) + ss) * 1000;
		return ltime;
	}

	private static Time longToTime(long mytime) {
		/*
		 * long型に格納された時間をTime型に変換
		 */
		//ミリ秒から秒に変換
		long timess = mytime / 1000;
		long ss = timess % 60;
		//秒から分に変換
		timess = (timess - ss) / 60;
		long mm = timess % 60;
		//分から時に変換
		long hh = (timess - ss) / 60;
		String sTime = Long.toString(hh) + ":" + Long.toString(mm) + ":" + Long.toString(ss);
		return Time.valueOf(sTime);
	}

	private static int timetoMiniteValue(String timeValue) {
		String[] time = timeValue.split(":");
		return Integer.parseInt(time[0]) * 60 + Integer.parseInt(time[1]);
	}

	public static String addTime(String timeValue, int minute) {
		/*
		 * 分単位で時間の足し算が可能
		 */
		long retlong = 0;
	
		if (ScenarioUtil.checkStringValue(timeValue)) {
			retlong = TimeToMillsecound(timeValueOf(timeValue)) + minute * 60000;
		}
		return longToTime(retlong).toString().replaceAll(":00$", "");
	
	}

	public static String minusTime(String timeValue, int minute) {
		/*
		 * 分単位で時間の引き算が可能
		 */
		long retlong = 0;
	
		if (ScenarioUtil.checkStringValue(timeValue)) {
			long timeValuelong = TimeToMillsecound(timeValueOf(timeValue));
			long minuslong = minute * 60000;
			if (timeValuelong <= minuslong) {
				timeValuelong += 86400000;
			}
			retlong = timeValuelong - minuslong;
		}
	
		return longToTime(retlong).toString().replaceAll(":00$", "");
	}

	public static String culcSum(String value1, String value2) {
		/*
		 * String形式のままで時間の足し算が可能
		 */
		long retlong = 0;
		if (ScenarioUtil.checkStringValue(value1, value2)) {
			long value1tolong = TimeToMillsecound(timeValueOf(value1));
			long value2tolong = TimeToMillsecound(timeValueOf(value2));
			retlong = value1tolong + value2tolong;
		}
		return longToTime(retlong).toString().replaceAll(":00$", "");
	}

	public static String culcExtent(String smaller, String larger) {
		/*
		 * String形式のままで時間の引き算が可能
		 */
		long retlong = 0;

		if (ScenarioUtil.checkStringValue(larger, smaller)) {
			long along = TimeToMillsecound(timeValueOf(larger));
			long blong = TimeToMillsecound(timeValueOf(smaller));
			if (along <= blong) {
				along += 86400000;
			}
			retlong = along - blong;
		}

		return longToTime(retlong).toString().replaceAll(":00$", "");
	}

	public static int culcSumtoInteger(String value1, String value2) {
		return timetoMiniteValue(culcSum(value1, value2));
	}

	public static int culcExtenttoInteger(String smaller, String larger) {
		return timetoMiniteValue(culcExtent(smaller, larger));
	}

}
