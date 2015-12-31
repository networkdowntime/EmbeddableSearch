package net.networkdowntime.search.histogram;

import gnu.trove.map.hash.TIntLongHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TLongByteHashMap;
import gnu.trove.map.hash.TLongIntHashMap;
import net.networkdowntime.search.SearchResult;
import net.networkdowntime.search.SearchResultComparator;
import net.networkdowntime.search.SearchResultType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The search histogram tracks the association between a key word, it's search results,
 * and the occurrence count/weight of the result.
 * 
 * This implementation stores the hashcode of the word and a count based on how
 * many times it has been added/removed. Methods are static to reduce the memory
 * footprint of the class.
 * 
 * So there are two different key data structures in the Search Histogram. 
 * 	1. Single Result Map - This is an optimization to reduce overhead for sparsely used
 * 		words that are indexed. Basically, if a word only matches one result then it
 * 		only incurs the penalty of an extra int and long value being stored. Words
 * 		that show up in this data structure have an implied count of 1.
 * 	2. Multi-Result Map - Words that have multiple search results get stored here.
 * 		This applies to counts of 2 for the same word or matches more than one search
 * 		result.  The key is the word's hashcode and the value is a histogram hashmap
 * 		containing the search result and it's histogram count.
 * 
 * This software is licensed under the MIT license
 * Copyright (c) 2015 Ryan Wiles
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation 
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, 
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software 
 * is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE 
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR 
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * @author rwiles
 *
 */
abstract class UnigramSearchHistogram {
	static final Logger logger = LogManager.getLogger(UnigramSearchHistogram.class.getName());

	protected TIntObjectHashMap<TLongByteHashMap> multiResultMap = new TIntObjectHashMap<TLongByteHashMap>();
	protected TIntLongHashMap singleResultMap = new TIntLongHashMap();

	/**
	 * Get the total search weight from the multi-result hashmap.
	 * 
	 * @param word Key Hash key of the word
	 * @return The total weight of the word in the multi-result map
	 */
	protected int getMultiResultCount(int wordKey) {
		TLongByteHashMap hashMap = multiResultMap.get(wordKey);
		int count = 0;
	
		if (hashMap != null) {
			for (byte resultCount : hashMap.values()) {
				count += resultCount;
			}
		}
		return count;
	}

	/**
	 * Adds a word along with it's result to the search histogram
	 * @param word Key Hash key of the word 
	 * @param resultKey The search result to associate with the word
	 */
	protected void addInternal(int wordKey, Long resultKey) {
		TLongByteHashMap hashMap = multiResultMap.get(wordKey);

		if (hashMap == null) { // not more than 1 result already

			if (!singleResultMap.contains((wordKey))) { // no matches, put result into the single result map
				singleResultMap.put((wordKey), resultKey);
			} else { // one result already
				hashMap = new TLongByteHashMap();
				multiResultMap.put(wordKey, hashMap);

				// move match from the single result map to the multi result map
				long originalResult = singleResultMap.remove(wordKey);

				if (originalResult == resultKey) { // we now have a count of two for the original result
					hashMap.put(originalResult, (byte) 2);
				} else {
					hashMap.put(originalResult, (byte) 1);
					hashMap.put(resultKey, (byte) 1);
				}
			}

		} else { // more than 1 result already
			hashMap = multiResultMap.get(wordKey);
			if (hashMap.contains(resultKey)) {
				hashMap.put(resultKey, (byte) (hashMap.get(resultKey) + 1));
			} else {
				hashMap.put(resultKey, (byte) 1);
			}
		}
	}

	/**
	 * Removes a word/result from the search histogram.  If the word is associated with multiple results, they will be left alone.
	 * 
	 * @param word Key Hash key of the word
	 * @param resultKey The result to be removed.
	 */
	protected void removeInternal(int wordKey, Long resultKey) {
		int count = 0;
	
		TLongByteHashMap hashMap = multiResultMap.get(wordKey);
	
		if (hashMap == null) { // not more than 1 result already
			if (singleResultMap.contains(wordKey)) { // one result
				singleResultMap.remove(wordKey); // now no results
			}
		} else { // more than 1 result already
			if (hashMap.contains(resultKey)) {
				hashMap.remove(resultKey);
			}
	
			count = getMultiResultCount(wordKey);
	
			if (count == 1) {
				singleResultMap.put((wordKey), resultKey);
				count = 1;
			}
		}
	
		count = getOccuranceCount(wordKey);
	
		if (count == 1) {
			singleResultMap.put(wordKey, resultKey); // now one result
			multiResultMap.remove(wordKey);
		}
	}

	/**
	 * Checks whether the search histogram contains the word.
	 * 
	 * @param word Word to look for
	 * @return true/false based on whether the word was found
	 */
	protected boolean contains(String word) {
		word = word.toLowerCase();
		int wordKey = word.hashCode();
		return (singleResultMap.contains((wordKey))) || (multiResultMap.get(wordKey) != null);
	}

	/**
	 * Gets the total occurrence count of the word in the search histogram
	 * 
	 * @param word Key Hash key of the word 
	 * @return The total occurrence count of the word or 0 if not found
	 */
	protected int getOccuranceCount(int wordKey) {

		int count = getMultiResultCount(wordKey);

		if (count == 0 && singleResultMap.contains((wordKey))) { // not in the multi-result map and one result
			count = 1;
		}

		return count;
	}

	/**
	 * Gets the ordered set of search words based on their weight
	 * 
	 * @param words The set of words to get the search results for
	 * @param limit Max number of results to return
	 * @return
	 */
	protected List<String> getOrderedResults(Set<String> words, int limit) {

		TreeSet<Tuple<String>> orderedResults = Tuple.createOrderedResultsTree(new String());

		for (String word : words) {
			Tuple<String> t = new Tuple<String>();
			t.word = word;
			t.count = getOccuranceCount(word.hashCode());
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

	/**
	 * Get the search results by the words submitted, aggregating each word's resulting ids and ordering those resulting id's by result weight.
	 * 
	 * @param words The set of words to get the search results for.
	 * @param limit Max number of results to return
	 *  
	 * @return A set containing the matched search results up to the specified limit
	 */
	@SuppressWarnings("rawtypes")
	public FixedSizeSortedSet<SearchResult> getSearchResults(Set<String> words, int limit) {

		TLongIntHashMap results = new TLongIntHashMap();

		for (String word : words) {
			logger.debug("Looking for word: " + word);

			int count = 0;

			if (word != null) {
				int wordKey = word.hashCode();
				TLongByteHashMap hashMap = multiResultMap.get(wordKey);

				if (hashMap == null) { // 0 or 1 result

					if (singleResultMap.contains(wordKey)) { // 1 result
						long result = singleResultMap.get(wordKey);

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

		FixedSizeSortedSet<SearchResult> orderedResults = new FixedSizeSortedSet<SearchResult>(new SearchResultComparator(), limit);

		int count = 0;
		for (Long result : results.keys()) {
			count = results.get(result);
			orderedResults.add(new SearchResult<Long>(SearchResultType.Long, result, count));

//			logger.debug("result: " + result + "; count: " + count);
		}

		return orderedResults;
	}
}