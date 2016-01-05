package net.networkdowntime.search.engine;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import net.networkdowntime.search.SearchResult;

public class InMemorySearchEngineTest {

	InMemorySearchEngine searchEngine = new InMemorySearchEngine();

	@Before
	public void setUp() throws Exception {
		searchEngine.add(2l, "cacao");
		searchEngine.add(2l, "cacao");
		searchEngine.add(2l, "cacao");
		searchEngine.add(2l, "cacao");
		
		searchEngine.add(5l, "ban");

		searchEngine.add(4l, "bad");
		searchEngine.add(4l, "bad");

		searchEngine.add(0l, "band");
		searchEngine.add(0l, "band");
		searchEngine.add(0l, "band");
		searchEngine.add(0l, "band");
		searchEngine.add(0l, "band");
		searchEngine.add(0l, "band");

		searchEngine.add(1l, "banana");
		searchEngine.add(1l, "banana");
		searchEngine.add(1l, "banana");
		searchEngine.add(1l, "banana");
		searchEngine.add(1l, "banana");

		searchEngine.add(3l, "bandy");
		searchEngine.add(3l, "bandy");
		searchEngine.add(3l, "bandy");
		
		searchEngine.add(6l, "The quick brown fox jumps over the lazy dog");
	}

	@Test
	public void testUnigramHistogramOrderedCommpletions() {
		List<String> orderedList = searchEngine.getCompletions("a", false, 10);

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
		List<String> orderedList = searchEngine.getCompletions("quick b", false, 10);
		assertEquals("quick brown", orderedList.get(0));
	}

	@Test
	public void testDigramHistogramCompletionFirstWordFuzzyMatch() {
		List<String> orderedList = searchEngine.getCompletions("uic b", true, 10);
		assertEquals("quick brown", orderedList.get(0));
	}

	@Test
	public void testDigramHistogramCompletionFirstWordFuzzyMatch2() {
		List<String> orderedList = searchEngine.getCompletions("uic browns", true, 10);
		assertEquals("quick brown", orderedList.get(0));
	}

	@Test
	public void testDigramHistogramCompletionFirstWordFuzzyMatch3() {
		List<String> orderedList = searchEngine.getCompletions("uic bro", true, 10);
		assertEquals(1, orderedList.size());
		assertEquals("quick brown", orderedList.get(0));
	}

	@Test
	public void testDigramHistogramCompletionWordReversal() {
		List<String> orderedList = searchEngine.getCompletions("brown uic", false, 10);
		assertEquals("quick brown", orderedList.get(0));
	}

	@Test
	public void testSearchSingleElementMatchLong() {
		searchEngine.add(1l, "singleElementLong");
		Set<SearchResult> results = searchEngine.search("singleElementLong", 1);
		assertEquals(1, results.size());
		for (SearchResult result : results) {
			assertEquals(1, (long) result.getResult());
		}
	}

//	@Test
//	public void testSearchSingleElementMatchString() {
//		searchEngine.add("one", "singleElementString");
//		Set<SearchResult> results = searchEngine.search("singleElementString", 1);
//		assertEquals(1, results.size());
//		for (SearchResult result : results) {
//			assertEquals("one", (String) result.getResult());
//		}
//	}

	@Test
	public void testSearchMultiResultMatchLong1() {
		searchEngine.add(1l, "multiResultLong1");
		searchEngine.add(1l, "multiResultLong1");
		Set<SearchResult> results = searchEngine.search("multiResultLong1", 1);
		assertEquals(1, results.size());
		for (SearchResult result : results) {
			assertEquals(1, (long) result.getResult());
		}
	}

//	@Test
//	public void testSearchMultiResultMatchString1() {
//		searchEngine.add("one", "multiResultString1");
//		searchEngine.add("one", "multiResultString1");
//		Set<SearchResult> results = searchEngine.search("multiResultString1", 1);
//		assertEquals(1, results.size());
//		for (SearchResult result : results) {
//			assertEquals("one", (String) result.getResult());
//		}
//	}
//
//	@Test
//	public void testSearchString() {
//		searchEngine.add("mySearchResult", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod");
//		Set<SearchResult> results = searchEngine.search("sect am", 3); // will contain "mySearchResult"
//		for (SearchResult result : results) {
//			assertEquals("mySearchResult", (String) result.getResult());
//		}
//		
//	}
	
