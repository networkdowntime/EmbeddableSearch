package net.networkdowntime.search.histogram;

import static org.junit.Assert.*;

import java.util.ArrayList;
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
		DigramHistogram histogram = new DigramHistogram();
		
		assertEquals(0, DigramHistogram.getOccuranceCount(histogram, "apple", "zoo"));
		
		DigramHistogram.add(histogram, "apple", "zoo");
		assertEquals(1, DigramHistogram.getOccuranceCount(histogram, "apple", "zoo"));
		
		DigramHistogram.add(histogram, "apple", "zoo");
		assertEquals(2, DigramHistogram.getOccuranceCount(histogram, "apple", "zoo"));
		
		DigramHistogram.add(histogram, "zoo", "apple");
		assertEquals(2, DigramHistogram.getOccuranceCount(histogram, "apple", "zoo"));
		assertEquals(1, DigramHistogram.getOccuranceCount(histogram, "zoo", "apple"));
		
		DigramHistogram.add(histogram, "zoo", "animals");
		DigramHistogram.add(histogram, "apple", "jacks");

		Set<String> secondWords = new HashSet<String>();
		List<String> results;
		
		secondWords.add("jacks");
		secondWords.add("zoo");
		results = DigramHistogram.getOrderedResults(histogram, "apple", secondWords, 20);
		assertEquals("apple zoo", results.get(0));
		assertEquals("apple jacks", results.get(1));
		assertEquals("zoo apple", results.get(2));
		secondWords.clear();

		secondWords.add("apple");
		secondWords.add("jacks");
		results = DigramHistogram.getOrderedResults(histogram, "zoo", secondWords, 20);
		assertEquals("apple zoo", results.get(0));
		assertEquals("zoo apple", results.get(1));
		secondWords.clear();

		secondWords.add("zoo");
		secondWords.add("jacks");
		results = DigramHistogram.getOrderedResults(histogram, "animals", secondWords, 20);
		assertEquals("zoo animals", results.get(0));
		secondWords.clear();
	}

}
