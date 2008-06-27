package net.sf.okapi.applications.rainbow.packages.test;

import net.sf.okapi.applications.rainbow.packages.IReader;
import net.sf.okapi.common.resource.IExtractionItem;

public class Reader implements IReader {
	
	public void closeDocument () {
	}

	public IExtractionItem getItem () {
		return null;
	}

	public void openDocument (String path) {
	}

	public boolean readItem () {
		return false;
	}

}
