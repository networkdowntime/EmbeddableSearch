package net.networkdowntime.search.histogram;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import gnu.trove.map.hash.TObjectIntHashMap;

public class DigramStringSearchHistogramTest {

	@Before
	public void setUp() throws Exception {
	}

	public Set<String> toSet(String... strings) {
		Set<String> words = new HashSet<String>();
		words.addAll(Arrays.asList(strings));
		return words;
	}

	@Test
	public void testAddSearchSingleResultMap() {
		DigramStringSearchHistogram digramHistogram = new DigramStringSearchHistogram();
		String desiredResultId = "result1";

		digramHistogram.add("word1", "word2", desiredResultId);

		TObjectIntHashMap<String> results = digramHistogram.getSearchResults(toSet("word1 word2"), 1);

		assertEquals(1, results.size()); // only 1 result returned
		assertTrue(results.contains(desiredResultId)); // desired result key contained
		assertEquals(1, results.get(desiredResultId)); // desired result has correct weight
	}

	@Test
	public void testAddSearchSingleResultMapWeightMultiplier() {
		DigramStringSearchHistogram digramHistogram = new DigramStringSearchHistogram();
		String desiredResultId = "result1";

		digramHistogram.add("word1", "word2", desiredResultId);

		TObjectIntHashMap<String> results = digramHistogram.getSearchResults(toSet("word1 word2"), 10);

		assertEquals(1, results.size()); // only 1 result returned
		assertTrue(results.contains(desiredResultId)); // desired result key contained
		assertEquals(10, results.get(desiredResultId)); // desired result has correct weight
	}

	@Test
	public void testAddSearchReversedOrderSingleResultMap() {
		DigramStringSearchHistogram digramHistogram = new DigramStringSearchHistogram();
		String desiredResultId = "result1";

		digramHistogram.add("word1", "word2", desiredResultId);

		TObjectIntHashMap<String> results = digramHistogram.getSearchResults(toSet("word2 word1"), 1);

		assertEquals(1, results.size()); // only 1 result returned
		assertTrue(results.contains(desiredResultId)); // desired result key contained
		assertEquals(1, results.get(desiredResultId)); // desired result has correct weight
	}

	@Test
	public void testAddRemoveSearchSingleResultMap1() {
		DigramStringSearchHistogram digramHistogram = new DigramStringSearchHistogram();
		String desiredResultId = "result1";

		digramHistogram.add("word1", "word2", desiredResultId);
		digramHistogram.remove("word1", "word2", desiredResultId);

		TObjectIntHashMap<String> results = digramHistogram.getSearchResults(toSet("word1 word2"), 1);

		assertEquals(0, results.size());
	}

	@Test
	public void testAddRemoveSearchSingleResultMap2() {
		DigramStringSearchHistogram digramHistogram = new DigramStringSearchHistogram();
		String desiredResultId = "result1";

		digramHistogram.add("word1", "word2", desiredResultId);
		digramHistogram.add("word3", "word4", desiredResultId); // creates another single result map for "word3"
		digramHistogram.remove("word1", "word2", desiredResultId);

		TObjectIntHashMap<String> results = digramHistogram.getSearchResults(toSet("word1 word2"), 1);

		assertEquals(0, results.size());
	}

	@Test
	public void testAddSearchMultiResultMap1() {
		DigramStringSearchHistogram digramHistogram = new DigramStringSearchHistogram();
		String desiredResultId = "result1";

		digramHistogram.add("word1", "word2", desiredResultId);
		digramHistogram.add("word1", "word2", desiredResultId);

		TObjectIntHashMap<String> results = digramHistogram.getSearchResults(toSet("word1 word2"), 1);

		assertEquals(1, results.size()); // only 1 result returned
		assertTrue(results.contains(desiredResultId)); // desired result key contained
		assertEquals(2, results.get(desiredResultId)); // desired result has correct weight
	}

