Embeddable Search With Partial Word Auto-Complete
====

A lightweight embeddable search engine for Java that provides robust auto-completion.  Auto-completion can also be used stand-alone.  

I've frequently lamented the ability to simply add search functionality to some of my java projects.  This search implementation strives to provide a lightweight 
in-memory search implementation that supports auto-completion and ordered search results.  

At the moment the project provides the ability to store a long value as a search result for a given string.  This matches well with the typical pattern of storing a surrogate-key for the ID in the database allowing data to be indexed on application startup and the search results to be matched back to the data in the database.  
The search engine uses the auto-completion internally.

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
searchEngine.add(null, 6l, "The quick brown fox jumps over the lazy dog");
searchEngine.getCompletions("brown uic", false, 10); // will contain "quick brown"
Set<Long> results = searchEngine.search("uic browns", 3); // will contain 6
```
	
Implementation:
----
Search functionality provided through a custom histogram implementation that tracks the different search results for a keyword and their counts.  Search results are
returned ordered by their weighted search results.

To provide auto-completion, when words are added they get added to both a full prefix-trie and a partial suffix-trie.  These allow words to be easily auto-completed by determining possible missing beginnings, endings, or both from a word fragment.

Unigram and Digram Histograms, allow word and word pair frequency to be tracked.  This allows the auto-suggest to recommend the most common words and word pairs.

Future Plans:
----
- Add a string search result implmenentation that can allow URLs to be returned.
- Add a object search result implmenentation that can allow object references to be returned.

