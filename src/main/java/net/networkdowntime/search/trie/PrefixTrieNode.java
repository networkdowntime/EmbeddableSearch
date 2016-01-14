package net.networkdowntime.search.trie;


import gnu.trove.map.hash.TCharObjectHashMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A prefix trie is a data structure that starting with the last letter of a word and iterates backwards through all of the 
 * characters building a tree structure. When another word is added it either builds another tree in the data structure, if 
 * the ending of the word does not already exist, or extends a existing tree with the characters at the beginning of the word 
 * that are different than the already indexed words.  This is recursively done for all prefixes of the word to be added by 
 * stripping the last character from the end and re-adding the new word.  The non-full prefix trie can tell you the beginnings 
 * of all of the words with a specific ending while the full prefix trie can tell you all of the beginnings for any substrings 
 * of the word.
 * 
 * One of the downsides to a trie is memory usage in a performant implementation.  Several attempts were made to improve the memory usage of this class.
 * 	Switching out from the common hashmap implementation to the trove data structures.
 * 	Experimenting with the initial size and load factor of the hashmaps.
 * 	Creating the children hashmap on demand.
 * 	One potential improvement that I haven't tested yet is converting the methods to static to reduce the per Object memory footprint.
 * 
 * Full Prefix Tree vs Not: I characterize a full prefix tree to include not just the word, but also every prefix of the word. The non-full prefix tree does not 
 * recursively index all of the words prefixes.  
 * 
 * Example full prefix tree for "foo":
 * 	Root Node: 2 children [f, o]
 *		Child Node: f: 0 children [￿]
 *		Child Node: o: 2 children [f, o]
 *			Child Node: f: 0 children [￿]
 *			Child Node: o: 1 children [f]
 *				Child Node: f: 0 children [￿]
 * 
 * Example non-full prefix tree for "foo":
 *	Root Node: 1 children [o, ]
 *		Child Node: o: 1 children [o, ]
 *			Child Node: o: 1 children [f, ]
 *				Child Node: f: 0 children [￿]
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
public class PrefixTrieNode {

	private boolean createFullPrefixTree = true;

	private TCharObjectHashMap<PrefixTrieNode> children = null;

	char prefix;
	boolean isEnd = false;
	boolean isFullWordEnd = false;

	/**
	 * Default constructor generates a full prefix tree
	 */
	public PrefixTrieNode() {
	}

	public PrefixTrieNode(boolean createFullPrefixTree, char prefix) {
		this.createFullPrefixTree = createFullPrefixTree;
		this.prefix = prefix;
	}

	/**
	 * Allows the creator to create either a full of non-full prefix tree
	 * 
	 * @param createFullPrefixTree
	 */
	public PrefixTrieNode(boolean createFullPrefixTree) {
		this.createFullPrefixTree = createFullPrefixTree;
	}

	//	@Override
	public static List<String> getCompletions(PrefixTrieNode node, String searchString, int limit) {
		List<String> completions = new ArrayList<String>();

		PrefixTrieNode currentNode = node;
		int i = searchString.length() - 1;

		while (i >= 0) {
			char c = searchString.charAt(i);

			if (currentNode != null && currentNode.children != null) {
				currentNode = currentNode.children.get(c);
			} else {
				currentNode = null;
			}

			if (currentNode == null) { // no match
				return completions;
			}

			i--;
		}

		PrefixTrieNode.getCompletionsInternal(currentNode, completions, searchString, limit);

		return completions;
	}

	/**
	 * Private internal method to walk the trie and find the completions, this is called from the last matching node
	 * of the suffix
	 * 
	 * @param prefix The prefix to find the completions for
	 * @param limit Max number of results to return
	 * @return Not null list of the found completions
	 */
	private static void getCompletionsInternal(PrefixTrieNode node, List<String> completions, String suffix, int limit) {

		if (node.isEnd) {
			completions.add(suffix);
			if (completions.size() >= limit)
				return;
		}

		if (node.children != null) {
			for (Object obj : node.children.values()) {
				PrefixTrieNode child = (PrefixTrieNode) obj;
				PrefixTrieNode.getCompletionsInternal(child, completions, child.prefix + suffix, limit);
				if (completions.size() >= limit)
					break;
			}
		}
	}

	private static void getCompletionsFullWords(PrefixTrieNode node, Set<String> completions, String suffix) {
		if (node.isEnd && node.isFullWordEnd) {
			completions.add(suffix);
		}

		if (node.children != null) {
			if (node.prefix == 0 && suffix.length() > 0) { // root node
				PrefixTrieNode child = node.children.get(suffix.charAt(suffix.length() - 1));
				if (child != null) {
					PrefixTrieNode.getCompletionsFullWords(child, completions, suffix);
				}
			} else {
				for (Object obj : node.children.values()) {
					PrefixTrieNode child = (PrefixTrieNode) obj;
					PrefixTrieNode.getCompletionsFullWords(child, completions, child.prefix + suffix);
				}
			}
		}
	}

	public static void add(PrefixTrieNode node, String word) {
		addInternal(node, word, true);
	}

	/**
	 * Private internal method to recursively add prefix the prefix trie structure
	 * 
	 * @param prefix Prefix to be added to the trie
	 */
	private static void addInternal(PrefixTrieNode node, String prefix, boolean isFullWord) {

		int length = prefix.length();
		node.children = (node.children != null) ? node.children : new TCharObjectHashMap<PrefixTrieNode>(1, 0.75f);

		char c = prefix.charAt(length - 1);
		PrefixTrieNode child = node.children.get(c);

		if (child == null) {
			child = new PrefixTrieNode(node.createFullPrefixTree, c);
			node.children.put(c, child);
		}

		if (length == 1) { // This is the end of the string and not on the root node, add a child marker to denote end of suffix
			child.isEnd = true;
			child.isFullWordEnd = isFullWord;
		} else {
			String subString = prefix.substring(0, length - 1);

			PrefixTrieNode.addInternal(child, subString, isFullWord);

			if (node.createFullPrefixTree && node.prefix == 0) { // full tree and root node
				PrefixTrieNode.addInternal(node, subString, false);
			}
		}
	}

