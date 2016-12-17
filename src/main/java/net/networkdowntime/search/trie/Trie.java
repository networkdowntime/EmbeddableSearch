package net.networkdowntime.search.trie;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gnu.trove.map.hash.TCharObjectHashMap;

/**
 * Base class for implementing a trie or a partial trie. The trie can either a Suffix Trie or an Inverted Suffix Trie depending on the implementation of the abstract methods.
 * 
 * This software is licensed under the MIT license Copyright (c) 2015 Ryan Wiles
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * @author rwiles
 *
 */
public abstract class Trie {
	private static final Logger LOGGER = LogManager.getLogger(Trie.class.getName());

	protected boolean createFullTrie = true;
	protected TrieNode rootNode = new TrieNode();

	// tracks the maximium height of the tree
	private int height;

	/**
	 * Default constructor generates a full suffix trie
	 */
	public Trie() {
	}

	/**
	 * Allows the creator to create either a full of non-full trie
	 * 
	 * @param createFullTrie
	 */
	public Trie(boolean createFullTrie) {
		this.createFullTrie = createFullTrie;
	}

	/**
	 * Gets either the beginning or ending character from the word. Beginning character for a Suffix, ending for an Inverted Suffix
	 * 
	 * @param word
	 * @return
	 */
	protected abstract char getChar(String word);

	/**
	 * Gets the character from the opposite end of the word than getChar()
	 * 
	 * @param word
	 * @return
	 */
	protected abstract char getOppositeChar(String word);

	/**
	 * Removes a character from a end of the string and returns the substring. Remove the beginning character for a Suffix, ending for an Inverted Suffix
	 * 
	 * @param word
	 * @return
	 */
	protected abstract String getSubstring(String word);

	protected abstract String getSubstring(String word, int index);
	
	/**
	 * Concatenates the character and the wordPart string and returns the new cost string. wordPart + c for Suffix, c + wordPart for Inverted Suffix
	 * 
	 * @param c
	 * @param wordPart
	 * @param cost How much to increment the returned cost by
	 * @return
	 */
	protected abstract CostString addCharToWordPart(char c, CostString wordPart, int cost);

	/**
	 * Inserts the character into the wordPart string at the specified index and returns the new cost string. wordPart + c for Suffix, c + wordPart for Inverted Suffix
	 * 
	 * @param c
	 * @param wordPart
	 * @param cost How much to increment the returned cost by
	 * @return
	 */
	protected abstract CostString addCharToWordPart(char c, int index, CostString wordPart, int cost);
	
	/**
	 * Inserts the character into the wordPart string at the specified index and returns the new cost string. wordPart + c for Suffix, c + wordPart for Inverted Suffix
	 * 
	 * @param c
	 * @param wordPart
	 * @param cost How much to increment the returned cost by
	 * @return
	 */
	protected abstract CostString deleteCharFromWordPart(int index, CostString wordPart, int cost);
	
	/**
	 * Transposes the character at the specified index with the following character and returns the new cost string.
	 * 
	 * @param c
	 * @param wordPart
	 * @param cost How much to increment the returned cost by
	 * @return
	 */
	protected abstract CostString transposeCharsInWordPart(int startIndex, CostString wordPart, int cost);

	/**
	 * Concatenates the character and the wordPart string and returns the new cost string. wordPart + c for Suffix, c + wordPart for Inverted Suffix
	 * 
	 * @param c
	 * @param wordPart
	 * @param cost How much to increment the returned cost by
	 * @return
	 */
//	protected abstract CostString addCharToWordPartBackwards(char c, CostString wordPart, int cost);

	/**
	 * Gets a char[] in the correct order for node traversal to find completions. left to right for Suffix, reverse order for Inverted Suffix
	 * 
	 * @param word
	 * @return
	 */
	protected abstract char[] getCharArr(String word);

	/**
	 * Adds a word to the trie
	 * 
	 * @param word
	 */
	public int add(String word) {
		int cost = addInternal(rootNode, word, true, 0);
		if (word.length() > height) {
			height = word.length();
		}
		return cost;
	}

