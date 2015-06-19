package net.networkdowntime.search.engine;

import static org.junit.Assert.*;

import java.util.List;

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
		List<String> orderedList = se.getCompletions("a", false);
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
		List<String> orderedList = se.getCompletions("a", false);
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
		List<String> orderedList = se.getCompletions("quick b", false);
		assertEquals("quick brown", orderedList.get(0));
	}

	@Test
	public void testDigramHistogramCompletionFirstWordFuzzyMatch() {
		List<String> orderedList = se.getCompletions("uic b", true);
		assertEquals("quick brown", orderedList.get(0));
	}

	@Test
	public void testDigramHistogramCompletionFirstWordFuzzyMatch2() {
		List<String> orderedList = se.getCompletions("uic brown", true);
//		for (String s : orderedList) 
//			System.out.println(s);
		assertEquals("quick brown", orderedList.get(0));
	}

	@Test
	public void testDigramHistogramCompletionFirstWordFuzzyMatch3() {
		List<String> orderedList = se.getCompletions("uic browns", true);
		assertEquals(1, orderedList.size());
		assertEquals("quick brown", orderedList.get(0));
	}

	@Test
	public void testDigramHistogramCompletionWordReversal() {
		List<String> orderedList = se.getCompletions("brown uic", false);
		assertEquals("quick brown", orderedList.get(0));
	}

	@Test
	public void testCapacity() {
		long baseNumber = 100000000;
		for (long i=0; i <      5000000; i++) { // max limit where it runs out of memory 7168000
			se.add(null, i, (baseNumber + i) + "");
			if (i % 1000 == 0) 
				System.out.println(i);
		}
	}
}
