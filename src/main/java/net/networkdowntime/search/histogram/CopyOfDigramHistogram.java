//package net.networkdowntime.search.histogram;
//
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.TreeSet;
//
//public class CopyOfDigramHistogram {
//
//	// To minimize map size, the lexographically smaller word is stored as the primary lookup word
//	Map<Integer, UnigramHistogram> histogram = new HashMap<Integer, UnigramHistogram>();
//	
//	// A false entry in orderReversed indicates that the word order has been reversed
//	// A true entry in orderReversed indicates that both forward and reverse order are valid
//	Map<Integer, Boolean> orderReversed = new HashMap<Integer, Boolean>();
//
//	public static void add(CopyOfDigramHistogram digram, String firstWord, String secondWord) {
//		firstWord = firstWord.toLowerCase();
//		secondWord = secondWord.toLowerCase();
//		
//		boolean reversed = firstWord.compareTo(secondWord) > 0;
//		if (reversed) {
//			String temp = secondWord;
//			secondWord = firstWord;
//			firstWord = temp;
//		}
//
//		UnigramHistogram unigram = digram.histogram.get(firstWord.hashCode());
//		if (unigram == null) {
//			unigram = new UnigramHistogram();
//			digram.histogram.put(firstWord.hashCode(), unigram);
//		}
//		int beforeCount = UnigramHistogram.getOccuranceCount(unigram, secondWord);
//		
//		UnigramHistogram.add(unigram, secondWord);
//
//		Boolean previouslyReversed = digram.orderReversed.get(firstWord.hashCode());
//		
//		System.out.println("DigramHistogram.add(): firstWord=" + firstWord + "; secondWord=" + secondWord + "; reversed=" + reversed + "; previouslyReversed=" + previouslyReversed + "; beforeCount=" + beforeCount + "; count=" + UnigramHistogram.getOccuranceCount(unigram, secondWord));
//		
//		if (previouslyReversed == null) {
//			if (reversed) {
//				if (beforeCount > 0) {
//					System.out.println("case 1");
//					// previously added in non-reversed order, marking as true (both forward/backward are valid)
//					digram.orderReversed.put(firstWord.hashCode(), true);
//				} else {
//					System.out.println("case 2");
//					// new entry and is reversed, marking as false (only backward is valid)
//					digram.orderReversed.put(firstWord.hashCode(), false);
//				}
//			}
//		} else if (!previouslyReversed && !reversed) {
//			System.out.println("case 5");
//			// previously backwards and currently forward, changing from false to true
//			digram.orderReversed.put(firstWord.hashCode(), true);
//		}
//	}
//	
////	public static void remove(DigramHistogram unigram, String word) {
////		word = word.toLowerCase();
////
////		Integer count = unigram.histogram.get(word.hashCode());
////		
////		if (count != null) {
////			if (count <= 1) {
////				unigram.histogram.remove(word.hashCode());
////			} else {
////				count = count - 1;
////				unigram.histogram.put(word.hashCode(), count);
////			}
////		}
////	}
//	
//	public static int getOccuranceCount(CopyOfDigramHistogram digram, String firstWord, String secondWord) {
//		firstWord = firstWord.toLowerCase();
//		secondWord = secondWord.toLowerCase();
//		
//		boolean reversed = firstWord.compareTo(secondWord) > 0;
//		if (reversed) {
//			String temp = secondWord;
//			secondWord = firstWord;
//			firstWord = temp;
//		}
//
//		int count = 0;
//		
//		UnigramHistogram unigram = digram.histogram.get(firstWord.hashCode());
//		if (unigram != null) {
//			count = UnigramHistogram.getOccuranceCount(unigram, secondWord);
//		}
//		
//		return count;
//	}
//	
//	private static void getResults(CopyOfDigramHistogram digram, String firstWord, List<String> secondWords, TreeSet<Tuple> orderedResults) {
//		for (String secondWord : secondWords) {
//			System.out.println("Checking " + firstWord + " " + secondWord + "; " + firstWord.compareTo(secondWord));
//			Tuple t = new Tuple();
//			t.count = getOccuranceCount(digram, firstWord, secondWord);
//			if (t.count > 0) {
//
//				Boolean reversed = null;
//				if (firstWord.compareTo(secondWord) > 0) { // reversed
//					reversed = digram.orderReversed.get(secondWord.hashCode());
//				} else {
//					reversed = digram.orderReversed.get(firstWord.hashCode());
//				}
//				
//				System.out.println("digram.orderReversed.get(secondWord.hashCode())=" + digram.orderReversed.get(secondWord.hashCode()));
//				System.out.println("digram.orderReversed.get(firstWord.hashCode())=" + digram.orderReversed.get(firstWord.hashCode()));
//				System.out.println("reversed=" + reversed);
//
//				if (reversed == null) { // was not reversed when stored
//					if (firstWord.compareTo(secondWord) > 0) { // query reversed
//						System.out.println("case 1");
//					} else {
//						System.out.println("case 2");
//					}
//				} else {
//					if (reversed) {
//						if (firstWord.compareTo(secondWord) > 0) { // query reversed
//							System.out.println("case 3");
//						} else {
//							System.out.println("case 4");
//						}
//					} else {
//						if (firstWord.compareTo(secondWord) > 0) { // query reversed
//							System.out.println("case 5");
//						} else {
//							System.out.println("case 6");
//						}
//					}
//				}
//				
//				if (reversed != null && !reversed) {
//					if (firstWord.compareTo(secondWord) > 0) { // case 1
//						System.out.println("case 1");
//						t.word = firstWord + " " + secondWord;
//					} else { // case 2
//						System.out.println("case 2");
//						t.word = secondWord + " " + firstWord;
//					}
//				} else { // reversed is null or true (should maintain the order of the request query)
//					if (firstWord.compareTo(secondWord) > 0) { // case 3 
//						System.out.println("case 3");
//						t.word = firstWord + " " + secondWord;
//					} else { // case 4
//						System.out.println("case 4");
//						t.word = secondWord + " " + firstWord;
//					}
//				}
//				orderedResults.add(t);
//			}
//		}
//		
//	}
//
//	private static List<String> orderResults(TreeSet<Tuple> orderedResults, int limit) {
//		List<String> retval = new ArrayList<String>();
//		
//		int count = 0;
//		Iterator<Tuple> iter = orderedResults.iterator();
//		while (iter.hasNext()) {
//			Tuple tuple = iter.next();
//			count++;
//			retval.add(tuple.word);
//			
//			if (count == limit) {
//				break;
//			}
//		}
//		return retval;
//	}
//	
//	public static List<String> getOrderedResults(CopyOfDigramHistogram digram, String firstWord, List<String> secondWords, int limit) {
//		
//		TreeSet<Tuple> orderedResults = Tuple.createOrderedResultsTree();
//		
//		CopyOfDigramHistogram.getResults(digram, firstWord, secondWords, orderedResults);
//		List<String> retval = orderResults(orderedResults, limit);
//		
//		return retval;
//	}
//	
//	public static List<String> getOrderedResults(CopyOfDigramHistogram digram, List<String> firstWords, List<String> secondWords, int limit) {
//		
//		TreeSet<Tuple> orderedResults = Tuple.createOrderedResultsTree();
//		
//		for (String firstWord : firstWords) {
//			CopyOfDigramHistogram.getResults(digram, firstWord, secondWords, orderedResults);
//		}
//		
//		List<String> retval = orderResults(orderedResults, limit);
//		
//		return retval;
//	}
//	
//}
