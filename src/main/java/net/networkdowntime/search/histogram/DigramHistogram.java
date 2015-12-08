package net.networkdowntime.search.histogram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class DigramHistogram {

	Map<Integer, UnigramHistogram> histogram = new HashMap<Integer, UnigramHistogram>();

	public static void add(DigramHistogram digram, String firstWord, String secondWord) {
		firstWord = firstWord.toLowerCase();
		secondWord = secondWord.toLowerCase();

		UnigramHistogram unigram = digram.histogram.get(firstWord.hashCode());
		if (unigram == null) {
			unigram = new UnigramHistogram();
			digram.histogram.put(firstWord.hashCode(), unigram);
		}

		UnigramHistogram.add(unigram, secondWord);

	}

	public static void remove(DigramHistogram digram, String firstWord, String secondWord) {
		firstWord = firstWord.toLowerCase();
		secondWord = secondWord.toLowerCase();

		UnigramHistogram unigram = digram.histogram.get(firstWord.hashCode());
		if (unigram != null) {
			int count = UnigramHistogram.remove(unigram, secondWord);
			if (count == 0)
				digram.histogram.remove(firstWord.hashCode());
		}
	}

	public static int getOccuranceCount(DigramHistogram digram, String firstWord, String secondWord) {
		firstWord = firstWord.toLowerCase();
		secondWord = secondWord.toLowerCase();

		int count = 0;

		UnigramHistogram unigram = digram.histogram.get(firstWord.hashCode());
		if (unigram != null) {
			count = UnigramHistogram.getOccuranceCount(unigram, secondWord);
		}

		return count;
	}

	private static void getResults(DigramHistogram digram, String firstWord, Set<String> secondWords, TreeSet<Tuple<String>> orderedResults) {
		for (String secondWord : secondWords) {
			Tuple<String> t = new Tuple<String>();
			t.count = getOccuranceCount(digram, firstWord, secondWord);
			if (t.count > 0) {

				t.word = firstWord + " " + secondWord;
				orderedResults.add(t);

			}

			t = new Tuple<String>();
			t.count = getOccuranceCount(digram, secondWord, firstWord);
			if (t.count > 0) {

				t.word = secondWord + " " + firstWord;
				orderedResults.add(t);

			}

		}

		if (secondWords.isEmpty()) {
			System.out.println("secondWords is empty");
			UnigramHistogram unigram = digram.histogram.get(firstWord.hashCode());
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

	public static List<String> getOrderedResults(DigramHistogram digram, String firstWord, Set<String> secondWords, int limit) {

		TreeSet<Tuple<String>> orderedResults = Tuple.createOrderedResultsTree(new String());

		DigramHistogram.getResults(digram, firstWord, secondWords, orderedResults);
		List<String> retval = orderResults(orderedResults, limit);

		return retval;
	}

	public static List<String> getOrderedResults(DigramHistogram digram, Set<String> firstWords, Set<String> secondWords, int limit) {

		TreeSet<Tuple<String>> orderedResults = Tuple.createOrderedResultsTree(new String());

		for (String firstWord : firstWords) {
			DigramHistogram.getResults(digram, firstWord, secondWords, orderedResults);
		}

		List<String> retval = orderResults(orderedResults, limit);

		return retval;
	}

}
