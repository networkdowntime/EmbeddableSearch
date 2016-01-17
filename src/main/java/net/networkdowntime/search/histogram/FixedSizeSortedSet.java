package net.networkdowntime.search.histogram;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Provides a generics implementation of a fixed size generic TreeSet that keeps the largest values up to the max size specified
 * 
 * This software is licensed under the MIT license
 * Copyright (c) 2015 Ryan Wiles
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation 
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, 
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software 
 * is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE 
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR 
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * @author rwiles
 *
 * @param <E>
 */
public class FixedSizeSortedSet<E> extends TreeSet<E> {
	private static final long serialVersionUID = 1L;

	private final transient Comparator<? super E> comparator;
	private final int maxSize;

	/**
	 * Creates a new FixedSizeSortedSet using a specified Comparator and a maxSize.
	 * 
	 * @param comparator The Comparator to use for sorting
	 * @param maxSize Max size of the set
	 */
	public FixedSizeSortedSet(Comparator<? super E> comparator, int maxSize) {
		super(comparator);
		this.comparator = comparator;
		this.maxSize = maxSize;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean add(E e) {
		if (size() >= maxSize) {
			E smallest = last();
			int comparison;
			if (comparator == null)
				comparison = ((Comparable<E>) e).compareTo(smallest);
			else
				comparison = comparator.compare(e, smallest);
			if (comparison < 0) {
				remove(smallest);
				return super.add(e);
			}
			return false;
		} else {
			return super.add(e);
		}
	}

	/**
	 * Returns the contents of up to a maximum specified size
	 * 
	 * @param limit Max number of results to return
	 * @return An ordered set of results up to the the specified limit
	 */
	public Set<E> getResultSet(int limit) {
		Set<E> retval = new LinkedHashSet<E>();
		int count = 0;

		Iterator<E> iter = this.iterator();
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