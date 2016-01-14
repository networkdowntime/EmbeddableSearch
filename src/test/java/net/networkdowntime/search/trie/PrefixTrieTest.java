package net.networkdowntime.search.trie;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import net.networkdowntime.search.trie.PrefixTrieNode;

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
		PrefixTrieNode prefixTrie = new PrefixTrieNode();
		PrefixTrieNode.add(prefixTrie, "qwerty");
	}

	@Test
	public void testPartialTrieAddOneWord() {
		PrefixTrieNode prefixTrie = new PrefixTrieNode(false);
		PrefixTrieNode.add(prefixTrie, "qwerty");
	}

	@Test
	public void testAddTwoWords() {
		PrefixTrieNode prefixTrie = new PrefixTrieNode();
		PrefixTrieNode.add(prefixTrie, "qwerty");
		PrefixTrieNode.add(prefixTrie, "wombat");
	}

	@Test
	public void testPartialTrieAddTwoWords() {
		PrefixTrieNode prefixTrie = new PrefixTrieNode(false);
		PrefixTrieNode.add(prefixTrie, "qwerty");
		PrefixTrieNode.add(prefixTrie, "wombat");
	}

	@Test
	public void testGetCompletionsNoMatch() {
		PrefixTrieNode prefixTrie = new PrefixTrieNode();
		PrefixTrieNode.add(prefixTrie, "cacao");
		List<String> completions = PrefixTrieNode.getCompletions(prefixTrie, "not_there", 50);
		assertTrue(completions != null);
		assertEquals(0, completions.size());
	}

	@Test
	public void testPartialTrieGetCompletionsNoMatch() {
		PrefixTrieNode prefixTrie = new PrefixTrieNode(false);
		PrefixTrieNode.add(prefixTrie, "cacao");
		List<String> completions = PrefixTrieNode.getCompletions(prefixTrie, "not_there", 50);
		assertTrue(completions != null);
		assertEquals(0, completions.size());
	}

	@Test
	public void testGetCompletions2() {
		PrefixTrieNode prefixTrie = new PrefixTrieNode();
		PrefixTrieNode.add(prefixTrie, "cacao");
		List<String> completions = PrefixTrieNode.getCompletions(prefixTrie, "o", 50);
		assertEquals("cacao", completions.get(0));
	}

	@Test
	public void testPartialTrieGetCompletions2() {
		PrefixTrieNode prefixTrie = new PrefixTrieNode(false);
		PrefixTrieNode.add(prefixTrie, "cacao");
		List<String> completions = PrefixTrieNode.getCompletions(prefixTrie, "o", 50);
		assertEquals("cacao", completions.get(0));
	}

	@Test
	public void testGetCompletions3() {
		PrefixTrieNode prefixTrie = new PrefixTrieNode();
		PrefixTrieNode.add(prefixTrie, "cacao");
		PrefixTrieNode.add(prefixTrie, "ban");
		PrefixTrieNode.add(prefixTrie, "bad");
		PrefixTrieNode.add(prefixTrie, "band");
		PrefixTrieNode.add(prefixTrie, "banana");
		PrefixTrieNode.add(prefixTrie, "bandy");
		List<String> completions = PrefixTrieNode.getCompletions(prefixTrie, "a", 50);
		assertEquals(5, completions.size());
		assertTrue(completions.contains("ca"));
		assertTrue(completions.contains("caca"));
		assertTrue(completions.contains("ba"));
		assertTrue(completions.contains("bana"));
		assertTrue(completions.contains("banana"));
	}

	@Test
	public void testPartialTrieGetCompletions3() {
		PrefixTrieNode prefixTrie = new PrefixTrieNode(false);
		PrefixTrieNode.add(prefixTrie, "cacao");
		PrefixTrieNode.add(prefixTrie, "ban");
		PrefixTrieNode.add(prefixTrie, "bad");
		PrefixTrieNode.add(prefixTrie, "band");
		PrefixTrieNode.add(prefixTrie, "banana");
		PrefixTrieNode.add(prefixTrie, "bandy");
		List<String> completions = PrefixTrieNode.getCompletions(prefixTrie, "a", 50);
		assertEquals(1, completions.size());
		assertTrue(completions.contains("banana"));
	}

	@Test
	public void testExpectedTraceFoo() {
		PrefixTrieNode prefixTrie = new PrefixTrieNode();
		PrefixTrieNode.add(prefixTrie, "foo");
		List<String> actualTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		assertEquals(expectedTraceFoo.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTraceFoo.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieExpectedTraceFoo() {
		PrefixTrieNode prefixTrie = new PrefixTrieNode(false);
		PrefixTrieNode.add(prefixTrie, "foo");
		List<String> actualTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		assertEquals(expectedTracePartialFoo.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTracePartialFoo.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testExpectedTraceFooTwo() {
		PrefixTrieNode prefixTrie = new PrefixTrieNode();
		PrefixTrieNode.add(prefixTrie, "foo");
		PrefixTrieNode.add(prefixTrie, "two");
		List<String> actualTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		assertEquals(expectedTraceFooTwo.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTraceFooTwo.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieExpectedTraceFooTwo() {
		PrefixTrieNode prefixTrie = new PrefixTrieNode(false);
		PrefixTrieNode.add(prefixTrie, "foo");
		PrefixTrieNode.add(prefixTrie, "two");
		List<String> actualTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		assertEquals(expectedTracePartialFooTwo.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTracePartialFooTwo.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveSingleNodeOffExistingWord() {
		PrefixTrieNode prefixTrie = new PrefixTrieNode();
		PrefixTrieNode.add(prefixTrie, "foo");
		PrefixTrieNode.add(prefixTrie, "bfoo");
		PrefixTrieNode.remove(prefixTrie, "bfoo");
		List<String> actualTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		assertEquals(expectedTraceFoo.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTraceFoo.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveSingleNodeOffExistingWord() {
		PrefixTrieNode prefixTrie = new PrefixTrieNode(false);
		PrefixTrieNode.add(prefixTrie, "foo");
		PrefixTrieNode.add(prefixTrie, "bfoo");
		PrefixTrieNode.remove(prefixTrie, "bfoo");
		List<String> actualTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		assertEquals(expectedTracePartialFoo.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTracePartialFoo.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveMultipleNodeOffExistingWord() {
		PrefixTrieNode prefixTrie = new PrefixTrieNode();
		PrefixTrieNode.add(prefixTrie, "foo");
		List<String> expectedTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		PrefixTrieNode.add(prefixTrie, "barfoo");
		PrefixTrieNode.remove(prefixTrie, "barfoo");
		List<String> actualTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveMultipleNodeOffExistingWord() {
		PrefixTrieNode prefixTrie = new PrefixTrieNode();
		PrefixTrieNode.add(prefixTrie, "foo");
		List<String> expectedTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		PrefixTrieNode.add(prefixTrie, "barfoo");
		PrefixTrieNode.remove(prefixTrie, "barfoo");
		List<String> actualTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveSingleCharWordBeginningOfExistingWord() {
		PrefixTrieNode prefixTrie = new PrefixTrieNode();
		PrefixTrieNode.add(prefixTrie, "fod");
		List<String> expectedTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		PrefixTrieNode.add(prefixTrie, "f");
		PrefixTrieNode.remove(prefixTrie, "f");
	
		List<String> actualTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		assertEquals(expectedTrace.size(), actualTrace.size());
	
		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveSingleCharWordBeginningOfExistingWord() {
		PrefixTrieNode prefixTrie = new PrefixTrieNode(false);
		PrefixTrieNode.add(prefixTrie, "fod");
		List<String> expectedTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		PrefixTrieNode.add(prefixTrie, "f");
		PrefixTrieNode.remove(prefixTrie, "f");
	
		List<String> actualTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		assertEquals(expectedTrace.size(), actualTrace.size());
	
		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveSingleCharWordSubsetOfExistingWord() {
		PrefixTrieNode prefixTrie = new PrefixTrieNode();
		PrefixTrieNode.add(prefixTrie, "fod");
		List<String> expectedTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		PrefixTrieNode.add(prefixTrie, "o");
		PrefixTrieNode.remove(prefixTrie, "o");

		List<String> actualTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveSingleCharWordSubsetOfExistingWord() {
		PrefixTrieNode prefixTrie = new PrefixTrieNode(false);
		PrefixTrieNode.add(prefixTrie, "fod");
		List<String> expectedTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		PrefixTrieNode.add(prefixTrie, "o");
		PrefixTrieNode.remove(prefixTrie, "o");

		List<String> actualTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveSingleCharWordEndOfExistingWord() {
		PrefixTrieNode prefixTrie = new PrefixTrieNode();
		PrefixTrieNode.add(prefixTrie, "fod");
		List<String> expectedTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		PrefixTrieNode.add(prefixTrie, "d");
		PrefixTrieNode.remove(prefixTrie, "d");

		List<String> actualTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveSingleCharWordEndOfExistingWord() {
		PrefixTrieNode prefixTrie = new PrefixTrieNode(false);
		PrefixTrieNode.add(prefixTrie, "fod");
		List<String> expectedTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		PrefixTrieNode.add(prefixTrie, "d");
		PrefixTrieNode.remove(prefixTrie, "d");

		List<String> actualTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveMultiCharWordBeginningOfExistingWord() {
		PrefixTrieNode prefixTrie = new PrefixTrieNode();
		PrefixTrieNode.add(prefixTrie, "food");
		List<String> expectedTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		PrefixTrieNode.add(prefixTrie, "fo");
		PrefixTrieNode.remove(prefixTrie, "fo");

		List<String> actualTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveMultiCharWordBeginningOfExistingWord() {
		PrefixTrieNode prefixTrie = new PrefixTrieNode(false);
		PrefixTrieNode.add(prefixTrie, "food");
		List<String> expectedTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		PrefixTrieNode.add(prefixTrie, "fo");
		PrefixTrieNode.remove(prefixTrie, "fo");

		List<String> actualTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveMultiCharWordSubsetOfExistingWord() {
		PrefixTrieNode prefixTrie = new PrefixTrieNode();
		PrefixTrieNode.add(prefixTrie, "food");
		List<String> expectedTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		PrefixTrieNode.add(prefixTrie, "oo");
		PrefixTrieNode.remove(prefixTrie, "oo");

		List<String> actualTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveMultiCharWordSubsetOfExistingWord() {
		PrefixTrieNode prefixTrie = new PrefixTrieNode(false);
		PrefixTrieNode.add(prefixTrie, "food");
		List<String> expectedTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		PrefixTrieNode.add(prefixTrie, "oo");
		PrefixTrieNode.remove(prefixTrie, "oo");

		List<String> actualTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveMultiCharWordEndOfExistingWord() {
		PrefixTrieNode prefixTrie = new PrefixTrieNode();
		PrefixTrieNode.add(prefixTrie, "food");
		List<String> expectedTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		PrefixTrieNode.add(prefixTrie, "od");
		PrefixTrieNode.remove(prefixTrie, "od");

		List<String> actualTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveMultiCharWordEndOfExistingWord() {
		PrefixTrieNode prefixTrie = new PrefixTrieNode(false);
		PrefixTrieNode.add(prefixTrie, "food");
		List<String> expectedTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		PrefixTrieNode.add(prefixTrie, "od");
		PrefixTrieNode.remove(prefixTrie, "od");

		List<String> actualTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testAddRemove6() {
		PrefixTrieNode prefixTrie = new PrefixTrieNode();
		PrefixTrieNode.add(prefixTrie, "foo");
		PrefixTrieNode.add(prefixTrie, "two");
		List<String> expectedTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		PrefixTrieNode.add(prefixTrie, "fo");
		PrefixTrieNode.remove(prefixTrie, "fo");
		List<String> actualTrace = PrefixTrieNode.getTrace(prefixTrie, 0);

		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieAddRemove6() {
		PrefixTrieNode prefixTrie = new PrefixTrieNode(false);
		PrefixTrieNode.add(prefixTrie, "foo");
		PrefixTrieNode.add(prefixTrie, "two");
		List<String> expectedTrace = PrefixTrieNode.getTrace(prefixTrie, 0);
		PrefixTrieNode.add(prefixTrie, "fo");
		PrefixTrieNode.remove(prefixTrie, "fo");
		List<String> actualTrace = PrefixTrieNode.getTrace(prefixTrie, 0);

		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

}
