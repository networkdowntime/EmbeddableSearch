package net.networkdowntime.search;

public class SearchResult implements Comparable<SearchResult> {
	String type;
	Object result;
	int weight;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	@Override
	public int compareTo(SearchResult o) {
		return (this.weight < o.weight) ? -1: (this.weight > o.weight) ? 1:0 ;
	}
}
