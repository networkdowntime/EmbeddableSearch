package net.networkdowntime.search.textProcessing;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Attempts to sanitize input text.  Strips out common words, provides for min and max length of keywords, removes special characters from the beginning/end of words,
 * ignores numbers less than 5 characters.  See scrubKeywords() for exact details.
 * 
 * @author rwiles
 *
 */
public class KeywordScrubber {
	// by default ignore the most common three letter words in English
	private static String[] defaultIgnoreWords = new String[] {"and", "the"};
	
	private Set<String>					doNotIndexWords		= new TreeSet<String>();
	
	int minKeywordLength = 1;
	int maxKeywordLength = 40;

	
	/**
	 * Default constructor, 2-40 min/max length, ignores ["and", "the"]
	 */
	public KeywordScrubber() {
		addDefaultIgnoreWords(defaultIgnoreWords);
	}
	
	
	/**
	 * Allows min/max keyword length to be specified.  Ignores ["and", "the"]
	 * @param minKeywordLength
	 * @param maxKeywordLength
	 */
	public KeywordScrubber(int minKeywordLength, int maxKeywordLength) {
		this.minKeywordLength = minKeywordLength;
		this.maxKeywordLength = maxKeywordLength;
		addDefaultIgnoreWords(defaultIgnoreWords);
	}
	
	
	/**
 	 * Allows min/max keyword length to be specified, supports custom ignore array.
 	 * 
	 * @param minKeywordLength
	 * @param maxKeywordLength
	 * @param ignoreWords
	 */
	public KeywordScrubber(int minKeywordLength, int maxKeywordLength, String[] ignoreWords) {
		this.minKeywordLength = minKeywordLength;
		this.maxKeywordLength = maxKeywordLength;
		addDefaultIgnoreWords(ignoreWords);
	}
	
	
	/**
	 * Scrubs an array of words and returns the sanitized results.
	 * 
	 * @param words String array of words to be sanitized
	 * @return Not-null list of sanitized keywords
	 */
	public List<String> scrubKeywords(String[] words) {
		List<String> newKeywords = new ArrayList<String>();

		for (String word : words) {
			word = scrubKeyword(word.toLowerCase());
			if (word != null) {
				newKeywords.add(scrubKeyword(word));
			}
		}
		
		return newKeywords;
	}
	
	
	/**
	 * Scrubs a single word
	 * @param keyword String to be sanitized 
	 * @return Sanitized string or null
	 */
	public String scrubKeyword(String keyword) {
		boolean changed = false;
		
		while (keyword.startsWith("\"") //
				|| keyword.startsWith("�") // 
				|| keyword.startsWith("�") //
				|| keyword.startsWith("�") // this is not a hyphen! 
				|| keyword.startsWith("-") //
				|| keyword.startsWith("~") //
				|| keyword.startsWith("(") //
				|| keyword.startsWith("'") //
				|| keyword.startsWith(">") //
				|| keyword.startsWith(":") //
				|| keyword.startsWith(",") //
				|| keyword.startsWith(".") //
				|| keyword.startsWith("!") //
				|| keyword.startsWith("`") //
				|| keyword.startsWith("*") //
				|| keyword.startsWith("|") //
				|| keyword.startsWith("[") //
				|| keyword.startsWith(";")) {
			keyword = keyword.substring(1);
			changed = true;
		}

		while (keyword.endsWith("\"") //
				|| keyword.endsWith(")") //
				|| keyword.endsWith("(") //
				|| keyword.endsWith("/") //
				|| keyword.endsWith("'") //
				|| keyword.endsWith(":") //
				|| keyword.endsWith(",") //
				|| keyword.endsWith(".") //
				|| keyword.endsWith("-") //
				|| keyword.endsWith("!") //
				|| keyword.endsWith("?") //
				|| keyword.endsWith("`") //
				|| keyword.endsWith("*") //
				|| keyword.endsWith("]") //
				|| keyword.endsWith(";")) {
			keyword = keyword.substring(0, keyword.length() - 1);
			changed = true;
		}

		if (keyword.startsWith("mailto:") && keyword.length() > 7) {
			keyword = keyword.substring(7);
			changed = true;
		}

		if (keyword.startsWith("http://") && keyword.length() > 7) {
			keyword = keyword.substring(7);
			changed = true;
		}

		if (keyword.startsWith("https://") && keyword.length() > 8) {
			keyword = keyword.substring(8);
			changed = true;
		}

		if (keyword.length() < minKeywordLength || keyword.length() > maxKeywordLength) // nothing two characters or less 
			return null;

		if ((keyword.length() == 7 || keyword.length() == 8) && keyword.matches("^[0-9]+:[0-9]+:[0-9]+$")) // a common time format 07:12:20 or 7:12:20
			return null;

		if (keyword.matches("^[0-9]+:[0-9]+:[0-9]+\\.[0-9]+$")) // a common time format 07:12:20.0123 or 7:12:20.01234
			return null;

		if ((keyword.length() == 4 || keyword.length() == 5) && (keyword.matches("^[0-9]+:[0-9]+$"))) // a common time format 07:12 or 7:12
			return null;

		if (keyword.matches("^[0-9]+/[0-9]+/[0-9]+$")) // dates 08/20/2001 or 08/20/01
			return null;

		if (keyword.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}$")) // dates 2001-08-20
			return null;

		if (keyword.matches("^[0-9]+\\.[0-9]+$")) // simple numbers with decimals
			return null;

		if (keyword.length() < 5 && keyword.matches("^[0-9]+$")) // numbers with less than 5 numbers
			return null;

		if (keyword.matches("^[\\-]+$")) // common separator -------------------
			return null;

		if (keyword.matches("^[#]+$")) // common separator ==================
			return null;

		if (keyword.matches("^[=]+$")) // common separator ==================
			return null;

		if (keyword.matches("^[_]+$")) // common separator _________________
			return null;

		if (keyword.matches("^[*]+$")) // common separator *****************
			return null;

		if (doNotIndexWords.contains(keyword)) // too common words
			return null;

		if (changed) // repeat until the minimum keyword is reached
			keyword = scrubKeyword(keyword);

		return keyword;
	}


	/**
	 * Internal method to add words to the ignore words list
	 * @param ignoreWords String array of the words to ignore
	 */
	private void addDefaultIgnoreWords(String[] ignoreWords) {
		for (String wordToIgnore : ignoreWords) {
			doNotIndexWords.add(wordToIgnore);
		}
	}
}
