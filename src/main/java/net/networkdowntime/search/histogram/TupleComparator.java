package net.networkdowntime.search.histogram;

import java.util.Comparator;

public class TupleComparator<E extends Comparable<E>> implements Comparator<Tuple<E>> {
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
