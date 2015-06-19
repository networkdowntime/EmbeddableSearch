package net.networkdowntime.search.histogram;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

public class TupleTest {

	TreeSet<Tuple<String>> tree = Tuple.createOrderedResultsTree(new String());
	
	@Before
	public void setUp() throws Exception {
		Tuple<String> t;
		
		t = new Tuple<String>();
		t.word = "one";
		t.count = 1;
		tree.add(t);
		
		t = new Tuple<String>();
		t.word = "two";
		t.count = 2;
		tree.add(t);
		
		t = new Tuple<String>();
		t.word = "three";
		t.count = 3;
		tree.add(t);
		
		t = new Tuple<String>();
		t.word = "four";
		t.count = 4;
		tree.add(t);
		
		t = new Tuple<String>();
		t.word = "five";
		t.count = 5;
		tree.add(t);
	}

	@Test
	public void testTreeOrder1() {
		Iterator<Tuple<String>> iter = tree.iterator();
		Tuple<String> t;
		
		t = iter.next();
		assertEquals("five", t.word);
		
		t = iter.next();
		assertEquals("four", t.word);
		
		t = iter.next();
		assertEquals("three", t.word);
		
		t = iter.next();
		assertEquals("two", t.word);
		
		t = iter.next();
		assertEquals("one", t.word);
	}
	
	@Test
	public void testTreeOrder2() {
		TreeSet<Tuple<Long>> tree = Tuple.createOrderedResultsTree(new Long(0));

		Tuple<Long> t;
		
		t = new Tuple<Long>();
		t.word = 1l;
		t.count = 1;
		tree.add(t);
		
		t = new Tuple<Long>();
		t.word = 2l;
		t.count = 2;
		tree.add(t);
		
		t = new Tuple<Long>();
		t.word = 3l;
		t.count = 3;
		tree.add(t);
		
		t = new Tuple<Long>();
		t.word = 4l;
		t.count = 4;
		tree.add(t);
		
		t = new Tuple<Long>();
		t.word = 5l;
		t.count = 5;
		tree.add(t);
		
		Iterator<Tuple<Long>> iter = tree.iterator();

		t = iter.next();
		assertEquals(5l, (long) t.word);
		
		t = iter.next();
		assertEquals(4l, (long) t.word);
		
		t = iter.next();
		assertEquals(3l, (long) t.word);
		
		t = iter.next();
		assertEquals(2l, (long) t.word);
		
		t = iter.next();
		assertEquals(1l, (long) t.word);
	}
	
	@Test
	public void testUpdateSortTupleArray1() {
		@SuppressWarnings("unchecked")
		Tuple<String>[] arr = tree.toArray(new Tuple[0]);
		arr = Tuple.updateSortTupleArray(arr, "four", 6, 5);
		assertEquals("four", arr[0].word);
		assertEquals("five", arr[1].word);
		assertEquals("three", arr[2].word);
		assertEquals("two", arr[3].word);
		assertEquals("one", arr[4].word);
	}

	@Test
	public void testUpdateSortTupleArray2() {
		@SuppressWarnings("unchecked")
		Tuple<String>[] arr = tree.toArray(new Tuple[0]);
		arr = Tuple.updateSortTupleArray(arr, "six", 6, 5);
		assertEquals("six", arr[0].word);
		assertEquals("five", arr[1].word);
		assertEquals("four", arr[2].word);
		assertEquals("three", arr[3].word);
		assertEquals("two", arr[4].word);
		
	}

}