	/**
	 * Private internal method to recursively add the wordPart to the trie structure
	 * 
	 * @param wordPart Word part to be added to the trie
	 */
	private int addInternal(TrieNode node, String wordPart, boolean isFullWord, int cost) {
		cost += 1;
		// LOGGER.debugprintln("\t\t" + this.getClass().getSimpleName() +
		// ": adding wordPart: " + wordPart + ", currentCost: " + cost);

		int length = wordPart.length();
		node.children = (node.children != null) ? node.children : new TCharObjectHashMap<TrieNode>(1, 0.9f);

		char c = getChar(wordPart);
		TrieNode child = node.children.get(c);

		if (child == null) {
			child = new TrieNode(c);
			node.children.put(c, child);
		}

		if (length == 1) { // This is the end of the string and not on the root
							// node, add a child marker to denote end of suffix
			child.isEnd = true;
			child.isFullWordEnd = child.isFullWordEnd || isFullWord;
		} else {
			String subString = getSubstring(wordPart);

			cost += addInternal(child, subString, isFullWord, cost);

			if (createFullTrie && node.isRootNode()) { // full tree and root node
				cost += addInternal(node, subString, false, cost);
			}
		}
		return cost;
	}

	/**
	 * Remove a word from the trie. Much more expensive than adding a word because the entire trie has to be walked to ensure nodes needed to ensure the integrity of the trie are not pruned.
	 * 
	 * @param wordToRemove
	 */
	public void remove(String wordToRemove) {
		Set<String> wordsToPreserve = new HashSet<String>();

		if (createFullTrie) { // have to account for all words that share a
								// character with wordToRemove
			getFullWordsForRemoval(rootNode, wordToRemove, wordsToPreserve, new CostString("", 0));
			wordsToPreserve.remove(wordToRemove);
		}

		removeInternal(rootNode, wordToRemove, true, wordsToPreserve);

	}

	/**
	 * Private internal method to recursively remove the wordPart from the trie structure
	 * 
	 * @param node
	 * @param wordPart
	 * @param isFullWord
	 * @param wordsToPreserve
	 */
	private void removeInternal(TrieNode node, String wordPart, boolean isFullWord, Set<String> wordsToPreserve) {
		int length = wordPart.length();

		char c = getChar(wordPart);
		TrieNode child = node.children.get(c);
		boolean isRootNode = node.isRootNode();
		boolean isPartOfWordToPreserve = false;

		for (String wordToPreserve : wordsToPreserve) {
			isPartOfWordToPreserve = isPartOfWordToPreserve || child.c == getOppositeChar(wordToPreserve);
		}

		if (length == 1) {
			child.isFullWordEnd = child.isFullWordEnd && !isFullWord;

			if (!isPartOfWordToPreserve || !createFullTrie) {
				child.isEnd = false;
			}

			if (!child.isEnd && !child.isFullWordEnd && child.children == null) {
				node.children.remove(c);
			}
		} else {
			String subString = getSubstring(wordPart);
			removeInternal(child, subString, isFullWord, wordsToPreserve);

			if (createFullTrie && isRootNode) { // full tree and root node
				removeInternal(node, subString, false, wordsToPreserve);
			}

			if (child.children.isEmpty() && !child.isEnd) {
				node.children.remove(child.c);
			}

		}
	}

	/**
	 * Gets the completions from the trie for the given word part up to the limit. Walks the trie until it finds the last node for the word part and then calls getCompletionsInternal() to find the
	 * completions.
	 * 
	 * @param wordPart The word part to find completions for.
	 * @param editDistanceMax Max edit distance for the requested completions
	 * @param subStringOnly Assume that there are no typos in the wordPart (i.e. exact pattern match), use edit distance to only find word completions
	 * @return A list of the completed words.
	 */
	public Set<CostString> getCompletions(CostString wordPart, int editDistanceMax, boolean subStringOnly) {
		LOGGER.debug(getClass().getSimpleName() + ":");
		Map<Integer, CostString> completions = new HashMap<Integer, CostString>();
		Map<Integer, CostString> failures = new HashMap<Integer, CostString>();

		getRootCompletions(completions, failures, wordPart, editDistanceMax, subStringOnly, 0);

		// do sorting of completions here, need to implement comparator
		Set<CostString> retval = new HashSet<CostString>(completions.values());
		return retval;
		// return getCompletions1(wordPart, editDistanceMax, count);
	}

