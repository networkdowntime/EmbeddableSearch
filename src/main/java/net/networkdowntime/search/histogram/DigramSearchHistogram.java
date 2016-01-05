package net.networkdowntime.search.histogram;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TLongIntHashMap;
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
		UnigramSearchHistogram unigram = histogram.get(firstWord.hashCode());
		if (unigram != null) {

			int count = UnigramSearchHistogram.removeInternal(unigram, secondWord.hashCode(), resultKey);

			if (count == 0) {
				histogram.remove(firstWord.hashCode());
			}
		}
	}

	//	@SuppressWarnings("rawtypes")
	//	protected FixedSizeSortedSet<SearchResult> getSearchResults(Set<String> firstWords, Set<String> secondWords, int limit) {
	//
	//		FixedSizeSortedSet<SearchResult> orderedResults = new FixedSizeSortedSet<SearchResult>(new SearchResultComparator(), limit);
	//
	//		for (String firstWord : firstWords) {
	////			orderedResults.addAll(getResults(firstWord, secondWords, limit));
	//		}
	//
	//		// swap the word order
	//		for (String secondWord : secondWords) {
	////			orderedResults.addAll(getResults(secondWord, firstWords, limit));
	//		}
	//
	//		return orderedResults;
	//	}

	/**
	 * For a given set of search terms, returns the results in order of most common occurrence.
	 * A swapped order of first and second words are also taken into consideration.
	 * 
	 * @param searchTerms Set of potentially multiple word strings
	 * @param weightMultiplier Multiplier of how much additional weight to apply to these results
	 * @return A TLongIntHashMap containing all of the results and their weights
	 */
	@SuppressWarnings("rawtypes")
	protected TLongIntHashMap getResultsRaw(Set<String> searchTerms, int weightMultiplier) {
		TLongIntHashMap results = new TLongIntHashMap();

		for (String term : searchTerms) {
			String[] keywords;
			if (term.contains(" ")) {
				keywords = term.split(" ");
			} else {
				keywords = new String[] { term };
			}

			String currentWord = null;
			String previousWord = null;

			for (int i = 0; i < keywords.length; i++) {
				previousWord = (currentWord != null) ? currentWord : null;
				currentWord = keywords[i];

				if (previousWord != null) {
					logger.debug("Looking for " + previousWord + " " + currentWord);
					UnigramSearchHistogram unigram = histogram.get(previousWord.hashCode());

					if (unigram != null) {
						UnigramSearchHistogram.getSearchResults(unigram, results, currentWord, weightMultiplier);
					}

					logger.debug("Looking for " + currentWord + " " + previousWord);
					unigram = histogram.get(currentWord.hashCode());

					if (unigram != null) {
						UnigramSearchHistogram.getSearchResults(unigram, results, previousWord, weightMultiplier);
					}
				}
			}
		}
		return results;
	}

}
