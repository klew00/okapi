package net.sf.okapi.applications.rainbow.packages.test;

import java.io.FileInputStream;
import java.io.IOException;

import net.sf.okapi.applications.rainbow.packages.IReader;
import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.filters.xliff.XLIFFReader;

public class Reader implements IReader {
	
	XLIFFReader reader;
	
	public Reader () {
		reader = new XLIFFReader();
	}

	public void closeDocument () {
	}

	public IExtractionItem getItem () {
		return reader.getItem();
	}

	public void openDocument (String path) {
		try {
			reader.open(new FileInputStream(path), true);
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public boolean readItem () {
		int n;
		do {
			switch ( (n = reader.readItem()) ) {
			case XLIFFReader.RESULT_ENDTRANSUNIT:
				return true;
			}
		} while ( n > XLIFFReader.RESULT_ENDINPUT );
		return false;
	}

}
