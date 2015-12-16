package net.networkdowntime.search.engine;

import gnu.trove.set.hash.TLinkedHashSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.networkdowntime.search.SearchResult;
import net.networkdowntime.search.SearchResultComparator;
import net.networkdowntime.search.SearchResultType;
import net.networkdowntime.search.histogram.FixedSizeSortedSet;
import net.networkdowntime.search.histogram.Tuple;
import net.networkdowntime.search.histogram.UnigramLongSearchHistogram;
import net.networkdowntime.search.histogram.UnigramStringSearchHistogram;
import net.networkdowntime.search.textProcessing.ContentSplitter;
import net.networkdowntime.search.textProcessing.KeywordScrubber;
import net.networkdowntime.search.textProcessing.HtmlTagTextScrubber;
import net.networkdowntime.search.textProcessing.TextScrubber;

/**
 * Implementation of an in-memory search engine with robust auto-complete capabilities and ordering of the results based on their
 * search rankings.
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
public class InMemorySearchEngine implements SearchEngine {
	static final Logger logger = LogManager.getLogger(InMemorySearchEngine.class.getName());

	private UnigramLongSearchHistogram unigramLongSearchHistogram = new UnigramLongSearchHistogram();
	private UnigramStringSearchHistogram unigramStringSearchHistogram = new UnigramStringSearchHistogram();

	private Autocomplete autocomplete = new Autocomplete();

	private TextScrubber textScrubber = new HtmlTagTextScrubber();
	private ContentSplitter splitter = new ContentSplitter();
	private KeywordScrubber keywordScrubber = new KeywordScrubber();

	// The following variables are used for tracking the times of various parts of the search operations
	private long timeForScrubbing = 0;
	private long timeForCompletions = 0;
	private long timeForUniqCompletions = 0;
	private long timeForSearchResults = 0;
	// end timing variables

	/**
	 * Resets the timing variables
	 */
	public void resetTimes() {
		timeForScrubbing = 0;
		timeForCompletions = 0;
		timeForUniqCompletions = 0;
		timeForSearchResults = 0;
	}

	/**
	 * Print the timing variables out to logger.info()
	 */
	public void printTimes() {
		logger.info("\ttimeForScrubbing: " + (timeForScrubbing / 1000d) + " secs");
		logger.info("\ttimeForCompletions: " + (timeForCompletions / 1000d) + " secs");
		logger.info("\ttimeForUniqCompletions: " + (timeForUniqCompletions / 1000d) + " secs");
		logger.info("\ttimeForSearchResults: " + (timeForSearchResults / 1000d) + " secs");

		logger.info("\ttotal time: " + ((timeForScrubbing + timeForCompletions + timeForUniqCompletions + timeForSearchResults) / 1000d) + "secs");
	}

	/**
	 * Print out the search result times and reset them.
	 */
	public void printTimesAndReset() {
		printTimes();
		resetTimes();
	}

	@Override
	public void add(Long searchResult, String text) {
		this.add((Object) searchResult, text);
	}

	@Override
	public void add(String searchResult, String text) {
		this.add((Object) searchResult, text);

	}

	/**
	 * Indexes the supplied text and associates it with the search result.
	 * 
	 * @param searchResult Search result to associate to the keywords in the text
	 * @param text String to scrub, split, and index to the search result
	 */
	private void add(Object searchResult, String text) {
		text = textScrubber.scrubText(text);
		String[] words = splitter.splitContent(text);
		List<String> keywords = keywordScrubber.scrubKeywords(words);

		autocomplete.add(keywords);

		if (searchResult instanceof Long) {
			logger.debug("Adding Long result: " + searchResult);
			for (String word : keywords) {
				unigramLongSearchHistogram.add(word, (Long) searchResult);
			}
		} else if (searchResult instanceof String) {
			logger.debug("Adding Long result: " + searchResult);
			for (String word : keywords) {
				unigramStringSearchHistogram.add(word, (String) searchResult);
			}
		}
	}

	@Override
	public void remove(Long searchResult, String text) {
		this.add((Object) searchResult, text);
	}

	@Override
	public void remove(String searchResult, String text) {
		this.add((Object) searchResult, text);
	}

	/**
	 * De-indexes the supplied text from the search result.
	 * 
	 * @param searchResult Search result to de-index from the keywords in the text
	 * @param text String to scrub, split, and de-index to the search result
	 */
	public void remove(Object searchResult, String text) {
		text = textScrubber.scrubText(text);
		String[] words = splitter.splitContent(text);
		List<String> keywords = keywordScrubber.scrubKeywords(words);

		autocomplete.remove(keywords);

		if (searchResult instanceof Long) {
			for (String word : keywords) {
				unigramLongSearchHistogram.remove(word, (Long) searchResult);
			}
		} else if (searchResult instanceof String) {
			for (String word : keywords) {
				unigramStringSearchHistogram.remove(word, (String) searchResult);
			}
		}
	}

	@Override
	public List<String> getCompletions(String searchTerm, boolean fuzzyMatch, int limit) {
		return new ArrayList<String>(autocomplete.getCompletions(searchTerm, fuzzyMatch, limit));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Set<SearchResult> search(String searchTerm, int limit) {
		long t1 = System.currentTimeMillis();
		logger.debug("Searching for text \"" + searchTerm + "\"");

		searchTerm = textScrubber.scrubText(searchTerm);
		String[] words = splitter.splitContent(searchTerm);
		List<String> keywords = keywordScrubber.scrubKeywords(words);

		logger.debug("Scrubbed Test \"" + searchTerm + "\"");
		logger.debug("Keywords:", keywords);

		timeForScrubbing += System.currentTimeMillis() - t1;
		t1 = System.currentTimeMillis();

		List<String> completions = getCompletions(searchTerm, true, limit * 2);

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

		FixedSizeSortedSet<SearchResult> results = unigramLongSearchHistogram.getSearchResults(uniqCompletions, limit);
		FixedSizeSortedSet<SearchResult> resultsString = unigramStringSearchHistogram.getSearchResults(uniqCompletions, limit);

		logger.debug("Long results: " + results.size());
		logger.debug("String results: " + resultsString.size());

		for (SearchResult searchResult : resultsString) { // combine and sort the results from each type
			results.add(searchResult);
		}
		
		timeForSearchResults += System.currentTimeMillis() - t1;
		return results;
	}

}
