package net.networkdowntime.search.textProcessing;

public class ContentSplitter {

	private String splittingRegex; // = "\\.\\s+|\\.$|\\.{3,}|\\s+|,";

	
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
		
		splittingRegex += "|\\s{0,}<\\s{0,}"; // { led or followed by a whitespace
		splittingRegex += "|\\s{0,}>\\s{0,}"; // } led or followed by a whitespace

	}
	
	
	public String getSplittingRegex() {
		return splittingRegex;
	}


	public void setSplittingRegex(String splittingRegex) {
		this.splittingRegex = splittingRegex;
	}


	public String[] splitContent(String content) {
		return content.split(splittingRegex);
	}
	
	public static void main(String... args) {
		testSplit("The! [quick? brown] & fox. (3.1415) Jumps, {over; the:lazy} dog.");
	}
	
	public static void testSplit(String content) {
		ContentSplitter cs = new ContentSplitter();
		
		System.out.println("Splitting: " + content);
		for (String s : cs.splitContent(content)) {
			System.out.println("\t" + s);
		}
	}
}
