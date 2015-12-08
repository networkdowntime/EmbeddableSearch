# search
A lightweight embeddable search engine for Java.  

I've frequently lamented the ability to simply add search functionality to some of my java projects.  This search implementation strives to provide a lightweight in-memory search implementation that supports auto-completion and ordered search results.  

At the moment the project provides the ability to store a long value as a search result for a given string.  This matches well with the typical pattern of storing a surrogate-key for the ID in the database allowing data to be indexed on application startup and the search results to be matched back to the data in the database.

Implementation:
To provide auto-complention, when words are added they get added to both a prefix-trie and a suffix-trie.  These allow words to be easily auto-completed by determining possible missing beginnings, endings, or both from a word fragment.

Unigram and Digram Histograms, allow word and word pair frequency to be tracked.  This allows the auto-suggest to recommend the most common words and word pairs.

Future Plans:
Add a string search result implmenentation that can allow URLs to be returned.
Add a object search result implmenentation that can allow object references to be returned.

