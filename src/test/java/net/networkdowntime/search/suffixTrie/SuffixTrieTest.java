package net.networkdowntime.search.suffixTrie;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class SuffixTrieTest {
	SuffixTrieNode prefixTrie;

	@Before
	public void setUp() throws Exception {
		prefixTrie = new SuffixTrieNode();
		
		prefixTrie.add("cacao");
		prefixTrie.add("ban");
		prefixTrie.add("bad");
		prefixTrie.add("band");
		prefixTrie.add("banana");
		prefixTrie.add("bandy");
		
	}

	@Test
	public void testGetCompletionsNotNull() {
		List<String> completions = prefixTrie.getCompletions("not_there");
		assertTrue(completions != null);
	}

	@Test
	public void testGetCompletions1() {
		List<String> completions = prefixTrie.getCompletions("not_there");
		assertEquals(5, completions.size());
	}

	@Test
	public void testGetCompletions2() {
		List<String> completions = prefixTrie.getCompletions("o");
		assertEquals("o", completions.get(0));
	}

	@Test
	public void testGetCompletions3() {
		List<String> completions = prefixTrie.getCompletions("a");
		assertEquals(9, completions.size());
	}

	@Test
	public void testGetCompletions4() {
		List<String> completions = prefixTrie.getCompletions("bananas");
		assertEquals(1, completions.size());
	}

}
