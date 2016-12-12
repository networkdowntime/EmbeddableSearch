package net.networkdowntime.search.trie;

/**
 * An inverted suffix trie is a data structure, that starting with the last letter of a word, iterates backwards through all of the characters building a tree structure. When another word is added it
 * either builds another tree in the data structure, if the ending of the word does not already exist, or extends a existing tree with the characters at the beginning of the word that are different
 * than the already indexed words. This is recursively done for all prefixes of the word to be added by stripping the last character from the end and re-adding the new word. The non-full inverted
 * suffix trie can tell you the beginnings of all of the words with a specific ending while the full inverted suffix trie can tell you all of the beginnings for any substrings of the word.
 * 
 * One of the downsides to a trie is memory usage in a performant implementation. Several attempts were made to improve the memory usage of this class: - Switching out from the common hashmap
 * implementation to the trove data structures. - Experimenting with the initial size and load factor of the hashmaps. - Creating the children hashmap on demand (children are null if there are not
 * children).
 * 
 * Full Inverted Suffix Trie vs Partial: The full inverted tree to includes not just the word, but also every prefix of the word. The non-full inverted trie does not recursively index all of the
 * word's suffixes.
 * 
 * Example full inverted suffix trie for "foo": Root Node: 2 children [f, o] Child Node: f: 0 children [￿] Child Node: o: 2 children [f, o] Child Node: f: 0 children [￿] Child Node: o: 1 children [f]
 * Child Node: f: 0 children [￿, FWE]
 * 
 * Example non-full inverted suffix trie for "foo": Root Node: 1 children [o, ] Child Node: o: 1 children [o, ] Child Node: o: 1 children [f, ] Child Node: f: 0 children [￿, FWE]
 * 
 * This software is licensed under the MIT license Copyright (c) 2016 Ryan Wiles
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
public class InvertedSuffixTrie extends Trie {

	/**
	 * Default constructor generates a full trie
	 */
	public InvertedSuffixTrie() {
		super();
	}

	/**
	 * Allows the creator to create either a full of non-full trie
	 * 
	 * @param createFullTrie
	 */
	public InvertedSuffixTrie(boolean createFullTrie) {
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
	protected String getSubstring(String word, int index) {
		return word.substring(index, word.length());
	}
	@Override
	protected CostString addCharToWordPart(char c, CostString wordPart, int cost) {
		return new CostString(c + wordPart.str, wordPart.cost + cost);
	}

	@Override
	protected CostString addCharToWordPart(char c, int index, CostString wordPart, int cost) {
		String insertion = wordPart.substring(0, wordPart.length() - index) + c + wordPart.substring(wordPart.length() - index, wordPart.length());
		return new CostString(insertion, wordPart.cost + cost);
	}

	@Override
	protected CostString deleteCharFromWordPart(int index, CostString wordPart, int cost) {
		String deletion = wordPart.substring(0, wordPart.length() - (index + 1)) + wordPart.substring(wordPart.length() - index, wordPart.length());
		return new CostString(deletion, wordPart.cost + cost);
	}

	@Override
	protected CostString transposeCharsInWordPart(int startIndex, CostString wordPart, int cost) {
		int altIndex = wordPart.length() - startIndex - 1;
//		System.out.println("wordPart: " + wordPart + "; wordPart.length(): " + wordPart.length() + "; startIndex: " + startIndex + "; altIndex: " + altIndex);
//		System.out.println("wordPart.substring(0, " + (altIndex - 1) + "): " + wordPart.substring(0, altIndex - 1));
//		System.out.println("wordPart.substring(" + (altIndex - 1) + ", " + (altIndex) + "): " + wordPart.substring(altIndex - 1, altIndex));
//		System.out.println("wordPart.substring(" + (altIndex) + ", " + (altIndex + 1) + "): " + wordPart.substring(altIndex, altIndex + 1));
//		System.out.println("wordPart.substring(" + (altIndex + 1) + ", " + wordPart.length() + "): " + wordPart.substring(altIndex + 1, wordPart.length()));
		String transpose = wordPart.substring(0, altIndex - 1) + wordPart.substring(altIndex, altIndex + 1) + wordPart.substring(altIndex - 1, altIndex) + wordPart.substring(altIndex + 1, wordPart.length());
		return new CostString(transpose, wordPart.cost + cost);
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
