package net.networkdowntime.search.trie;

import gnu.trove.map.hash.TCharObjectHashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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
public class SuffixTrieNode {

	private boolean createFullSuffixTree = true;

	TCharObjectHashMap<SuffixTrieNode> children = null;

	char suffix;
	boolean isEnd = false;
	boolean isFullWordEnd = false;

	/**
	 * Default constructor generates a full suffix tree
	 */
	public SuffixTrieNode() {
	}

	public SuffixTrieNode(boolean createFullSuffixTree, char suffix) {
		this.createFullSuffixTree = createFullSuffixTree;
		this.suffix = suffix;
	}

	/**
	 * Allows the creator to create either a full of non-full suffix tree
	 * 
	 * @param createFullSuffixTree
	 */
	public SuffixTrieNode(boolean createFullSuffixTree) {
		this.createFullSuffixTree = createFullSuffixTree;
	}

	//	@Override
	public static List<String> getCompletions(SuffixTrieNode node, String searchString, int limit) {
		List<String> completions = new ArrayList<String>();

		SuffixTrieNode currentNode = node;
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

		SuffixTrieNode.getCompletionsInternal(currentNode, completions, searchString, limit);

		return completions;
	}

	/**
	 * Private internal method to walk the trie and find the completions, this is called from the last matching node
	 * of the prefix
	 * 
	 * @param prefix The prefix to find the completions for
	 * @param limit Max number of results to return
	 * @return Not null list of the found completions
	 */
	private static void getCompletionsInternal(SuffixTrieNode node, List<String> completions, String prefix, int limit) {

		if (node.isEnd) {
			completions.add(prefix);
			if (completions.size() >= limit)
				return;
		}

		if (node.children != null) {
			for (Object obj : node.children.values()) {
				SuffixTrieNode child = (SuffixTrieNode) obj;
				SuffixTrieNode.getCompletionsInternal(child, completions, prefix + child.suffix, limit);
				if (completions.size() >= limit)
					break;
			}
		}
	}

	private static void getCompletionsFullWords(SuffixTrieNode node, Set<String> completions, String prefix) {
		if (node.isEnd && node.isFullWordEnd) {
			completions.add(prefix);
		}

		if (node.children != null) {
			if (node.suffix == 0 && prefix.length() > 0) { // root node
				SuffixTrieNode child = node.children.get(prefix.charAt(0));
				if (child != null) {
					SuffixTrieNode.getCompletionsFullWords(child, completions, prefix);
				}
			} else {
				for (Object obj : node.children.values()) {
					SuffixTrieNode child = (SuffixTrieNode) obj;
					SuffixTrieNode.getCompletionsFullWords(child, completions, prefix + child.suffix);
				}
			}
		}
	}

	public static void add(SuffixTrieNode node, String word) {
		addInternal(node, word, true);
	}

	/**
	 * Private internal method to recursively add a suffix to the suffix trie structure
	 * 
	 * @param suffix Suffix to be added to the trie
	 */
	private static void addInternal(SuffixTrieNode node, String suffix, boolean isFullWord) {

		int length = suffix.length();
		node.children = (node.children != null) ? node.children : new TCharObjectHashMap<SuffixTrieNode>(1, 0.75f);

		char c = suffix.charAt(0);
		SuffixTrieNode child = node.children.get(c);

		if (child == null) {
			child = new SuffixTrieNode(node.createFullSuffixTree, c);
			node.children.put(c, child);
		}

		if (length == 1) { // This is the end of the string and not on the root node, add a child marker to denote end of suffix
			child.isEnd = true;
			child.isFullWordEnd = isFullWord;
		} else {
			String subString = suffix.substring(1);

			SuffixTrieNode.addInternal(child, subString, isFullWord);

			if (node.createFullSuffixTree && node.suffix == 0) { // full tree and root node
				SuffixTrieNode.addInternal(node, subString, false);
			}
		}
	}

	// to be called on the root node
	public static void remove(SuffixTrieNode node, String wordToRemove) {
		Set<String> allFullWords = new HashSet<String>();

		if (node.createFullSuffixTree) { // have to account for all words that share a character with wordToRemove
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

	private static void removeInternal(SuffixTrieNode node, String word, boolean isFullWord, Set<String> wordsToPreserve, int tabs) {
		int length = word.length();

		char c = word.charAt(0);
		SuffixTrieNode child = node.children.get(c);
		boolean isRootNode = node.suffix == 0;
		boolean isBeginningOfWordToPreserve = false;
		boolean isPartOfWordToPreserve = false;
		boolean isEndOfWordToPreserve = false;

		for (String wordToPreserve : wordsToPreserve) {
			String charStr = "" + child.suffix;
			isBeginningOfWordToPreserve = isBeginningOfWordToPreserve || child.suffix == wordToPreserve.charAt(0);
			isPartOfWordToPreserve = isPartOfWordToPreserve || wordToPreserve.contains(charStr);
			isEndOfWordToPreserve = isEndOfWordToPreserve || child.suffix == wordToPreserve.charAt(wordToPreserve.length() - 1);
		}

		if (length == 1) {
			child.isFullWordEnd = child.isFullWordEnd && !isFullWord;

			if (!isEndOfWordToPreserve || !node.createFullSuffixTree) {
				child.isEnd = false;
			}

			if (!child.isEnd && !child.isFullWordEnd && child.children == null) {
				node.children.remove(c);
			}
		} else {
			String subString = word.substring(1);
			SuffixTrieNode.removeInternal(child, subString, isFullWord, wordsToPreserve, tabs + 1);

			if (node.createFullSuffixTree && isRootNode) { // full tree and root node
				SuffixTrieNode.removeInternal(node, subString, false, wordsToPreserve, tabs + 1);
			}

			if (child.children.size() == 0 && !child.isEnd) {
				node.children.remove(child.suffix);
			}

		}
	}

	//	 Uncomment the following if you want to play around or do debugging.
	public static void main(String[] args) {
		SuffixTrieNode t = new SuffixTrieNode(true);
		SuffixTrieNode.add(t, "oof");
		SuffixTrieNode.add(t, "owt");
		SuffixTrieNode.print(t);
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

	private static String getTabs(int tabSpaces) {
		String tabs = "";
		for (int i = 0; i < tabSpaces; i++) {
			tabs = tabs + "\t";
		}
		return tabs;
	}

	public static void print(SuffixTrieNode node) {
		List<String> trace = getTrace(node, 0);
		for (String s : trace)
			System.out.println(s);
	}

	static List<String> getTrace(SuffixTrieNode node, int tabSpaces) {
		List<String> trace = new ArrayList<String>();
		String tabs = getTabs(tabSpaces);

		if (node.suffix == 0) {
			boolean isFirst = true;
			StringBuffer buff = new StringBuffer(tabs + "Root Node: " + node.children.size() + " children [");
			for (Object obj : node.children.values()) {
				if (!isFirst) {
					buff.append(", ");
				}
				buff.append(((SuffixTrieNode) obj).suffix);
				isFirst = false;
			}
			buff.append("]");
			trace.add(buff.toString());
		} else {
			StringBuffer buff = new StringBuffer(tabs + "Child Node: " + node.suffix + ": " + ((node.children == null) ? "0" : node.children.size()) + " children [");
			boolean isFirst = true;
			if (node.children != null) {
				for (Object obj : node.children.values()) {
					if (!isFirst) {
						buff.append(", ");
					}
					buff.append(((SuffixTrieNode) obj).suffix);
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
			for (Object obj : node.children.values()) {
				trace.addAll(getTrace(((SuffixTrieNode) obj), tabSpaces + 1));
			}
		}
		return trace;
	}

}
