package net.networkdowntime.search.histogram;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class UnigramHistogramTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testAdd() {
		UnigramHistogram histogram = new UnigramHistogram();
		UnigramHistogram.add(histogram, "foo");
	}

	@Test
	public void testRemove() {
		UnigramHistogram histogram = new UnigramHistogram();
		UnigramHistogram.add(histogram, "foo");
		UnigramHistogram.remove(histogram, "foo");
	}

	@Test
	public void testGetOccuranceCount1() {
		UnigramHistogram histogram = new UnigramHistogram();
		UnigramHistogram.add(histogram, "foo");
		assertTrue(UnigramHistogram.getOccuranceCount(histogram, "foo") == 1);
	}

	@Test
	public void testGetOccuranceCount2() {
		UnigramHistogram histogram = new UnigramHistogram();
		UnigramHistogram.add(histogram, "foo");
		UnigramHistogram.add(histogram, "foo");
		assertTrue(UnigramHistogram.getOccuranceCount(histogram, "foo") == 2);
	}

	@Test
	public void testGetOccuranceCount3() {
		UnigramHistogram histogram = new UnigramHistogram();
		UnigramHistogram.add(histogram, "foo");
		UnigramHistogram.add(histogram, "foo");
		UnigramHistogram.remove(histogram, "foo");
		assertTrue(UnigramHistogram.getOccuranceCount(histogram, "foo") == 1);
	}

}
