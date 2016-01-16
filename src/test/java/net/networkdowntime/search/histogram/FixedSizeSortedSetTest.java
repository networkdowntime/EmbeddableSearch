package net.networkdowntime.search.histogram;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class FixedSizeSortedSetTest {

	int maxSetSize = 5;
	int numOfElemenets = 6;
	FixedSizeSortedSet<Tuple<Long>> orderedResults;

	@Before
	public void setUp() throws Exception {
		orderedResults = new FixedSizeSortedSet<Tuple<Long>>((new Tuple<Long>()).new TupleComparator<Long>(), maxSetSize);
		
		for (int i = 0; i < numOfElemenets; i++) {
			Tuple t = new Tuple();
			t.word = (char) (i + (int) 'a') + "";
			t.count = i;
			orderedResults.add(t);
		}

	}

	@Test
	public void testFixedSizeSortedSet1() {
				
		@SuppressWarnings("unchecked")
		Tuple<Long>[] results = (Tuple<Long>[]) orderedResults.descendingSet().toArray(new Tuple[0]);
		for (int i = maxSetSize - 1; i >= 0 ; i--) {
			Tuple<Long> t = results[i];
			String expectedWord = (char) (i + ((int) 'a') + (numOfElemenets - maxSetSize)) + "";
			long expectedCount = i + (numOfElemenets - maxSetSize);
			assertEquals(expectedWord, t.word);
			assertEquals(expectedCount, t.count);
		}
	}
	
	@Test
	public void testGetResultSet() {
		Set<Tuple<Long>> results1 = orderedResults.getResultSet(maxSetSize);
		
		int i = maxSetSize - 1;
		for (Tuple t : results1) {
			String expectedWord = (char) (i + ((int) 'a') + (numOfElemenets - maxSetSize)) + "";
			long expectedCount = i + (numOfElemenets - maxSetSize);
			assertEquals(expectedWord, t.word);
			assertEquals(expectedCount, t.count);
			i--;
		}
	}
	
}
