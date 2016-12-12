package net.networkdowntime.search.trie;

/**
 * A suffix trie is a data structure that, starting with the first letter of a word, iterates forward through all of the 
 * characters building a tree structure. When another word is added it either builds another tree in the data structure, if 
 * the beginning of the word does not already exist, or extends a existing tree with the characters at the ending of the
 * word that are different than the already indexed words.  This is recursively done for all suffixes of the word to be added
 * by stripping the first character from the end and re-adding the new word.  The non-full suffix trie can tell you the 
 * endings of all of the words with a specific beginning while the full suffix trie can tell you all of the endings for any
 * substrings of the word.
 * 
 * One of the downsides to a trie is memory usage in a performant implementation.  Several attempts were made to improve the 
 * memory usage of this class:
 * 	- Switching out from the common hashmap implementation to the trove data structures.
 * 	- Experimenting with the initial size and load factor of the hashmaps.
 * 	- Creating the children hashmap on demand (children are null if there are not children).
 * 
 * Full Suffix Trie vs Partial: The full suffix trie includes not just the word, but also every suffix of the 
 * word. The non-full suffix trie does not recursively index all of the word's prefixes.
 * 
 * Example full suffix trie for "foo":
 *	Root Node: 2 children [o, f]
 *		Child Node: o: 1 children [o, ￿]
 *			Child Node: o: 0 children [￿]
 *		Child Node: f: 1 children [o]
 *			Child Node: o: 1 children [o]
 *				Child Node: o: 0 children [￿, FWE]
 * 
 * Example non-full suffix tree for "foo":
 *	Root Node: 1 children [f]
 *		Child Node: f: 1 children [o]
 *			Child Node: o: 1 children [o]
 *				Child Node: o: 1 children [o, FWE]
 *  
 * This software is licensed under the MIT license
 * Copyright (c) 2016 Ryan Wiles
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
public class SuffixTrie extends Trie {

	/**
	 * Default constructor generates a full trie
	 */
	public SuffixTrie() {
		super();
	}

	/**
	 * Allows the creator to create either a full of non-full trie
	 * 
	 * @param createFullTrie
	 */
	public SuffixTrie(boolean createFullTrie) {
		super(createFullTrie);
	}

	@Override
	protected char getChar(String word) {
		return word.charAt(0);
	}

	@Override
	protected char getOppositeChar(String word) {
		return word.charAt(word.length() - 1);
	}

	@Override
	protected String getSubstring(String word) {
		return word.substring(1);
	}

	@Override
	protected String getSubstring(String word, int index) {
		return word.substring(0, index);
	}

	@Override
	protected CostString addCharToWordPart(char c, CostString wordPart, int cost) {
		return new CostString(wordPart.str + c, wordPart.cost + cost);
	}

	@Override
	protected CostString addCharToWordPart(char c, int index, CostString wordPart, int cost) {
		String insterion = wordPart.substring(0, index) + c + wordPart.substring(index, wordPart.length());
		return new CostString(insterion, wordPart.cost + cost);
	}

	@Override
	protected CostString deleteCharFromWordPart(int index, CostString wordPart, int cost) {
		String deletion = wordPart.substring(0, index) + wordPart.substring(index + 1, wordPart.length());
		return new CostString(deletion, wordPart.cost + cost);
	}

	@Override
	protected CostString transposeCharsInWordPart(int startIndex, CostString wordPart, int cost) {
		String transpose = wordPart.substring(0, startIndex) + wordPart.substring(startIndex + 1, startIndex + 2) + wordPart.substring(startIndex, startIndex + 1) + wordPart.substring(startIndex + 2, wordPart.length());
		return new CostString(transpose, wordPart.cost + cost);
	}

	@Override
	protected char[] getCharArr(String word) {
		return word.toCharArray();
	}
}
