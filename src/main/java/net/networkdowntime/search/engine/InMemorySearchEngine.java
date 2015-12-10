package net.networkdowntime.search.engine;

import gnu.trove.set.hash.TLinkedHashSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.networkdowntime.search.histogram.DigramHistogram;
import net.networkdowntime.search.histogram.UnigramLongSearchHistogram;
import net.networkdowntime.search.textProcessing.ContentSplitter;
import net.networkdowntime.search.textProcessing.KeywordScrubber;
import net.networkdowntime.search.textProcessing.HtmlTagTextScrubber;
import net.networkdowntime.search.textProcessing.TextScrubber;
import net.networkdowntime.search.trie.PrefixTrieNode;
import net.networkdowntime.search.trie.SuffixTrieNode;
import net.networkdowntime.search.trie.Trei;

public class InMemorySearchEngine implements SearchEngine {
	static final Logger logger = LogManager.getLogger(InMemorySearchEngine.class.getName());

	Trei prefixTrie = new PrefixTrieNode();
	SuffixTrieNode suffixTrie = new SuffixTrieNode(false);
	UnigramLongSearchHistogram unigramSearchHistogram = new UnigramLongSearchHistogram();
	DigramHistogram digramHistogram = new DigramHistogram();
	TextScrubber textScrubber = new HtmlTagTextScrubber();
	ContentSplitter splitter = new ContentSplitter();
	KeywordScrubber keywordScrubber = new KeywordScrubber();

	// The following variables are used for tracking the times of various parts of the search operations
	long timeForScrubbing = 0;
	long timeForCompletions = 0;
	long timeForUniqCompletions = 0;
	long timeForSearchResults = 0;
	public static long timeToBuildSearchResultsMap = 0;
	public static long timeToBuildSearchResultsOrdered = 0;
	// end timing variables
	
	/**
	 * Resets the timing variables
	 */
	public void resetTimes() {
		timeForScrubbing = 0;
		timeForCompletions = 0;
		timeForUniqCompletions = 0;
		timeForSearchResults = 0;
		timeToBuildSearchResultsMap = 0;
		timeToBuildSearchResultsOrdered = 0;
	}
	
	/**
	 * Print the timing variables out to logger.info()
	 */
	public void printTimes() {
		logger.info("\ttimeForScrubbing: " + (timeForScrubbing / 1000d) + " secs");
		logger.info("\ttimeForCompletions: " + (timeForCompletions / 1000d) + " secs");
		logger.info("\ttimeForUniqCompletions: " + (timeForUniqCompletions / 1000d) + " secs");
		logger.info("\ttimeForSearchResults: " + (timeForSearchResults / 1000d) + " secs");
		logger.info("\t\tgetSearchResults(): Time to build results hashmap: " + (timeToBuildSearchResultsMap / 1000d) + " secs");
		logger.info("\t\tgetSearchResults(): Time to build results orderedResults: " + (timeToBuildSearchResultsOrdered / 1000d) + " secs");

		logger.info("\ttotal time: " + ((timeForScrubbing + timeForCompletions + timeForUniqCompletions + timeForSearchResults) / 1000d) + "secs");
	}
	

	/**
	 * Print out the search result times and reset them.
	 */
	public void printTimesAndReset() {
		printTimes();
		resetTimes();
	}
	

	/**
	 * Internal get completions implementation.
	 * 
	 * @param singleWord Not-null single word to search for
	 * @param resultLimit Max number of results to return
	 * @return A list of the completions for the word ordered by their search ranking
	 */
	private List<String> getCompletions(String singleWord, int resultLimit) {
		Set<String> completions = new TLinkedHashSet<String>();

		if (singleWord.length() > 0) {
			for (String wordPlusPrefix : prefixTrie.getCompletions(singleWord)) {
				for (String completeWord : suffixTrie.getCompletions(wordPlusPrefix)) {
					completions.add(completeWord);
				}
			}
		}
		logger.debug("Unordered Completions for " + singleWord + ":", completions);

		boolean wordExactMatch = unigramSearchHistogram.contains(singleWord);
		List<String> orderedCompletions = new ArrayList<String>();
		
		orderedCompletions = unigramSearchHistogram.getOrderedResults(completions, resultLimit);

		if (wordExactMatch) {
			if (!orderedCompletions.contains(singleWord)) {
				orderedCompletions.remove(orderedCompletions.size() - 1);
				orderedCompletions.add(singleWord);
			}
		}

		logger.debug("Ordered Completions for " + singleWord + ":", orderedCompletions);
		return orderedCompletions;
	}
	