	private void getRootCompletions(Map<Integer, CostString> completions, Map<Integer, CostString> failures, CostString wordPart, int editDistanceMax, boolean subStringOnly, int tabs) {
		LOGGER.debug(getTabs(tabs) + "getCompletions1(): '" + wordPart + "' - " + wordPart.cost + ", failures.size(): " + failures.size());
		
		// try the exact wordPart first to handle no misspellings in the wordPart, but allow all completions
		CostString failure = failures.get(wordPart.hashCode());
		if (failure == null || wordPart.cost < failure.cost) {
			getCompletionsWalkTree(completions, failures, rootNode, wordPart, 0, editDistanceMax, subStringOnly, tabs + 1);
		} else {
			LOGGER.debug(getTabs(tabs) + "\tFailure found for " + wordPart + " - " + wordPart.cost + ", skipping");
		}
		
		if (!subStringOnly && wordPart.cost < editDistanceMax) {
			
			for (int i = 0; i < wordPart.length(); i++) {
				// find allowable deletions at current index
				CostString deletionMisspelling = deleteCharFromWordPart(i, wordPart, 1);
				LOGGER.debug(getTabs(tabs) + "\tdeleted char at index: " + i + "; new wordPart: '" + deletionMisspelling + "' - " + deletionMisspelling.cost);
				getRootCompletions(completions, failures, deletionMisspelling, editDistanceMax, subStringOnly, tabs + 1);
			}
			
		}
	}

	private void getCompletionsWalkTree(Map<Integer, CostString> completions, Map<Integer, CostString> failures, TrieNode node, CostString wordPart, int startingWordPartIndex, int editDistanceMax, boolean subStringOnly,	int tabs) {
		TrieNode currentNode = node;
		char[] charArray = getCharArr(wordPart.str);
		
		// walks the Trie based on the characters in the wordPart. fails if wordPart is not in Trie.
		for (int i = startingWordPartIndex; i < charArray.length; i++) {

			if (LOGGER.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder(getTabs(tabs) + "getCompletionsWalkTree(): index: " + i + "; char '" + charArray[i] + "'; " + wordPart + " - " + wordPart.cost + "; node char '" + currentNode.c + "'; children [");
			if (currentNode.children != null) {
				for (TrieNode n : currentNode.children.valueCollection()) {
					sb.append(n.c + ", ");
				}
			}
			sb.append("]");
			LOGGER.debug(sb.toString());
			}
			
			if (!subStringOnly && currentNode != null && wordPart.cost < editDistanceMax) {
				
				// find allowable deletions at current index
				CostString deletionMisspelling = deleteCharFromWordPart(i, wordPart, 1);
				LOGGER.debug(getTabs(tabs) + "\tdeleted char at index: " + i + "; new wordPart: '" + deletionMisspelling + "' - " + deletionMisspelling.cost);
				getCompletionsWalkTree(completions, failures, currentNode, deletionMisspelling, i, editDistanceMax, subStringOnly, tabs + 1);

				if (i + 1 < charArray.length) {
					// find allowable transpositions at current index
					CostString transposeMisspelling = transposeCharsInWordPart(i, wordPart, 1);
					LOGGER.debug(getTabs(tabs) + "\ttransposed characters at indexes: " + i + "," + (i + 1) + "; new wordPart: '" + transposeMisspelling + "'");
					getCompletionsWalkTree(completions, failures, currentNode, transposeMisspelling, i, editDistanceMax, subStringOnly, tabs + 1);
				}

				// find allowable insertion at current index
				if (currentNode.children != null) {
					for (TrieNode n : currentNode.children.valueCollection()) {
						if (i > 0) {
							CostString insterionMisspelling = addCharToWordPart(n.c, i, wordPart, 1);
							LOGGER.debug(getTabs(tabs) + "\tinserted '" + n.c + "' at index: " + (i + 1) + "; new wordPart: '" + insterionMisspelling + "'");
							getCompletionsWalkTree(completions, failures, currentNode, insterionMisspelling, i, editDistanceMax, subStringOnly, tabs + 1);
						}
						
						CostString replacementMisspelling = addCharToWordPart(n.c, i, deletionMisspelling, 0);
						LOGGER.debug(getTabs(tabs) + "\treplaced '" + n.c + "' at index: " + (i + 1) + "; new wordPart: " + replacementMisspelling + " - " + replacementMisspelling.cost);
						getCompletionsWalkTree(completions, failures, currentNode, replacementMisspelling, i, editDistanceMax, subStringOnly, tabs + 1);
					}
				}
			}
			
			char c = charArray[i];
			if (currentNode != null && currentNode.children != null) {
				currentNode = currentNode.children.get(c);
			} else {
				currentNode = null;
			}

			if (currentNode == null) { // no match in the part of the string processed so far
				LOGGER.debug(getTabs(tabs) + "\tAdded failure for " + wordPart + " - " + wordPart.cost + " in getCompletionsExactWordPartMatch()");
				failures.put(wordPart.hashCode(), wordPart);
				return; // then return
			}
		}

		getCompletionsTailInsertions(completions, failures, currentNode, wordPart, editDistanceMax, subStringOnly, tabs + 1);
	}

