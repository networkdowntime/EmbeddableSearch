package net.networkdowntime.search.trie;

/**
 * A prefix trie is a data structure that starting with the last letter of a word and iterates backwards through all of the 
 * characters building a tree structure. When another word is added it either builds another tree in the data structure, if 
 * the ending of the word does not already exist, or extends a existing tree with the characters at the beginning of the word 
 * that are different than the already indexed words.  This is recursively done for all prefixes of the word to be added by 
 * stripping the last character from the end and re-adding the new word.  The non-full prefix trie can tell you the beginnings 
 * of all of the words with a specific ending while the full prefix trie can tell you all of the beginnings for any substrings 
 * of the word.
 * 
 * One of the downsides to a trie is memory usage in a performant implementation.  Several attempts were made to improve the 
 * memory usage of this class:
 * 	- Switching out from the common hashmap implementation to the trove data structures.
 * 	- Experimenting with the initial size and load factor of the hashmaps.
 * 	- Creating the children hashmap on demand (children are null if there are not children).
 * 
 * Full Prefix Tree vs Partial: I characterize a full prefix tree to include not just the word, but also every prefix of the 
 * word. The non-full prefix tree does not recursively index all of the words suffixes.  
 * 
 * Example full prefix tree for "foo":
 * 	Root Node: 2 children [f, o]
 *		Child Node: f: 0 children [￿]
 *		Child Node: o: 2 children [f, o]
 *			Child Node: f: 0 children [￿]
 *			Child Node: o: 1 children [f]
 *				Child Node: f: 0 children [￿, FWE]
 * 
 * Example non-full prefix tree for "foo":
 *	Root Node: 1 children [o, ]
 *		Child Node: o: 1 children [o, ]
 *			Child Node: o: 1 children [f, ]
 *				Child Node: f: 0 children [￿, FWE]
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
public class PrefixTrie extends Trie {

	/**
	 * Default constructor generates a full trie
	 */
	public PrefixTrie() {
		super();
	}

	/**
	 * Allows the creator to create either a full of non-full trie
	 * 
	 * @param createFullTrie
	 */
	public PrefixTrie(boolean createFullTrie) {
		super(createFullTrie);
	}

	@Override
	protected char getChar(String word) {
		return word.charAt(word.length() - 1);
	}

	@Override
	protected char getOppositeChar(String word) {
		return word.charAt(0);
	}

	@Override
	protected String getSubstring(String word) {
		return word.substring(0, word.length() - 1);
	}

	@Override
	protected String addCharToWordPart(char c, String wordPart) {
		return c + wordPart;
	}

	@Override
	protected char[] getCharArr(String word) {
		char[] retval = word.toCharArray();

		int endCharIndex = retval.length - 1;
		int halfLength = retval.length / 2;

		for (int i = 0; i < halfLength; i++) {

			char t = retval[i];
			retval[i] = retval[endCharIndex - i];
			retval[endCharIndex - i] = t;
		}
		return retval;
	}
}
