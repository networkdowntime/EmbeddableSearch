package net.networkdowntime.search.histogram;

import gnu.trove.map.hash.TIntLongHashMap;
import gnu.trove.map.hash.TLongByteHashMap;
import gnu.trove.map.hash.TLongIntHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.networkdowntime.search.Logger;
import net.networkdowntime.search.engine.InMemorySearchEngine;
import net.networkdowntime.search.histogram.Tuple.TupleComparator;

/**
 * Single word/string histogram.
 * 
 * This implementation stores the hashcode of the word and a list of matching results.
 * Methods are static to reduce the memory footprint of the class.
 * 
 * This class can be used for in-memory search
 *  
 * @author rwiles
 *
 */
public class UnigramSearchHistogram {

	@SuppressWarnings("unchecked")
	private Tuple<String>[] mostCommonWords = new Tuple[15];
	private Map<Integer, TLongByteHashMap> histogram = new HashMap<Integer, TLongByteHashMap>();
	private TIntLongHashMap singleResultMap = new TIntLongHashMap();
	
	public static Tuple<String>[] getMostCommonWords(UnigramSearchHistogram unigram) {
		return unigram.mostCommonWords;
	}
	
	public static void add(UnigramSearchHistogram unigram, String word, Long result) {
		word = word.toLowerCase();
		
		TLongByteHashMap hashMap = unigram.histogram.get(word.hashCode());
		byte count = 0;
		
		if (hashMap == null) { // not more than 1 result already
			
			if (unigram.singleResultMap.contains((word.hashCode()))) { // one result already
				hashMap  = new TLongByteHashMap(); // create new hashmap
				hashMap.put(unigram.singleResultMap.get((word.hashCode())), (byte) 1); // move existing result into hashmap
				hashMap.put(result, (byte) 1); // add new result into hashmap
				count = 2;
				unigram.histogram.put(word.hashCode(), hashMap);
			} else { // no results, put into single result map
				unigram.singleResultMap.put((word.hashCode()), result);
				count = 1;
			}
			
		} else { // more than 1 result already
		
			hashMap = unigram.histogram.get(word.hashCode());
			count = hashMap.get(result);
			hashMap.put(result, ++count);
		}
		
		unigram.mostCommonWords = Tuple.updateSortTupleArray(unigram.mostCommonWords, word, count, 15);
	}
	
	public static void remove(UnigramSearchHistogram unigram, String word, Long result) {
		word = word.toLowerCase();

		int count = 0;
		
		TLongByteHashMap hashMap = unigram.histogram.get(word.hashCode());

		if (hashMap == null) { // not more than 1 result already
			if (unigram.singleResultMap.contains(word.hashCode())) { // one result
				unigram.singleResultMap.remove(word.hashCode()); // now no results
			}
		} else { // more than 1 result already
			count = hashMap.get(result);
			
			if (count == 1) {
				hashMap.remove(result);
			} else {
				hashMap.put(result, (byte) --count);
			}
		}

		count = getOccuranceCount(unigram, word);
		
		if (count == 1) {
			unigram.singleResultMap.put(word.hashCode(), result); // now one result
			unigram.histogram.remove(word.hashCode());
		}

		unigram.mostCommonWords = Tuple.updateSortTupleArray(unigram.mostCommonWords, word, count, 15);
	}
	
	public static boolean contains(UnigramSearchHistogram unigram, String word) {
		word = word.toLowerCase();
		return (unigram.singleResultMap.contains((word.hashCode()))) || (unigram.histogram.get(word.hashCode()) != null);
	}
	
	public static int getOccuranceCount(UnigramSearchHistogram unigram, String word) {
		word = word.toLowerCase();
		int count = 0;
		
		TLongByteHashMap hashMap = unigram.histogram.get(word.hashCode());

		if (hashMap == null) {
			if (unigram.singleResultMap.contains((word.hashCode()))) { // one result already
				count = 1;
			}
		} else {
			for (byte b : hashMap.values())
				count += b;
		}
		
		return count;
	}
	
	public static List<String> getOrderedResults(UnigramSearchHistogram unigram, Set<String> words, int limit) {
		
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
	
	public static Set<Long> getSearchResults(UnigramSearchHistogram unigram, Set<String> words, int limit, Logger logger) {
		long t1 = System.currentTimeMillis();
		
		TLongIntHashMap results = new TLongIntHashMap();
		
		for (String word : words) {
			logger.log(1, "Looking for word: " + word);

			if (word == null) 
				System.out.println("word is null");
	
			TLongByteHashMap hashMap = unigram.histogram.get(word.hashCode());
			if (hashMap == null) { // not more than 1 result
				long result = unigram.singleResultMap.get(word.hashCode());
				
				if (result != 0) { // 1 result
					int count = results.get(result);
					if (count != 0) {
						results.put(result, count + 1);
					} else {
						results.put(result, 1);
					}
				}
			} else {
				long[] hashMapResults = hashMap.keys();
				byte[] hashMapCounts = hashMap.values();
				
				for (int i=0; i < hashMapResults.length; i++) {
					long hashMapResult = hashMapResults[i];
					results.put(hashMapResult, results.get(hashMapResult) + hashMapCounts[i]);
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
			
//			logger.log(1, "result: " + t.word + "; count: " + t.count);
			orderedResults.add(t);
		}
		
		InMemorySearchEngine.timeToBuildSearchResultsOrdered += System.currentTimeMillis() - t1;
		t1 = System.currentTimeMillis();

		
		Set<Long> retval = new HashSet<Long>();
		int count = 0;
		Iterator<Tuple<Long>> iter = orderedResults.iterator();
		while (iter.hasNext()) {
			Tuple<Long> tuple = iter.next();
			count++;
			retval.add(tuple.word);
			
			if (count == limit) {
				break;
			}
		}
		
		InMemorySearchEngine.timeToBuildSearchResultsReturn += System.currentTimeMillis() - t1;

		return retval;
	}
}
