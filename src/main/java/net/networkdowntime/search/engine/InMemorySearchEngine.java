package net.networkdowntime.search.engine;

import gnu.trove.set.hash.TLinkedHashSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.networkdowntime.search.SupportedSearchResults;
import net.networkdowntime.search.histogram.UnigramLongSearchHistogram;
import net.networkdowntime.search.histogram.UnigramStringSearchHistogram;
import net.networkdowntime.search.textProcessing.ContentSplitter;
import net.networkdowntime.search.textProcessing.KeywordScrubber;
import net.networkdowntime.search.textProcessing.HtmlTagTextScrubber;
import net.networkdowntime.search.textProcessing.TextScrubber;

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
	public List<String> getCompletions(String searchTerm, boolean fuzzyMatch, int limit) {
		return new ArrayList<String>(autocomplete.getCompletions(searchTerm, fuzzyMatch, limit));
	}

	@Override
	public Set<Long> search(SupportedSearchResults type, String searchTerm, int limit) {
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

		Set<Long> results = unigramLongSearchHistogram.getSearchResults(uniqCompletions, limit);

		timeForSearchResults += System.currentTimeMillis() - t1;
		return results;
	}

	@Override
	public void add(SupportedSearchResults type, Long id, String text) {
		text = textScrubber.scrubText(text);
		String[] words = splitter.splitContent(text);
		List<String> keywords = keywordScrubber.scrubKeywords(words);

		autocomplete.add(keywords);

		for (String word : keywords) {
			unigramLongSearchHistogram.add(word, id);
		}
	}

	@Override
	public void remove(SupportedSearchResults type, Long id, String text) {
		text = textScrubber.scrubText(text);
		String[] words = splitter.splitContent(text);
		List<String> keywords = keywordScrubber.scrubKeywords(words);

		autocomplete.remove(keywords);

		for (String word : keywords) {
			unigramLongSearchHistogram.remove(word, id);
		}
	}
	
}
