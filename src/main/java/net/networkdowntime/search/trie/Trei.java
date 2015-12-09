package net.networkdowntime.search.trie;

import java.util.List;

public interface Trei {

	/**
	 * Finds all possible completions for the submitted searchString.  Should only performed on the root node.
	 * 
	 * @param searchString The word-stub to find completions for  
	 * @return Not null list of the found completions
	 */
	public abstract List<String> getCompletions(String searchString);

	/**
	 * Adds a word to the trei
	 * @param word Word to be added
	 */
	public abstract void add(String word);

}