	// to be called on the root node
	public static void remove(PrefixTrieNode node, String wordToRemove) {
		Set<String> allFullWords = new HashSet<String>();

		if (node.createFullPrefixTree) { // have to account for all words that share a character with wordToRemove
			getCompletionsFullWords(node, allFullWords, "");
		} else {
			getCompletionsFullWords(node, allFullWords, wordToRemove.substring(wordToRemove.length() - 1));
		}

		Set<String> wordsToPreserve = new HashSet<String>();
		for (String fullWord : allFullWords) {
			if (fullWord.contains(wordToRemove) && !wordToRemove.equals(fullWord)) {
				wordsToPreserve.add(fullWord);
			}
		}

		removeInternal(node, wordToRemove, true, wordsToPreserve, 0);

	}

	private static void removeInternal(PrefixTrieNode node, String word, boolean isFullWord, Set<String> wordsToPreserve, int tabs) {
		int length = word.length();

		char c = word.charAt(length - 1);
		PrefixTrieNode child = node.children.get(c);
		boolean isRootNode = node.prefix == 0;
		boolean isBeginningOfWordToPreserve = false;
		boolean isPartOfWordToPreserve = false;
		boolean isEndOfWordToPreserve = false;

		for (String wordToPreserve : wordsToPreserve) {
			String charStr = "" + child.prefix;
			isBeginningOfWordToPreserve = isBeginningOfWordToPreserve || child.prefix == wordToPreserve.charAt(0);
			isPartOfWordToPreserve = isPartOfWordToPreserve || wordToPreserve.contains(charStr);
			isEndOfWordToPreserve = isEndOfWordToPreserve || child.prefix == wordToPreserve.charAt(wordToPreserve.length() - 1);
		}

		if (length == 1) {
			child.isFullWordEnd = child.isFullWordEnd && !isFullWord;

			if (!isBeginningOfWordToPreserve || !node.createFullPrefixTree) {
				child.isEnd = false;
			}

			if (!child.isEnd && !child.isFullWordEnd && child.children == null) {
				node.children.remove(c);
			}
		} else {
			String subString = word.substring(0, length - 1);
			PrefixTrieNode.removeInternal(child, subString, isFullWord, wordsToPreserve, tabs + 1);

			if (node.createFullPrefixTree && isRootNode) { // full tree and root node
				PrefixTrieNode.removeInternal(node, subString, false, wordsToPreserve, tabs + 1);
			}

			if (child.children.size() == 0 && !child.isEnd) {
				node.children.remove(child.prefix);
			}

		}
	}

	// Uncomment the following if you want to play around or do debugging.
	public static void main(String[] args) {
		PrefixTrieNode t = new PrefixTrieNode();
		PrefixTrieNode.add(t, "fod");
		PrefixTrieNode.print(t);
		List<String> expectedTrace = PrefixTrieNode.getTrace(t, 0);

		PrefixTrieNode.add(t, "f");
		PrefixTrieNode.print(t);
		PrefixTrieNode.remove(t, "f");
		PrefixTrieNode.print(t);
		List<String> actualTrace = PrefixTrieNode.getTrace(t, 0);

		boolean matches = true;
		for (int i = 0; i < actualTrace.size(); i++) {
			matches = matches && actualTrace.get(i).equals(expectedTrace.get(i));
		}
		System.out.println("Matches: " + matches);
	}

	private static String getTabs(int tabSpaces) {
		String tabs = "";
		for (int i = 0; i < tabSpaces; i++) {
			tabs = tabs + "\t";
		}
		return tabs;
	}

	public static void print(PrefixTrieNode node) {
		List<String> trace = getTrace(node, 0);
		for (String s : trace)
			System.out.println(s);
	}

	static List<String> getTrace(PrefixTrieNode node, int tabSpaces) {
		List<String> trace = new ArrayList<String>();
		String tabs = getTabs(tabSpaces);

		if (node.prefix == 0) {
			boolean isFirst = true;
			StringBuffer buff = new StringBuffer(tabs + "Root Node: " + node.children.size() + " children [");
			for (Object obj : node.children.values()) {
				if (!isFirst) {
					buff.append(", ");
				}
				buff.append(((PrefixTrieNode) obj).prefix);
				isFirst = false;
			}
			buff.append("]");
			trace.add(buff.toString());
		} else {
			StringBuffer buff = new StringBuffer(tabs + "Child Node: " + node.prefix + ": " + ((node.children == null) ? "0" : node.children.size()) + " children [");
			boolean isFirst = true;
			if (node.children != null) {
				for (Object obj : node.children.values()) {
					if (!isFirst) {
						buff.append(", ");
					}
					buff.append(((PrefixTrieNode) obj).prefix);
					isFirst = false;
				}
			}
			if (node.isEnd) {
				if (!isFirst) {
					buff.append(", ");
				}
				buff.append(Character.MAX_VALUE);
				isFirst = false;
			}
			if (node.isFullWordEnd) {
				if (!isFirst) {
					buff.append(", ");
				}
				buff.append("FWE");
				isFirst = false;
			}
			buff.append("]");
			trace.add(buff.toString());
		}

		if (node.children != null) {
			for (Object n : node.children.values()) {
				trace.addAll(getTrace(((PrefixTrieNode) n), tabSpaces + 1));
			}
		}
		return trace;
	}

}
