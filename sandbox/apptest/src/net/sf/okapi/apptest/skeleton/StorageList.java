package net.sf.okapi.apptest.skeleton;

import java.util.ArrayList;

import net.sf.okapi.apptest.common.IReferenceable;
import net.sf.okapi.apptest.common.IResource;
import net.sf.okapi.apptest.common.ISkeleton;
import net.sf.okapi.apptest.filters.IWriterHelper;

public class StorageList extends ArrayList<IResource>
	implements IResource, IReferenceable {

	private static final long serialVersionUID = 1L;
	
	private String id;

	public String toString (IWriterHelper writerHelper) {
		return mergeList(this, writerHelper);
	}

	private boolean isReference;
	
	public String getId () {
		return id;
	}

	public void setId (String id) {
		this.id = id;
	}

	public boolean isReferent () {
		return isReference;
	}

	public void setIsReferent (boolean value) {
		isReference = value;
	}

	public ISkeleton getSkeleton () {
		// Not used
		return null;
	}

	public void setSkeleton (ISkeleton skeleton) {
		// Not used
	}

	private String mergeList (StorageList list,
		IWriterHelper writerHelper)
	{
		IReferenceable ref;
		StringBuilder tmp = new StringBuilder();
		for ( IResource res : list ) {
			if ( res instanceof StorageList ) {
				// Recursive call for lists
				tmp.append(mergeList((StorageList)res, writerHelper));
			}
			else if ( res instanceof IReferenceable ) {
				ref = (IReferenceable)res;
				if ( ref.isReferent() ) continue; // Skip referents
				else tmp.append(ref.toString(writerHelper));
			}
			else { // Simple IResource
				tmp.append(res.toString());
			}
		}
		return tmp.toString();
	}

}