	/* (non-Javadoc)
	 * @see net.networkdowntime.search.engine.SearchEngine#getCompletions(java.lang.String, boolean)
	 */
	@Override
	public List<String> getCompletions(String searchTerm, boolean fuzzyMatch) {
		List<String> completions = new ArrayList<String>();

		searchTerm = textScrubber.scrubText(searchTerm);
		String[] words = splitter.splitContent(searchTerm);
		
		if (words.length == 0) {
			return completions; // no-op - nothing to do
		} else if (words.length == 1) {
			
			completions = getCompletions(searchTerm, 10);

		} else {
			
			String wordOne = words[words.length - 2];
			String wordTwo = words[words.length - 1];
			
			Set<String> wordOneCompletions = new HashSet<String>(getCompletions(wordOne, 50));
			Set<String> wordTwoCompletions = new HashSet<String>(getCompletions(wordTwo, 50));
			
			if (wordTwoCompletions.isEmpty()) {
				for (String completeWord : suffixTrie.getCompletions(wordTwo)) {
					wordTwoCompletions.add(completeWord);
				}
			}

			completions = digramHistogram.getOrderedResults(wordOneCompletions, wordTwoCompletions, 10);
			
		}
		return completions;
	}


	/* (non-Javadoc)
	 * @see net.networkdowntime.search.engine.SearchEngine#search(java.lang.String, int)
	 */
	@Override
	public Set<Long> search(String searchTerm, int limit) {
		long t1 = System.currentTimeMillis();
		logger.debug("Searching for text \"" + searchTerm + "\"");
		
		searchTerm = textScrubber.scrubText(searchTerm);
		logger.debug("Scrubbed Test \"" + searchTerm + "\"");

		String[] words = splitter.splitContent(searchTerm);
		logger.debug("Split content:", words);

		List<String> keywords = keywordScrubber.scrubKeywords(words);
		logger.debug("Keywords:", keywords);

		timeForScrubbing += System.currentTimeMillis() - t1;
		t1 = System.currentTimeMillis();
		
		List<String> completions = new ArrayList<String>();
		for (String word : keywords) {
			completions.addAll(getCompletions(word, true));
		}
		
		timeForCompletions += System.currentTimeMillis() - t1;
		t1 = System.currentTimeMillis();

		TLinkedHashSet<String> uniqCompletions = new TLinkedHashSet<String>();
		
		
		for (String completion : completions) {
			logger.debug("Completion: " + completion);
			
			words = splitter.splitContent(completion);
			
			for (String word : words) {
				uniqCompletions.add(word);
			}
		}
		
		timeForUniqCompletions += System.currentTimeMillis() - t1;
		logger.debug("Uniq Completions:", uniqCompletions);
		logger.debug("\tgot uniq completions; size = " + uniqCompletions.size());
		t1 = System.currentTimeMillis();

		
		Set<Long> results = unigramSearchHistogram.getSearchResults(uniqCompletions, limit);

		timeForSearchResults += System.currentTimeMillis() - t1;
		return results;
	}

	/* (non-Javadoc)
	 * @see net.networkdowntime.search.engine.SearchEngine#add(java.lang.String, long, java.lang.String)
	 */
	@Override
	public void add(String type, Long id, String text) {
		text = textScrubber.scrubText(text);
		String[] words = splitter.splitContent(text);
		List<String> keywords = keywordScrubber.scrubKeywords(words);
		
		for (int i = 0; i < keywords.size(); i++) {
			String word = keywords.get(i);
			prefixTrie.add(word);
			suffixTrie.add(word);
			unigramSearchHistogram.add(word, id);
			if (i > 0) {
				String prevWord = keywords.get(i - 1);
				digramHistogram.add(prevWord, word);
			}
		}
	}

	/* (non-Javadoc)
	 * @see net.networkdowntime.search.engine.SearchEngine#remove(java.lang.String, long, java.lang.String)
	 */
	@Override
	public void remove(String type, Long id, String text) {
		text = textScrubber.scrubText(text);
		String[] words = splitter.splitContent(text);
		List<String> keywords = keywordScrubber.scrubKeywords(words);

		for (int i = 0; i < keywords.size(); i++) {
			String word = keywords.get(i);
			// no good way to remove from prefixTrie right now
			// no good way to remove from suffixTrie right now
			unigramSearchHistogram.remove(word, id);
			if (i > 0) {
				String prevWord = keywords.get(i - 1);
				digramHistogram.remove(prevWord, word);
			}
		}
	}

	
}
