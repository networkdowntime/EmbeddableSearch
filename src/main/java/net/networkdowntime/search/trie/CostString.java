package net.networkdowntime.search.trie;

public class CostString {

	public String str;
	public int cost;

	public CostString(String str) {
		this.str = str;
		this.cost = 0;
	}

	public CostString(String str, int cost) {
		this.str = str;
		this.cost = cost;
	}

	@Override
	public String toString() {
		return str;
	}

	@Override
	public int hashCode() {
		return str.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CostString) {
			return str.equals(((CostString) obj).str);
		}
		return str.equals(obj);
	}
	
	public int length() {
		return str.length();
	}
	
	public String substring(int beginIndex, int endIndex) {
		return str.substring(beginIndex, endIndex);
	}

}
