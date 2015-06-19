package net.networkdowntime.search.prefixTrie;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class PrefixTrieTest {
	PrefixTrieNode prefixTrie;

	@Before
	public void setUp() throws Exception {
		prefixTrie = new PrefixTrieNode();
		
		prefixTrie.add("cacao");
		prefixTrie.add("ban");
		prefixTrie.add("bad");
		prefixTrie.add("band");
		prefixTrie.add("banana");
		prefixTrie.add("bandy");
		
	}

	@Test
	public void testAdd() {
		prefixTrie.add("qwerty");
	}

	@Test
	public void testGetCompletionsNotNull() {
		List<String> completions = prefixTrie.getCompletions("not_there");
		assertTrue(completions != null);
	}

	@Test
	public void testGetCompletions1() {
		List<String> completions = prefixTrie.getCompletions("not_there");
		assertEquals(0, completions.size());
	}

	@Test
	public void testGetCompletions2() {
		List<String> completions = prefixTrie.getCompletions("o");
		assertEquals("cacao", completions.get(0));
	}

	@Test
	public void testGetCompletions3() {
		List<String> completions = prefixTrie.getCompletions("a");
		assertEquals(5, completions.size());
	}

}
