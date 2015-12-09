package net.networkdowntime.search.histogram;

import gnu.trove.map.hash.TIntLongHashMap;
import gnu.trove.map.hash.TLongByteHashMap;
import gnu.trove.map.hash.TLongIntHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.networkdowntime.search.engine.InMemorySearchEngine;

/**
 * Single word/string histogram.
 * 
 * This implementation stores the hashcode of the word and a count based on how
 * many times it has been added/removed. Methods are static to reduce the memory
 * footprint of the class.
 * 
 * So there are two different datastructures in the Search Histogram. 1. Single
 * Result Map - This is an optimization to reduce overhead for sparsely used
 * words that are indexed. Basically, if a word only matches one result then it
 * only incurs the penalty of an extra int and long value being stored. Words
 * that show up in this have an implied count of 1 2.
 * 
 * 
 * @author rwiles
 *
 */
public class UnigramLongSearchHistogram {
	static final Logger logger = LogManager.getLogger(UnigramLongSearchHistogram.class.getName());

	@SuppressWarnings("unchecked")
	private Tuple<String>[] mostCommonWords = new Tuple[15];
	private Map<Integer, TLongByteHashMap> multiResultMap = new HashMap<Integer, TLongByteHashMap>();
	private TIntLongHashMap singleResultMap = new TIntLongHashMap();

	// public static Tuple<String>[] getMostCommonWords(UnigramHistogram
	// unigram) {
	// return UnigramHistogram.getMostCommonWords(unigram);
	// }

	public static void add(UnigramLongSearchHistogram unigram, String word, Long result) {
		word = word.toLowerCase();
		int wordKey = word.hashCode();
		int count = 0;

		TLongByteHashMap hashMap = unigram.multiResultMap.get(wordKey);

		if (hashMap == null) { // not more than 1 result already

			if (!unigram.singleResultMap.contains((wordKey))) { // no matches,
																// put result
																// into the
																// single result
																// map
				unigram.singleResultMap.put((wordKey), result);
				count = 1;
			} else { // one result already
				hashMap = new TLongByteHashMap();
				unigram.multiResultMap.put(wordKey, hashMap);

				// move match from the single result map to the multi result map
				long originalResult = unigram.singleResultMap.remove(wordKey);

				if (originalResult == result) { // we now have a count of two
												// for the original result
					hashMap.put(originalResult, (byte) 2);
				} else {
					hashMap.put(originalResult, (byte) 1);
					hashMap.put(result, (byte) 1);
				}
				count = 2;
			}

		} else { // more than 1 result already
			hashMap = unigram.multiResultMap.get(wordKey);
			if (hashMap.contains(result)) {
				hashMap.put(result, (byte) (hashMap.get(result) + 1));
			} else {
				hashMap.put(result, (byte) 1);
			}
			count = unigram.getMultiResultCount(hashMap);
		}

		unigram.mostCommonWords = Tuple.updateSortTupleArray(unigram.mostCommonWords, word, count, 15);
	}

	private int getMultiResultCount(TLongByteHashMap hashMap) {
		int count = 0;
		for (byte resultCount : hashMap.values()) {
			count += resultCount;
		}
		return count;
	}

	public static void remove(UnigramLongSearchHistogram unigram, String word, Long result) {
		word = word.toLowerCase();
		int wordKey = word.hashCode();
		int count = 0;

		TLongByteHashMap hashMap = unigram.multiResultMap.get(wordKey);

		if (hashMap == null) { // not more than 1 result already
			if (unigram.singleResultMap.contains(wordKey)) { // one result
				unigram.singleResultMap.remove(wordKey); // now no results
			}
		} else { // more than 1 result already
			if (hashMap.contains(result)) {
				hashMap.remove(result);
			}

			count = unigram.getMultiResultCount(hashMap);

			if (count == 1) {
				unigram.singleResultMap.put((wordKey), result);
				count = 1;
			}
		}

		count = getOccuranceCount(unigram, word);

		if (count == 1) {
			unigram.singleResultMap.put(wordKey, result); // now one result
			unigram.multiResultMap.remove(wordKey);
		}

		unigram.mostCommonWords = Tuple.updateSortTupleArray(unigram.mostCommonWords, word, count, 15);
	}

