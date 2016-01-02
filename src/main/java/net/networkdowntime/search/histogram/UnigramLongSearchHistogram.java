package net.networkdowntime.search.histogram;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Extends the UnigramSearchHistogram and provides public methods for add and remove.
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
public class UnigramLongSearchHistogram extends UnigramSearchHistogram {
	static final Logger logger = LogManager.getLogger(UnigramLongSearchHistogram.class.getName());

	/**
	 * Adds a word along with it's result to the search histogram
	 * 
	 * @param word Word to be added
	 * @param result The search result to associate with the word
	 */
	public void add(String word, Long result) {
		word = word.toLowerCase();
		int wordKey = word.hashCode();

		addInternal(this, wordKey, result);
	}

	/**
	 * Removes a word/result from the search histogram.  If the word is associated with multiple results, they will be left alone.
	 * 
	 * @param word The word to remove the result for.
	 * @param result The result to be removed.
	 */
	public void remove(String word, Long result) {
		word = word.toLowerCase();
		int wordKey = word.hashCode();

		removeInternal(this, wordKey, result);
	}

}