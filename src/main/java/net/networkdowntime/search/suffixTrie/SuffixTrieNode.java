package net.networkdowntime.search.suffixTrie;

import gnu.trove.map.hash.TCharObjectHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Attempting a modified suffix trie.
 * 472,400 on keywords if I create the full suffix tree
 * 578,543 for a full suffix tree with HashMap size of 1, load factor of .75
 * 604,402 by only initializing the LinkedHashMap on demand 
 * 674,406 by switching to a HashMap on demand 
 * Able to successfully index all 843,888 keywords
 * 
 * @author rwiles
 *
 */
public class SuffixTrieNode {

	private static boolean createFullSuffixTree = true;
	
	TCharObjectHashMap<SuffixTrieNode> children = null;
	
	char suffix;
	boolean isEnd = false;

	
	public SuffixTrieNode() {
		
	}

	
	public SuffixTrieNode(boolean fullSuffixTree) {
		createFullSuffixTree = fullSuffixTree;
	}
	
	
	// only performed on the root node
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
					completions.addAll(previousNode.getCompletions(searchString.substring(0, i), 0));
				}
				return completions;
			}
			i++;
		}

		completions.addAll(currentNode.getCompletions(searchString, 0));
		
		return completions;
	}
	
	
	private List<String> getCompletions(String prefix, int wordHashCode) {
		List<String> completions = new ArrayList<String>();

		if (this.isEnd) {
			completions.add(prefix);
		}
		
		if (children != null) {
			for (Object obj : children.values()) {
				SuffixTrieNode child = (SuffixTrieNode) obj;
				completions.addAll(child.getCompletions(prefix + child.suffix, wordHashCode));
			}
		}

		return completions;
	}

	
	public void add(String suffix) {
		addInternal(suffix.toLowerCase());
		
	}
	
	
	private void addInternal(String suffix) {

		SuffixTrieNode child = null;

		if (suffix.length() > 0) {
			
			if (children == null)
				children = new TCharObjectHashMap<SuffixTrieNode>();// HashMap<Integer, SuffixTrieNode>(1, 0.75f);
			
			char c = suffix.charAt(0);
			child = children.get(c);

			if (child == null) {
				child = new SuffixTrieNode();
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

	
//	public void print() {
//		print(0);
//	}

	
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

	
	public static void main(String... args) {
		SuffixTrieNode root = new SuffixTrieNode();
	
//		root.add("cacao");
//		root.add("ban");
//		root.add("bad");
//		root.add("band");
//		root.add("banana");
//		root.add("bandy");
//		root.print();
//		
//		searchFor(root, "b");
//		searchFor(root, "ba");
//		searchFor(root, "ban");
		long baseLoanNumber = 100000000;
		for (long i=0; i <     25570000; i++) { // max limit where it runs out of memory
			root.add((baseLoanNumber + i) + "");
			if (i % 1000 == 0) 
				System.out.println(i);
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

	
//	private static void searchFor(Node suffixTree, String searchString) {
//		for (String s : suffixTree.getCompletions(searchString)) {
//			System.out.println(s);
//		}
//		System.out.println();
//	}

}