	@Test
	public void testSearchMultiResultMatchOrderingLong() {
		searchEngine.add(1l, "multiResultMatchOrderingLong1");
		searchEngine.add(1l, "multiResultMatchOrderingLong1");
		searchEngine.add(2l, "multiResultMatchOrderingLong1");
		searchEngine.add(2l, "multiResultMatchOrderingLong1");
		searchEngine.add(2l, "multiResultMatchOrderingLong1");
		searchEngine.add(3l, "multiResultMatchOrderingLong1");
		Set<SearchResult> results = searchEngine.search("multiResultMatchOrderingLong1", 3);
		assertEquals(3, results.size());
		
		SearchResult[] arr = results.toArray(new SearchResult[0]);
		assertEquals(2l, (long) (arr[0]).getResult());
		assertEquals(1l, (long) (arr[1]).getResult());
		assertEquals(3l, (long) (arr[2]).getResult());
	}

//	@Test
//	public void testSearchMultiResultMatchOrderingString() {
//		searchEngine.add("one", "multiResultMatchOrderingString1");
//		searchEngine.add("one", "multiResultMatchOrderingString1");
//		searchEngine.add("two", "multiResultMatchOrderingString1");
//		searchEngine.add("two", "multiResultMatchOrderingString1");
//		searchEngine.add("two", "multiResultMatchOrderingString1");
//		searchEngine.add("three", "multiResultMatchOrderingString1");
//		Set<SearchResult> results = searchEngine.search("multiResultMatchOrderingString1", 3);
//		assertEquals(3, results.size());
//		
//		SearchResult[] arr = results.toArray(new SearchResult[0]);
//		assertEquals("two", (String) (arr[0]).getResult());
//		assertEquals("one", (String) (arr[1]).getResult());
//		assertEquals("three", (String) (arr[2]).getResult());
//	}

//	// Leaving this commented out right now because it takes a while to run
//	// Using largish numbers as strings to simulate a deterministic dataset for capacity testing
//	@Test
//	public void testCapacity() {
//		searchEngine.resetTimes();
//		long numOfElementsToTest = 10000000; // can handle 20,000,000 on my 16GB MacBook Pro using 12G of heap 
//		long baseNumber = 100000000; // yields a 9 digit string
//		double percentageToDisplay = 1;
//		long percentage = Math.round(numOfElementsToTest / (100 / percentageToDisplay));
//		System.out.print("Test InMemorySearchEngine Capacity, Filling: ");
//		for (long i=0; i < numOfElementsToTest; i++) { 
//			searchEngine.add(i, (baseNumber + i) + "");
//			if (i % percentage == 0) {
//				int currentPercentage = (int) (100 * ((double) i / numOfElementsToTest));
//				if (currentPercentage != 0)
//					System.out.print(",");
//				if (currentPercentage % 10 == 0)
//					System.out.println();
//				System.out.print(" " + currentPercentage + "%");
//			}
//		}
//		
//		System.out.print("\rSearching for test strings: ");
//		for (long i= 0; i < numOfElementsToTest; i++) { 
//			boolean foundResult = false;
//			for (SearchResult result : searchEngine.search((baseNumber + i) + "", 10)) {
//				foundResult |= ((Long) result.getResult()) == i;
//			}
//			assertTrue(foundResult);
//			if (i % percentage == 0) {
//				int currentPercentage = (int) (100 * ((double) i / numOfElementsToTest));
//				if (currentPercentage != 0)
//					System.out.print(",");
//				if (currentPercentage % 10 == 0)
//					System.out.println();
//				System.out.print(" " + currentPercentage + "%");
//			}
//		}
//		System.out.println();
//		searchEngine.printTimes();
//	}
}
