package net.networkdowntime.search.trie;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import net.networkdowntime.search.trie.SuffixTrieNode;

import org.junit.BeforeClass;
import org.junit.Test;

public class SuffixTrieTest {
	SuffixTrieNode suffixTrie;

	static List<String> expectedTraceOof = new ArrayList<String>();
	static List<String> expectedTracePartialOof = new ArrayList<String>();
	static List<String> expectedTraceOofOwt = new ArrayList<String>();
	static List<String> expectedTracePartialOofOwt = new ArrayList<String>();

	@BeforeClass
	public static void oneTimeSetup() throws Exception {
		expectedTraceOof.add("Root Node: 2 children [f, o]");
		expectedTraceOof.add("	Child Node: f: 0 children [￿]");
		expectedTraceOof.add("	Child Node: o: 2 children [f, o]");
		expectedTraceOof.add("		Child Node: f: 0 children [￿]");
		expectedTraceOof.add("		Child Node: o: 1 children [f]");
		expectedTraceOof.add("			Child Node: f: 0 children [￿, FWE]");

		expectedTracePartialOof.add("Root Node: 1 children [o]");
		expectedTracePartialOof.add("	Child Node: o: 1 children [o]");
		expectedTracePartialOof.add("		Child Node: o: 1 children [f]");
		expectedTracePartialOof.add("			Child Node: f: 0 children [￿, FWE]");

		expectedTraceOofOwt.add("Root Node: 4 children [o, f, t, w]");
		expectedTraceOofOwt.add("	Child Node: o: 3 children [o, f, w]");
		expectedTraceOofOwt.add("		Child Node: o: 1 children [f]");
		expectedTraceOofOwt.add("			Child Node: f: 0 children [￿, FWE]");
		expectedTraceOofOwt.add("		Child Node: f: 0 children [￿]");
		expectedTraceOofOwt.add("		Child Node: w: 1 children [t]");
		expectedTraceOofOwt.add("			Child Node: t: 0 children [￿, FWE]");
		expectedTraceOofOwt.add("	Child Node: f: 0 children [￿]");
		expectedTraceOofOwt.add("	Child Node: t: 0 children [￿]");
		expectedTraceOofOwt.add("	Child Node: w: 1 children [t]");
		expectedTraceOofOwt.add("		Child Node: t: 0 children [￿]");

		expectedTracePartialOofOwt.add("Root Node: 1 children [o]");
		expectedTracePartialOofOwt.add("	Child Node: o: 2 children [w, o]");
		expectedTracePartialOofOwt.add("		Child Node: w: 1 children [t]");
		expectedTracePartialOofOwt.add("			Child Node: t: 0 children [￿, FWE]");
		expectedTracePartialOofOwt.add("		Child Node: o: 1 children [f]");
		expectedTracePartialOofOwt.add("			Child Node: f: 0 children [￿, FWE]");

	}

