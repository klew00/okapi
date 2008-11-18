package net.sf.okapi.apptest.common;

import net.sf.okapi.apptest.filters.IWriterHelper;

public interface ISkeletonPart {

	String toString ();
	
	String toString (IWriterHelper writerHelper);
	
}
