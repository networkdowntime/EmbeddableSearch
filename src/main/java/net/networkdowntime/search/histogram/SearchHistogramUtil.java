package net.networkdowntime.search.histogram;

import gnu.trove.map.hash.TLongIntHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.networkdowntime.search.SearchResult;
import net.networkdowntime.search.SearchResultComparator;
import net.networkdowntime.search.SearchResultType;

public class SearchHistogramUtil {

	public static FixedSizeSortedSet<SearchResult> resultsMapToLongSet(SearchResultType resultType, TLongIntHashMap results, int limit) {
		FixedSizeSortedSet<SearchResult> orderedResults = new FixedSizeSortedSet<SearchResult>(new SearchResultComparator(), limit);

		int count = 0;
		for (Long result : results.keys()) {
			count = results.get(result);
			orderedResults.add(new SearchResult<Long>(resultType, result, count));
		}

		return orderedResults;
	}

	public static FixedSizeSortedSet<SearchResult> resultsMapToStringSet(SearchResultType resultType, TObjectIntHashMap<String> results, int limit) {
		FixedSizeSortedSet<SearchResult> orderedResults = new FixedSizeSortedSet<SearchResult>(new SearchResultComparator(), limit);

		int count = 0;
		for (Object result : results.keys()) {
			count = results.get(result);
			orderedResults.add(new SearchResult<String>(resultType, (String) result, count));
		}

		return orderedResults;
	}

	public static void addResultToMap(TLongIntHashMap results, TLongIntHashMap resultsToAdd) {
		long[] resultsToAddKeys = resultsToAdd.keys();
		int[] resultsToAddValues = resultsToAdd.values();

		for (int i = 0; i < resultsToAddKeys.length; i++) {
			int count = 0;

			if (results.contains(resultsToAddKeys[i])) {
				count = results.get(resultsToAddKeys[i]);
			}
			count += resultsToAddValues[i];
			results.put(resultsToAddKeys[i], count);
		}
	}

	public static void addResultToMap(TObjectIntHashMap<String> results, TObjectIntHashMap<String> resultsToAdd) {
		Object[] resultsToAddKeys = resultsToAdd.keys();
		int[] resultsToAddValues = resultsToAdd.values();

		for (int i = 0; i < resultsToAddKeys.length; i++) {
			int count = 0;

			if (results.contains(resultsToAddKeys[i])) {
				count = results.get(resultsToAddKeys[i]);
			}
			count += resultsToAddValues[i];
			results.put((String) resultsToAddKeys[i], count);
		}
	}

	static void addResultToMap(TLongIntHashMap results, long result, int countIncrement) {
		int count = 0;

		if (results.contains(result)) {
			count = results.get(result);
		}
		count += countIncrement;
		results.put(result, count);

	}
}
