package net.networkdowntime.search.histogram;


import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

// Provides an implementation of a fixed size generic TreeSet that keeps the largest values up to the max size specified

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

	@SuppressWarnings("unchecked")
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
	
	public Set<E> getResultSet(int limit) {
		Set<E> retval = new LinkedHashSet<E>();
		int count = 0;
		
		Iterator<E> iter = (Iterator<E>) this.iterator();
		while (iter.hasNext()) {
			E val = iter.next();
			retval.add(val);

			count++;
			if (count == limit) {
				break;
			}
		}
		return retval;
	}
}