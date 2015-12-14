package net.networkdowntime.search.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gnu.trove.set.hash.TLinkedHashSet;
import net.networkdowntime.search.histogram.DigramHistogram;
import net.networkdowntime.search.histogram.UnigramHistogram;
import net.networkdowntime.search.textProcessing.ContentSplitter;
import net.networkdowntime.search.textProcessing.HtmlTagTextScrubber;
import net.networkdowntime.search.textProcessing.KeywordScrubber;
import net.networkdowntime.search.textProcessing.TextScrubber;
import net.networkdowntime.search.trie.PrefixTrieNode;
import net.networkdowntime.search.trie.SuffixTrieNode;

/**
 * Implements auto-complete functionality for words using a full prefix-trie and a partial suffix-trie.  
 * Completions are ordered based on histogram ordering.  Support unigram and digram word ordering.
 *  
 * @author rwiles
 *
 */
public class Autocomplete {
	static final Logger logger = LogManager.getLogger(Autocomplete.class.getName());

	private UnigramHistogram unigramHistogram = new UnigramHistogram();
	private DigramHistogram digramHistogram = new DigramHistogram();
	private PrefixTrieNode prefixTrie = new PrefixTrieNode();
	private SuffixTrieNode suffixTrie = new SuffixTrieNode(false);

	private TextScrubber textScrubber = new HtmlTagTextScrubber();
	private ContentSplitter contentSplitter = new ContentSplitter();
	private KeywordScrubber keywordScrubber = new KeywordScrubber();

	/**
	 * Creates an instance of the Autocomplete class that uses the default TextScrubber, ContentSplitter, and KeywordScrubber.
	 * Histogram word ordering is used for autocompletion.
	 */
	public Autocomplete() {
		this.textScrubber = new HtmlTagTextScrubber();
		this.contentSplitter = new ContentSplitter();
		this.keywordScrubber = new KeywordScrubber();
	}

	/**
	 * Allows for custom implementations of TextScrubber, ContentSplitter, and KeywordScrubber to be used for auto-completion.
	 * 
	 * @param textScrubber TextScrubber to use
	 * @param contentSplitter ContentSplitter to use
	 * @param keywordScrubber KeywordScrubber to use
	 */
	public Autocomplete(TextScrubber textScrubber, ContentSplitter contentSplitter, KeywordScrubber keywordScrubber) {
		this.textScrubber = textScrubber;
		this.contentSplitter = contentSplitter;
		this.keywordScrubber = keywordScrubber;
	}

	/**
	 * Adds text to auto-completion
	 * 
	 * @param text The text to add
	 */
	public void add(String text) {
		text = textScrubber.scrubText(text);
		String[] words = contentSplitter.splitContent(text);
		List<String> keywords = keywordScrubber.scrubKeywords(words);

		this.add(keywords);
	}

	/**
	 * Adds text to auto-completion
	 * 
	 * @param text The text to add
	 */
	void add(List<String> keywords) {
		String currentWord = null;
		String previousWord = null;
		for (int i = 0; i < keywords.size(); i++) {
			previousWord = (currentWord != null) ? currentWord : null;
			currentWord = keywords.get(i);

			prefixTrie.add(currentWord);
			suffixTrie.add(currentWord);

			UnigramHistogram.add(unigramHistogram, currentWord);
			if (previousWord != null) {
				digramHistogram.add(previousWord, currentWord);
			}
		}
	}

	/**
	 * Currently decrements the word from the histogram tracking to lower its auto-completion ranking.
	 * Does not currently remove them from the Treis.
	 * 
	 * @param text A string containing the text to remove
	 */
	public void remove(String text) {
		text = textScrubber.scrubText(text);
		String[] words = contentSplitter.splitContent(text);
		List<String> keywords = keywordScrubber.scrubKeywords(words);

		this.remove(keywords);
	}

	/**
	 * Takes a list of pre-scrubbed, split, and keyword scrubbed strings and removes them.
	 * 
	 * @param keywords List of strings to remove
	 */
	void remove(List<String> keywords) {
		String currentWord = null;
		String previousWord = null;
		for (int i = 0; i < keywords.size(); i++) {
			previousWord = (currentWord != null) ? currentWord : null;
			currentWord = keywords.get(i);

			// TODO Implement remove functionality for the Tries, don't have a mechanism to do that right now

			UnigramHistogram.remove(unigramHistogram, currentWord);
			if (previousWord != null) {
				digramHistogram.remove(previousWord, currentWord);
			}
		}
	}

