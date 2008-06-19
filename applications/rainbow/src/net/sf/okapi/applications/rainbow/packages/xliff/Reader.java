package net.sf.okapi.applications.rainbow.packages.xliff;

import java.io.FileInputStream;

import net.sf.okapi.applications.rainbow.packages.IReader;
import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.filters.xliff.OldXLIFFReader;

/**
 * Implements IReader for generic XLIFF translation packages.
 */
public class Reader implements IReader {
	
	OldXLIFFReader reader;
	
	public Reader () {
		reader = new OldXLIFFReader();
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
		catch ( Exception e ) {
			throw new RuntimeException(e);
		}
	}

	public boolean readItem () {
		int n;
		do {
			switch ( (n = reader.readItem()) ) {
			case OldXLIFFReader.RESULT_ENDTRANSUNIT:
				return true;
			}
		} while ( n > OldXLIFFReader.RESULT_ENDINPUT );
		return false;
	}

}
