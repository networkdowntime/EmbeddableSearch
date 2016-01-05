package net.networkdowntime.search.histogram;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TLongIntHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.networkdowntime.search.SearchResult;
import net.networkdowntime.search.SearchResultComparator;
import net.networkdowntime.search.SearchResultType;

/**
 * Wrapper around UnigramSearchHistogram to provide String lookups for search results.
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
public class UnigramStringSearchHistogram extends UnigramSearchHistogram {
	static final Logger logger = LogManager.getLogger(UnigramStringSearchHistogram.class.getName());

	private TIntObjectHashMap<String> stringLookupMap = new TIntObjectHashMap<String>();

	/**
	 * Adds a word along with it's result to the search histogram
	 * 
	 * @param word Word to be added
	 * @param result The search result to associate with the word
	 */
	public void add(String word, String result) {
		int wordKey = word.hashCode();
		int resultKey = result.hashCode();

		if (!stringLookupMap.containsKey(resultKey)) {
			stringLookupMap.put(resultKey, result);
		}

		addInternal(this, wordKey, (long) resultKey);
	}

	/**
	 * Removes a word/result from the search histogram.  If the word is associated with multiple results, they will be left alone.
	 * 
	 * @param word The word to remove the result for.
	 * @param result The result to be removed.
	 */
	public void remove(String word, String result) {
		int wordKey = word.hashCode();
		int resultKey = result.hashCode();

		if (!multiResultMap.containsKey(wordKey)) {
			stringLookupMap.remove(resultKey);
		}

		removeInternal(this, wordKey, (long) resultKey);
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
	public TObjectIntHashMap<String> getSearchResults(Set<String> words, int limit) {

		TLongIntHashMap longResults = super.getSearchResults(this, words);
		TObjectIntHashMap<String> stringResults = new TObjectIntHashMap<String>();

		long[] longResultKeys = longResults.keys();
		int[] longResultValues = longResults.values();
		
		for (int i = 0; i < longResultKeys.length; i++) {
			stringResults.put(stringLookupMap.get(((Long) longResultKeys[i]).intValue()), longResultValues[i]);
		}
		return stringResults;
	}
}