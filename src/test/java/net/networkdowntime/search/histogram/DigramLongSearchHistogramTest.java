package net.networkdowntime.search.histogram;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import net.networkdowntime.search.SearchResult;

public class DigramLongSearchHistogramTest {

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
		DigramSearchHistogram digramHistogram = new DigramSearchHistogram();
		digramHistogram.add("word1", "word2", 1);

		FixedSizeSortedSet<SearchResult> results = digramHistogram.getSearchResults(toSet("word1"), toSet("word2"), 1);

		assertEquals(1, results.size());
		for (SearchResult<Long> result : results) {
			assertEquals(1, result.getResult().longValue());
		}
	}

	@Test
	public void testAddSearchReversedOrderSingleResultMap() {
		DigramSearchHistogram digramHistogram = new DigramSearchHistogram();
		digramHistogram.add("word1", "word2", 1);

		FixedSizeSortedSet<SearchResult> results = digramHistogram.getSearchResults(toSet("word2"), toSet("word1"), 1);

		assertEquals(1, results.size());
		for (SearchResult<Long> result : results) {
			assertEquals(1, result.getResult().longValue());
		}
	}

	@Test
	public void testAddRemoveSearchSingleResultMap1() {
		DigramSearchHistogram digramHistogram = new DigramSearchHistogram();
		digramHistogram.add("word1", "word2", 1);
		digramHistogram.remove("word1", "word2", 1);

		FixedSizeSortedSet<SearchResult> results = digramHistogram.getSearchResults(toSet("word1"), toSet("word2"), 1);

		assertEquals(0, results.size());
	}

	@Test
	public void testAddRemoveSearchSingleResultMap2() {
		DigramSearchHistogram digramHistogram = new DigramSearchHistogram();
		digramHistogram.add("word1", "word2", 1);
		digramHistogram.add("word3", "word4", 1); // creates another single result map for "word3"
		digramHistogram.remove("word1", "word2", 1);

		FixedSizeSortedSet<SearchResult> results = digramHistogram.getSearchResults(toSet("word1"), toSet("word2"), 1);

		assertEquals(0, results.size());
	}

	@Test
	public void testAddSearchMultiResultMap1() {
		DigramSearchHistogram digramHistogram = new DigramSearchHistogram();
		digramHistogram.add("word1", "word2", 1);
		digramHistogram.add("word1", "word2", 1);

		FixedSizeSortedSet<SearchResult> results = digramHistogram.getSearchResults(toSet("word1"), toSet("word2"), 1);

		assertEquals(1, results.size());
		for (SearchResult<Long> result : results) {
			assertEquals(1, result.getResult().longValue());
			assertEquals(6, result.getWeight());
		}
	}

	@Test
	public void testAddSearchMultiResultMap2() {
		DigramSearchHistogram digramHistogram = new DigramSearchHistogram();
		digramHistogram.add("word1", "word2", 1);
		digramHistogram.add("word1", "word3", 1);

		FixedSizeSortedSet<SearchResult> results = digramHistogram.getSearchResults(toSet("word1"), toSet("word2"), 1);

		assertEquals(1, results.size());
		for (SearchResult<Long> result : results) {
			assertEquals(1, result.getResult().longValue());
			assertEquals(3, result.getWeight());
		}
	}

	@Test
	public void testAddSearchReversedOrderMultiResultMap1() {
		DigramSearchHistogram digramHistogram = new DigramSearchHistogram();
		digramHistogram.add("word1", "word2", 1);
		digramHistogram.add("word1", "word2", 1);

		FixedSizeSortedSet<SearchResult> results = digramHistogram.getSearchResults(toSet("word2"), toSet("word1"), 1);

		assertEquals(1, results.size());
		for (SearchResult<Long> result : results) {
			assertEquals(1, result.getResult().longValue());
			assertEquals(6, result.getWeight());
		}
	}

	@Test
	public void testAddRemoveSearchMultiResultMap1() {
		DigramSearchHistogram digramHistogram = new DigramSearchHistogram();
		digramHistogram.add("word1", "word2", 1);
		digramHistogram.add("word1", "word2", 1);
		digramHistogram.remove("word1", "word2", 1);

		FixedSizeSortedSet<SearchResult> results = digramHistogram.getSearchResults(toSet("word1"), toSet("word2"), 1);

		assertEquals(0, results.size());
		for (SearchResult<Long> result : results) {
			assertEquals(1, result.getResult().longValue());
			assertEquals(3, result.getWeight());
		}
	}
	
	@Test
	public void testAddRemoveSearchMultiResultMap2() {
		DigramSearchHistogram digramHistogram = new DigramSearchHistogram();
		digramHistogram.add("word1", "word2", 1);
		digramHistogram.add("word1", "word2", 1);
		digramHistogram.add("word1", "word3", 2);
		digramHistogram.remove("word1", "word2", 1);

		FixedSizeSortedSet<SearchResult> results = digramHistogram.getSearchResults(toSet("word1"), toSet("word2"), 1);
		assertEquals(0, results.size());
		
		results = digramHistogram.getSearchResults(toSet("word1"), toSet("word3"), 1);
		assertEquals(1, results.size());
		for (SearchResult<Long> result : results) {
			assertEquals(2, result.getResult().longValue());
			assertEquals(3, result.getWeight());
		}
	}
	
}
