package net.networkdowntime.search.engine;

import gnu.trove.set.hash.TLinkedHashSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.networkdowntime.search.Logger;
import net.networkdowntime.search.histogram.DigramHistogram;
import net.networkdowntime.search.histogram.UnigramLongSearchHistogram;
import net.networkdowntime.search.prefixTrie.PrefixTrieNode;
import net.networkdowntime.search.suffixTrie.SuffixTrieNode;
import net.networkdowntime.search.textProcessing.ContentSplitter;
import net.networkdowntime.search.textProcessing.KeywordScrubber;
import net.networkdowntime.search.textProcessing.TextScrubber;

public class InMemorySearchEngine implements SearchEngine {

	Logger logger = new Logger(false);
	PrefixTrieNode prefixTrie = new PrefixTrieNode();
	SuffixTrieNode suffixTrie = new SuffixTrieNode(false);
	UnigramLongSearchHistogram unigramHistogram = new UnigramLongSearchHistogram();
	DigramHistogram digramHistogram = new DigramHistogram();
	TextScrubber textScrubber = new TextScrubber();
	ContentSplitter splitter = new ContentSplitter();
	KeywordScrubber keywordScrubber = new KeywordScrubber();
	
	long timeForScrubbing = 0;
	long timeForCompletions = 0;
	long timeForUniqCompletions = 0;
	long timeForSearchResults = 0;
	public static long timeToBuildSearchResultsMap = 0;
	public static long timeToBuildSearchResultsOrdered = 0;
	
	
	public void resetTimes() {
		timeForScrubbing = 0;
		timeForCompletions = 0;
		timeForUniqCompletions = 0;
		timeForSearchResults = 0;
		timeToBuildSearchResultsMap = 0;
		timeToBuildSearchResultsOrdered = 0;
	}
	
	
	@Override
	public void printTimes() {
		System.out.println("\ttimeForScrubbing: " + (timeForScrubbing / 1000d) + " secs");
		System.out.println("\ttimeForCompletions: " + (timeForCompletions / 1000d) + " secs");
		System.out.println("\ttimeForUniqCompletions: " + (timeForUniqCompletions / 1000d) + " secs");
		System.out.println("\ttimeForSearchResults: " + (timeForSearchResults / 1000d) + " secs");
		System.out.println("\t\tgetSearchResults(): Time to build results hashmap: " + (timeToBuildSearchResultsMap / 1000d) + " secs");
		System.out.println("\t\tgetSearchResults(): Time to build results orderedResults: " + (timeToBuildSearchResultsOrdered / 1000d) + " secs");

		System.out.println("\ttotal time: " + ((timeForScrubbing + timeForCompletions + timeForUniqCompletions + timeForSearchResults) / 1000d) + "secs");
	}
	

	@Override
	public void printTimesAndReset() {
		printTimes();
		resetTimes();
	}
	

	private List<String> getCompletions(String word, int resultLimit) {
		Set<String> completions = new TLinkedHashSet<String>();

		if (word.length() > 0) {
			for (String wordPlusPrefix : prefixTrie.getCompletions(word)) {
				for (String completeWord : suffixTrie.getCompletions(wordPlusPrefix)) {
					completions.add(completeWord);
				}
			}
		}
		logger.log(1, "Unordered Completions for " + word + ":", completions);

		boolean wordExactMatch = UnigramLongSearchHistogram.contains(unigramHistogram, word);
		List<String> orderedCompletions = new ArrayList<String>();
		
		orderedCompletions = UnigramLongSearchHistogram.getOrderedResults(unigramHistogram, completions, resultLimit);

		if (wordExactMatch) {
			if (!orderedCompletions.contains(word)) {
				orderedCompletions.remove(orderedCompletions.size() - 1);
				orderedCompletions.add(word);
			}
		}

		logger.log(1, "Ordered Completions for " + word + ":", orderedCompletions);
		return orderedCompletions;
	}
	
	
	/**
	 * google's autocomplete does the following:
	 * 1st word - suffix only completion
	 * space after 1st word - prefix completion of 1st word, 2-gram recommendation
	 * 
	 */
	@Override
	public List<String> getCompletions(String wordStub, boolean fuzzyMatch) {
		List<String> completions = new ArrayList<String>();

		wordStub = textScrubber.scrubText(wordStub);
		String[] words = splitter.splitContent(wordStub);
		
		if (words.length == 0) {
			return completions; // no-op - nothing to do
		} else if (words.length == 1) {
			
			completions = getCompletions(wordStub, 10);

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

			completions = DigramHistogram.getOrderedResults(digramHistogram, wordOneCompletions, wordTwoCompletions, 10);
			
		}
		return completions;
	}


	@Override
	public Set<Long> search(String text, int limit) {
		long t1 = System.currentTimeMillis();
		logger.log(0, "Searching for text \"" + text + "\"");
		
		text = textScrubber.scrubText(text);
		logger.log(0, "Scrubbed Test \"" + text + "\"");

		String[] words = splitter.splitContent(text);
		logger.log(1, "Split content:", words);

		List<String> keywords = keywordScrubber.scrubKeywords(words);
		logger.log(1, "Keywords:", keywords);

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
			logger.log(0, "Completion: " + completion);
			
			words = splitter.splitContent(completion);
			
			for (String word : words) {
				uniqCompletions.add(word);
			}
		}
		
		timeForUniqCompletions += System.currentTimeMillis() - t1;
		logger.log(1, "Uniq Completions:", uniqCompletions);
		t1 = System.currentTimeMillis();

//		System.out.println("\tgot uniq completions; size = " + uniqCompletions.size());
		
		Set<Long> results = UnigramLongSearchHistogram.getSearchResults(unigramHistogram, uniqCompletions, limit, logger);

		timeForSearchResults += System.currentTimeMillis() - t1;
		return results;
	}

	@Override
	public void add(String type, Long id, String text) {
		text = textScrubber.scrubText(text);
		String[] words = splitter.splitContent(text);
		List<String> keywords = keywordScrubber.scrubKeywords(words);
		
		for (int i = 0; i < keywords.size(); i++) {
			String word = keywords.get(i);
			prefixTrie.add(word);
			suffixTrie.add(word);
			UnigramLongSearchHistogram.add(unigramHistogram, word, id);
			if (i > 0) {
				String prevWord = keywords.get(i - 1);
				DigramHistogram.add(digramHistogram, prevWord, word);
			}
		}
	}

	@Override
	public void remove(String type, Long id, String text) {
		text = textScrubber.scrubText(text);
		String[] words = splitter.splitContent(text);
		List<String> keywords = keywordScrubber.scrubKeywords(words);

		for (int i = 0; i < keywords.size(); i++) {
			String word = keywords.get(i);
			// no good way to remove from prefixTrie right now
			// no good way to remove from suffixTrie right now
			UnigramLongSearchHistogram.remove(unigramHistogram, word, id);
			if (i > 0) {
				String prevWord = keywords.get(i - 1);
				DigramHistogram.remove(digramHistogram, prevWord, word);
			}
		}
	}

	
}
