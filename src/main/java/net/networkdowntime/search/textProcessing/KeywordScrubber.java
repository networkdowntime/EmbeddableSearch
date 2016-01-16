package net.networkdowntime.search.textProcessing;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import gnu.trove.set.TCharSet;
import gnu.trove.set.hash.TCharHashSet;

/**
 * Attempts to sanitize input text.  Strips out common words, provides for min and max length of keywords, removes special 
 * characters from the beginning/end of words, ignores numbers less than 5 characters.  See scrubKeywords() for exact details.
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
public class KeywordScrubber {
	// by default ignore the most common three letter words in English
	private static String[] defaultIgnoreWords = new String[] { "and", "the" };

	private Set<String> doNotIndexWords = new TreeSet<String>();

	private int minKeywordLength = 1;
	private int maxKeywordLength = 40;

	private static TCharSet invalidCharWordBeginnings = new TCharHashSet();
	private static TCharSet invalidCharWordEndings = new TCharHashSet();

	private static Pattern dateMatcher = Pattern.compile("(^[0-9]+/[0-9]+/[0-9]+$|^[0-9]{4}-[0-9]{2}-[0-9]{2}$)"); // dates 08/20/2001, 08/20/01, or 2001-08-20
	private static Pattern timeMatcher1 = Pattern.compile("^[0-9]+:[0-9]+:[0-9]+$"); // a common time format 07:12:20 or 7:12:20
	private static Pattern timeMatcher2 = Pattern.compile("^[0-9]+:[0-9]+:[0-9]+\\.[0-9]+$"); // a common time format 07:12:20.0123 or 7:12:20.01234
	private static Pattern timeMatcher3 = Pattern.compile("^[0-9]+:[0-9]+$"); // a common time format 07:12 or 7:12

	static {
		invalidCharWordBeginnings.add('\"');
		invalidCharWordBeginnings.add('�'); // this is not a hyphen! but sometimes shows up as one
		invalidCharWordBeginnings.add('-');
		invalidCharWordBeginnings.add('~');
		invalidCharWordBeginnings.add('(');
		invalidCharWordBeginnings.add('\'');
		invalidCharWordBeginnings.add('>');
		invalidCharWordBeginnings.add(':');
		invalidCharWordBeginnings.add(',');
		invalidCharWordBeginnings.add('.');
		invalidCharWordBeginnings.add('!');
		invalidCharWordBeginnings.add('`');
		invalidCharWordBeginnings.add('*');
		invalidCharWordBeginnings.add('|');
		invalidCharWordBeginnings.add('[');
		invalidCharWordBeginnings.add(';');
		
		invalidCharWordEndings.add('\"');
		invalidCharWordEndings.add(')');
		invalidCharWordEndings.add('(');
		invalidCharWordEndings.add('/');
		invalidCharWordEndings.add('\'');
		invalidCharWordEndings.add(':');
		invalidCharWordEndings.add(',');
		invalidCharWordEndings.add('.');
		invalidCharWordEndings.add('-');
		invalidCharWordEndings.add('!');
		invalidCharWordEndings.add('?');
		invalidCharWordEndings.add('`');
		invalidCharWordEndings.add('*');
		invalidCharWordEndings.add(']');
		invalidCharWordEndings.add(';');
	}

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
	 * @param scrubbedKeyword String to be sanitized 
	 * @return Sanitized string or null
	 */
	public String scrubKeyword(String keyword) {
		String scrubbedKeyword = keyword;
		boolean hasChanged = false;

		while (!scrubbedKeyword.isEmpty() && invalidCharWordBeginnings.contains(scrubbedKeyword.charAt(0))) {
			scrubbedKeyword = scrubbedKeyword.substring(1);
			hasChanged = true;
		}

		int lastCharIndex = scrubbedKeyword.length() - 1;
		while (!scrubbedKeyword.isEmpty() && invalidCharWordBeginnings.contains(scrubbedKeyword.charAt(lastCharIndex))) {
			scrubbedKeyword = scrubbedKeyword.substring(0, lastCharIndex);
			hasChanged = true;
			lastCharIndex--;
		}
		
		if (scrubbedKeyword.startsWith("mailto:") && scrubbedKeyword.length() > 7) {
			scrubbedKeyword = scrubbedKeyword.substring(7);
			hasChanged = true;
		}

		if (scrubbedKeyword.startsWith("http://") && scrubbedKeyword.length() > 7) {
			scrubbedKeyword = scrubbedKeyword.substring(7);
			hasChanged = true;
		}

		if (scrubbedKeyword.startsWith("https://") && scrubbedKeyword.length() > 8) {
			scrubbedKeyword = scrubbedKeyword.substring(8);
			hasChanged = true;
		}

		if (scrubbedKeyword.length() < minKeywordLength || scrubbedKeyword.length() > maxKeywordLength) // nothing two characters or less 
			return null;

		if ((scrubbedKeyword.length() == 7 || scrubbedKeyword.length() == 8) && timeMatcher1.matcher(scrubbedKeyword).matches()) // a common time format 07:12:20 or 7:12:20
			return null;

		if (timeMatcher2.matcher(scrubbedKeyword).matches()) // a common time format 07:12:20.0123 or 7:12:20.01234
			return null;

		if ((scrubbedKeyword.length() == 4 || scrubbedKeyword.length() == 5) && timeMatcher3.matcher(scrubbedKeyword).matches()) // a common time format 07:12 or 7:12
			return null;

		if (dateMatcher.matcher(scrubbedKeyword).matches()) // dates 08/20/2001, 08/20/01, or 2001-08-20
			return null;

		if (scrubbedKeyword.matches("^[0-9]+\\.[0-9]+$")) // simple numbers with decimals
			return null;

		if (scrubbedKeyword.length() < 5 && scrubbedKeyword.matches("^[0-9]+$")) // numbers with less than 5 numbers
			return null;

		if (scrubbedKeyword.matches("^[\\-]+$")) // common separator -------------------
			return null;

		if (scrubbedKeyword.matches("^[#]+$")) // common separator ==================
			return null;

		if (scrubbedKeyword.matches("^[=]+$")) // common separator ==================
			return null;

		if (scrubbedKeyword.matches("^[_]+$")) // common separator _________________
			return null;

		if (scrubbedKeyword.matches("^[*]+$")) // common separator *****************
			return null;

		if (doNotIndexWords.contains(scrubbedKeyword)) // too common words
			return null;

		if (hasChanged) // repeat until the minimum keyword is reached
			scrubbedKeyword = scrubKeyword(scrubbedKeyword);

		return scrubbedKeyword;
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
