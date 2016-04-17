package net.networkdowntime.search.trie;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import net.networkdowntime.search.trie.PrefixTrie;

import org.junit.BeforeClass;
import org.junit.Test;

public class PrefixTrieTest {
	static List<String> expectedTraceFoo = new ArrayList<String>();
	static List<String> expectedTracePartialFoo = new ArrayList<String>();
	static List<String> expectedTraceFooTwo = new ArrayList<String>();
	static List<String> expectedTracePartialFooTwo = new ArrayList<String>();

	@BeforeClass
	public static void oneTimeSetup() throws Exception {
		expectedTraceFoo.add("Root Node: 2 children [f, o]");
		expectedTraceFoo.add("	Child Node: f: 0 children [￿]");
		expectedTraceFoo.add("	Child Node: o: 2 children [f, o]");
		expectedTraceFoo.add("		Child Node: f: 0 children [￿]");
		expectedTraceFoo.add("		Child Node: o: 1 children [f]");
		expectedTraceFoo.add("			Child Node: f: 0 children [￿, FWE]");

		expectedTracePartialFoo.add("Root Node: 1 children [o]");
		expectedTracePartialFoo.add("	Child Node: o: 1 children [o]");
		expectedTracePartialFoo.add("		Child Node: o: 1 children [f]");
		expectedTracePartialFoo.add("			Child Node: f: 0 children [￿, FWE]");

		expectedTraceFooTwo.add("Root Node: 4 children [o, f, t, w]");
		expectedTraceFooTwo.add("	Child Node: o: 3 children [o, f, w]");
		expectedTraceFooTwo.add("		Child Node: o: 1 children [f]");
		expectedTraceFooTwo.add("			Child Node: f: 0 children [￿, FWE]");
		expectedTraceFooTwo.add("		Child Node: f: 0 children [￿]");
		expectedTraceFooTwo.add("		Child Node: w: 1 children [t]");
		expectedTraceFooTwo.add("			Child Node: t: 0 children [￿, FWE]");
		expectedTraceFooTwo.add("	Child Node: f: 0 children [￿]");
		expectedTraceFooTwo.add("	Child Node: t: 0 children [￿]");
		expectedTraceFooTwo.add("	Child Node: w: 1 children [t]");
		expectedTraceFooTwo.add("		Child Node: t: 0 children [￿]");

		expectedTracePartialFooTwo.add("Root Node: 1 children [o]");
		expectedTracePartialFooTwo.add("	Child Node: o: 2 children [w, o]");
		expectedTracePartialFooTwo.add("		Child Node: w: 1 children [t]");
		expectedTracePartialFooTwo.add("			Child Node: t: 0 children [￿, FWE]");
		expectedTracePartialFooTwo.add("		Child Node: o: 1 children [f]");
		expectedTracePartialFooTwo.add("			Child Node: f: 0 children [￿, FWE]");

	}

	@Test
	public void testAddOneWord() {
		PrefixTrie prefixTrie = new PrefixTrie();
		prefixTrie.add("qwerty");
	}

	@Test
	public void testPartialTrieAddOneWord() {
		PrefixTrie prefixTrie = new PrefixTrie(false);
		prefixTrie.add("qwerty");
	}

	@Test
	public void testAddTwoWords() {
		PrefixTrie prefixTrie = new PrefixTrie();
		prefixTrie.add("qwerty");
		prefixTrie.add("wombat");
	}

	@Test
	public void testPartialTrieAddTwoWords() {
		PrefixTrie prefixTrie = new PrefixTrie(false);
		prefixTrie.add("qwerty");
		prefixTrie.add("wombat");
	}

	@Test
	public void testGetCompletionsFullMatch() {
		PrefixTrie prefixTrie = new PrefixTrie();
		prefixTrie.add("cacao");
		List<String> completions = prefixTrie.getCompletions("cacao", 50);
		assertEquals("cacao", completions.get(0));
	}

	@Test
	public void testPartialGetCompletionsFullMatch() {
		PrefixTrie prefixTrie = new PrefixTrie(false);
		prefixTrie.add("cacao");
		List<String> completions = prefixTrie.getCompletions("cacao", 50);
		assertEquals("cacao", completions.get(0));
	}

	@Test
	public void testGetCompletionsNoMatch() {
		PrefixTrie prefixTrie = new PrefixTrie();
		prefixTrie.add("cacao");
		List<String> completions = prefixTrie.getCompletions("not_there", 50);
		assertTrue(completions != null);
		assertEquals(0, completions.size());
	}

	@Test
	public void testPartialTrieGetCompletionsNoMatch() {
		PrefixTrie prefixTrie = new PrefixTrie(false);
		prefixTrie.add("cacao");
		List<String> completions = prefixTrie.getCompletions("not_there", 50);
		assertTrue(completions != null);
		assertEquals(0, completions.size());
	}

