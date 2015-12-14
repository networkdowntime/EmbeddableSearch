package net.networkdowntime.search.histogram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Implements a digram histogram.  For any two word pairs it tracks the frequency that those words were added.
 * 
 * @author rwiles
 *
 */
public class DigramHistogram {

	Map<Integer, UnigramHistogram> histogram = new HashMap<Integer, UnigramHistogram>();

	/**
	 * Adds a word pair to the digram histogram
	 * 
	 * @param digram
	 * @param firstWord
	 * @param secondWord
	 */
	public void add(String firstWord, String secondWord) {
		firstWord = firstWord.toLowerCase();
		secondWord = secondWord.toLowerCase();

		UnigramHistogram unigram = histogram.get(firstWord.hashCode());
		if (unigram == null) {
			unigram = new UnigramHistogram();
			histogram.put(firstWord.hashCode(), unigram);
		}

		UnigramHistogram.add(unigram, secondWord);

	}

	/**
	 * Removes a word pair from the digram histogram.  If the first word no longer has a matching second word, then the first word is also removed.
	 * 
	 * @param firstWord The first word to remove
	 * @param secondWord The second word to remove
	 */
	public void remove(String firstWord, String secondWord) {
		firstWord = firstWord.toLowerCase();
		secondWord = secondWord.toLowerCase();

		UnigramHistogram unigram = histogram.get(firstWord.hashCode());
		if (unigram != null) {
			int count = UnigramHistogram.remove(unigram, secondWord);
			if (count == 0)
				histogram.remove(firstWord.hashCode());
		}
	}

	/**
	 * Returns the occurrence count for the word pair.
	 * 
	 * @param firstWord The first word
	 * @param secondWord The second word
	 * @return The occurrence count of the word pair
	 */
	public int getOccuranceCount(String firstWord, String secondWord) {
		firstWord = firstWord.toLowerCase();
		secondWord = secondWord.toLowerCase();

		int count = 0;

		UnigramHistogram unigram = histogram.get(firstWord.hashCode());
		if (unigram != null) {
			count = UnigramHistogram.getOccurrenceCount(unigram, secondWord);
		}

		return count;
	}

	/**
	 * For a given first word and a set of second words, returns the results in order of most common occurrence. 
	 * A swapped order of first and second words are also taken into consideration.
	 * 
	 * @param firstWord The first word for consideration
	 * @param secondWords A set of second words for consideration
	 * @param limit Max number of results to return
	 * @return
	 */
	public List<String> getOrderedResults(String firstWord, Set<String> secondWords, int limit) {

		TreeSet<Tuple<String>> orderedResults = getResults(firstWord, secondWords);
		List<String> retval = toList(orderedResults, limit);

		return retval;
	}

	/**
	 * For a given set of first words and a set of second words, returns the results in order of most common occurrence. 
	 * A swapped order of first words and second words are also taken into consideration.
	 * 
	 * @param firstWord A set of first words for consideration
	 * @param secondWords A set of second words for consideration
	 * @param limit Max number of results to return
	 * @return
	 */
	public List<String> getOrderedResults(Set<String> firstWords, Set<String> secondWords, int limit) {

		TreeSet<Tuple<String>> orderedResults = Tuple.createOrderedResultsTree(new String());

		for (String firstWord : firstWords) {
			orderedResults = getResults(firstWord, secondWords);
		}

		List<String> retval = toList(orderedResults, limit);

		return retval;
	}

	/**
	 * An internal method that for a given first word and a set of second words, returns the results in order of most common occurrence.
	 * A swapped order of first and second words are also taken into consideration.
	 * 
	 * @param firstWord The first word for consideration
	 * @param secondWords A set of second words for consideration
	 * @param orderedResults Not-null TreeSet of Tuples
	 */
	private TreeSet<Tuple<String>> getResults(String firstWord, Set<String> secondWords) {
		TreeSet<Tuple<String>> orderedResults = Tuple.createOrderedResultsTree(new String());

		for (String secondWord : secondWords) {
			Tuple<String> t = new Tuple<String>();
			t.count = getOccuranceCount(firstWord, secondWord);
			if (t.count > 0) {

				t.word = firstWord + " " + secondWord;
				orderedResults.add(t);

			}

			t = new Tuple<String>();
			t.count = getOccuranceCount(secondWord, firstWord);
			if (t.count > 0) {

				t.word = secondWord + " " + firstWord;
				orderedResults.add(t);

			}

		}

		if (secondWords.isEmpty()) {
			UnigramHistogram unigram = histogram.get(firstWord.hashCode());
			if (unigram != null) {
				for (Tuple<String> t : UnigramHistogram.getMostCommonWords(unigram)) {
					orderedResults.add(t);
				}
			}
		}

		return orderedResults;
	}

	/**
	 * An internal method that Takes a TreeSet and returns it as a list of strings
	 * 
	 * @param orderedResults The ordered tree set to be converted to a list
	 * @param limit
	 * @return
	 */
	private static List<String> toList(TreeSet<Tuple<String>> orderedResults, int limit) {
		List<String> retval = new ArrayList<String>();

		int count = 0;

		for (Tuple<String> tuple : orderedResults) {
			if (tuple != null) {
				count++;
				retval.add(tuple.word);

				if (count == limit) {
					break;
				}
			}
		}

		return retval;
	}

}
