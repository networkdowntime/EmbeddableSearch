package net.networkdowntime.search.engine;

import java.util.List;
import java.util.Set;

public interface SearchEngine {

	public List<String> getCompletions(String wordStub, boolean fuzzyMatch);
	
	public Set<Long> search(String wordStub, int limit);
	
	public void add(String type, Long id, String text);
	
	public void remove(String type, Long id, String text);
	
	public void printTimesAndReset();
	
	public void printTimes();

}