	public static boolean contains(UnigramLongSearchHistogram unigram, String word) {
		word = word.toLowerCase();
		int wordKey = word.hashCode();
		return (unigram.singleResultMap.contains((wordKey))) || (unigram.multiResultMap.get(wordKey) != null);
	}

	public static int getOccuranceCount(UnigramLongSearchHistogram unigram, String word) {
		word = word.toLowerCase();
		int wordKey = word.hashCode();

		int count = 0;

		TLongByteHashMap hashMap = unigram.multiResultMap.get(wordKey);

		if (hashMap == null) {
			if (unigram.singleResultMap.contains((wordKey))) { // one result
																// already
				count = 1;
			}
		} else {
			for (byte b : hashMap.values())
				count += b;
		}

		return count;
	}

	public static List<String> getOrderedResults(UnigramLongSearchHistogram unigram, Set<String> words, int limit) {

		TreeSet<Tuple<String>> orderedResults = Tuple.createOrderedResultsTree(new String());

		for (String word : words) {
			Tuple<String> t = new Tuple<String>();
			t.word = word;
			t.count = getOccuranceCount(unigram, word);
			if (t.count > 0) {
				orderedResults.add(t);
			}
		}

		List<String> retval = new ArrayList<String>();

		int count = 0;
		Iterator<Tuple<String>> iter = orderedResults.iterator();
		while (iter.hasNext()) {
			Tuple<String> tuple = iter.next();
			count++;
			retval.add(tuple.word);

			if (count == limit) {
				break;
			}
		}

		return retval;
	}

	// search results should be by words submitted, aggregating each words resulting ids and ordering those resulting ids
	public static Set<Long> getSearchResults(UnigramLongSearchHistogram unigram, Set<String> words, int limit) {
		long t1 = System.currentTimeMillis();

		TLongIntHashMap results = new TLongIntHashMap();

		for (String word : words) {
			logger.debug("Looking for word: " + word);

			int count = 0;
			
			if (word != null) {
				int wordKey = word.hashCode();
				TLongByteHashMap hashMap = unigram.multiResultMap.get(wordKey);

				if (hashMap == null) { // 0 or 1 result

					if (unigram.singleResultMap.contains(wordKey)) { // 1 result
						long result = unigram.singleResultMap.get(wordKey);
						
						if (results.contains(result)) {
							count = results.get(result);
						}
						count++;
						results.put(result, count);
					}
				} else { // more than one result
					long[] hashMapResults = hashMap.keys();
					byte[] hashMapCounts = hashMap.values();

					for (int i = 0; i < hashMapResults.length; i++) {
						count = 0;
						long result = hashMapResults[i];
						
						if (results.contains(result)) {
							count = results.get(result);
						}
						count += hashMapCounts[i];
						results.put(result, count);
					}
				}
			}
		}

		InMemorySearchEngine.timeToBuildSearchResultsMap += System.currentTimeMillis() - t1;
		t1 = System.currentTimeMillis();

		FixedSizeSortedSet<Tuple<Long>> orderedResults = new FixedSizeSortedSet<Tuple<Long>>((new Tuple<Long>()).new TupleComparator<Long>(), limit);

		for (Long result : results.keys()) {
			Tuple<Long> t = new Tuple<Long>();
			t.word = result;
			t.count = results.get(result);

			logger.debug("result: " + t.word + "; count: " + t.count);
			orderedResults.add(t);
		}

		InMemorySearchEngine.timeToBuildSearchResultsOrdered += System.currentTimeMillis() - t1;
		t1 = System.currentTimeMillis();

		Set<Long> retval = new LinkedHashSet<Long>();
		for (Tuple<Long> t : orderedResults.getResultSet(limit)) {
			retval.add(t.word);
		}
		return retval;
	}
}