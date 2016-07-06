package net.networkdowntime.search.text.processing;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import net.networkdowntime.search.text.processing.HtmlTagTextScrubber;
import net.networkdowntime.search.text.processing.TextScrubber;

public class TextScrubberTest {

	TextScrubber textScrubber = new HtmlTagTextScrubber();

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testNoChange() {
		String text = "The! [quick? brown] & fox. (3.1415) Jumps, {over; the:lazy} dog.";
		assertEquals(text, textScrubber.scrubText(text));
	}

	@Test
	public void testRemoveHtmlTag() {
		String text = "The! [quick? <div class=\"foo\">brown</div>] & fox. (3.1415) Jumps, {over; the:lazy} dog.";
		assertEquals("The! [quick?  brown ] & fox. (3.1415) Jumps, {over; the:lazy} dog.", textScrubber.scrubText(text));
	}

}
