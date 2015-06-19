package net.networkdowntime.search;

import java.util.List;
import java.util.Set;

public class Logger {
	private boolean debug = false;

	public Logger(boolean debug) {
		setDebug(debug);
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	public String getTabs(int numOfTabs) {
		String tabs = "";
		if (numOfTabs > 0) {
			for (int i = 0; i < numOfTabs; i++) {
				tabs += "\t";
			}
		}
		return tabs;
	}

	public void log(int numOfTabs, String text) {
		if (debug) {
			String tabs = getTabs(numOfTabs);
			System.out.println(tabs + text);
		}
	}
	
	public void log(int numOfTabs, String label, String text) {
		if (debug) {
			String tabs = getTabs(numOfTabs - 1);
			System.out.println(tabs + label);
			tabs = getTabs(numOfTabs);
			System.out.println(tabs + text);
		}
	}
	
	public void log(int numOfTabs, String label, String[] text) {
		if (debug) {
			String tabs = getTabs(numOfTabs - 1);
			System.out.println(tabs + label);
			tabs = getTabs(numOfTabs);
			for (String s : text) {
				System.out.println(tabs + s);
			}
		}
	}
	
	public void log(int numOfTabs, String label, List<String> text) {
		if (debug) {
			String tabs = getTabs(numOfTabs - 1);
			System.out.println(tabs + label);
			tabs = getTabs(numOfTabs);
			for (String s : text) {
				System.out.println(tabs + s);
			}
		}
	}
	
	
	public void log(int numOfTabs, String label, Set<String> text) {
		if (debug) {
			String tabs = getTabs(numOfTabs - 1);
			System.out.println(tabs + label);
			tabs = getTabs(numOfTabs);
			for (String s : text) {
				System.out.println(tabs + s);
			}
		}
	}

}
