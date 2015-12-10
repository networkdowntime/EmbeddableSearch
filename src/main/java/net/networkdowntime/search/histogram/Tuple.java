package net.networkdowntime.search.histogram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

/**
 * A class to store a comparable generics object with an associated count along and be able to sort them based on their counts firstly and their objects secondly.
 * 
 * @author rwiles
 *
 * @param <T>
 */
public class Tuple<T extends Comparable<T>> {
	public T word;
	public int count;
	

	/**
	 * Creates and returns a TreeSet with an appropriate comparator for ordering the Tuples.
	 * 
	 * @param obj Instance of the comparable object contained in the tuples being sorted.
	 * @return TreeSet ordered by their Tuples
	 */
	public static <E extends Comparable<E>> TreeSet<Tuple<E>> createOrderedResultsTree(E obj) {
		return new TreeSet<Tuple<E>>((new Tuple<E>()).new TupleComparator<E>());
	}

	
	/**
	 * Updates a given Tuple with a new count, adding if it doesn't exist, and returning a sorted array of Tuples constrained by the limit.
	 * 
	 * @param values An array of Tuples to be updated
	 * @param word The object for the tuple
	 * @param count The new count for that tuple
	 * @param limit Max number of results to return
	 * @return A sorted array of Tuples
	 */
	public static <E extends Comparable<E>> Tuple<E>[] updateSortTupleArray(Tuple<E>[] values, E word, int count, int limit) {
		boolean updated = false;
		for (Tuple<E> t : values) {
			if (t != null && word.equals(t.word)) {
				t.count = count;
				updated = true;
			}
		}
		
		List<Tuple<E>> list = new ArrayList<Tuple<E>>(Arrays.asList(values));
		
		if (!updated) {
			Tuple<E> t = new Tuple<E>();
			t.word = word;
			t.count = count;
			list.add(t);
		}
		
		list.sort((new Tuple<E>()).new TupleComparator<E>());

		while (list.size() > limit) {
			list.remove(list.size() - 1);
		}
		
		return list.toArray(values);
	}
	
	
	/**
	 * The Tuple Comparator compares tuples based first by their counts and secondly by comparing their objects.
	 * 
	 * @author rwiles
	 *
	 * @param <E>
	 */
	class TupleComparator<E extends Comparable<E>> implements Comparator<Tuple<E>> {
		@Override
		public int compare(Tuple<E> o1, Tuple<E> o2) {
			if (o1 == null && o2 == null)
				return 0;
			else if (o1 != null && o2 == null)
				return -1;
			else if (o1 == null && o2 != null)
				return 1;
			else if (o1.count != o2.count) {
				Integer i1 = o1.count;
				Integer i2 = o2.count;
				return i2.compareTo(i1);
			} else {
				return o1.word.compareTo(o2.word);
			}
		}
	}

}