	@Test
	public void testGetCompletions2() {
		PrefixTrie prefixTrie = new PrefixTrie();
		prefixTrie.add("cacao");
		List<String> completions = prefixTrie.getCompletions("o", 50);
		assertEquals("cacao", completions.get(0));
	}

	@Test
	public void testPartialTrieGetCompletions2() {
		PrefixTrie prefixTrie = new PrefixTrie(false);
		prefixTrie.add("cacao");
		List<String> completions = prefixTrie.getCompletions("o", 50);
		assertEquals("cacao", completions.get(0));
	}

	@Test
	public void testGetCompletions3() {
		PrefixTrie prefixTrie = new PrefixTrie();
		prefixTrie.add("cacao");
		prefixTrie.add("ban");
		prefixTrie.add("bad");
		prefixTrie.add("band");
		prefixTrie.add("banana");
		prefixTrie.add("bandy");
		List<String> completions = prefixTrie.getCompletions("a", 50);
		assertEquals(5, completions.size());
		assertTrue(completions.contains("ca"));
		assertTrue(completions.contains("caca"));
		assertTrue(completions.contains("ba"));
		assertTrue(completions.contains("bana"));
		assertTrue(completions.contains("banana"));
	}

	@Test
	public void testPartialTrieGetCompletions3() {
		PrefixTrie prefixTrie = new PrefixTrie(false);
		prefixTrie.add("cacao");
		prefixTrie.add("ban");
		prefixTrie.add("bad");
		prefixTrie.add("band");
		prefixTrie.add("banana");
		prefixTrie.add("bandy");
		List<String> completions = prefixTrie.getCompletions("a", 50);
		assertEquals(1, completions.size());
		assertTrue(completions.contains("banana"));
	}

