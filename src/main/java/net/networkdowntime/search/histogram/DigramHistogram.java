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

	public int getOccuranceCount(String firstWord, String secondWord) {
		firstWord = firstWord.toLowerCase();
		secondWord = secondWord.toLowerCase();

		int count = 0;

		UnigramHistogram unigram = histogram.get(firstWord.hashCode());
		if (unigram != null) {
			count = UnigramHistogram.getOccuranceCount(unigram, secondWord);
		}

		return count;
	}

	public List<String> getOrderedResults(String firstWord, Set<String> secondWords, int limit) {

		TreeSet<Tuple<String>> orderedResults = Tuple.createOrderedResultsTree(new String());

		getResults(firstWord, secondWords, orderedResults);
		List<String> retval = orderResults(orderedResults, limit);

		return retval;
	}

	public List<String> getOrderedResults(Set<String> firstWords, Set<String> secondWords, int limit) {

		TreeSet<Tuple<String>> orderedResults = Tuple.createOrderedResultsTree(new String());

		for (String firstWord : firstWords) {
			getResults(firstWord, secondWords, orderedResults);
		}

		List<String> retval = orderResults(orderedResults, limit);

		return retval;
	}

	private void getResults(String firstWord, Set<String> secondWords, TreeSet<Tuple<String>> orderedResults) {
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
	
	}

	private static List<String> orderResults(TreeSet<Tuple<String>> orderedResults, int limit) {
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
