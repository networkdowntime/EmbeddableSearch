package net.networkdowntime.search.textProcessing;

/**
 * The HtmlTagTextScrubber is a preprocessor that does scrubbing of multi-word input text removing HTML tags prior to splitting 
 * the content and scrubbing the keywords.
 * HTML tags can contain additional fields that would lose context after splitting:
 * 	i.e. <input type="text"><input/>
 *  
 * @author rwiles
 *
 */
public class HtmlTagTextScrubber implements TextScrubber {

	private String htmlTagRegex = "</?\\w+((\\s+\\w+(\\s*=\\s*(?:\".*?\"|'.*?'|[^'\">\\s]+))?)+\\s*|\\s*)/?>";

	public HtmlTagTextScrubber() {

	}

	@Override
	public String scrubText(String textToScrub) {
		String retval;

		retval = textToScrub.replaceAll(htmlTagRegex, " ");

		return retval;
	}
}
