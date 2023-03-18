package home.tool;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ScenarioUtil {

	/**
	 * Object型のnullや空文字のチェックを行う
	 * @param values
	 * @return
	 */
	public static boolean checkObjectValue(Object... values) {
		return Arrays.asList(values).stream().allMatch(value -> value != null);
	}

	/**
	 * String型のnullや空文字のチェックを行う
	 * @param values
	 * @return
	 */
	public static boolean checkStringValue(String... values) {
		return Arrays.asList(values).stream().allMatch(value -> !Optional.ofNullable(value).orElse("").isEmpty());
	}

	/**
	 * 配列型のnullや空配列のチェックを行う
	 * @param <T>
	 * @param values
	 * @return
	 */
	public static <T> boolean checkList(List<T>... values) {
		return Arrays.asList(values).stream().allMatch(value -> !Optional.ofNullable(value).orElse(new ArrayList<>()).isEmpty());
	}

	/**
	 * 分割した文字をArrayListに格納する
	 * @param value
	 * @param regex
	 * @return
	 */
	public static List<String> split(String value, String regex) {
		return Arrays.asList(value.split(regex));
	}

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

	/**
	 * Streamのmap機能を使用して、ArrayListを編集する
	 * @param <T>
	 * @param <R>
	 * @param editList
	 * @param function
	 * @return
	 */
	public static <T, R> List<R> mappingArrayList(List<T> editList, Function<T, R> function) {
		return editList.stream().map(function).collect(Collectors.toList());
	}

	/**
	 *  Streamのfilter機能を使用して、ArrayListをから特定の要素を抽出する
	 * @param <T>
	 * @param editList
	 * @param predicate
	 * @return
	 */
	public static <T> List<T> filteringArrayList(List<T> editList, Predicate<T> predicate) {
		return editList.stream().filter(predicate).collect(Collectors.toList());
	}

	/**
	 * Streamのmap機能を使用して、ArrayListから文字列を作成する
	 * @param <T>
	 * @param editList
	 * @param function
	 * @param delimiter
	 * @return
	 */
	public static <T> String mappingJoining(List<T> editList, Function<T, String> function, CharSequence delimiter) {
		return editList.stream().map(function).collect(Collectors.joining(delimiter));
	}

}
