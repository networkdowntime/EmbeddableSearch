package net.networkdowntime.search.trie;

import static org.junit.Assert.*;

import java.util.List;

import net.networkdowntime.search.trie.SuffixTrieNode;

import org.junit.Before;
import org.junit.Test;

public class SuffixTrieTest {
	SuffixTrieNode suffixTrie;

	@Before
	public void setUp() throws Exception {
		suffixTrie = new SuffixTrieNode(true);
		
		suffixTrie.add("cacao");
		suffixTrie.add("ban");
		suffixTrie.add("bad");
		suffixTrie.add("band");
		suffixTrie.add("banana");
		suffixTrie.add("bandy");
		
	}

	@Test
	public void testGetCompletionsNotNull() {
		List<String> completions = suffixTrie.getCompletions("not_there", 50);
		assertTrue(completions != null);
	}

	@Test
	public void testGetCompletions1() {
		List<String> completions = suffixTrie.getCompletions("not_there", 50);
		assertEquals(5, completions.size());
	}

	@Test
	public void testGetCompletions2() {
		List<String> completions = suffixTrie.getCompletions("o", 50);
		assertEquals("o", completions.get(0));
	}

	@Test
	public void testGetCompletions3() {
		List<String> completions = suffixTrie.getCompletions("a", 50);
		assertEquals(9, completions.size());
	}

	@Test
	public void testGetCompletions4() {
		List<String> completions = suffixTrie.getCompletions("bananas", 50);
		assertEquals(1, completions.size());
	}

}
