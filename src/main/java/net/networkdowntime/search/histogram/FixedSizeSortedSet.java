package net.networkdowntime.search.histogram;

import gnu.trove.map.hash.TIntLongHashMap;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

public class FixedSizeSortedSet<E> extends TreeSet<E> {
	private static final long serialVersionUID = 1L;

	private final Comparator<? super E> _comparator;
	private final int _maxSize;

	public FixedSizeSortedSet(int maxSize) {
		this(null, maxSize);
	}

	public FixedSizeSortedSet(Comparator<? super E> comparator, int maxSize) {
		super(comparator);
		_comparator = comparator;
		_maxSize = maxSize;
	}

	@Override
	public boolean add(E e) {
		if (size() >= _maxSize) {
			E smallest = last();
			int comparison;
			if (_comparator == null)
				comparison = ((Comparable<E>) e).compareTo(smallest);
			else
				comparison = _comparator.compare(e, smallest);
			if (comparison < 0) {
				remove(smallest);
				return super.add(e);
			}
			return false;
		} else {
			return super.add(e);
		}
	}
	
	public static void main(String... args) {
		FixedSizeSortedSet<Tuple<Long>> orderedResults = new FixedSizeSortedSet<Tuple<Long>>((new Tuple<Long>()).new TupleComparator<Long>(), 5);
		
		for (int i = 0; i < 7; i++) {
			Tuple t = new Tuple();
			t.word = "a";
			t.count = i;
			orderedResults.add(t);
		}
		
		Iterator<Tuple<Long>> iter = orderedResults.iterator();
		while (iter.hasNext()) {
			Tuple<Long> tuple = iter.next();
			System.out.println(tuple.word + "; " + tuple.count);
		}
		
		TIntLongHashMap map = new TIntLongHashMap();
		map.put(1, 2l);
		Long val = map.get(1);
		System.out.println(val);
	}
}