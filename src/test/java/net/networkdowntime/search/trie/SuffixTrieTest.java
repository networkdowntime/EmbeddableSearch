package net.networkdowntime.search.trie;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import net.networkdowntime.search.trie.SuffixTrie;

import org.junit.BeforeClass;
import org.junit.Test;

public class SuffixTrieTest {
	SuffixTrie suffixTrie;

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
		SuffixTrie suffixTrie = new SuffixTrie();
		suffixTrie.add("qwerty");
	}

	@Test
	public void testPartialTrieAddOneWord() {
		SuffixTrie suffixTrie = new SuffixTrie(false);
		suffixTrie.add("qwerty");
	}

	@Test
	public void testAddTwoWords() {
		SuffixTrie suffixTrie = new SuffixTrie();
		suffixTrie.add("qwerty");
		suffixTrie.add("wombat");
	}

	@Test
	public void testPartialTrieAddTwoWords() {
		SuffixTrie suffixTrie = new SuffixTrie(false);
		suffixTrie.add("qwerty");
		suffixTrie.add("wombat");
	}

	@Test
	public void testGetCompletionsNoMatch() {
		SuffixTrie suffixTrie = new SuffixTrie();
		suffixTrie.add("cacao");
		List<String> completions = suffixTrie.getCompletions("not_there", 50);
		assertTrue(completions != null);
		assertEquals(0, completions.size());
	}

	@Test
	public void testPartialTrieGetCompletionsNoMatch() {
		SuffixTrie suffixTrie = new SuffixTrie(false);
		suffixTrie.add("cacao");
		List<String> completions = suffixTrie.getCompletions("not_there", 50);
		assertTrue(completions != null);
		assertEquals(0, completions.size());
	}

	@Test
	public void testGetCompletions2() {
		SuffixTrie suffixTrie = new SuffixTrie();
		suffixTrie.add("oacac");
		List<String> completions = suffixTrie.getCompletions("o", 50);
		assertEquals("oacac", completions.get(0));
	}

	@Test
	public void testPartialTrieGetCompletions2() {
		SuffixTrie suffixTrie = new SuffixTrie(false);
		suffixTrie.add("oacac");
		List<String> completions = suffixTrie.getCompletions("o", 50);
		assertEquals("oacac", completions.get(0));
	}

	@Test
	public void testGetCompletions3() {
		SuffixTrie suffixTrie = new SuffixTrie();
		suffixTrie.add("oacac");
		suffixTrie.add("nab");
		suffixTrie.add("dab");
		suffixTrie.add("dnab");
		suffixTrie.add("ananab");
		suffixTrie.add("ydnab");
		List<String> completions = suffixTrie.getCompletions("a", 50);
		assertEquals(5, completions.size());
		assertTrue(completions.contains("ac"));
		assertTrue(completions.contains("acac"));
		assertTrue(completions.contains("ab"));
		assertTrue(completions.contains("anab"));
		assertTrue(completions.contains("ananab"));
	}

	@Test
	public void testPartialTrieGetCompletions3() {
		SuffixTrie suffixTrie = new SuffixTrie(false);
		suffixTrie.add("oacac");
		suffixTrie.add("nab");
		suffixTrie.add("dab");
		suffixTrie.add("dnab");
		suffixTrie.add("ananab");
		suffixTrie.add("ydnab");
		List<String> completions = suffixTrie.getCompletions("a", 50);
		assertEquals(1, completions.size());
		assertTrue(completions.contains("ananab"));
	}

	@Test
	public void testExpectedTraceFoo() {
		SuffixTrie suffixTrie = new SuffixTrie();
		suffixTrie.add("oof");
		List<String> actualTrace = suffixTrie.getTrace();
		assertEquals(expectedTraceOof.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTraceOof.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieExpectedTraceFoo() {
		SuffixTrie suffixTrie = new SuffixTrie(false);
		suffixTrie.add("oof");
		List<String> actualTrace = suffixTrie.getTrace();
		assertEquals(expectedTracePartialOof.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTracePartialOof.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testExpectedTraceFooTwo() {
		SuffixTrie suffixTrie = new SuffixTrie();
		suffixTrie.add("oof");
		suffixTrie.add("owt");
		List<String> actualTrace = suffixTrie.getTrace();
		assertEquals(expectedTraceOofOwt.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTraceOofOwt.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieExpectedTraceFooTwo() {
		SuffixTrie suffixTrie = new SuffixTrie(false);
		suffixTrie.add("oof");
		suffixTrie.add("owt");
		List<String> actualTrace = suffixTrie.getTrace();
		assertEquals(expectedTracePartialOofOwt.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTracePartialOofOwt.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveSingleNodeOffExistingWord() {
		SuffixTrie suffixTrie = new SuffixTrie();
		suffixTrie.add("oof");
		suffixTrie.add("oofb");
		suffixTrie.remove("oofb");
		List<String> actualTrace = suffixTrie.getTrace();
		assertEquals(expectedTraceOof.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTraceOof.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveSingleNodeOffExistingWord() {
		SuffixTrie suffixTrie = new SuffixTrie(false);
		suffixTrie.add("oof");
		suffixTrie.add("oofb");
		suffixTrie.remove("oofb");
		List<String> actualTrace = suffixTrie.getTrace();
		assertEquals(expectedTracePartialOof.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTracePartialOof.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveMultipleNodeOffExistingWord() {
		SuffixTrie suffixTrie = new SuffixTrie();
		suffixTrie.add("oof");
		List<String> expectedTrace = suffixTrie.getTrace();
		suffixTrie.add("oofrab");
		suffixTrie.remove("oofrab");
		List<String> actualTrace = suffixTrie.getTrace();
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveMultipleNodeOffExistingWord() {
		SuffixTrie suffixTrie = new SuffixTrie();
		suffixTrie.add("oof");
		List<String> expectedTrace = suffixTrie.getTrace();
		suffixTrie.add("oofrab");
		suffixTrie.remove("oofrab");
		List<String> actualTrace = suffixTrie.getTrace();
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveSingleCharWordBeginningOfExistingWord() {
		SuffixTrie suffixTrie = new SuffixTrie();
		suffixTrie.add("dof");
		List<String> expectedTrace = suffixTrie.getTrace();
		suffixTrie.add("f");
		suffixTrie.remove("f");

		List<String> actualTrace = suffixTrie.getTrace();
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveSingleCharWordBeginningOfExistingWord() {
		SuffixTrie suffixTrie = new SuffixTrie(false);
		suffixTrie.add("dof");
		List<String> expectedTrace = suffixTrie.getTrace();
		suffixTrie.add("f");
		suffixTrie.remove("f");

		List<String> actualTrace = suffixTrie.getTrace();
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveSingleCharWordSubsetOfExistingWord() {
		SuffixTrie suffixTrie = new SuffixTrie();
		suffixTrie.add("dof");
		List<String> expectedTrace = suffixTrie.getTrace();
		suffixTrie.add("o");
		suffixTrie.remove("o");

		List<String> actualTrace = suffixTrie.getTrace();
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveSingleCharWordSubsetOfExistingWord() {
		SuffixTrie suffixTrie = new SuffixTrie(false);
		suffixTrie.add("dof");
		List<String> expectedTrace = suffixTrie.getTrace();
		suffixTrie.add("o");
		suffixTrie.remove("o");

		List<String> actualTrace = suffixTrie.getTrace();
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveSingleCharWordEndOfExistingWord() {
		SuffixTrie suffixTrie = new SuffixTrie();
		suffixTrie.add("dof");
		List<String> expectedTrace = suffixTrie.getTrace();
		suffixTrie.add("d");
		suffixTrie.remove("d");

		List<String> actualTrace = suffixTrie.getTrace();
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveSingleCharWordEndOfExistingWord() {
		SuffixTrie suffixTrie = new SuffixTrie(false);
		suffixTrie.add("dof");
		List<String> expectedTrace = suffixTrie.getTrace();
		suffixTrie.add("d");
		suffixTrie.remove("d");

		List<String> actualTrace = suffixTrie.getTrace();
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveMultiCharWordBeginningOfExistingWord() {
		SuffixTrie suffixTrie = new SuffixTrie();
		suffixTrie.add("doof");
		List<String> expectedTrace = suffixTrie.getTrace();
		suffixTrie.add("of");
		suffixTrie.remove("of");

		List<String> actualTrace = suffixTrie.getTrace();
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveMultiCharWordBeginningOfExistingWord() {
		SuffixTrie suffixTrie = new SuffixTrie(false);
		suffixTrie.add("doof");
		List<String> expectedTrace = suffixTrie.getTrace();
		suffixTrie.add("of");
		suffixTrie.remove("of");

		List<String> actualTrace = suffixTrie.getTrace();
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveMultiCharWordSubsetOfExistingWord() {
		SuffixTrie suffixTrie = new SuffixTrie();
		suffixTrie.add("doof");
		List<String> expectedTrace = suffixTrie.getTrace();
		suffixTrie.add("oo");
		suffixTrie.remove("oo");

		List<String> actualTrace = suffixTrie.getTrace();
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveMultiCharWordSubsetOfExistingWord() {
		SuffixTrie suffixTrie = new SuffixTrie(false);
		suffixTrie.add("doof");
		List<String> expectedTrace = suffixTrie.getTrace();
		suffixTrie.add("oo");
		suffixTrie.remove("oo");

		List<String> actualTrace = suffixTrie.getTrace();
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testRemoveMultiCharWordEndOfExistingWord() {
		SuffixTrie suffixTrie = new SuffixTrie();
		suffixTrie.add("doof");
		List<String> expectedTrace = suffixTrie.getTrace();
		suffixTrie.add("do");
		suffixTrie.remove("do");

		List<String> actualTrace = suffixTrie.getTrace();
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieRemoveMultiCharWordEndOfExistingWord() {
		SuffixTrie suffixTrie = new SuffixTrie(false);
		suffixTrie.add("doof");
		List<String> expectedTrace = suffixTrie.getTrace();
		suffixTrie.add("do");
		suffixTrie.remove("do");

		List<String> actualTrace = suffixTrie.getTrace();
		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testAddRemove6() {
		SuffixTrie suffixTrie = new SuffixTrie();
		suffixTrie.add("oof");
		suffixTrie.add("owt");
		List<String> expectedTrace = suffixTrie.getTrace();
		suffixTrie.add("of");
		suffixTrie.remove("of");
		List<String> actualTrace = suffixTrie.getTrace();

		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}

	@Test
	public void testPartialTrieAddRemove6() {
		SuffixTrie suffixTrie = new SuffixTrie(false);
		suffixTrie.add("oof");
		suffixTrie.add("owt");
		List<String> expectedTrace = suffixTrie.getTrace();
		suffixTrie.add("of");
		suffixTrie.remove("of");
		List<String> actualTrace = suffixTrie.getTrace();

		assertEquals(expectedTrace.size(), actualTrace.size());

		for (int i = 0; i < actualTrace.size(); i++) {
			assertEquals(expectedTrace.get(i), actualTrace.get(i));
		}
	}
}
