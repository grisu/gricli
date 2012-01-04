package grisu.gricli.util;

import java.util.Arrays;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

public class OutputHelpers {

	public static String getTable(List<List<String>> listlist) {
		return getTable(listlist, false, 0, new Integer[] {});
	}

	public static String getTable(List<List<String>> listlist,
			boolean emptyLineAfterTitle, int leftColumnMinWidth,
			Integer[] columnsToRightAlign) {

		Arrays.sort(columnsToRightAlign);

		final int listSize = listlist.get(0).size();

		final List<Integer> maxValues = new LinkedList<Integer>();
		for (int listIndex = 0; listIndex < listSize; listIndex++) {
			int max = 0;
			for (int list = 0; list < listlist.size(); list++) {
				final int length = listlist.get(list).get(listIndex).length();
				if (length > max) {
					max = length;
				}
			}
			if ((listIndex == 0) && (max < leftColumnMinWidth)) {
				maxValues.add(leftColumnMinWidth);
			} else {
				maxValues.add(max);
			}
		}

		final StringBuffer result = new StringBuffer();
		final Formatter formatter = new Formatter(result, Locale.US);

		for (int i = 0; i < listlist.size(); i++) {

			if (emptyLineAfterTitle && (i == 1)) {
				result.append("\n");
			}

			final StringBuffer formatterString = new StringBuffer();

			for (int j = 0; j < listlist.get(i).size(); j++) {
				formatterString.append("%");
				if (Arrays.binarySearch(columnsToRightAlign, j) < 0) {
					formatterString.append("-");
				}
				formatterString.append(maxValues.get(j) + 3 + "s");
			}
			formatterString.append("%n");

			final List<String> list = listlist.get(i);

			formatter.format(formatterString.toString(),
					list.toArray(new String[] {}));

		}

		return result.toString();
	}

	public static String getTable(Map<String, String> map) {

		final List<List<String>> listlist = Lists.newLinkedList();
		for (final String key : map.keySet()) {
			final List<String> list = Lists.newLinkedList();
			list.add(key);
			String value = map.get(key);
			if (StringUtils.isBlank(value)) {
				value = "";
			}
			list.add(value);
			listlist.add(list);
		}

		return getTable(listlist);
	}

	public static void main(String[] args) {

		final List<List<String>> list = Lists.newArrayList();

		final List<String> list1 = Lists.newArrayList(
				"value1asdfsafsfsafsfsadfsadfsa", "value2", "fffffffffffffff");
		final List<String> list2 = Lists.newArrayList(
				"valsdfdue1asdfasdfsfsdf", "value2", "value3");
		final List<String> list3 = Lists.newArrayList("valueas1", "valufdfde2",
				"value3");
		final List<String> list4 = Lists.newArrayList(
				"valaaue1asfsdfasfsadfsdfsdfa", "value2", "value3");

		list.add(list1);
		list.add(list2);
		list.add(list3);
		list.add(list4);

		final String result = getTable(list);

		System.out.println(result);

	}

}
