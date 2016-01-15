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
import net.networkdowntime.search.trie.PrefixTrie;
import net.networkdowntime.search.trie.SuffixTrie;

/**
 * Implements auto-complete functionality for words using a full prefix-trie and a partial suffix-trie.  
 * Completions are ordered based on histogram ordering.  Support unigram and digram word ordering.
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
public class Autocomplete {
	static final Logger logger = LogManager.getLogger(Autocomplete.class.getName());

	private UnigramHistogram unigramHistogram = new UnigramHistogram();
	private DigramHistogram digramHistogram = new DigramHistogram();
	private PrefixTrie prefixTrie = new PrefixTrie();
	private SuffixTrie suffixTrie = new SuffixTrie(false);

	private TextScrubber textScrubber = null;
	private ContentSplitter contentSplitter = null;
	private KeywordScrubber keywordScrubber = null;

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
	 * @param keywords The text to add
	 */
	void add(List<String> keywords) {
		String previousWord = null;
		for (String currentWord : keywords) {
			prefixTrie.add(currentWord);
			suffixTrie.add(currentWord);

			UnigramHistogram.add(unigramHistogram, currentWord);
			if (previousWord != null) {
				digramHistogram.add(previousWord, currentWord);
			}
			previousWord = currentWord;
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
		String previousWord = null;
		for (String currentWord : keywords) {
			
			if (UnigramHistogram.contains(unigramHistogram, currentWord)) {
				prefixTrie.remove(currentWord);
				suffixTrie.remove(currentWord);
				UnigramHistogram.remove(unigramHistogram, currentWord);
			}
			if (previousWord != null) {
				digramHistogram.remove(previousWord, currentWord);
			}
			previousWord = currentWord;
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
		boolean hasTrailingSpace = autocompleteInput.endsWith(" ");

		autocompleteInput = textScrubber.scrubText(autocompleteInput);
		String[] words = contentSplitter.splitContent(autocompleteInput);
		List<String> keywords = keywordScrubber.scrubKeywords(words);

		return getCompletions(keywords, fuzzyMatch, hasTrailingSpace, limit);
	}

	private Set<String> getSingleWordCompletions(Set<String> currentWordCompletions, String word, int limit) {
		currentWordCompletions = new TLinkedHashSet<String>(UnigramHistogram.getOrderedResults(unigramHistogram, currentWordCompletions, limit));

		// makes sense that if there is an exact match, it should show up in the results
		boolean wordExactMatch = UnigramHistogram.contains(unigramHistogram, word);
		if (wordExactMatch) {
			if (!currentWordCompletions.contains(word)) {
				currentWordCompletions.remove(currentWordCompletions.size() - 1);
				currentWordCompletions.add(word);
			}
		}
		return currentWordCompletions;
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
	Set<String> getCompletions(List<String> keywords, boolean fuzzyMatch, boolean hasTrailingSpace, int limit) {
		logger.debug("getCompletions() Begin: keywords.size(): " + keywords.size() + "; fuzzyMatch: " + fuzzyMatch + "; hasTrailingSpace: " + hasTrailingSpace + "; limit: " + limit);

		Set<String> orderedCompletions = new TLinkedHashSet<String>();

		if (keywords.size() == 0) {
			return orderedCompletions; // no-op - nothing to do
		} else {
			String currentWord = keywords.get(keywords.size() - 1);
			Set<String> currentWordCompletions = getCompletionsSingleWordUnordered(currentWord, fuzzyMatch, limit * 3);

			if (logger.isDebugEnabled()) {
				logger.debug("currentWord:" + currentWord);
				logger.debug("currentWordCompletions:");
				for (String string : currentWordCompletions) {
					logger.debug("\t" + string);
				}
			}

			if (keywords.size() == 1 && !hasTrailingSpace) { // one word
				logger.debug("one keyword, no trailing space");

				orderedCompletions = getSingleWordCompletions(currentWordCompletions, currentWord, limit);
			} else { // either two words or looking for two words

				List<String> digramCompletions;

				if (hasTrailingSpace) {
					logger.debug("has trailing space, look for digram completions based on the previous words completions");

					digramCompletions = digramHistogram.getOrderedResults(currentWordCompletions, null, limit);

				} else {
					logger.debug("no trailing space, look for digram completions based on the previous words completions");

					String previousWord = keywords.get(keywords.size() - 2);
					Set<String> previousWordCompletions = getCompletionsSingleWordUnordered(previousWord, fuzzyMatch, limit * 3);

					if (logger.isDebugEnabled()) {
						logger.debug("previousWord: " + previousWord);
						logger.debug("previousWordCompletions");
						for (String s : previousWordCompletions) {
							logger.debug("\t" + s);
						}
					}
					
					digramCompletions = digramHistogram.getOrderedResults(previousWordCompletions, currentWordCompletions, limit);
				}

				if (logger.isDebugEnabled()) {
					logger.debug("digramCompletions for:" + currentWord);
					for (String s : digramCompletions) {
						logger.debug("\t" + s);
					}
				}

				if (digramCompletions.isEmpty()) {
					digramCompletions.addAll(getSingleWordCompletions(currentWordCompletions, currentWord, limit));
				}
				
				if (keywords.size() == 2 && !hasTrailingSpace) {
					orderedCompletions.addAll(digramCompletions);
				} else { // add the beginning back to the results
					String beginningOfInput;
					if (hasTrailingSpace) {
						beginningOfInput = listToString(keywords, 1);
					} else {
						beginningOfInput = listToString(keywords, 2);
					}
					
					for (String completion : digramCompletions) {
						orderedCompletions.add(beginningOfInput + " " + completion);
					}
				}
			}
		}

		logger.debug("getCompletions() End");
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
			for (String wordPlusPrefix : prefixTrie.getCompletions(word, limit * 2)) {
				for (String completedWord : suffixTrie.getCompletions(wordPlusPrefix, limit * 2)) {
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
