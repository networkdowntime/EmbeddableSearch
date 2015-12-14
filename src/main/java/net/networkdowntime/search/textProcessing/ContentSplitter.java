package net.networkdowntime.search.textProcessing;

/**
 * Content Splitter uses regex processing to split apart strings into indexable words.
 * The default splitting regex includes whitespaces, various punctuation marks, parenthesis,
 * brackets, braces, or angle brackets.  See constructor implementation for exact details
 * 
 * @author rwiles
 *
 */
public class ContentSplitter {

	private String splittingRegex;

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
	}

	/**
	 * Splits the content using the splitting regular expression
	 * 
	 * @param content String containing the string to split into words
	 * @return String array containing the individual split into words 
	 */
	public String[] splitContent(String content) {
		return content.split(splittingRegex);
	}

}