	@Test
	public void testExpectedTraceFoo() {
		PrefixTrie prefixTrie = new PrefixTrie();
		prefixTrie.add("foo");
		List<String> actualTrace = prefixTrie.getTrace();
		assertEquals(expectedTraceFoo.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTraceFoo.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieExpectedTraceFoo() {
		PrefixTrie prefixTrie = new PrefixTrie(false);
		prefixTrie.add("foo");
		List<String> actualTrace = prefixTrie.getTrace();
		assertEquals(expectedTracePartialFoo.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTracePartialFoo.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testExpectedTraceFooTwo() {
		PrefixTrie prefixTrie = new PrefixTrie();
		prefixTrie.add("foo");
		prefixTrie.add("two");
		List<String> actualTrace = prefixTrie.getTrace();
		assertEquals(expectedTraceFooTwo.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTraceFooTwo.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieExpectedTraceFooTwo() {
		PrefixTrie prefixTrie = new PrefixTrie(false);
		prefixTrie.add("foo");
		prefixTrie.add("two");
		List<String> actualTrace = prefixTrie.getTrace();
		assertEquals(expectedTracePartialFooTwo.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTracePartialFooTwo.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveSingleNodeOffExistingWord() {
		PrefixTrie prefixTrie = new PrefixTrie();
		prefixTrie.add("foo");
		prefixTrie.add("bfoo");
		prefixTrie.remove("bfoo");
		List<String> actualTrace = prefixTrie.getTrace();
		assertEquals(expectedTraceFoo.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTraceFoo.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveSingleNodeOffExistingWord() {
		PrefixTrie prefixTrie = new PrefixTrie(false);
		prefixTrie.add("foo");
		prefixTrie.add("bfoo");
		prefixTrie.remove("bfoo");
		List<String> actualTrace = prefixTrie.getTrace();
		assertEquals(expectedTracePartialFoo.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTracePartialFoo.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveMultipleNodeOffExistingWord() {
		PrefixTrie prefixTrie = new PrefixTrie();
		prefixTrie.add("foo");
		List<String> expectedTrace = prefixTrie.getTrace();
		prefixTrie.add("barfoo");
		prefixTrie.remove("barfoo");
		List<String> actualTrace = prefixTrie.getTrace();
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveMultipleNodeOffExistingWord() {
		PrefixTrie prefixTrie = new PrefixTrie();
		prefixTrie.add("foo");
		List<String> expectedTrace = prefixTrie.getTrace();
		prefixTrie.add("barfoo");
		prefixTrie.remove("barfoo");
		List<String> actualTrace = prefixTrie.getTrace();
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveSingleCharWordBeginningOfExistingWord() {
		PrefixTrie prefixTrie = new PrefixTrie();
		prefixTrie.add("fod");
		List<String> expectedTrace = prefixTrie.getTrace();
		prefixTrie.add("f");
		prefixTrie.remove("f");
	
		List<String> actualTrace = prefixTrie.getTrace();
		assertEquals(expectedTrace.size(), actualTrace.size());
	
		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveSingleCharWordBeginningOfExistingWord() {
		PrefixTrie prefixTrie = new PrefixTrie(false);
		prefixTrie.add("fod");
		List<String> expectedTrace = prefixTrie.getTrace();
		prefixTrie.add("f");
		prefixTrie.remove("f");
	
		List<String> actualTrace = prefixTrie.getTrace();
		assertEquals(expectedTrace.size(), actualTrace.size());
	
		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveSingleCharWordSubsetOfExistingWord() {
		PrefixTrie prefixTrie = new PrefixTrie();
		prefixTrie.add("fod");
		List<String> expectedTrace = prefixTrie.getTrace();
		prefixTrie.add("o");
		prefixTrie.remove("o");

		List<String> actualTrace = prefixTrie.getTrace();
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveSingleCharWordSubsetOfExistingWord() {
		PrefixTrie prefixTrie = new PrefixTrie(false);
		prefixTrie.add("fod");
		List<String> expectedTrace = prefixTrie.getTrace();
		prefixTrie.add("o");
		prefixTrie.remove("o");

		List<String> actualTrace = prefixTrie.getTrace();
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveSingleCharWordEndOfExistingWord() {
		PrefixTrie prefixTrie = new PrefixTrie();
		prefixTrie.add("fod");
		List<String> expectedTrace = prefixTrie.getTrace();
		prefixTrie.add("d");
		prefixTrie.remove("d");

		List<String> actualTrace = prefixTrie.getTrace();
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveSingleCharWordEndOfExistingWord() {
		PrefixTrie prefixTrie = new PrefixTrie(false);
		prefixTrie.add("fod");
		List<String> expectedTrace = prefixTrie.getTrace();
		prefixTrie.add("d");
		prefixTrie.remove("d");

		List<String> actualTrace = prefixTrie.getTrace();
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveMultiCharWordBeginningOfExistingWord() {
		PrefixTrie prefixTrie = new PrefixTrie();
		prefixTrie.add("food");
		List<String> expectedTrace = prefixTrie.getTrace();
		prefixTrie.add("fo");
		prefixTrie.remove("fo");

		List<String> actualTrace = prefixTrie.getTrace();
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveMultiCharWordBeginningOfExistingWord() {
		PrefixTrie prefixTrie = new PrefixTrie(false);
		prefixTrie.add("food");
		List<String> expectedTrace = prefixTrie.getTrace();
		prefixTrie.add("fo");
		prefixTrie.remove("fo");

		List<String> actualTrace = prefixTrie.getTrace();
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveMultiCharWordSubsetOfExistingWord() {
		PrefixTrie prefixTrie = new PrefixTrie();
		prefixTrie.add("food");
		List<String> expectedTrace = prefixTrie.getTrace();
		prefixTrie.add("oo");
		prefixTrie.remove("oo");

		List<String> actualTrace = prefixTrie.getTrace();
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveMultiCharWordSubsetOfExistingWord() {
		PrefixTrie prefixTrie = new PrefixTrie(false);
		prefixTrie.add("food");
		List<String> expectedTrace = prefixTrie.getTrace();
		prefixTrie.add("oo");
		prefixTrie.remove("oo");

		List<String> actualTrace = prefixTrie.getTrace();
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveMultiCharWordEndOfExistingWord() {
		PrefixTrie prefixTrie = new PrefixTrie();
		prefixTrie.add("food");
		List<String> expectedTrace = prefixTrie.getTrace();
		prefixTrie.add("od");
		prefixTrie.remove("od");

		List<String> actualTrace = prefixTrie.getTrace();
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveMultiCharWordEndOfExistingWord() {
		PrefixTrie prefixTrie = new PrefixTrie(false);
		prefixTrie.add("food");
		List<String> expectedTrace = prefixTrie.getTrace();
		prefixTrie.add("od");
		prefixTrie.remove("od");

		List<String> actualTrace = prefixTrie.getTrace();
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testAddRemove6() {
		PrefixTrie prefixTrie = new PrefixTrie();
		prefixTrie.add("foo");
		prefixTrie.add("two");
		List<String> expectedTrace = prefixTrie.getTrace();
		prefixTrie.add("fo");
		prefixTrie.remove("fo");
		List<String> actualTrace = prefixTrie.getTrace();

		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieAddRemove6() {
		PrefixTrie prefixTrie = new PrefixTrie(false);
		prefixTrie.add("foo");
		prefixTrie.add("two");
		List<String> expectedTrace = prefixTrie.getTrace();
		prefixTrie.add("fo");
		prefixTrie.remove("fo");
		List<String> actualTrace = prefixTrie.getTrace();

		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testFuzzyMatchInsertions() {
		PrefixTrie prefixTrie = new PrefixTrie(true);
		String[] distanceOneInsertion = new String[] { "awhat", "whata", "whaat" }; // beginning, ending, middle

		List<String> words = new ArrayList<String>();
		words.addAll(Arrays.asList(distanceOneInsertion));
		for (String s : words)
			prefixTrie.add(s);

		Set<String> completions = prefixTrie.getFuzzyCompletions("what");
		for (String s : words) {
			assertTrue(completions.contains(s));
		}
	}

	@Test
	public void testPartialFuzzyMatchInsertions() {
		PrefixTrie prefixTrie = new PrefixTrie(false);
		String[] distanceOneInsertion = new String[] { "awhat", "whata", "whaat" }; // beginning, ending, middle

		List<String> words = new ArrayList<String>();
		words.addAll(Arrays.asList(distanceOneInsertion));
		for (String s : words)
			prefixTrie.add(s);

		Set<String> completions = prefixTrie.getFuzzyCompletions("what");
		for (String s : words) {
			assertTrue(completions.contains(s));
		}
	}

	@Test
	public void testFuzzyMatchDeletions() {
		PrefixTrie prefixTrie = new PrefixTrie(true);
		String[] distanceOneDeletion = new String[] { "hat", "wha", "wat" }; // beginning, ending, middle

		List<String> words = new ArrayList<String>();
		words.addAll(Arrays.asList(distanceOneDeletion));
		for (String s : words)
			prefixTrie.add(s);

		Set<String> completions = prefixTrie.getFuzzyCompletions("what");
		for (String s : words) {
			assertTrue(completions.contains(s));
		}
	}

	@Test
	public void testPartialFuzzyMatchDeletions() {
		PrefixTrie prefixTrie = new PrefixTrie(false);
		String[] distanceOneDeletion = new String[] { "hat", "wha", "wat" }; // beginning, ending, middle

		List<String> words = new ArrayList<String>();
		words.addAll(Arrays.asList(distanceOneDeletion));
		for (String s : words)
			prefixTrie.add(s);

		Set<String> completions = prefixTrie.getFuzzyCompletions("what");
		for (String s : words) {
			assertTrue(completions.contains(s));
		}
	}

	@Test
	public void testFuzzyMatchSubstitutions() {
		PrefixTrie prefixTrie = new PrefixTrie(true);
		String[] distanceOneSubstitution = new String[] { "that", "whac", "wbat" }; // beginning, ending, middle

		List<String> words = new ArrayList<String>();
		words.addAll(Arrays.asList(distanceOneSubstitution));
		for (String s : words)
			prefixTrie.add(s);

		Set<String> completions = prefixTrie.getFuzzyCompletions("what");
		for (String s : words) {
			assertTrue(completions.contains(s));
		}
	}

	@Test
	public void testPartialFuzzyMatchSubstitutions() {
		PrefixTrie prefixTrie = new PrefixTrie(false);
		String[] distanceOneSubstitution = new String[] { "that", "whac", "wbat" }; // beginning, ending, middle

		List<String> words = new ArrayList<String>();
		words.addAll(Arrays.asList(distanceOneSubstitution));
		for (String s : words)
			prefixTrie.add(s);

		Set<String> completions = prefixTrie.getFuzzyCompletions("what");
		for (String s : words) {
			assertTrue(completions.contains(s));
		}
	}

	@Test
	public void testFuzzyMatchTranspositions() {
		PrefixTrie prefixTrie = new PrefixTrie(true);
		String[] distanceOneTransposition = new String[] { "hwat", "whta", "waht" }; // beginning, ending, middle

		List<String> words = new ArrayList<String>();
		words.addAll(Arrays.asList(distanceOneTransposition));
		for (String s : words)
			prefixTrie.add(s);

		Set<String> completions = prefixTrie.getFuzzyCompletions("what");
		for (String s : words) {
			assertTrue(completions.contains(s));
		}
	}

	@Test
	public void testPartialFuzzyMatchTranspositions() {
		PrefixTrie suffixTrie = new PrefixTrie(false);
		String[] distanceOneTransposition = new String[] { "hwat", "whta", "waht" }; // beginning, ending, middle

		List<String> words = new ArrayList<String>();
		words.addAll(Arrays.asList(distanceOneTransposition));
		for (String s : words)
			suffixTrie.add(s);

		Set<String> completions = suffixTrie.getFuzzyCompletions("what");
		for (String s : words) {
			assertTrue(completions.contains(s));
		}
	}
}
