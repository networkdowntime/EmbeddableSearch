package net.networkdowntime.search.engine;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class AutocompleteTest {

	Autocomplete autocomplete = new Autocomplete();

	@Before
	public void setUp() throws Exception {
		autocomplete.add("cacao");
		autocomplete.add("cacao");
		autocomplete.add("cacao");
		autocomplete.add("cacao");

		autocomplete.add("ban");

		autocomplete.add("bad");
		autocomplete.add("bad");

		autocomplete.add("band");
		autocomplete.add("band");
		autocomplete.add("band");
		autocomplete.add("band");
		autocomplete.add("band");
		autocomplete.add("band");

		autocomplete.add("banana");
		autocomplete.add("banana");
		autocomplete.add("banana");
		autocomplete.add("banana");
		autocomplete.add("banana");

		autocomplete.add("bandy");
		autocomplete.add("bandy");
		autocomplete.add("bandy");

		autocomplete.add("The quick brown fox jumps over the lazy dog");
	}

	@Test
	public void testUnigramHistogramOrderedCommpletions() {
		List<String> orderedList = new ArrayList<String>(autocomplete.getCompletions("a", false, 10));

		assertEquals("band", orderedList.get(0));
		assertEquals("banana", orderedList.get(1));
		assertEquals("cacao", orderedList.get(2));
		assertEquals("bandy", orderedList.get(3));
		assertEquals("bad", orderedList.get(4));
		assertEquals("ban", orderedList.get(5));
		assertEquals("lazy", orderedList.get(6));
	}

	@Test
	public void testSearchResults() {
		List<String> orderedList = new ArrayList<String>(autocomplete.getCompletions("a", false, 10));
		assertEquals("band", orderedList.get(0));
		assertEquals("banana", orderedList.get(1));
		assertEquals("cacao", orderedList.get(2));
		assertEquals("bandy", orderedList.get(3));
		assertEquals("bad", orderedList.get(4));
		assertEquals("ban", orderedList.get(5));
		assertEquals("lazy", orderedList.get(6));
	}

	@Test
	public void testDigramHistogramCompletionFirstWordExcactMatch() {
		List<String> orderedList = new ArrayList<String>(autocomplete.getCompletions("quick b", false, 10));
		assertEquals("quick brown", orderedList.get(0));
	}

	@Test
	public void testDigramHistogramCompletionFirstWordFuzzyMatch() {
		List<String> orderedList = new ArrayList<String>(autocomplete.getCompletions("uic b", true, 10));
		assertEquals("quick brown", orderedList.get(0));
	}

	@Test
	public void testDigramHistogramCompletionFirstWordFuzzyMatch2() {
		List<String> orderedList = new ArrayList<String>(autocomplete.getCompletions("uic browns", true, 10));
		assertEquals("quick brown", orderedList.get(0));
	}

	@Test
	public void testDigramHistogramCompletionFirstWordFuzzyMatch3() {
		List<String> orderedList = new ArrayList<String>(autocomplete.getCompletions("uic bro", true, 10));
		assertEquals(1, orderedList.size());
		assertEquals("quick brown", orderedList.get(0));
	}

	@Test
	public void testDigramHistogramCompletionWordReversal() {
		List<String> orderedList = new ArrayList<String>(autocomplete.getCompletions("brown uic", false, 10));
		assertEquals("quick brown", orderedList.get(0));
	}

	// Leaving this commented out right now because it takes a while to run
	// Using largish numbers as strings to simulate a deterministic dataset for capacity testing
	@Test
	public void testCapacity() {
		long numOfElementsToTest = 5000000; // max limit where it runs out of memory is 7,168,000 on my 16GB MacBook Pro
		long baseNumber = 100000000; // yields a 9 digit string
		long tenPercent = Math.round(numOfElementsToTest / 10.0d);
		System.out.print("Test Autocomplete Capacity, Filling: ");
		for (long i = 0; i < numOfElementsToTest; i++) {
			autocomplete.add((baseNumber + i) + "");
			if (i % tenPercent == 0) {
				int percentage = (int) (100 * ((double) i / numOfElementsToTest));
				if (percentage != 0)
					System.out.print(",");
				System.out.print(" " + percentage + "%");
			}
		}
	}
}
