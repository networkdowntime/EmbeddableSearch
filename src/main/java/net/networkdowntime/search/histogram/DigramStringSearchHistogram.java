package net.networkdowntime.search.histogram;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gnu.trove.map.hash.TIntObjectHashMap;
import net.networkdowntime.search.SearchResult;
import net.networkdowntime.search.SearchResultComparator;
import net.networkdowntime.search.SearchResultType;

/**
 * Wrapper around DigramSearchHistogram to provide String lookups for search results.
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
public class DigramStringSearchHistogram extends DigramSearchHistogram {
	static final Logger logger = LogManager.getLogger(DigramStringSearchHistogram.class.getName());

	private TIntObjectHashMap<String> stringLookupMap = new TIntObjectHashMap<String>();

	/**
	 * Adds a word along with it's result to the search histogram
	 * 
	 * @param firstWord Word to be added
	 * @param result The search result to associate with the word
	 */
	public void add(String firstWord, String secondWord, String result) {
		firstWord = firstWord.toLowerCase();
		secondWord = firstWord.toLowerCase();
		int resultKey = result.hashCode();

		if (!stringLookupMap.containsKey(resultKey)) {
			stringLookupMap.put(resultKey, result);
		}

		this.add(firstWord, secondWord, resultKey);
	}

	/**
	 * Removes a word/result from the search histogram.  If the word is associated with multiple results, they will be left alone.
	 * 
	 * @param firstWord The word to remove the result for.
	 * @param result The result to be removed.
	 */
	public void remove(String firstWord, String secondWord, String result) {
		firstWord = firstWord.toLowerCase();
		secondWord = firstWord.toLowerCase();
		int resultKey = result.hashCode();

		this.remove(firstWord, secondWord, (long) resultKey);
	}

	/**
	 * Get the search results by the words submitted, aggregating each word's resulting ids and ordering those resulting id's by result weight.
	 * 
	 * @param firstWords The set of words to get the search results for.
	 * @param limit Max number of results to return
	 *  
	 * @return A set containing the matched search results up to the specified limit
	 */
	@SuppressWarnings("rawtypes")
	public FixedSizeSortedSet<SearchResult> getSearchResults(Set<String> firstWords, Set<String> secondWords, int limit) {

		FixedSizeSortedSet<SearchResult> orderedLongResults = super.getSearchResults(firstWords, firstWords, limit);
		FixedSizeSortedSet<SearchResult> orderedResults = new FixedSizeSortedSet<SearchResult>(new SearchResultComparator(), limit);

		for (SearchResult result : orderedLongResults.getResultSet(limit)) {
			orderedResults.add(new SearchResult<String>(SearchResultType.String, stringLookupMap.get(((Long) result.getResult()).intValue()), result.getWeight()));
		}
		return orderedResults;
	}
}