package net.networkdowntime.search.textProcessing;

/**
 * Interface for implementing various text scrubbers that can clean an input string.  An example is the HtmlTagTextScrubber which will strip the HTML tag elements 
 * from a string.
 * 
 * @author rwiles
 *
 */
public interface TextScrubber {

	/**
	 * Runs the scrubText on the input returning a sanitized string.
	 * @param textToScrub Text to sanitize
	 * @return Sanitized text
	 */
	public abstract String scrubText(String textToScrub);

}