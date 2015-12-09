package net.networkdowntime.search.trie;

import gnu.trove.map.hash.TCharObjectHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A suffix trei is a data structure that starting with the last letter of a word and iterates forward through all of the characters building a tree structure.
 * When another word is added it either builds another tree in the data structure, if the beginning of the word does not already exist, or extends a existing tree
 * with the characters at the ending of the word that are different than the already indexed words.  This is recursively done for all suffixes of the word to be added
 * by stripping the first character from the end and re-adding the new word.  The non-full suffix trei can tell you the endings of all of the words with a specific 
 * beginning while the full prefix trei can tell you all of the endings for any substrings of the word.
 * 
 * One of the downsides to a trei is memory usage in a performant implementation.  Several attempts were made to improve the memory usage of this class.
 * 	Switching out from the common hashmap implementation to the trove data structures.
 * 	Experimenting with the initial size and load factor of the hashmaps.
 * 	Creating the children hashmap on demand.
 * 	One potential improvement that I haven't tested yet is converting the methods to static to reduce the per Object memory footprint.
 * 
 * Full Suffix Tree vs Not: I characterize a full suffix tree to include not just the word, but also every suffix of the word. The non-full suffix tree does not 
 * recursively index all of the words prefixes.  
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
	 * @param createFullSuffixTree
	 */
	public SuffixTrieNode(boolean createFullSuffixTree) {
		this.createFullSuffixTree = createFullSuffixTree;
	}
	
	
	/* (non-Javadoc)
	 * @see net.networkdowntime.search.trie.Trei#getCompletions(java.lang.String)
	 */
	@Override
	public List<String> getCompletions(String searchString) {
		searchString = searchString.toLowerCase();

		List<String> completions = new ArrayList<String>();

		SuffixTrieNode currentNode = this;
		SuffixTrieNode previousNode = this;
		
		int i = 0;
		
		while (i < searchString.length()) {
			char c = searchString.charAt(i);
	
			previousNode = currentNode;
			if (currentNode.children != null) {
				currentNode = currentNode.children.get(c);
			} else {
				currentNode = null;
			}
			
			if (currentNode == null) { // no match
				if (previousNode != null) {
					completions.addAll(previousNode.getCompletionsInternal(searchString.substring(0, i)));
				}
				return completions;
			}
			i++;
		}

		completions.addAll(currentNode.getCompletionsInternal(searchString));
		
		return completions;
	}
	
	
	/**
	 * Private internal method to walk the trei and find the completions
	 * @param prefix The prefix to find the completions for
	 * @return Not null list of the found completions
	 */
	private List<String> getCompletionsInternal(String prefix) {
		List<String> completions = new ArrayList<String>();

		if (this.isEnd) {
			completions.add(prefix);
		}
		
		if (children != null) {
			for (Object obj : children.values()) {
				SuffixTrieNode child = (SuffixTrieNode) obj;
				completions.addAll(child.getCompletionsInternal(prefix + child.suffix));
			}
		}

		return completions;
	}

	
	/* (non-Javadoc)
	 * @see net.networkdowntime.search.trie.Trei#add(java.lang.String)
	 */
	@Override
	public void add(String suffix) {
		addInternal(suffix.toLowerCase());
		
	}
	
	
	/**
	 * Private internal method to recursively add prefix the prefix trei structure
	 * @param prefix Prefix to be added to the trei
	 */
	private void addInternal(String suffix) {

		SuffixTrieNode child = null;

		if (suffix.length() > 0) {
			
			if (children == null)
				children = new TCharObjectHashMap<SuffixTrieNode>();// HashMap<Integer, SuffixTrieNode>(1, 0.75f);
			
			char c = suffix.charAt(0);
			child = children.get(c);

			if (child == null) {
				child = new SuffixTrieNode(createFullSuffixTree);
				child.suffix = c;
				children.put(c, child);
			} 
			
			if (suffix.length() > 1) {
				child.addInternal(suffix.substring(1));
			} else if (suffix.length() == 1) { // This is the end of the string and not on the root node, add a child marker to denote end of suffix
				child.isEnd = true;
			}
			
			if (createFullSuffixTree) {
				if (this.suffix == 0) { // if this is the root node
					this.addInternal(suffix.substring(1));
				}
			}

		}
	}

	
	// Uncomment the following if you want to play around or do debugging.
//	public static void main(String[] args) {
//		SuffixTrieNode t = new SuffixTrieNode(true);
//		t.add("foo");
//		
//		t.print();
//	}
//
//	public void print() {
//		print(0);
//	}
//	
//	private void print(int tabSpaces) {
//		String tabs = getTabs(tabSpaces);
//
//		if (children != null) {
//			if (suffix == 0) {
//				System.out.print(tabs + "Root Node: " + children.size()	+ " children [");
//				for (Object obj : children.values()) {
//					SuffixTrieNode n = (SuffixTrieNode) obj;
//					System.out.print(n.suffix + ", ");
//				}
//				System.out.println("]");
//			} else {
//				System.out.print(tabs + "Child Node: " + suffix + ": " + ((children == null) ? "0" : children.size()) + " children [");
//				if (children != null) {
//					for (Object obj : children.values()) {
//						SuffixTrieNode n = (SuffixTrieNode) obj;
//						System.out.print(n.suffix + ", ");
//					}
//				}
//				if (isEnd) {
//					System.out.print(Character.MAX_VALUE);
//				}
//				System.out.println("]");
//			}
//	
//			if (children != null) {
//				for (Object obj : children.values()) {
//					SuffixTrieNode n = (SuffixTrieNode) obj;
//					n.print(tabSpaces + 1);
//				}
//			}
//		}
//	}
//
//	private static String getTabs(int tabSpaces) {
//		String tabs = "";
//		for (int i = 0; i < tabSpaces; i++) {
//			tabs = tabs + "\t";
//		}
//		return tabs;
//	}

}