	@Test
	public void testAddOneWord() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode();
		SuffixTrieNode.add(suffixTrie, "qwerty");
	}

	@Test
	public void testPartialTrieAddOneWord() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode(false);
		SuffixTrieNode.add(suffixTrie, "qwerty");
	}

	@Test
	public void testAddTwoWords() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode();
		SuffixTrieNode.add(suffixTrie, "qwerty");
		SuffixTrieNode.add(suffixTrie, "wombat");
	}

	@Test
	public void testPartialTrieAddTwoWords() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode(false);
		SuffixTrieNode.add(suffixTrie, "qwerty");
		SuffixTrieNode.add(suffixTrie, "wombat");
	}

	@Test
	public void testGetCompletionsNoMatch() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode();
		SuffixTrieNode.add(suffixTrie, "cacao");
		List<String> completions = SuffixTrieNode.getCompletions(suffixTrie, "not_there", 50);
		assertTrue(completions != null);
		assertEquals(0, completions.size());
	}

	@Test
	public void testPartialTrieGetCompletionsNoMatch() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode(false);
		SuffixTrieNode.add(suffixTrie, "cacao");
		List<String> completions = SuffixTrieNode.getCompletions(suffixTrie, "not_there", 50);
		assertTrue(completions != null);
		assertEquals(0, completions.size());
	}

	@Test
	public void testGetCompletions2() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode();
		SuffixTrieNode.add(suffixTrie, "oacac");
		List<String> completions = SuffixTrieNode.getCompletions(suffixTrie, "o", 50);
		assertEquals("oacac", completions.get(0));
	}

	@Test
	public void testPartialTrieGetCompletions2() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode(false);
		SuffixTrieNode.add(suffixTrie, "oacac");
		List<String> completions = SuffixTrieNode.getCompletions(suffixTrie, "o", 50);
		assertEquals("oacac", completions.get(0));
	}

	@Test
	public void testGetCompletions3() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode();
		SuffixTrieNode.add(suffixTrie, "oacac");
		SuffixTrieNode.add(suffixTrie, "nab");
		SuffixTrieNode.add(suffixTrie, "dab");
		SuffixTrieNode.add(suffixTrie, "dnab");
		SuffixTrieNode.add(suffixTrie, "ananab");
		SuffixTrieNode.add(suffixTrie, "ydnab");
		List<String> completions = SuffixTrieNode.getCompletions(suffixTrie, "a", 50);
		assertEquals(5, completions.size());
		assertTrue(completions.contains("ac"));
		assertTrue(completions.contains("acac"));
		assertTrue(completions.contains("ab"));
		assertTrue(completions.contains("anab"));
		assertTrue(completions.contains("ananab"));
	}

	@Test
	public void testPartialTrieGetCompletions3() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode(false);
		SuffixTrieNode.add(suffixTrie, "oacac");
		SuffixTrieNode.add(suffixTrie, "nab");
		SuffixTrieNode.add(suffixTrie, "dab");
		SuffixTrieNode.add(suffixTrie, "dnab");
		SuffixTrieNode.add(suffixTrie, "ananab");
		SuffixTrieNode.add(suffixTrie, "ydnab");
		List<String> completions = SuffixTrieNode.getCompletions(suffixTrie, "a", 50);
		assertEquals(1, completions.size());
		assertTrue(completions.contains("ananab"));
	}

	@Test
	public void testExpectedTraceFoo() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode();
		SuffixTrieNode.add(suffixTrie, "oof");
		List<String> actualTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		assertEquals(expectedTraceOof.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTraceOof.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieExpectedTraceFoo() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode(false);
		SuffixTrieNode.add(suffixTrie, "oof");
		List<String> actualTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		assertEquals(expectedTracePartialOof.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTracePartialOof.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testExpectedTraceFooTwo() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode();
		SuffixTrieNode.add(suffixTrie, "oof");
		SuffixTrieNode.add(suffixTrie, "owt");
		List<String> actualTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		assertEquals(expectedTraceOofOwt.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTraceOofOwt.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieExpectedTraceFooTwo() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode(false);
		SuffixTrieNode.add(suffixTrie, "oof");
		SuffixTrieNode.add(suffixTrie, "owt");
		List<String> actualTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		assertEquals(expectedTracePartialOofOwt.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTracePartialOofOwt.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveSingleNodeOffExistingWord() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode();
		SuffixTrieNode.add(suffixTrie, "oof");
		SuffixTrieNode.add(suffixTrie, "oofb");
		SuffixTrieNode.remove(suffixTrie, "oofb");
		List<String> actualTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		assertEquals(expectedTraceOof.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTraceOof.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveSingleNodeOffExistingWord() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode(false);
		SuffixTrieNode.add(suffixTrie, "oof");
		SuffixTrieNode.add(suffixTrie, "oofb");
		SuffixTrieNode.remove(suffixTrie, "oofb");
		List<String> actualTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		assertEquals(expectedTracePartialOof.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTracePartialOof.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveMultipleNodeOffExistingWord() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode();
		SuffixTrieNode.add(suffixTrie, "oof");
		List<String> expectedTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		SuffixTrieNode.add(suffixTrie, "oofrab");
		SuffixTrieNode.remove(suffixTrie, "oofrab");
		List<String> actualTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveMultipleNodeOffExistingWord() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode();
		SuffixTrieNode.add(suffixTrie, "oof");
		List<String> expectedTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		SuffixTrieNode.add(suffixTrie, "oofrab");
		SuffixTrieNode.remove(suffixTrie, "oofrab");
		List<String> actualTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveSingleCharWordBeginningOfExistingWord() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode();
		SuffixTrieNode.add(suffixTrie, "dof");
		List<String> expectedTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		SuffixTrieNode.add(suffixTrie, "f");
		SuffixTrieNode.remove(suffixTrie, "f");

		List<String> actualTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveSingleCharWordBeginningOfExistingWord() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode(false);
		SuffixTrieNode.add(suffixTrie, "dof");
		List<String> expectedTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		SuffixTrieNode.add(suffixTrie, "f");
		SuffixTrieNode.remove(suffixTrie, "f");

		List<String> actualTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveSingleCharWordSubsetOfExistingWord() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode();
		SuffixTrieNode.add(suffixTrie, "dof");
		List<String> expectedTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		SuffixTrieNode.add(suffixTrie, "o");
		SuffixTrieNode.remove(suffixTrie, "o");

		List<String> actualTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveSingleCharWordSubsetOfExistingWord() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode(false);
		SuffixTrieNode.add(suffixTrie, "dof");
		List<String> expectedTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		SuffixTrieNode.add(suffixTrie, "o");
		SuffixTrieNode.remove(suffixTrie, "o");

		List<String> actualTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveSingleCharWordEndOfExistingWord() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode();
		SuffixTrieNode.add(suffixTrie, "dof");
		List<String> expectedTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		SuffixTrieNode.add(suffixTrie, "d");
		SuffixTrieNode.remove(suffixTrie, "d");

		List<String> actualTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveSingleCharWordEndOfExistingWord() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode(false);
		SuffixTrieNode.add(suffixTrie, "dof");
		List<String> expectedTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		SuffixTrieNode.add(suffixTrie, "d");
		SuffixTrieNode.remove(suffixTrie, "d");

		List<String> actualTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveMultiCharWordBeginningOfExistingWord() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode();
		SuffixTrieNode.add(suffixTrie, "doof");
		List<String> expectedTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		SuffixTrieNode.add(suffixTrie, "of");
		SuffixTrieNode.remove(suffixTrie, "of");

		List<String> actualTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveMultiCharWordBeginningOfExistingWord() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode(false);
		SuffixTrieNode.add(suffixTrie, "doof");
		List<String> expectedTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		SuffixTrieNode.add(suffixTrie, "of");
		SuffixTrieNode.remove(suffixTrie, "of");

		List<String> actualTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveMultiCharWordSubsetOfExistingWord() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode();
		SuffixTrieNode.add(suffixTrie, "doof");
		List<String> expectedTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		SuffixTrieNode.add(suffixTrie, "oo");
		SuffixTrieNode.remove(suffixTrie, "oo");

		List<String> actualTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveMultiCharWordSubsetOfExistingWord() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode(false);
		SuffixTrieNode.add(suffixTrie, "doof");
		List<String> expectedTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		SuffixTrieNode.add(suffixTrie, "oo");
		SuffixTrieNode.remove(suffixTrie, "oo");

		List<String> actualTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveMultiCharWordEndOfExistingWord() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode();
		SuffixTrieNode.add(suffixTrie, "doof");
		List<String> expectedTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		SuffixTrieNode.add(suffixTrie, "do");
		SuffixTrieNode.remove(suffixTrie, "do");

		List<String> actualTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveMultiCharWordEndOfExistingWord() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode(false);
		SuffixTrieNode.add(suffixTrie, "doof");
		List<String> expectedTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		SuffixTrieNode.add(suffixTrie, "do");
		SuffixTrieNode.remove(suffixTrie, "do");

		List<String> actualTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testAddRemove6() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode();
		SuffixTrieNode.add(suffixTrie, "oof");
		SuffixTrieNode.add(suffixTrie, "owt");
		List<String> expectedTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		SuffixTrieNode.add(suffixTrie, "of");
		SuffixTrieNode.remove(suffixTrie, "of");
		List<String> actualTrace = SuffixTrieNode.getTrace(suffixTrie, 0);

		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieAddRemove6() {
		SuffixTrieNode suffixTrie = new SuffixTrieNode(false);
		SuffixTrieNode.add(suffixTrie, "oof");
		SuffixTrieNode.add(suffixTrie, "owt");
		List<String> expectedTrace = SuffixTrieNode.getTrace(suffixTrie, 0);
		SuffixTrieNode.add(suffixTrie, "of");
		SuffixTrieNode.remove(suffixTrie, "of");
		List<String> actualTrace = SuffixTrieNode.getTrace(suffixTrie, 0);

		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}
}
