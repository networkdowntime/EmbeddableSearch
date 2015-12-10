package net.networkdowntime.search.trie;

import gnu.trove.map.hash.TCharObjectHashMap;

import java.util.ArrayList;
import java.util.List;

/**
 * A prefix trei is a data structure that starting with the last letter of a word and iterates backwards through all of the characters building a tree structure.
 * When another word is added it either builds another tree in the data structure, if the ending of the word does not already exist, or extends a existing tree
 * with the characters at the beginning of the word that are different than the already indexed words.  This is recursively done for all prefixes of the word to be added
 * by stripping the last character from the end and re-adding the new word.  The non-full prefix trei can tell you the beginnings of all of the words with a specific 
 * ending while the full prefix trei can tell you all of the beginnings for any substrings of the word.
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
	 * @param createFullPrefixTree
	 */
	public PrefixTrieNode(boolean createFullPrefixTree) {
		this.createFullPrefixTree = createFullPrefixTree;
	}
	

	/* (non-Javadoc)
	 * @see net.networkdowntime.search.trie.Trei#getCompletions(java.lang.String)
	 */
	@Override
	public List<String> getCompletions(String searchString) {
		// TODO move toLowerCase out of the prefix trei node to make this class more generic
		searchString = searchString.toLowerCase();
		
		List<String> completions = new ArrayList<String>();

		PrefixTrieNode currentNode = this;
		
		int i = searchString.length() - 1;
		
		while (i >= 0) {
			char c = searchString.charAt(i);
			
			if (currentNode.children != null) {
				currentNode = currentNode.children.get(c);
				
				if (currentNode == null) { // no match
					return completions;
				}
			}
			i--;
			
		}

		completions.addAll(currentNode.getCompletionsInternal(searchString));
		
		return completions;
	}
	
	/**
	 * Private internal method to walk the trei and find the completions
	 * @param suffix The suffix to find the completions for
	 * @return Not null list of the found completions
	 */
	private List<String> getCompletionsInternal(String suffix) {
		List<String> completions = new ArrayList<String>();

		if (this.isEnd) {
			completions.add(suffix);
		}
		
		if (children != null) {
			for (Object obj : children.values()) {
				PrefixTrieNode child = (PrefixTrieNode) obj;
				completions.addAll(child.getCompletionsInternal(child.prefix + suffix));
			}
		}

		return completions;
	}

	/* (non-Javadoc)
	 * @see net.networkdowntime.search.trie.Trei#add(java.lang.String)
	 */
	@Override
	public void add(String word) {
		addInternal(word.toLowerCase());
		
	}
	
	/**
	 * Private internal method to recursively add prefix the prefix trei structure
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
			
			if (prefix.length() > 1) {
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


// Uncomment the following if you want to play around or do debugging.
//	public static void main(String[] args) {
//		PrefixTrieNode t = new PrefixTrieNode();
//		t.add("foo");
//		t.print();
//		
//	}
//	private static String getTabs(int tabSpaces) {
//		String tabs = "";
//		for (int i = 0; i < tabSpaces; i++) {
//			tabs = tabs + "\t";
//		}
//		return tabs;
//		
//	}
//
//	
//	public void print() {
//		print(0);
//	}
//
//	
//	private void print(int tabSpaces) {
//		String tabs = getTabs(tabSpaces);
//
//		if (prefix == 0) {
//			System.out.print(tabs + "Root Node: " + children.size()	+ " children [");
//			for (Object n : children.values()) {
//				System.out.print(((PrefixTrieNode)n).prefix + ", ");
//			}
//			System.out.println("]");
//		} else {
//			System.out.print(tabs + "Child Node: " + prefix + ": " + ((children == null) ? "0" : children.size()) + " children [");
//			if (children != null) {
//				for (Object n : children.values()) {
//					System.out.print(((PrefixTrieNode)n).prefix + ", ");
//				}
//			}
//			if (isEnd) {
//				System.out.print(Character.MAX_VALUE);
//			}
//			System.out.println("]");
//		}
//
//		if (children != null) {
//			for (Object n : children.values()) {
//				((PrefixTrieNode)n).print(tabSpaces + 1);
//			}
//		}
//	}
	
}
