package net.networkdowntime.search.histogram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class Tuple<T extends Comparable<T>> {
	public T word;
	public int count;
	

	public static <E extends Comparable<E>> TreeSet<Tuple<E>> createOrderedResultsTree(E obj) {
		return new TreeSet<Tuple<E>>((new Tuple<E>()).new TupleComparator<E>());
	}

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
