package net.networkdowntime.search.trie;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gnu.trove.map.hash.TCharObjectHashMap;

/**
 * Base class for implementing a trie or a partial trie.  
 * The trie can either a Suffix Trie or a Prefix Trie depending on the implementation of the abstract methods.
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
public abstract class Trie {
	private static final Logger LOGGER = LogManager.getLogger(Trie.class.getName());

	protected boolean createFullTrie = true;
	protected TrieNode rootNode = new TrieNode();

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
	 * Gets either the beginning or ending character from the word.
	 * Beginning character for a Suffix, ending for a Prefix
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
	 * Removes a character from a end of the string and returns the substring.
	 * Remove the beginning character for a Suffix, ending for a Prefix
	 * 
	 * @param word
	 * @return
	 */
	protected abstract String getSubstring(String word);

	/**
	 * Concatenates the character and the wordPart and returns the new string.
	 * wordPart + c for Suffix, c + wordPart for Prefix
	 * @param c
	 * @param wordPart
	 * @return
	 */
	protected abstract String addCharToWordPart(char c, String wordPart);

	/**
	 * Gets a char[] in the correct order for node traversal to find completions.
	 * left to right for Suffix, reverse order for prefix
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
	public void add(String word) {
		addInternal(rootNode, word, true);
	}

	/**
	 * Private internal method to recursively add the wordPart the trie structure
	 * 
	 * @param wordPart Prefix to be added to the trie
	 */
	private void addInternal(TrieNode node, String wordPart, boolean isFullWord) {

		int length = wordPart.length();
		node.children = (node.children != null) ? node.children : new TCharObjectHashMap<TrieNode>(1, 0.9f);

		char c = getChar(wordPart);
		TrieNode child = node.children.get(c);

		if (child == null) {
			child = new TrieNode(c);
			node.children.put(c, child);
		}

		if (length == 1) { // This is the end of the string and not on the root node, add a child marker to denote end of suffix
			child.isEnd = true;
			child.isFullWordEnd = child.isFullWordEnd || isFullWord;
		} else {
			String subString = getSubstring(wordPart);

			addInternal(child, subString, isFullWord);

			if (createFullTrie && node.c == 0) { // full tree and root node
				addInternal(node, subString, false);
			}
		}
	}

	/**
	 * Remove a word from the trie.  Much more expensive than adding a word because the entire trie has
	 * to be walked to ensure nodes needed to ensure the integrity of the trie are not pruned.
	 * 
	 * @param wordToRemove
	 */
	public void remove(String wordToRemove) {
		Set<String> wordsToPreserve = new HashSet<String>();

		if (createFullTrie) { // have to account for all words that share a character with wordToRemove
			getFullWordsForRemoval(rootNode, wordToRemove, wordsToPreserve, "");
			wordsToPreserve.remove(wordToRemove);
		}

		removeInternal(rootNode, wordToRemove, true, wordsToPreserve);

	}

	/**
	 * Private internal method to recursively remove the wordPart the trie structure
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
		boolean isRootNode = node.c == 0;
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
	 * Gets the completions from the trie for the given word part up to the limit.
	 * Walks the trie until it finds the last node for the word part and then
	 * calls getCompletionsInternal() to find the completions.
	 * 
	 * @param wordPart The word part to find completions for.
	 * @param limit Max number of results to return
	 * @return A list of the completed words.
	 */
	public List<String> getCompletions(String wordPart, int limit) {
		List<String> completions = new ArrayList<String>();

		TrieNode currentNode = rootNode;

		for (char c : getCharArr(wordPart)) {
			if (currentNode != null && currentNode.children != null) {
				currentNode = currentNode.children.get(c);
			} else {
				currentNode = null;
			}

			if (currentNode == null) { // no match
				return completions;
			}
		}

		getCompletionsInternal(currentNode, completions, wordPart, 0, limit);

		return completions;
	}

	/**
	 * Private internal method to walk the trie and find the completions, this is called from the last matching node
	 * of the suffix
	 * 
	 * @param node The current node
	 * @param completions The list of completed words 
	 * @param wordPart The word being build up from walking the trie
	 * @param size Tracks the number of found completions
	 * @param limit Max number of results to return
	 * @return the current number of completions
	 */
	private int getCompletionsInternal(TrieNode node, List<String> completions, String wordPart, int size, int limit) {
		int newSize = size;
		if (node.isEnd) {
			completions.add(wordPart);
			newSize++;
		}

		if (node.children != null && size <= limit) {
			for (Object obj : node.children.values()) {
				TrieNode child = (TrieNode) obj;
				newSize = getCompletionsInternal(child, completions, addCharToWordPart(child.c, wordPart), newSize, limit);
				if (newSize == limit) {
					break;
				}
			}
		}
		return newSize;
	}

	/**
	 * Special method needed to support removing words from a non-partial (full) trie.  For a given full word to remove, finds all of the
	 * words that could be impacted and need to be preserved.
	 * 
	 * @param node The current node
	 * @param wordToRemove A full word that is part of the trie to be removed
	 * @param completions A list of the complete words that need to be preserved
	 * @param wordBeingBuilt The word being build up from walking the trie
	 */
	private void getFullWordsForRemoval(TrieNode node, String wordToRemove, Set<String> completions, String wordBeingBuilt) {
		if (node.isFullWordEnd && node.isEnd && wordBeingBuilt.contains(wordToRemove)) {
			completions.add(wordBeingBuilt);
		}

		if (node.children != null) {
			if (node.c == 0 && wordBeingBuilt.length() > 0) { // root node
				TrieNode child = node.children.get(getChar(wordBeingBuilt));
				if (child != null) {
					getFullWordsForRemoval(child, wordToRemove, completions, wordBeingBuilt);
				}
			} else {
				for (Object obj : node.children.values()) {
					TrieNode child = (TrieNode) obj;
					getFullWordsForRemoval(child, wordToRemove, completions, addCharToWordPart(child.c, wordBeingBuilt));
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

		// true,  true  = true
		// true,  false = false;
		// false, true  = true;
		// false, false = true;
		return !fullWordMatch || currentNode.isFullWordEnd;
//		if (fullWordMatch) {
//			return currentNode.isFullWordEnd;
//		} else {
//		}
	}

	/**
	 * Implements a spelling check for words with a Damerau-Levenshtein distance of 1
	 * 
	 * @param word
	 * @return
	 */
	public Set<String> getFuzzyCompletions(String word) {
		Set<String> completions = new HashSet<String>();
		completions.addAll(getMissingInsertions(word));
		completions.addAll(getMissingDeletions(word));
		completions.addAll(getMissingSubstitutions(word));
		completions.addAll(getMissingTranspositions(word));
		return completions;
	}

	private Set<String> getMissingInsertions(String word) {
		Set<String> completions = new HashSet<String>();

		char[] charArr = getCharArr(word);
		char[] newCharArr = new char[charArr.length + 1];

		newCharArr[0] = Character.MAX_VALUE;
		for (int i = 1; i < newCharArr.length; i++) {
			newCharArr[i] = charArr[i - 1];
		}

		int i = 0;
		do {
			for (char c : getChildrenForKnownPartOfWord(newCharArr)) {
				newCharArr[i] = c;
				String currentWord = new String(getCharArr(new String(newCharArr)));

//				System.out.println("checking for insertion: " + currentWord);
				if (containsWord(newCharArr, true)) {
					completions.add(currentWord);
				}
			}
			newCharArr[i] = Character.MAX_VALUE;

			if (i < newCharArr.length - 1) {
				char tmp = newCharArr[i];
				newCharArr[i] = newCharArr[i + 1];
				newCharArr[i + 1] = tmp;
			}
			i++;
		} while (i < newCharArr.length);

		return completions;
	}

	private Set<String> getMissingDeletions(String word) {
		Set<String> completions = new HashSet<String>();
		char[] charArr = word.toCharArray();
		char[] delCharArr = new char[charArr.length - 1];

		int i = 0;
		do {
			int charIndex = 0;
			for (int j = 0; j < charArr.length; j++) { // j represents the char to skip
				if (j == i) { // skip this character
				} else {
					delCharArr[charIndex] = charArr[j];
					charIndex++;
				}
			}
			String currentWord = new String(delCharArr);
//			System.out.println("checking for deletion: " + currentWord);
			if (containsWord(getCharArr(currentWord), true)) {
				completions.add(currentWord);
			}

			i++;
		} while (i < charArr.length);

		return completions;
	}

	private Set<String> getMissingSubstitutions(String word) {
		Set<String> completions = new HashSet<String>();

		char[] charArr = getCharArr(word);
		char[] newCharArr = new char[charArr.length];

		for (int i = 0; i < newCharArr.length; i++) {
			newCharArr[i] = charArr[i];
		}

		int i = 0;
		do {
			char tmp = newCharArr[i];
			newCharArr[i] = Character.MAX_VALUE;

			for (char c : getChildrenForKnownPartOfWord(newCharArr)) {
				newCharArr[i] = c;
				String currentWord = new String(getCharArr(new String(newCharArr)));

				if (containsWord(newCharArr, true)) {
					completions.add(currentWord);
				}
			}

			newCharArr[i] = tmp;
			i++;
		} while (i < newCharArr.length);

		return completions;
	}

	private Set<String> getMissingTranspositions(String word) {
		Set<String> completions = new HashSet<String>();

		char[] charArr = getCharArr(word);

		int i = 1;
		do {
			// transpose
			char tmp = charArr[i - 1];
			charArr[i - 1] = charArr[i];
			charArr[i] = tmp;

			if (containsWord(charArr, true)) {
				String currentWord = new String(getCharArr(new String(charArr)));
				completions.add(currentWord);
			}

			// undo transpose
			charArr[i] = charArr[i - 1];
			charArr[i - 1] = tmp;
			i++;
		} while (i < charArr.length);

		return completions;
	}

	private char[] getChildrenForKnownPartOfWord(char[] wordCharArr) {
		TrieNode currentNode = rootNode;

		int i = 0;
		do {
			char c = wordCharArr[i];
			//			System.out.println("node: " + currentNode.c + "; # of children: " + currentNode.children.size() + "; char: " + c);

			if (currentNode.children.contains(c)) {
				currentNode = currentNode.children.get(c);
			} else {
				return currentNode.children.keys();
			}
			i++;
		} while (currentNode != null && currentNode.children != null && i < wordCharArr.length);

		return new char[0];
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
		List<String> trace = getTrace();
		for (String s : trace)
			LOGGER.info(s);
	}

	/**
	 * Gets the trie structure as a list of strings.  Used in the print() method and for unit testing.
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

		if (node.c == 0) {
			boolean isFirst = true;
			StringBuilder buff = new StringBuilder(tabs + "Root Node: " + node.children.size() + " children [");
			for (Object obj : node.children.values()) {
				buff.append(addCommaIfNeeded(isFirst));
				buff.append(((TrieNode) obj).c);
				isFirst = false;
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
				trace.addAll(getTrace((TrieNode) obj, tabSpaces + 1));
			}
		}
		return trace;
	}

	private String addCommaIfNeeded(boolean isFirst) {
		return (isFirst) ? "" : ", ";
	}
}