	@Test
	public void testAddSearchMultiResultMap2() {
		DigramStringSearchHistogram digramHistogram = new DigramStringSearchHistogram();
		String desiredResultId = "result1";

		digramHistogram.add("word1", "word2", desiredResultId);
		digramHistogram.add("word1", "word3", desiredResultId);

		TObjectIntHashMap<String> results = digramHistogram.getSearchResults(toSet("word1 word2"), 1);

		assertEquals(1, results.size()); // only 1 result returned
		assertTrue(results.contains(desiredResultId)); // desired result key contained
		assertEquals(1, results.get(desiredResultId)); // desired result has correct weight
	}

	@Test
	public void testAddSearchReversedOrderMultiResultMap1() {
		DigramStringSearchHistogram digramHistogram = new DigramStringSearchHistogram();
		String desiredResultId = "result1";

		digramHistogram.add("word1", "word2", desiredResultId);
		digramHistogram.add("word1", "word2", desiredResultId);

		TObjectIntHashMap<String> results = digramHistogram.getSearchResults(toSet("word2 word1"), 1);

		assertEquals(1, results.size()); // only 1 result returned
		assertTrue(results.contains(desiredResultId)); // desired result key contained
		assertEquals(2, results.get(desiredResultId)); // desired result has correct weight
	}

	@Test
	public void testAddSearchReversedOrderMultiResultMap2() {
		DigramStringSearchHistogram digramHistogram = new DigramStringSearchHistogram();
		String desiredResultId = "result1";

		digramHistogram.add("word1", "word2", desiredResultId);
		digramHistogram.add("word2", "word1", desiredResultId);

		TObjectIntHashMap<String> results = digramHistogram.getSearchResults(toSet("word2 word1"), 1);

		assertEquals(1, results.size()); // only 1 result returned
		assertTrue(results.contains(desiredResultId)); // desired result key contained
		assertEquals(2, results.get(desiredResultId)); // desired result has correct weight
	}

	@Test
	public void testAddRemoveSearchMultiResultMap1() {
		DigramStringSearchHistogram digramHistogram = new DigramStringSearchHistogram();
		String desiredResultId = "result1";

		digramHistogram.add("word1", "word2", desiredResultId);
		digramHistogram.add("word1", "word2", desiredResultId);
		digramHistogram.remove("word1", "word2", desiredResultId);

		TObjectIntHashMap<String> results = digramHistogram.getSearchResults(toSet("word1 word2"), 1);

		assertEquals(0, results.size()); // only 1 result returned
	}

	@Test
	public void testAddRemoveSearchMultiResultMap2() {
		DigramStringSearchHistogram digramHistogram = new DigramStringSearchHistogram();
		String desiredResultId = "result1";

		digramHistogram.add("word1", "word2", desiredResultId);
		digramHistogram.add("word1", "word2", desiredResultId);
		digramHistogram.remove("word1", "word2", 2);

		TObjectIntHashMap<String> results = digramHistogram.getSearchResults(toSet("word1 word2"), 1);

		assertEquals(1, results.size()); // only 1 result returned
		assertTrue(results.contains(desiredResultId)); // desired result key contained
		assertEquals(2, results.get(desiredResultId)); // desired result has correct weight
	}

	@Test
	public void testAddRemoveSearchMultiResultMap3() {
		DigramStringSearchHistogram digramHistogram = new DigramStringSearchHistogram();
		String desiredResultId = "result1";
		String desiredResultId2 = "result2";

		digramHistogram.add("word1", "word2", desiredResultId);
		digramHistogram.add("word1", "word2", desiredResultId);
		digramHistogram.add("word1", "word3", desiredResultId2);
		digramHistogram.remove("word1", "word2", desiredResultId);

		TObjectIntHashMap<String> results = digramHistogram.getSearchResults(toSet("word1 word2"), 1);
		assertEquals(0, results.size());

		results = digramHistogram.getSearchResults(toSet("word1 word3"), 1);
		assertEquals(1, results.size()); // only 1 result returned
		assertTrue(results.contains(desiredResultId2)); // desired result key contained
		assertEquals(1, results.get(desiredResultId2)); // desired result has correct weight

	}

}
