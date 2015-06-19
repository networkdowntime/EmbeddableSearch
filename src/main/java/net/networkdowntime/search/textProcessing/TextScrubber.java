package net.networkdowntime.search.textProcessing;

/**
 * The TextScrubber is a preprocessor that does scrubbing of multi-word input text prior to splitting the content and scrubbing the keywords.
 * Removing HTML tags from the input.  HTML tags can contain additional fields that would lose context after splitting:
 * 	i.e. <input type="text"><input/>
 *  
 * @author rwiles
 *
 */
public class TextScrubber {

	private String htmlTagRegex = "</?\\w+((\\s+\\w+(\\s*=\\s*(?:\".*?\"|'.*?'|[^'\">\\s]+))?)+\\s*|\\s*)/?>";

	public TextScrubber() {

	}
	
	public String scrubText(String textToScrub) {
		String retval;
		
		retval = textToScrub.replaceAll(htmlTagRegex, " ");
		
		return retval;
	}
}
