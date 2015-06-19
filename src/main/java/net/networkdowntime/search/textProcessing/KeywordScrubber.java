package net.networkdowntime.search.textProcessing;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class KeywordScrubber {

	static TreeSet<String>					doNotIndexWords		= new TreeSet<String>();

	static { // only common three letter words. we are ignoring two letter words elsewhere
		doNotIndexWords.add("and");
		doNotIndexWords.add("the");
	}

	int minKeywordLength = 2;
	int maxKeywordLength = 40;
	
	public KeywordScrubber() {
		
	}
	
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
}
