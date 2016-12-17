package net.networkdowntime.search.trie;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

public class CostStringSet<C extends CostString> implements Set<C> {

	private TreeMap<Integer, C> map = new TreeMap<Integer, C>();

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return map.containsValue(o);
	}

	@Override
	public Iterator<C> iterator() {
		return map.values().iterator();
	}

	@Override
	public Object[] toArray() {
		return map.values().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return map.values().toArray(a);
	}

	@Override
	public boolean add(C csToAdd) {
		Objects.requireNonNull(csToAdd);

		C existingCs = map.get(csToAdd.hashCode());
		boolean isNewElement = existingCs == null;

		if (isNewElement) {
			map.put(csToAdd.hashCode(), csToAdd);
		} else {
			if (csToAdd.cost < existingCs.cost) {
				map.put(csToAdd.hashCode(), csToAdd);
			}
		}
		return isNewElement;
	}

	public C get(C costString) {
		Objects.requireNonNull(costString);
		return map.get(costString.hashCode());
	}

	@Override
	public boolean remove(Object csToRemove) {
		Objects.requireNonNull(csToRemove);

		return map.remove(csToRemove.hashCode()) != null;
	}

	@Override
	public boolean containsAll(Collection<?> collection) {
		for (Object o : collection) {
			if (o instanceof CostString) {
				if (!map.containsKey(((CostString) o).hashCode())) {
					return false;
				}
			} else {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends C> collection) {
		boolean hasAdded = false;
		for (C c : collection) {
			hasAdded |= add(c);
		}
		return hasAdded;
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		Objects.requireNonNull(collection);
		boolean modified = false;

		Set<Integer> toKeep = new HashSet<Integer>();
		for (Object c : collection) {
			if (c instanceof CostString) {
				toKeep.add(((CostString) c).hashCode());
			}
		}

		Set<Integer> existing = map.keySet();
		for (Integer hash : existing) {
			if (!toKeep.contains(hash)) {
				existing.remove(hash);
				modified = true;
			}
		}
		return modified;
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		Objects.requireNonNull(collection);
		boolean modified = false;

		Set<Integer> toRemove = new HashSet<Integer>();
		for (Object c : collection) {
			if (c instanceof CostString) {
				toRemove.add(((CostString) c).hashCode());
			}
		}

		Set<Integer> existing = map.keySet();
		for (Integer hash : existing) {
			if (toRemove.contains(hash)) {
				existing.remove(hash);
				modified = true;
			}
		}
		return modified;
	}

	@Override
	public void clear() {
		map.clear();
	}

}
