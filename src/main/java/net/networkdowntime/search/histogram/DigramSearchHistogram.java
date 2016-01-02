package net.networkdowntime.search.histogram;


import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gnu.trove.map.hash.TIntObjectHashMap;
import net.networkdowntime.search.SearchResult;
import net.networkdowntime.search.SearchResultComparator;

/**
 * Implements a digram search histogram.  For any two word pairs it tracks the results that those words appear together.
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
class DigramSearchHistogram {
	static final Logger logger = LogManager.getLogger(DigramSearchHistogram.class.getName());

	TIntObjectHashMap<UnigramSearchHistogram> histogram = new TIntObjectHashMap<UnigramSearchHistogram>();

	/**
	 * Adds a word pair search result to the digram histogram
	 * 
	 * @param firstWord
	 * @param secondWord
	 */
	protected void add(String firstWord, String secondWord, long resultKey) {
		firstWord = firstWord.toLowerCase();
		secondWord = secondWord.toLowerCase();

		UnigramSearchHistogram unigram = histogram.get(firstWord.hashCode());

		if (unigram == null) {
			unigram = new UnigramSearchHistogram();
			histogram.put(firstWord.hashCode(), unigram);
		}

		UnigramSearchHistogram.addInternal(unigram, secondWord.hashCode(), resultKey);

	}

	/**
	 * Removes a word pair search result from the digram histogram.  If the first word no longer has a matching second word, then the first word is also removed.
	 * 
	 * @param firstWord The first word to remove
	 * @param secondWord The second word to remove
	 */
	protected void remove(String firstWord, String secondWord, long resultKey) {
		firstWord = firstWord.toLowerCase();
		secondWord = secondWord.toLowerCase();

		UnigramSearchHistogram unigram = histogram.get(firstWord.hashCode());
		if (unigram != null) {

			int count = UnigramSearchHistogram.removeInternal(unigram, secondWord.hashCode(), resultKey);

			if (count == 0) {
				histogram.remove(firstWord.hashCode());
			}
		}
	}

	@SuppressWarnings("rawtypes")
	protected FixedSizeSortedSet<SearchResult> getSearchResults(Set<String> firstWords, Set<String> secondWords, int limit) {

		FixedSizeSortedSet<SearchResult> orderedResults = new FixedSizeSortedSet<SearchResult>(new SearchResultComparator(), limit);

		for (String firstWord : firstWords) {
			orderedResults.addAll(getResults(firstWord.toLowerCase(), secondWords, limit));
		}

		// swap the word order
		for (String secondWord : secondWords) {
			orderedResults.addAll(getResults(secondWord.toLowerCase(), firstWords, limit));
		}

		return orderedResults;
	}

	/**
	 * An internal method that for a given first word and a set of second words, returns the results in order of most common occurrence.
	 * A swapped order of first and second words are also taken into consideration.
	 * 
	 * @param firstWord The first word for consideration
	 * @param secondWords A set of second words for consideration
	 * @param orderedResults Not-null TreeSet of Tuples
	 */
	@SuppressWarnings("rawtypes")
	private FixedSizeSortedSet<SearchResult> getResults(String firstWord, Set<String> secondWords, int limit) {
		FixedSizeSortedSet<SearchResult> orderedResults = new FixedSizeSortedSet<SearchResult>(new SearchResultComparator(), limit);

		UnigramSearchHistogram unigram = histogram.get(firstWord.hashCode());

		if (unigram != null) {
			orderedResults.addAll(UnigramSearchHistogram.getSearchResults(unigram, secondWords, 3, limit));
		}

		return orderedResults;
	}

}
