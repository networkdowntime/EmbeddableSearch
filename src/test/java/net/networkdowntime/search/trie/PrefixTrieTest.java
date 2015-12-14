package net.networkdowntime.search.trie;

import static org.junit.Assert.*;

import java.util.List;

import net.networkdowntime.search.trie.PrefixTrieNode;
import net.networkdowntime.search.trie.Trei;

import org.junit.Before;
import org.junit.Test;

public class PrefixTrieTest {
	Trei prefixTrie;

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
		List<String> completions = prefixTrie.getCompletions("not_there", 50);
		assertTrue(completions != null);
	}

	@Test
	public void testGetCompletions1() {
		List<String> completions = prefixTrie.getCompletions("not_there", 50);
		assertEquals(0, completions.size());
	}

	@Test
	public void testGetCompletions2() {
		List<String> completions = prefixTrie.getCompletions("o", 50);
		assertEquals("cacao", completions.get(0));
	}

	@Test
	public void testGetCompletions3() {
		List<String> completions = prefixTrie.getCompletions("a", 50);
		assertEquals(5, completions.size());
	}

}
