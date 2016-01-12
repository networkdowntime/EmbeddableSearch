package net.networkdowntime.search.trie;

import gnu.trove.map.hash.TCharObjectHashMap;

import java.util.ArrayList;
import java.util.List;

/**
 * A prefix trei is a data structure that starting with the last letter of a word and iterates backwards through all of the 
 * characters building a tree structure. When another word is added it either builds another tree in the data structure, if 
 * the ending of the word does not already exist, or extends a existing tree with the characters at the beginning of the word 
 * that are different than the already indexed words.  This is recursively done for all prefixes of the word to be added by 
 * stripping the last character from the end and re-adding the new word.  The non-full prefix trei can tell you the beginnings 
 * of all of the words with a specific ending while the full prefix trei can tell you all of the beginnings for any substrings 
 * of the word.
 * 
 * One of the downsides to a trei is memory usage in a performant implementation.  Several attempts were made to improve the memory usage of this class.
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
public class PrefixTrieNode implements Trei {

	private boolean createFullPrefixTree = true;

	private TCharObjectHashMap<PrefixTrieNode> children = null;

	char prefix;
	boolean isEnd = false;

	/**
	 * Default constructor generates a full prefix tree
	 */
	public PrefixTrieNode() {
	}

	/**
	 * Allows the creator to create either a full of non-full prefix tree
	 * 
	 * @param createFullPrefixTree
	 */
	public PrefixTrieNode(boolean createFullPrefixTree) {
		this.createFullPrefixTree = createFullPrefixTree;
	}

	@Override
	public List<String> getCompletions(String searchString, int limit) {
		List<String> completions = new ArrayList<String>();

		PrefixTrieNode currentNode = this;
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

		currentNode.getCompletionsInternal(completions, searchString, limit);

		return completions;
	}

	/**
	 * Private internal method to walk the trei and find the completions, this is called from the last matching node
	 * of the suffix
	 * 
	 * @param prefix The prefix to find the completions for
	 * @param limit Max number of results to return
	 * @return Not null list of the found completions
	 */
	private void getCompletionsInternal(List<String> completions, String suffix, int limit) {

		if (this.isEnd) {
			completions.add(suffix);
			if (completions.size() >= limit)
				return;
		}

		if (children != null) {
			for (Object obj : children.values()) {
				PrefixTrieNode child = (PrefixTrieNode) obj;
				child.getCompletionsInternal(completions, child.prefix + suffix, limit);
				if (completions.size() >= limit)
					break;
			}
		}
	}

	@Override
	public void add(String word) {
		addInternal(word);
	}

	/**
	 * Private internal method to recursively add prefix the prefix trei structure
	 * 
	 * @param prefix Prefix to be added to the trei
	 */
	private void addInternal(String prefix) {

		PrefixTrieNode child = null;

		int length = prefix.length();

		if (length > 0) {

			if (children == null)
				children = new TCharObjectHashMap<PrefixTrieNode>(1, 0.75f);

			char c = prefix.charAt(length - 1);
			child = children.get(c);

			if (child == null) {
				child = new PrefixTrieNode(createFullPrefixTree);
				child.prefix = c;
				children.put(c, child);
			}

			if (length > 1) {
				child.addInternal(prefix.substring(0, length - 1));
			} else if (length == 1) { // This is the end of the string and not on the root node, add a child marker to denote end of suffix
				child.isEnd = true;
			}

			if (createFullPrefixTree) {
				if (this.prefix == 0 && length > 1) { // if this is the root node
					this.addInternal(prefix.substring(0, length - 1));
				}
			}

		}
	}

	public void remove(String word) {
		List<String> completions = getCompletions(word, 2);

		if (!completions.isEmpty() && completions.contains(word) && completions.size() == 1) {

			String prefixToBeRemoved = "";
			String subword = word;

			for (int i = 0; i <= word.length(); i++) {
				subword = word.substring(i);

				// need to know if the word is the only word on that branch of the trie and it's the exact word.
				completions = getCompletions(subword, 2);

				// check that it was a single word exact match
				if (!completions.isEmpty() && completions.contains(word) && completions.size() == 1) {
					// now, need to figure out how many of the letters in the word can be removed
					prefixToBeRemoved = prefixToBeRemoved + subword.substring(0, 1);
				} else {
					break;
				}
			}

			if (prefixToBeRemoved.length() > 0) {
				PrefixTrieNode currentNode = findLastNode(subword);

				currentNode.children.remove(prefixToBeRemoved.charAt(prefixToBeRemoved.length() - 1));
				if (currentNode.children.size() == 0) {
					currentNode.children = null;
				}
				remove(word.substring(0, word.length() - 1));
			}
		} else {
			PrefixTrieNode currentNode = findLastNode(word);
			currentNode.isEnd = false;
			if (word.length() > 0) {
				remove(word.substring(0, word.length() - 1));
			}
			
		}
	}

	private PrefixTrieNode findLastNode(String word) {
		PrefixTrieNode currentNode = this;
		int i = word.length() - 1;

		while (i >= 0) {
			char c = word.charAt(i);

			if (currentNode.children != null) {
				currentNode = currentNode.children.get(c);
			}
			i--;
		}
		return currentNode;
	}

	// Uncomment the following if you want to play around or do debugging.
	public static void main(String[] args) {
		PrefixTrieNode t = new PrefixTrieNode();
		t.add("barfoo");
		PrefixTrieNode.print(t);
		t.add("foo");
//		t.print();
//		for (String s : t.getCompletions("oo", 50)) {
//			System.out.println("found: " + s);
//		}
		//		System.out.println("Prefix for last node for b: " + t.findLastNode("b").prefix);
		t.remove("foo");
		PrefixTrieNode.print(t);
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

	private static List<String> getTrace(PrefixTrieNode node, int tabSpaces) {
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
