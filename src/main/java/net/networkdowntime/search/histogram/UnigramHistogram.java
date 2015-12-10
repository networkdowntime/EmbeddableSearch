package net.networkdowntime.search.histogram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Single word/string histogram.
 * 
 * This implementation stores the hashcode of the word and a count based on how many times it has been added/removed.
 * Methods are static to reduce the memory footprint of the class.  The rational behind this is to keep the unigram histogram
 * suitable for embedding within say a 2-gram histogram. 
 *  
 * @author rwiles
 *
 */
public class UnigramHistogram {

	@SuppressWarnings("unchecked")
	private Tuple<String>[] mostCommonWords = new Tuple[15];

	private Map<Integer, Integer> histogram = new HashMap<Integer, Integer>();
	
	
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
		if (count == null) {
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

		Integer count = unigram.histogram.get(word.hashCode());
		
		if (count != null) {
			if (count <= 1) {
				unigram.histogram.remove(word.hashCode());
			} else {
				count = count - 1;
				unigram.histogram.put(word.hashCode(), count);
				unigram.mostCommonWords = Tuple.updateSortTupleArray(unigram.mostCommonWords, word, count, 15);
			}
		}
		return unigram.histogram.size();
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

		Integer count = unigram.histogram.get(word.hashCode());

		if (count == null) {
			count = 0;
		}
		
		return count;
	}
	
	
	/**
	 * For a given set of words adds returns an order list based on each words occurrence count.
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
