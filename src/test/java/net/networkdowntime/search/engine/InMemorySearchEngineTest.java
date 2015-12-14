package net.networkdowntime.search.engine;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class InMemorySearchEngineTest {

	SearchEngine se = new InMemorySearchEngine();

	@Before
	public void setUp() throws Exception {
		se.add(null, 2l, "cacao");
		se.add(null, 2l, "cacao");
		se.add(null, 2l, "cacao");
		se.add(null, 2l, "cacao");
		
		se.add(null, 5l, "ban");

		se.add(null, 4l, "bad");
		se.add(null, 4l, "bad");

		se.add(null, 0l, "band");
		se.add(null, 0l, "band");
		se.add(null, 0l, "band");
		se.add(null, 0l, "band");
		se.add(null, 0l, "band");
		se.add(null, 0l, "band");

		se.add(null, 1l, "banana");
		se.add(null, 1l, "banana");
		se.add(null, 1l, "banana");
		se.add(null, 1l, "banana");
		se.add(null, 1l, "banana");

		se.add(null, 3l, "bandy");
		se.add(null, 3l, "bandy");
		se.add(null, 3l, "bandy");
		
		se.add(null, 6l, "The quick brown fox jumps over the lazy dog");
	}

	@Test
	public void testUnigramHistogramOrderedCommpletions() {
		List<String> orderedList = se.getCompletions("a", false, 10);

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
		List<String> orderedList = se.getCompletions("a", false, 10);
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
		List<String> orderedList = se.getCompletions("quick b", false, 10);
		assertEquals("quick brown", orderedList.get(0));
	}

	@Test
	public void testDigramHistogramCompletionFirstWordFuzzyMatch() {
		List<String> orderedList = se.getCompletions("uic b", true, 10);
		assertEquals("quick brown", orderedList.get(0));
	}

	@Test
	public void testDigramHistogramCompletionFirstWordFuzzyMatch2() {
		List<String> orderedList = se.getCompletions("uic browns", true, 10);
		assertEquals("quick brown", orderedList.get(0));
	}

	@Test
	public void testDigramHistogramCompletionFirstWordFuzzyMatch3() {
		List<String> orderedList = se.getCompletions("uic bro", true, 10);
		assertEquals(1, orderedList.size());
		assertEquals("quick brown", orderedList.get(0));
	}

	@Test
	public void testDigramHistogramCompletionWordReversal() {
		List<String> orderedList = se.getCompletions("brown uic", false, 10);
		assertEquals("quick brown", orderedList.get(0));
	}

	@Test
	public void testSearchSingleElementMatch() {
		se.add(null, 1l, "singleElement");
		Set<Long> results = se.search("singleElement", 1);
		assertEquals(1, results.size());
		for (long result : results) {
			assertEquals(1, result);
		}
	}

	@Test
	public void testSearchMultiResultMatch1() {
		se.add(null, 1l, "multiResult1");
		se.add(null, 1l, "multiResult1");
		Set<Long> results = se.search("multiResult1", 1);
		assertEquals(1, results.size());
		for (long result : results) {
			assertEquals(1, result);
		}
	}

	@Test
	public void testSearchMultiResultMatchOrdering() {
		se.add(null, 1l, "multiResultMatchOrdering1");
		se.add(null, 1l, "multiResultMatchOrdering1");
		se.add(null, 2l, "multiResultMatchOrdering1");
		se.add(null, 2l, "multiResultMatchOrdering1");
		se.add(null, 2l, "multiResultMatchOrdering1");
		se.add(null, 3l, "multiResultMatchOrdering1");
		Set<Long> results = se.search("multiResultMatchOrdering1", 3);
		assertEquals(3, results.size());
		
		Long[] arr = results.toArray(new Long[0]);
		assertEquals(2, (long) arr[0]);
		assertEquals(1, (long) arr[1]);
		assertEquals(3, (long) arr[2]);
	}

	// Leaving this commented out right now because it takes a while to run
	// Using largish numbers as strings to simulate a deterministic dataset for capacity testing
//	@Test
//	public void testCapacity() {
//		long numOfElementsToTest = 5000000; // max limit where it runs out of memory is 7,168,000 on my 16GB MacBook Pro
//		long baseNumber = 100000000; // yields a 9 digit string
//		long tenPercent = Math.round(numOfElementsToTest / 10.0d);
//		System.out.print("Test InMemorySearchEngine Capacity, Filling: ");
//		for (long i=0; i < numOfElementsToTest; i++) { 
//			se.add(null, i, (baseNumber + i) + "");
//			if (i % tenPercent == 0) {
//				int percentage = (int) (100 * ((double) i / numOfElementsToTest));
//				if (percentage != 0)
//					System.out.print(",");
//				System.out.print(" " + percentage + "%");
//			}
//		}
//		
//		System.out.print("\rSearching for test strings: ");
//		for (long i= 0; i < numOfElementsToTest; i++) { 
//			boolean foundResult = false;
//			for (Long result : se.search((baseNumber + i) + "", 10)) {
////				System.out.println(result);
//				foundResult |= result == i;
//			}
//			assertTrue(foundResult);
//			if (i % tenPercent == 0) {
//				int percentage = (int) (100 * ((double) i / numOfElementsToTest));
//				if (percentage != 0)
//					System.out.print(",");
//				System.out.print(" " + percentage + "%");
//			}
//		}
//		
//	}
}
