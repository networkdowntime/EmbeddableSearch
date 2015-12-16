package net.networkdowntime.search;

import java.util.Comparator;

/**
 * The Search Result Comparator compares search results based first by their counts and secondly by comparing their objects.
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
 * @param <E>
 */
@SuppressWarnings("rawtypes")
public class SearchResultComparator implements Comparator<SearchResult> {
	@Override
	public int compare(SearchResult o1, SearchResult o2) {
		if (o1 == null && o2 == null)
			return 0;
		else if (o1 != null && o2 == null)
			return -1;
		else if (o1 == null && o2 != null)
			return 1;
		else if (o1.weight != o2.weight) {
			Integer i1 = o1.weight;
			Integer i2 = o2.weight;
			return i2.compareTo(i1);
		} else {
			if (SearchResultType.Long.equals(o1.type)) {
				
				if (SearchResultType.Long.equals(o2.type)) {
					return ((Long) o1.result).compareTo((Long) o2.result);
				} else { // SearchResultType.String.equals(o2.type)
					return ((Long) o1.result).toString().compareTo((String) o2.result);
				}
				
			} else { // SearchResultType.String.equals(o1.type)
				
				if (SearchResultType.Long.equals(o2.type)) {
					return ((String) o1.result).compareTo(((Long) o2.result).toString());
				} else { // SearchResultType.String.equals(o2.type)
					return ((String) o1.result).compareTo((String) o2.result);
				}
				
			}
		}
	}
}
