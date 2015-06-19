package net.networkdowntime.search.textProcessing;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class KeywordScrubberTest {

	KeywordScrubber keywordScrubber = new KeywordScrubber();

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		String[] words = new String [] {
			"The",
			"", 
			"quick",
			"brown",
			"", 
			"fox", 
			"", 
			"3.1415", 
			"Jumps", 
			"", 
			"over", 
			"the", 
			"lazy", 
			"dog",
			"14789"
		};

		List<String> keywords = keywordScrubber.scrubKeywords(words);
		
		assertEquals("quick", keywords.get(0));
		assertEquals("brown", keywords.get(1));
		assertEquals("fox", keywords.get(2));
		assertEquals("jumps", keywords.get(3));
		assertEquals("over", keywords.get(4));
		assertEquals("lazy", keywords.get(5));
		assertEquals("dog", keywords.get(6));
		assertEquals("14789", keywords.get(7));

	}

}
