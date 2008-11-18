package net.sf.okapi.apptest.common;

import net.sf.okapi.apptest.filters.IWriterHelper;

public interface IReferenceable {

	public String toString (IWriterHelper writerHelper);

	public void setIsReferent (boolean value);
	
	public boolean isReferent ();
	
}
