package net.networkdowntime.search.trie;

import gnu.trove.map.hash.TCharObjectHashMap;

import java.util.ArrayList;
import java.util.List;

/**
 * A suffix trei is a data structure that starting with the last letter of a word and iterates forward through all of the 
 * characters building a tree structure. When another word is added it either builds another tree in the data structure, if 
 * the beginning of the word does not already exist, or extends a existing tree with the characters at the ending of the 
 * word that are different than the already indexed words.  This is recursively done for all suffixes of the word to be added
 * by stripping the first character from the end and re-adding the new word.  The non-full suffix trei can tell you the 
 * endings of all of the words with a specific beginning while the full prefix trei can tell you all of the endings for any 
 * substrings of the word.
 * 
 * One of the downsides to a trei is memory usage in a performant implementation.  Several attempts were made to improve the memory usage of this class.
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
public class SuffixTrieNode implements Trei {

	private boolean createFullSuffixTree = true;

	TCharObjectHashMap<SuffixTrieNode> children = null;

	char suffix;
	boolean isEnd = false;

	/**
	 * Default constructor generates a full suffix tree
	 */
	public SuffixTrieNode() {
	}

	/**
	 * Allows the creator to create either a full of non-full suffix tree
	 * 
	 * @param createFullSuffixTree
	 */
	public SuffixTrieNode(boolean createFullSuffixTree) {
		this.createFullSuffixTree = createFullSuffixTree;
	}

	@Override
	public List<String> getCompletions(String searchString, int limit) {
		List<String> completions = new ArrayList<String>();

		SuffixTrieNode currentNode = this;
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

		findLastNode(searchString).getCompletionsInternal(completions, searchString, limit);

		return completions;

	}

	/**
	 * Private internal method to walk the trei and find the completions
	 * 
	 * @param prefix The prefix to find the completions for
	 * @param limit Max number of results to return
	 * @return Not null list of the found completions
	 */
	private void getCompletionsInternal(List<String> completions, String prefix, int limit) {

		if (this.isEnd) {
			completions.add(prefix);
			if (completions.size() >= limit)
				return;
		}

		if (children != null) {
			for (Object obj : children.values()) {
				SuffixTrieNode child = (SuffixTrieNode) obj;
				child.getCompletionsInternal(completions, prefix + child.suffix, limit);
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
	 * Private internal method to recursively add a suffix to the suffix trei structure
	 * 
	 * @param suffix Suffix to be added to the trei
	 */
	private void addInternal(String suffix) {

		SuffixTrieNode child = null;

		int length = suffix.length();

		if (length > 0) {

			if (children == null)
				children = new TCharObjectHashMap<SuffixTrieNode>();// HashMap<Integer, SuffixTrieNode>(1, 0.75f);

			char c = suffix.charAt(0);
			child = children.get(c);

			if (child == null) {
				child = new SuffixTrieNode(createFullSuffixTree);
				child.suffix = c;
				children.put(c, child);
			}

			if (length > 1) {
				child.addInternal(suffix.substring(1));
			} else if (suffix.length() == 1) { // This is the end of the string and not on the root node, add a child marker to denote end of suffix
				child.isEnd = true;
			}

			if (createFullSuffixTree) {
				if (this.suffix == 0 && length > 1) { // if this is the root node
					this.addInternal(suffix.substring(1));
				}
			}

		}
	}

	public void remove(String word) {
		List<String> completions = getCompletions(word, 2);

		if (!completions.isEmpty() && completions.contains(word) && completions.size() == 1) {

			String suffixToBeRemoved = "";
			String subword = word;

			for (int i = word.length(); i >= 0; i--) {
				subword = word.substring(0, i);

				// need to know if the word is the only word on that branch of the trie and it's the exact word.
				completions = getCompletions(subword, 2);

				// check that it was a single word exact match
				if (!completions.isEmpty() && completions.contains(word) && completions.size() == 1) {
					// now, need to figure out how many of the letters in the word can be removed
					suffixToBeRemoved = subword.substring(i - 1) + suffixToBeRemoved;
				} else {
					break;
				}
			}
			if (suffixToBeRemoved.length() > 0) {
				SuffixTrieNode currentNode = findLastNode(subword);
				currentNode.children.remove(suffixToBeRemoved.charAt(0));
				if (currentNode.children.size() == 0) {
					currentNode.children = null;
				}
				remove(word.substring(1));
			}
		} else {
			SuffixTrieNode currentNode = findLastNode(word);
			currentNode.isEnd = false;
			if (word.length() > 0) {
				remove(word.substring(1));
			}
			
		}
	}

	private SuffixTrieNode findLastNode(String word) {
		SuffixTrieNode currentNode = this;
		int i = 0;

		while (i < word.length()) {
			char c = word.charAt(i);

			if (currentNode != null && currentNode.children != null) {
				currentNode = currentNode.children.get(c);
			}
			i++;
		}
		return currentNode;
	}
	
	//	 Uncomment the following if you want to play around or do debugging.
	public static void main(String[] args) {
		SuffixTrieNode t = new SuffixTrieNode(true);
		t.add("foodies");
		t.print();
		t.add("foo");
//		t.print();
		for (String s : t.getCompletions("fo", 50)) {
			System.out.println("found: " + s);
		}
		t.remove("foo");
		t.print();
		for (String s : t.getCompletions("fo", 50)) {
			System.out.println("found: " + s);
		}
	}

	private static String getTabs(int tabSpaces) {
		String tabs = "";
		for (int i = 0; i < tabSpaces; i++) {
			tabs = tabs + "\t";
		}
		return tabs;
	}

	public void print() {
		print(0);
	}

	private void print(int tabSpaces) {
		String tabs = getTabs(tabSpaces);

		if (suffix == 0) {
			boolean isFirst = true;
			System.out.print(tabs + "Root Node: " + children.size() + " children [");
			for (Object obj : children.values()) {
				if (!isFirst) {
					System.out.print(", ");
				}
				System.out.print(((SuffixTrieNode) obj).suffix);
				isFirst = false;
			}
			System.out.println("]");
		} else {
			System.out.print(tabs + "Child Node: " + suffix + ": " + ((children == null) ? "0" : children.size()) + " children [");
			boolean isFirst = true;
			if (children != null) {
				for (Object obj : children.values()) {
					if (!isFirst) {
						System.out.print(", ");
					}
					System.out.print(((SuffixTrieNode) obj).suffix);
					isFirst = false;
				}
			}
			if (this.isEnd) {
				if (!isFirst) {
					System.out.print(", ");
				}
				System.out.print(Character.MAX_VALUE);
			}
			System.out.println("]");
		}

		if (children != null) {
			for (Object obj : children.values()) {
				((SuffixTrieNode) obj).print(tabSpaces + 1);
			}
		}
	}

}
