Embeddable Search With Partial Word Auto-Complete
====

A fast and lightweight in memory embeddable search engine for Java.  Provides robust auto-completion, ordered search results, search results can be either
 Strings or Longs.  Auto-completion can also be used in independently of search.  

String search results work for a variety of types of searches such as returning a URL as the search results.
Using a Long search results matches well with the typical pattern of storing a surrogate-key for the ID in the database allowing data to be indexed on application
 startup and the search results to be matched back to the data in the database.
   
The search engine uses the auto-completion internally to improve matching results.  Results for matching Long and String search results returned together and ordered
 based on their weights.
 
Performance:
----
For 5 million unique 9 character strings:
'''
Avg. time to add: 0.0167 ms
Avg. time to search: 0.0538 ms
'''

Usage:
----

Stand-alone Autocomplete Usage:<br>
```
Autocomplete autocomplete = new Autocomplete();
autocomplete.add("The quick brown fox jumps over the lazy dog");
autocomplete.getCompletions("uic browns", true, 10); // will contain "quick brown"
```

Search Usage:
```
SearchEngine searchEngine = new InMemorySearchEngine();
searchEngine.add(6l, "The quick brown fox jumps over the lazy dog"); // Indexing content to a Long result
searchEngine.add("mySearchResult", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod"); // Indexing content to a String result
searchEngine.getCompletions("brown uic", false, 10); // will contain "quick brown"
Set<SearchResult> results1 = searchEngine.search("uic browns", 3); // will contain 6, matches to "quick brown"
Set<SearchResult> results2 = searchEngine.search("sect am", 3); // will contain "mySearchResult" matches to "amet, consectetur"
```
	
Implementation:
----
Search functionality provided through custom unigram histogram implementations that tracks the different search results for a keyword and their counts.  Search 
results are returned ordered by their weighted search results.

To provide auto-completion, when words are added they get added to both a full prefix-trie and a partial suffix-trie.  These allow words to be easily auto-completed by determining possible missing beginnings, endings, or both from a word fragment.

Unigram and Digram Histograms, allow word and word pair frequency to be tracked.  This allows the auto-suggest to recommend the most common words and natural follow up
words based on the indexed text.
