package net.networkdowntime.search.trie;

import gnu.trove.map.hash.TCharObjectHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A suffix trie is a data structure that starting with the last letter of a word and iterates forward through all of the 
 * characters building a tree structure. When another word is added it either builds another tree in the data structure, if 
 * the beginning of the word does not already exist, or extends a existing tree with the characters at the ending of the 
 * word that are different than the already indexed words.  This is recursively done for all suffixes of the word to be added
 * by stripping the first character from the end and re-adding the new word.  The non-full suffix trie can tell you the 
 * endings of all of the words with a specific beginning while the full prefix trie can tell you all of the endings for any 
 * substrings of the word.
 * 
 * One of the downsides to a trie is memory usage in a performant implementation.  Several attempts were made to improve the memory usage of this class.
 * 	Switching out from the common hashmap implementation to the trove data structures.
 * 	Experimenting with the initial size and load factor of the hashmaps.
 * 	Creating the children hashmap on demand.
 * 	One potential improvement that I haven't tested yet is converting the methods to static to reduce the per Object memory footprint.
 * 
 * Full Suffix Tree vs Not: I characterize a full suffix tree to include not just the word, but also every suffix of the 
 * word. The non-full suffix tree does not recursively index all of the words prefixes.  
 * 
 * Example full suffix tree for "foo":
 *	Root Node: 2 children [o, f]
 *		Child Node: o: 1 children [o, ￿]
 *		Child Node: f: 1 children [o]
 *			Child Node: o: 1 children [o]
 * 
 * Example non-full suffix tree for "foo":
 *	Root Node: 1 children [f]
 *		Child Node: f: 1 children [o]
 *			Child Node: o: 1 children [o]
 *  
 * Using this implementation I was able to successfully implement a full prefix trie across a sample 843,888 word data set.
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
public class SuffixTrie extends Trie {

	/**
	 * Default constructor generates a full trie
	 */
	public SuffixTrie() {
		super();
	}

	/**
	 * Allows the creator to create either a full of non-full trie
	 * 
	 * @param createFullTrie
	 */
	public SuffixTrie(boolean createFullTrie) {
		super(createFullTrie);
	}

	@Override
	protected char getChar(String word) {
		return word.charAt(0);
	}

	@Override
	protected char getOppositeChar(String word) {
		return word.charAt(word.length() - 1);
	}

	@Override
	protected String getSubstring(String word) {
		return word.substring(1);
	}

	@Override
	protected String addCharToWordPart(char c, String wordPart) {
		return wordPart + c;
	}

	@Override
	public List<String> getCompletions(String searchString, int limit) {
		List<String> completions = new ArrayList<String>();

		TrieNode currentNode = rootNode;
		int i = 0;

		while (i < searchString.length()) {
			char c = searchString.charAt(i);

			if (currentNode != null && currentNode.children != null) {
				currentNode = currentNode.children.get(c);
			} else {
				currentNode = null;
			}

			if (currentNode == null) { // no match
				return completions;
			}

			i++;
		}

		getCompletionsInternal(currentNode, completions, searchString, limit);

		return completions;
	}

	//	 Uncomment the following if you want to play around or do debugging.
	public static void main(String[] args) {
		SuffixTrie t = new SuffixTrie(true);
		t.add("oof");
		t.add("owt");
		t.print();
		//		List<String> expectedTrace = SuffixTrieNode.getTrace(t, 0);
		//
		//		SuffixTrieNode.add(t, "fod");
		//		SuffixTrieNode.remove(t, "fod");
		//		SuffixTrieNode.print(t);
		//		List<String> actualTrace = SuffixTrieNode.getTrace(t, 0);
		//
		//		boolean matches = true;
		//		for (int i = 0; i < actualTrace.size(); i++) {
		//			matches = matches && actualTrace.get(i).equals(expectedTrace.get(i));
		//		}
		//		System.out.println("Matches: " + matches);

		//		Set<String> completeWords = new HashSet<String>();
		//		t.getCompletionsFullWords(completeWords, "");
		//		System.out.println("complete words: " + t.collectionToString(completeWords));

	}

}
