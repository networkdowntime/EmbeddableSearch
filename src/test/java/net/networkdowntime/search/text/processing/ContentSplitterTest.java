package net.networkdowntime.search.text.processing;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import net.networkdowntime.search.text.processing.ContentSplitter;

public class ContentSplitterTest {

	ContentSplitter splitter = new ContentSplitter();

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		String text = "The! [quick? brown] & fox. (3.1415) Jumps, {over; the:lazy} dog.";
		String[] words = splitter.splitContent(text);

		assertEquals("The", words[0]);
		assertEquals("", words[1]);
		assertEquals("quick", words[2]);
		assertEquals("brown", words[3]);
		assertEquals("", words[4]);
		assertEquals("fox", words[5]);
		assertEquals("", words[6]);
		assertEquals("3.1415", words[7]);
		assertEquals("Jumps", words[8]);
		assertEquals("", words[9]);
		assertEquals("over", words[10]);
		assertEquals("the", words[11]);
		assertEquals("lazy", words[12]);
		assertEquals("dog", words[13]);
	}

}
