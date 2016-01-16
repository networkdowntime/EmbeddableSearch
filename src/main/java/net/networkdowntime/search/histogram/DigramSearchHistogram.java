package net.networkdowntime.search.histogram;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TLongIntHashMap;

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
	private static final Logger LOGGER = LogManager.getLogger(DigramSearchHistogram.class.getName());

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

	/**
	 * For a given set of search terms, returns the results in order of most common occurrence.
	 * A swapped order of first and second words are also taken into consideration.
	 * 
	 * @param searchTerms Set of potentially multiple word strings
	 * @param weightMultiplier Multiplier of how much additional weight to apply to these results
	 * @return A TLongIntHashMap containing all of the results and their weights
	 */
	protected TLongIntHashMap getResultsRaw(Set<String> searchTerms, int weightMultiplier) {
		TLongIntHashMap results = new TLongIntHashMap();

		for (String term : searchTerms) {
			String[] keywords;
			if (term.contains(" ")) {
				keywords = term.split(" ");
			} else {
				keywords = new String[] { term };
			}

			String previousWord = null;

			for (String currentWord : keywords) {
				if (previousWord != null) {
					LOGGER.debug("Looking for " + previousWord + " " + currentWord);
					UnigramSearchHistogram.getSearchResults(histogram.get(previousWord.hashCode()), results, currentWord, weightMultiplier);

					LOGGER.debug("Looking for " + currentWord + " " + previousWord);
					UnigramSearchHistogram.getSearchResults(histogram.get(currentWord.hashCode()), results, previousWord, weightMultiplier);
				}

				previousWord = currentWord;
			}
		}
		return results;
	}

}
