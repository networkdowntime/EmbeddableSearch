package net.networkdowntime.search.histogram;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import gnu.trove.map.hash.TIntIntHashMap;

/**
 * Single word/string histogram.
 * 
 * This implementation stores the hashcode of the word and a count based on how many times it has been added/removed.
 * Methods are static to reduce the memory footprint of the class.  The rational behind this is to keep the unigram histogram
 * suitable for embedding within say a 2-gram histogram. 
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
public class UnigramHistogram {

	@SuppressWarnings("unchecked")
	private Tuple<String>[] mostCommonWords = new Tuple[15];

	private TIntIntHashMap histogram = new TIntIntHashMap();

	/**
	 * Returns the most common words stored in the histogram.
	 * 
	 * @param unigram Instance of the histogram
	 * @return A Tuple array containing the most common words
	 */
	public static Tuple<String>[] getMostCommonWords(UnigramHistogram unigram) {
		return unigram.mostCommonWords;
	}

	/**
	 * Adds an occurrence of the word to the histogram
	 * 
	 * @param unigram Instance of the histogram
	 * @param word The word to be added
	 */
	public static void add(UnigramHistogram unigram, String word) {
		word = word.toLowerCase();

		Integer count = unigram.histogram.get(word.hashCode());
		if (unigram.histogram.getNoEntryValue() == count) {
			count = 1;
		} else {
			count = count + 1;
		}
		unigram.histogram.put(word.hashCode(), count);

		unigram.mostCommonWords = Tuple.updateSortTupleArray(unigram.mostCommonWords, word, count, 15);
	}

	/**
	 * Removes an occurrence of the word from the histogram decrementing it's count.  If the count is 0, the word is deleted.
	 * 
	 * @param unigram Instance of the histogram
	 * @param word The word to be removed
	 * @return The number of elements left in the histogram
	 */
	public static int remove(UnigramHistogram unigram, String word) {
		word = word.toLowerCase();
		int wordKey = word.hashCode();

		Integer count = unigram.histogram.get(wordKey);

		if (count != null) {
			if (count <= 1) {
				unigram.histogram.remove(wordKey);
			} else {
				count = count - 1;
				unigram.histogram.put(wordKey, count);
				unigram.mostCommonWords = Tuple.updateSortTupleArray(unigram.mostCommonWords, word, count, 15);
			}
		}
		return unigram.histogram.size();
	}

	/**
	 * Checks whether the search histogram contains the word.
	 * 
	 * @param word Word to look for
	 * @return true/false based on whether the word was found
	 */
	public static boolean contains(UnigramHistogram unigram, String word) {
		word = word.toLowerCase();
		int wordKey = word.hashCode();
		return (unigram.histogram.containsKey(wordKey));
	}

	/**
	 * Gets the histogram occurrence count of the word.
	 * 
	 * @param unigram Instance of the histogram
	 * @param word The word to get the occurrence count for
	 * @return The number of occurrences of the word or 0 if it is not in the histogram
	 */
	public static int getOccurrenceCount(UnigramHistogram unigram, String word) {
		word = word.toLowerCase();
		int wordKey = word.hashCode();

		Integer count = unigram.histogram.get(wordKey);

		if (unigram.histogram.getNoEntryValue() == count) {
			count = 0;
		}

		return count;
	}

	/**
	 * For a given set of words returns an ordered list based on each words occurrence count.
	 * 
	 * @param unigram Instance of the histogram
	 * @param words A List of words
	 * @param limit Max number of results to return 
	 * @return
	 */
	public static List<String> getOrderedResults(UnigramHistogram unigram, List<String> words, int limit) {

		TreeSet<Tuple<String>> orderedResults = Tuple.createOrderedResultsTree(new String());

		for (String word : words) {
			Tuple<String> t = new Tuple<String>();
			t.word = word;
			t.count = getOccurrenceCount(unigram, word);
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

}
