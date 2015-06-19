package net.networkdowntime.search.prefixTrie;

import gnu.trove.map.hash.TCharObjectHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Attempting a modified prefix trie.
 * 472,400 on keywords if I create the full suffix tree
 * 578,543 for a full suffix tree with HashMap size of 1, load factor of .75
 * 604,402 by only initializing the LinkedHashMap on demand 
 * 674,406 by switching to a HashMap on demand 
 * Able to successfully index all 843,888 keywords
 * 
 * @author rwiles
 *
 */
public class PrefixTrieNode {

	private static boolean createFullPrefixTree = true;
	
	TCharObjectHashMap<PrefixTrieNode> children = null; 
	
	char prefix;
	boolean isEnd = false;

	
	// only performed on the root node
	public List<String> getCompletions(String searchString) {
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

	
	public void add(String prefix) {
		addInternal(prefix.toLowerCase());
		
	}
	
	
	private void addInternal(String prefix) {

		PrefixTrieNode child = null;

		int length = prefix.length();
		
		if (length > 0) {
			
			if (children == null)
				children = new TCharObjectHashMap<PrefixTrieNode>(1, 0.75f);
			
			char c = prefix.charAt(length - 1);
			child = children.get(c);

			if (child == null) {
				child = new PrefixTrieNode();
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
//			for (PrefixTrieNode n : children.values()) {
//				System.out.print(n.prefix + ", ");
//			}
//			System.out.println("]");
//		} else {
//			System.out.print(tabs + "Child Node: " + prefix + ": " + ((children == null) ? "0" : children.size()) + " children [");
//			if (children != null) {
//				for (PrefixTrieNode n : children.values()) {
//					System.out.print(n.prefix + ", ");
//				}
//			}
//			if (isEnd) {
//				System.out.print(Character.MAX_VALUE);
//			}
//			System.out.println("]");
//		}
//
//		if (children != null) {
//			for (PrefixTrieNode n : children.values()) {
//				n.print(tabSpaces + 1);
//			}
//		}
//	}
	
}
