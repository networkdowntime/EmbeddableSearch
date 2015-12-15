package net.networkdowntime.search.engine;

import java.util.List;
import java.util.Set;

import net.networkdowntime.search.SupportedSearchResults;

/**
 * Interface for SearchEngine implementations
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
	public Set<Long> search(SupportedSearchResults type, String searchTerm, int limit);

	/**
	 * Indexes the supplied text and associates it with the id.
	 * 
	 * @param type Not currently implemented
	 * @param id Id to associate to the keywords in the text
	 * @param text String to scrub, split, and index to the id
	 */
	public void add(SupportedSearchResults type, Long id, String text);

	/**
	 * De-indexes the supplied text from the id.
	 * 
	 * @param type Not currently implemented
	 * @param id Id to de-index from the keywords in the text
	 * @param text String to scrub, split, and de-index to the id
	 */
	public void remove(SupportedSearchResults type, Long id, String text);

}
