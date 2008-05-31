package net.sf.okapi.applications.rainbow.packages.xliff;

import java.io.FileInputStream;

import net.sf.okapi.applications.rainbow.packages.IReader;
import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.filters.xliff.XLIFFReader;

/**
 * Implements IReader for generic XLIFF translation packages.
 */
public class Reader implements IReader {
	
	XLIFFReader reader;
	
	public Reader () {
		reader = new XLIFFReader();
	}

	public void closeDocument () {
	}

	public IExtractionItem getSourceItem () {
		return reader.getSourceItem();
	}

	public IExtractionItem getTargetItem () {
		return reader.getTargetItem();
	}

	public void openDocument (String path) {
		try {
			reader.open(new FileInputStream(path), true);
		}
		catch ( Exception e ) {
			throw new RuntimeException(e);
		}
	}

	public boolean readItem () {
		return (reader.readItem() > 0);
	}

}