	/**
	 * Get completions for the given input.  This will provide completions for missing prefix or suffix on the words.
	 * If using histogram word ordering it orders those completions based on their histogram occurrence counts.  This 
	 * only looks at the last two words in the text providing recommendations for continued auto-completions.  Assumption 
	 * is made that earlier words in the input where correctly matched via suggested completions. 
	 * 
	 * @param autocompleteInput String to search for completions, can contain multiple words or word fragments
	 * @param fuzzyMatch provides character back-off and re-searching if no completions are found 
	 * @param limit Max number of results to return
	 * @return Not-null set of the suggested completions
	 */
	public Set<String> getCompletions(String autocompleteInput, boolean fuzzyMatch, int limit) {
		autocompleteInput = textScrubber.scrubText(autocompleteInput);
		String[] words = contentSplitter.splitContent(autocompleteInput);
		List<String> keywords = keywordScrubber.scrubKeywords(words);

		Set<String> orderedCompletions = new TLinkedHashSet<String>();

		logger.debug("keywords.size(): " + keywords.size());

		if (keywords.size() == 0) {
			return orderedCompletions; // no-op - nothing to do
		} else {
			String currentWord = keywords.get(keywords.size() - 1);
			Set<String> currentWordCompletions = getCompletionsSingleWordUnordered(currentWord, fuzzyMatch, limit * 3);

			for (String s : currentWordCompletions) {
				logger.debug("\tcurrentWord: " + currentWord + "; currentWordCompletion: " + s);
			}

			if (keywords.size() == 1) { // one word
				currentWordCompletions = new TLinkedHashSet<String>(UnigramHistogram.getOrderedResults(unigramHistogram, new ArrayList<String>(currentWordCompletions), limit));

				// makes sense that if there is an exact match, it should show up in the results
				boolean wordExactMatch = UnigramHistogram.contains(unigramHistogram, currentWord);
				if (wordExactMatch) {
					if (!currentWordCompletions.contains(currentWord)) {
						currentWordCompletions.remove(currentWordCompletions.size() - 1);
						currentWordCompletions.add(currentWord);
					}
				}

				orderedCompletions = currentWordCompletions;
			} else { // at least two words
				String previousWord = keywords.get(keywords.size() - 2);
				Set<String> previousWordCompletions = getCompletionsSingleWordUnordered(previousWord, fuzzyMatch, limit * 3);

				for (String s : currentWordCompletions) {
					logger.debug("\tpreviousWord: " + previousWord + "; currentWordCompletion: " + s);
				}

				List<String> digramCompletions = digramHistogram.getOrderedResults(previousWordCompletions, currentWordCompletions, limit);

				for (String s : digramCompletions) {
					logger.debug("\tdigramCompletions: " + s);
				}

				if (keywords.size() == 2) {
					orderedCompletions.addAll(digramCompletions);
				} else { // add the beginning back to the results
					String beginningOfInput = listToString(keywords, 2);

					for (String completion : digramCompletions) {
						orderedCompletions.add(beginningOfInput + " " + completion);
					}
				}
			}
		}

		return orderedCompletions;
	}

	/**
	 * Internal method to get the unordered completions for a single word.
	 *  
	 * @param word Word to get the completions for
	 * @param fuzzyMatch provides character back-off and re-searching if no completions are found 
	 * @param limit Max number of results to return
	 * @return Not-null set of the suggested completions
	 */
	private Set<String> getCompletionsSingleWordUnordered(String word, boolean fuzzyMatch, int limit) {
		Set<String> completions = new TLinkedHashSet<String>();

		if (word != null && word.length() > 0) {
			for (String wordPlusPrefix : prefixTrie.getCompletions(word, limit)) {
				for (String completedWord : suffixTrie.getCompletions(wordPlusPrefix, 0)) {
					completions.add(completedWord);
				}
			}

			if (fuzzyMatch && completions.isEmpty()) {
				completions.addAll(getCompletionsSingleWordUnordered(word.substring(0, word.length() - 1), fuzzyMatch, limit));
			}
		}

		return completions;
	}

	/**
	 * Internal method to convert a string list back to a string.  Skips the last X number of words at the end.
	 * 
	 * @param keywords List of words to convert to a string
	 * @param numOfWordsAtEndToSkip Number of words at the end to skip
	 * @return
	 */
	private String listToString(List<String> keywords, int numOfWordsAtEndToSkip) {
		String retval = "";

		for (int i = 0; i < keywords.size() - numOfWordsAtEndToSkip; i++) {
			if (i > 0) {
				retval += " ";
			}
			retval += keywords.get(i);

		}
		return retval;
	}
}
