package net.sf.okapi.applications.rainbow.packages.xliff;

import java.io.FileInputStream;

import net.sf.okapi.applications.rainbow.lib.ILog;
import net.sf.okapi.applications.rainbow.packages.IReader;
import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.filters.xliff.XLIFFReader;

/**
 * Implements IReader for generic XLIFF translation packages.
 */
public class Reader implements IReader {
	
	ILog log;
	XLIFFReader reader;
	
	public Reader (ILog log) {
		this.log = log;
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
			log.error(e.getLocalizedMessage());
		}
	}

	public boolean readItem () {
		try {
			return (reader.readItem() > 0);
		}
		catch ( Exception e ) {
			log.error(e.getLocalizedMessage());
		}
		return false;
	}

}
