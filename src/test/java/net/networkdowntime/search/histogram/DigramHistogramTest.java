package net.networkdowntime.search.histogram;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class DigramHistogramTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		DigramHistogram digramHistogram = new DigramHistogram();
		
		assertEquals(0, digramHistogram.getOccuranceCount("apple", "zoo"));
		
		digramHistogram.add("apple", "zoo");
		assertEquals(1, digramHistogram.getOccuranceCount("apple", "zoo"));
		
		digramHistogram.add("apple", "zoo");
		assertEquals(2, digramHistogram.getOccuranceCount("apple", "zoo"));
		
		digramHistogram.add("zoo", "apple");
		assertEquals(2, digramHistogram.getOccuranceCount("apple", "zoo"));
		assertEquals(1, digramHistogram.getOccuranceCount("zoo", "apple"));
		
		digramHistogram.add("zoo", "animals");
		digramHistogram.add("apple", "jacks");

		Set<String> secondWords = new HashSet<String>();
		List<String> results;
		
		secondWords.add("jacks");
		secondWords.add("zoo");
		results = digramHistogram.getOrderedResults("apple", secondWords, 20);
		assertEquals("apple zoo", results.get(0));
		assertEquals("apple jacks", results.get(1));
		assertEquals("zoo apple", results.get(2));
		secondWords.clear();

		secondWords.add("apple");
		secondWords.add("jacks");
		results = digramHistogram.getOrderedResults("zoo", secondWords, 20);
		assertEquals("apple zoo", results.get(0));
		assertEquals("zoo apple", results.get(1));
		secondWords.clear();

		secondWords.add("zoo");
		secondWords.add("jacks");
		results = digramHistogram.getOrderedResults("animals", secondWords, 20);
		assertEquals("zoo animals", results.get(0));
		secondWords.clear();
	}

}
