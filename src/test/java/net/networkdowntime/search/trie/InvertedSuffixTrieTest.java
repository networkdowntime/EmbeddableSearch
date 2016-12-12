package net.networkdowntime.search.trie;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import net.networkdowntime.search.trie.InvertedSuffixTrie;

import org.junit.BeforeClass;
import org.junit.Test;

public class InvertedSuffixTrieTest {
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
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie();
		prefixTrie.add("qwerty");
	}

	@Test
	public void testPartialTrieAddOneWord() {
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie(false);
		prefixTrie.add("qwerty");
	}

	@Test
	public void testAddTwoWords() {
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie();
		prefixTrie.add("qwerty");
		prefixTrie.add("wombat");
	}

	@Test
	public void testPartialTrieAddTwoWords() {
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie(false);
		prefixTrie.add("qwerty");
		prefixTrie.add("wombat");
	}

	@Test
	public void testGetCompletionsNoMatch() {
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie();
		prefixTrie.add("cacao");
		Set<CostString> completions = prefixTrie.getCompletions(new CostString("not_there"), 50, true);
		assertTrue(completions != null);
		assertEquals(0, completions.size());
	}

	@Test
	public void testPartialTrieGetCompletionsNoMatch() {
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie(false);
		prefixTrie.add("cacao");
		Set<CostString> completions = prefixTrie.getCompletions(new CostString("not_there"), 50, true);
		assertTrue(completions != null);
		assertEquals(0, completions.size());
	}

	@Test
	public void testGetCompletions2() {
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie();
		prefixTrie.add("cacao");
		Set<CostString> completions = prefixTrie.getCompletions(new CostString("o"), 50, true);
		assertEquals("cacao", completions.iterator().next().str);
	}

	@Test
	public void testPartialTrieGetCompletions2() {
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie(false);
		prefixTrie.add("cacao");
		Set<CostString> completions = prefixTrie.getCompletions(new CostString("o"), 50, true);
		assertEquals("cacao", completions.iterator().next().str);
	}

	@Test
	public void testGetCompletions3() {
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie();
		prefixTrie.add("cacao");
		prefixTrie.add("ban");
		prefixTrie.add("bad");
		prefixTrie.add("band");
		prefixTrie.add("banana");
		prefixTrie.add("bandy");
		Set<CostString> completions = prefixTrie.getCompletions(new CostString("a"), 50, true);
		assertEquals(5, completions.size());
		assertTrue(completions.contains(new CostString("ca")));
		assertTrue(completions.contains(new CostString("caca")));
		assertTrue(completions.contains(new CostString("ba")));
		assertTrue(completions.contains(new CostString("bana")));
		assertTrue(completions.contains(new CostString("banana")));
	}

	@Test
	public void testPartialTrieGetCompletions3() {
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie(false);
		prefixTrie.add("cacao");
		prefixTrie.add("ban");
		prefixTrie.add("bad");
		prefixTrie.add("band");
		prefixTrie.add("banana");
		prefixTrie.add("bandy");
		Set<CostString> completions = prefixTrie.getCompletions(new CostString("a"), 50, true);
		assertEquals(1, completions.size());
		assertTrue(completions.contains(new CostString("banana")));
	}

	@Test
	public void testExpectedTraceFoo() {
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie();
		prefixTrie.add("foo");
		List<String> actualTrace = prefixTrie.getTrace();
		assertEquals(expectedTraceFoo.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTraceFoo.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieExpectedTraceFoo() {
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie(false);
		prefixTrie.add("foo");
		List<String> actualTrace = prefixTrie.getTrace();
		assertEquals(expectedTracePartialFoo.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTracePartialFoo.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testExpectedTraceFooTwo() {
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie();
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
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie(false);
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
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie();
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
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie(false);
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
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie();
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
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie();
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
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie();
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
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie(false);
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
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie();
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
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie(false);
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
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie();
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
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie(false);
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
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie();
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
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie(false);
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
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie();
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
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie(false);
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
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie();
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
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie(false);
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
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie();
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
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie(false);
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
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie(true);
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
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie(false);
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
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie(true);
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
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie(false);
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
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie(true);
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
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie(false);
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
		InvertedSuffixTrie prefixTrie = new InvertedSuffixTrie(true);
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
		InvertedSuffixTrie suffixTrie = new InvertedSuffixTrie(false);
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
