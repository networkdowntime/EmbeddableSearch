package net.networkdowntime.search;

/**
 * Contains the details about the result of a search.  Including the SearchResultType, the actual result, and its search weight. 
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
 * @param <T>
 */
public class SearchResult<T> {
	SearchResultType type;
	T result;
	int weight;

	/**
	 * Creates a Search Result with the specified values
	 * 
	 * @param type The data type that is being returned in result
	 * @param result The search result
	 * @param weight The weight of that search result
	 */
	public SearchResult(SearchResultType type, T result, int weight) {
		super();
		this.type = type;
		this.result = result;
		this.weight = weight;
	}

	/**
	 * Gets the data type of the search result
	 * 
	 * @return The search result type
	 */
	public SearchResultType getType() {
		return type;
	}

	/**
	 * Gets the search result value
	 * 
	 * @return The value for the search result
	 */
	public T getResult() {
		return result;
	}

	/**
	 * Gets the weight of the search result
	 * 
	 * @return
	 */
	public int getWeight() {
		return weight;
	}

}
