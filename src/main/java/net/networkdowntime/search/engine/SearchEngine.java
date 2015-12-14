package net.networkdowntime.search.engine;

import java.util.List;
import java.util.Set;

/**
 * Interface for SearchEngine implementations
 * @author rwiles
 *
 */
public interface SearchEngine {

	/**
	 * Get the known completions for the given string.  This will provide completions for missing prefix or suffix on the word and order based on the
	 * completions weight in the search histogram (i.e. completions that match more results will be returned first)
	 * 
	 * @param searchTerm String to search for completions, can contain multiple words or word fragments
	 * @param fuzzyMatch Not implemented 
	 * @param limit Max number of returned search results
	 * @return Not-null ordered list of suggested completions ordered by their search weight
	 */
	public List<String> getCompletions(String searchTerm, boolean fuzzyMatch, int limit);

	/**
	 * Returns an ordered set of search results based on the given search term.
	 * While not necessary, best results will be achieved by using strings returned from getCompletions()
	 * 
	 * @param searchTerm String that you want to get search results for
	 * @param limit Max number of returned search results
	 * @return
	 */
	public Set<Long> search(String searchTerm, int limit);

	/**
	 * Indexes the supplied text and associates it with the id.
	 * 
	 * @param type Not currently implemented
	 * @param id Id to associate to the keywords in the text
	 * @param text String to scrub, split, and index to the id
	 */
	public void add(String type, Long id, String text);

	/**
	 * De-indexes the supplied text from the id.
	 * 
	 * @param type Not currently implemented
	 * @param id Id to de-index from the keywords in the text
	 * @param text String to scrub, split, and de-index to the id
	 */
	public void remove(String type, Long id, String text);

}
