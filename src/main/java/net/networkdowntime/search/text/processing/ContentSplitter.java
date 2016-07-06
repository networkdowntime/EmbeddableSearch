package net.networkdowntime.search.text.processing;

import java.util.regex.Pattern;

/**
 * Content Splitter uses regex processing to split apart strings into indexable words.
 * The default splitting regex includes whitespaces, various punctuation marks, parenthesis,
 * brackets, braces, or angle brackets.  See constructor implementation for exact details
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
public class ContentSplitter {

	private String splittingRegex;
	private Pattern pattern;

	/**
	 * Default constructor, sets up the initial splitting regular expression 
	 */
	public ContentSplitter() {
		splittingRegex = "";
		splittingRegex += "\\s{0,}&\\s{0,}";// & led or followed by a whitespace or the end of a line
		splittingRegex += "|\\s+"; 			// whitespaces
		splittingRegex += "|\\.{1,}\\s+"; 	// one or more periods followed by 1+ whitespace
		splittingRegex += "|\\.$"; 			// period at the end of the line
		splittingRegex += "|,(\\s+|$)"; 	// , followed by a whitespace or the end of a line
		splittingRegex += "|!(\\s+|$)"; 	// ; followed by a whitespace or the end of a line
		splittingRegex += "|\\?(\\s+|$)"; 	// ? followed by a whitespace or the end of a line
		splittingRegex += "|;(\\s{0,}|$)"; 	// ; followed by a whitespace or the end of a line
		splittingRegex += "|\\s{0,}:\\s{0,}"; // : led or followed by a whitespace
		splittingRegex += "|\\s{0,}\\(\\s{0,}"; // ( led or followed by a whitespace
		splittingRegex += "|\\s{0,}\\)\\s{0,}"; // ) led or followed by a whitespace

		splittingRegex += "|\\s{0,}\\[\\s{0,}"; // [ led or followed by a whitespace
		splittingRegex += "|\\s{0,}\\]\\s{0,}"; // ] led or followed by a whitespace

		splittingRegex += "|\\s{0,}\\{\\s{0,}"; // { led or followed by a whitespace
		splittingRegex += "|\\s{0,}\\}\\s{0,}"; // } led or followed by a whitespace

		splittingRegex += "|\\s{0,}<\\s{0,}"; // < led or followed by a whitespace
		splittingRegex += "|\\s{0,}>\\s{0,}"; // > led or followed by a whitespace

		pattern = Pattern.compile(splittingRegex);
	}

	/**
	 * Gets the splitting regular expression.
	 * 
	 * @return The current splitting regular expression string
	 */
	public String getSplittingRegex() {
		return splittingRegex;
	}

	/**
	 * Sets the splitting regular expression.
	 * 
	 * @param splittingRegex String containing the regular expression to use for splitting words
	 */
	public void setSplittingRegex(String splittingRegex) {
		this.splittingRegex = splittingRegex;
		pattern = Pattern.compile(splittingRegex);
	}

	/**
	 * Splits the content using the splitting regular expression
	 * 
	 * @param content String containing the string to split into words
	 * @return String array containing the individual split into words 
	 */
	public String[] splitContent(String content) {
		return pattern.split(content);
	}

}
