package net.networkdowntime.search;

import java.util.ArrayList;
import java.util.List;

import net.networkdowntime.search.engine.InMemorySearchEngine;
import net.networkdowntime.search.histogram.DigramHistogram;
import net.networkdowntime.search.textProcessing.ContentSplitter;
import net.networkdowntime.search.textProcessing.KeywordScrubber;
import net.networkdowntime.search.textProcessing.TextScrubber;


public class Test {

	public static void main(String[] args) {

		InMemorySearchEngine se = new InMemorySearchEngine();
		
		se.add(null, null, "cacao");
		se.add(null, null, "cacao");
		se.add(null, null, "cacao");
		se.add(null, null, "cacao");
		
		se.add(null, null, "ban");

		se.add(null, null, "bad");
		se.add(null, null, "bad");

		se.add(null, null, "band");
		se.add(null, null, "band");
		se.add(null, null, "band");
		se.add(null, null, "band");
		se.add(null, null, "band");
		se.add(null, null, "band");

		se.add(null, null, "banana");
		se.add(null, null, "banana");
		se.add(null, null, "banana");
		se.add(null, null, "banana");
		se.add(null, null, "banana");

		se.add(null, null, "bandy");
		se.add(null, null, "bandy");
		se.add(null, null, "bandy");

		// Results should be ordered as follows:
		// 1 - band
		// 2 - banana
		// 3 - cacao
		// 4 - bandy
		// 5 - bad
		// 6 - ban

		System.out.println("getCompletions(\"a\")");
		for (String word : se.getCompletions("a", false)) {
			System.out.println("\t" + word);
		}

		System.out.println("getCompletions(\"ban\")");
		for (String word : se.getCompletions("ban", false)) {
			System.out.println("\t" + word);
		}

		System.out.println("getCompletions(\"banan\")");
		for (String word : se.getCompletions("banan", false)) {
			System.out.println("\t" + word);
		}

		System.out.println("getCompletions(\"anana\")");
		for (String word : se.getCompletions("anana", false)) {
			System.out.println("\t" + word);
		}

		System.out.println("getCompletions(\"banana\")");
		for (String word : se.getCompletions("banana", false)) {
			System.out.println("\t" + word);
		}

		se.add(null, null, "The quick brown fox jumps over the lazy dog");
		List<String> orderedList = se.getCompletions("ui b", true);
		for (String s : orderedList) 
			System.out.println(s);

	}

}
