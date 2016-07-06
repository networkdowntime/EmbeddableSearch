package net.networkdowntime.search.engine;

import gnu.trove.set.hash.TLinkedHashSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.networkdowntime.search.SearchResult;
import net.networkdowntime.search.histogram.DigramHistogram;
import net.networkdowntime.search.histogram.UnigramLongSearchHistogram;
import net.networkdowntime.search.histogram.UnigramStringSearchHistogram;
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
	UnigramLongSearchHistogram unigramLongSearchHistogram = new UnigramLongSearchHistogram();
	UnigramStringSearchHistogram unigramStringSearchHistogram = new UnigramStringSearchHistogram();
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

		boolean wordExactMatch = unigramLongSearchHistogram.contains(singleWord);
		if (!wordExactMatch) {
			wordExactMatch = unigramStringSearchHistogram.contains(singleWord);
		}
		List<String> orderedCompletions = new ArrayList<String>();

		orderedCompletions = unigramLongSearchHistogram.getOrderedResults(completions, resultLimit);
		orderedCompletions.addAll(unigramStringSearchHistogram.getOrderedResults(completions, resultLimit));

		if (wordExactMatch) {
			if (!orderedCompletions.contains(singleWord)) {
				orderedCompletions.remove(orderedCompletions.size() - 1);
				orderedCompletions.add(singleWord);
			}
		}

		logger.debug("Ordered Completions for " + singleWord + ":", orderedCompletions);
		return orderedCompletions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.networkdowntime.search.engine.SearchEngine#getCompletions(java.lang.String, boolean)
	 */
	@Override
	public List<String> getCompletions(String searchTerm, boolean fuzzyMatch) {
		List<String> completions = new ArrayList<String>();
		
		boolean endsWithSpace = searchTerm.endsWith(" ");
		searchTerm = textScrubber.scrubText(searchTerm);
		String[] words = splitter.splitContent(searchTerm);

		if (words.length == 0) {
			return completions; // no-op - nothing to do
		} else if (words.length == 1 && !endsWithSpace) {

			completions = getCompletions(searchTerm, 10);

		} else {
			System.out.println(words.length);
			String wordOne = "";
			String wordTwo = "";
			
			if (words.length == 1) {
				wordOne = words[words.length - 1].trim();
			} else if (words.length > 1) {
				wordOne = words[words.length - 2].trim();
				wordTwo = words[words.length - 1].trim();
			}

			Set<String> wordOneCompletions = new HashSet<String>(getCompletions(wordOne, 50));
			Set<String> wordTwoCompletions = new HashSet<String>(getCompletions(wordTwo, 150));

			if (wordTwoCompletions.isEmpty()) {
				for (String completeWord : suffixTrie.getCompletions(wordTwo)) {
					wordTwoCompletions.add(completeWord);
				}
			}

			completions = digramHistogram.getOrderedResults(wordOneCompletions, wordTwoCompletions, 10);

		}
		return completions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.networkdowntime.search.engine.SearchEngine#search(java.lang.String, int)
	 */
	@Override
	public Set<SearchResult> search(String searchTerm, int limit) {
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

		SortedSet<SearchResult> unifiedResults = new TreeSet<>(
				Comparator.reverseOrder());

		unifiedResults.addAll(unigramLongSearchHistogram.getSearchResults(uniqCompletions, limit));
		unifiedResults.addAll(unigramStringSearchHistogram.getSearchResults(uniqCompletions, limit));

		Set<SearchResult> retval = new LinkedHashSet<SearchResult>();
		int i = 1;
		for (SearchResult result : unifiedResults) {
			retval.add(result);
			if (i++ == limit)
				break;
		}
		timeForSearchResults += System.currentTimeMillis() - t1;
		return retval;
	}

	/*
	 * (non-Javadoc)
	 * 
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
			unigramLongSearchHistogram.add(word, id);
			if (i > 0) {
				String prevWord = keywords.get(i - 1);
				digramHistogram.add(prevWord, word);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.networkdowntime.search.engine.SearchEngine#add(java.lang.String, long, java.lang.String)
	 */
	@Override
	public void add(String type, String id, String text) {
		text = textScrubber.scrubText(text);
		String[] words = splitter.splitContent(text);
		List<String> keywords = keywordScrubber.scrubKeywords(words);

		for (int i = 0; i < keywords.size(); i++) {
			String word = keywords.get(i);
			prefixTrie.add(word);
			suffixTrie.add(word);
			unigramStringSearchHistogram.add(word, id);
			if (i > 0) {
				String prevWord = keywords.get(i - 1);
				digramHistogram.add(prevWord, word);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
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
			unigramLongSearchHistogram.remove(word, id);
			if (i > 0) {
				String prevWord = keywords.get(i - 1);
				digramHistogram.remove(prevWord, word);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.networkdowntime.search.engine.SearchEngine#remove(java.lang.String, long, java.lang.String)
	 */
	@Override
	public void remove(String type, String id, String text) {
		text = textScrubber.scrubText(text);
		String[] words = splitter.splitContent(text);
		List<String> keywords = keywordScrubber.scrubKeywords(words);

		for (int i = 0; i < keywords.size(); i++) {
			String word = keywords.get(i);
			// no good way to remove from prefixTrie right now
			// no good way to remove from suffixTrie right now
			unigramStringSearchHistogram.remove(word, id);
			if (i > 0) {
				String prevWord = keywords.get(i - 1);
				digramHistogram.remove(prevWord, word);
			}
		}
	}

}
