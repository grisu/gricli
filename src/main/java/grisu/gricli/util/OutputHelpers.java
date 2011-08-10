package grisu.gricli.util;

import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.Lists;

public class OutputHelpers {

	public static String getTable(List<List<String>> listlist) {
		return getTable(listlist, false);
	}

	public static String getTable(List<List<String>> listlist,
			boolean emptyLineAfterTitle) {

		int listSize = listlist.get(0).size();

		List<Integer> maxValues = new LinkedList<Integer>();
		for (int listIndex = 0; listIndex < listSize; listIndex++) {
			int max = 0;
			for (int list = 0; list < listlist.size(); list++) {
				int length = listlist.get(list).get(listIndex).length();
				if (length > max) {
					max = length;
				}
			}
			maxValues.add(max);
		}

		StringBuffer result = new StringBuffer();
		Formatter formatter = new Formatter(result, Locale.US);

		for (int i = 0; i < listlist.size(); i++) {

			if (emptyLineAfterTitle && (i == 1)) {
				result.append("\n");
			}

			StringBuffer formatterString = new StringBuffer();

			for (int j = 0; j < listlist.get(i).size(); j++) {
				formatterString.append("%-");
				formatterString.append(maxValues.get(j) + 3 + "s");
			}
			formatterString.append("%n");

			List<String> list = listlist.get(i);

			formatter.format(formatterString.toString(),
					list.toArray(new String[] {}));

		}

		return result.toString();
	}

	public static void main (String[] args) {

		List<List<String>> list = Lists.newArrayList();

		List<String> list1 = Lists.newArrayList(
				"value1asdfsafsfsafsfsadfsadfsa", "value2",
				"fffffffffffffff");
		List<String> list2 = Lists.newArrayList("valsdfdue1asdfasdfsfsdf",
				"value2",
				"value3");
		List<String> list3 = Lists.newArrayList("valueas1", "valufdfde2",
				"value3");
		List<String> list4 = Lists.newArrayList("valaaue1asfsdfasfsadfsdfsdfa",
				"value2", "value3");

		list.add(list1);
		list.add(list2);
		list.add(list3);
		list.add(list4);

		String result = getTable(list);

		System.out.println(result);

	}

}