	/**
	 * Private internal method to walk the trie and find the completions beyond the wordPart, this is called from the last matching node of the suffix. Pre-condition, that the trie has been walked to
	 * the end of the wordPart.
	 * 
	 * @param node The current node
	 * @param completions The list of completed words
	 * @param wordPart The word being build up from walking the trie
	 * @param size Tracks the number of found completions
	 * @param limit Max number of results to return
	 * @return the current number of completions
	 */
	private void getCompletionsTailInsertions(Map<Integer, CostString> completions, Map<Integer, CostString> failures, TrieNode node, CostString wordPart, int editDistanceMax, boolean subStringOnly,
			int tabs) {
		LOGGER.debug(getTabs(tabs) + "getCompletionsTailInsertions():");
		if (node != null) {
			if (node.isEnd) {
				addCompletion(completions, wordPart, tabs);
			}

			if (node.children != null) {
				if (wordPart.cost < editDistanceMax) {
					for (Object obj : node.children.values()) {
						TrieNode child = (TrieNode) obj;
						getCompletionsTailInsertions(completions, failures, child, addCharToWordPart(child.c, wordPart, 1), editDistanceMax, subStringOnly, tabs);
					}
				} else {
					LOGGER.debug(getTabs(tabs) + "Stopped looking completions of '" + wordPart + "': wordPart.cost of " + wordPart.cost + " >= editDistanceMax of " + editDistanceMax);
				}
			}
		}
	}

	/**
	 * Only adds the wordPart if it's not in completions or this wordPart has a lower cost than an existing completion for the same string
	 * 
	 * @param completions The list of completed words
	 * @param wordPart A completed word part, i.e. pattern match up to a end node
	 */
	private void addCompletion(Map<Integer, CostString> completions, CostString wordPart, int tabs) {
		CostString existing = completions.get(wordPart.hashCode());

		StringBuilder sb = new StringBuilder((getTabs(tabs) + "addCompletion(): " + wordPart + " - " + wordPart.cost));

		if (existing == null || existing.cost > wordPart.cost) {
			completions.put(wordPart.hashCode(), wordPart);
			sb.append(" added, hashcode = " + wordPart.hashCode() + " existing: " + existing);
		} else {
			sb.append(" already there with cost " + existing.cost + ", hashcode = " + wordPart.hashCode());
		}
		LOGGER.debug(sb.toString());
	}

	/**
	 * Special method needed to support removing words from a non-partial (full) trie. For a given full word to remove, finds all of the words that could be impacted and need to be preserved.
	 * 
	 * @param node The current node
	 * @param wordToRemove A full word that is part of the trie to be removed
	 * @param completions A list of the complete words that need to be preserved
	 * @param wordBeingBuilt The word being build up from walking the trie
	 */
	private void getFullWordsForRemoval(TrieNode node, String wordToRemove, Set<String> completions, CostString wordBeingBuilt) {
		if (node.isFullWordEnd && node.isEnd && wordBeingBuilt.str.contains(wordToRemove)) {
			completions.add(wordBeingBuilt.str);
		}

		if (node.children != null) {
			if (node.isRootNode() && wordBeingBuilt.str.length() > 0) { // root node
				TrieNode child = node.children.get(getChar(wordBeingBuilt.str));
				if (child != null) {
					getFullWordsForRemoval(child, wordToRemove, completions, wordBeingBuilt);
				}
			} else {
				for (Object obj : node.children.values()) {
					TrieNode child = (TrieNode) obj;
					getFullWordsForRemoval(child, wordToRemove, completions, addCharToWordPart(child.c, wordBeingBuilt, 0));
				}
			}
		}
	}

	/**
	 * Checks if the parameter is a known full word in the trie.
	 * 
	 * @param wordCharArr The word to check if it is a known full word in the trie in getCharArr() ordering
	 * @return true if the word is a known full word otherwise false
	 */
	public boolean containsWord(char[] wordCharArr, boolean fullWordMatch) {
		TrieNode currentNode = rootNode;

		for (char c : wordCharArr) {
			if (currentNode != null && currentNode.children != null) {
				currentNode = currentNode.children.get(c);
			} else {
				currentNode = null;
			}

			if (currentNode == null) { // no match
				return false;
			}
		}

		return !fullWordMatch || currentNode.isFullWordEnd;
	}

	/**
	 * Gets the specified number of tabs as a string.
	 * 
	 * @param tabSpaces
	 * @return
	 */
	private String getTabs(int tabSpaces) {
		String tabs = "";
		for (int i = 0; i < tabSpaces; i++) {
			tabs = tabs + "\t";
		}
		return tabs;
	}

	/**
	 * Prints the trie structure for inspection.
	 */
	public void print() {
		LOGGER.info(this.getClass().getSimpleName() + ":");
		List<String> trace = getTrace();
		for (String s : trace) {
			LOGGER.info(s);
		}
	}

	/**
	 * Gets the trie structure as a list of strings. Used in the print() method and for unit testing.
	 * 
	 * @return
	 */
	public List<String> getTrace() {
		return getTrace(rootNode, 0);
	}

	/**
	 * Internal method to walk the trie and build up a readable string representation.
	 * 
	 * @param node The current node
	 * @param tabSpaces The number of tab spaces to track indentation
	 * @return The trie structure as a list of strings
	 */
	private List<String> getTrace(TrieNode node, int tabSpaces) {
		List<String> trace = new ArrayList<String>();
		String tabs = getTabs(tabSpaces);

		if (node.isRootNode()) {
			boolean isFirst = true;
			int childrenSize = (node.children == null) ? 0 : node.children.size();
			StringBuilder buff = new StringBuilder(tabs + "Root Node: " + childrenSize + " children [");
			if (node.children != null) {
				for (Object obj : node.children.values()) {
					buff.append(addCommaIfNeeded(isFirst));
					buff.append(((TrieNode) obj).c);
					isFirst = false;
				}
			}
			buff.append("]");
			trace.add(buff.toString());
		} else {
			StringBuilder buff = new StringBuilder(tabs + "Child Node: " + node.c + ": " + ((node.children == null) ? "0" : node.children.size()) + " children [");
			boolean isFirst = true;
			if (node.children != null) {
				for (Object obj : node.children.values()) {
					buff.append(addCommaIfNeeded(isFirst));
					buff.append(((TrieNode) obj).c);
					isFirst = false;
				}
			}
			if (node.isEnd) {
				buff.append(addCommaIfNeeded(isFirst));
				buff.append(Character.MAX_VALUE);
				isFirst = false;
			}
			if (node.isFullWordEnd) {
				buff.append(addCommaIfNeeded(isFirst));
				buff.append("FWE");
				isFirst = false;
			}
			buff.append("]");
			trace.add(buff.toString());
		}

		if (node.children != null) {
			for (Object obj : node.children.values()) {
				TrieNode childNode = (TrieNode) obj;
				trace.addAll(getTrace(childNode, tabSpaces + 1));
			}
		}
		return trace;
	}

	private String addCommaIfNeeded(boolean isFirst) {
		return (isFirst) ? "" : ", ";
	}
}
