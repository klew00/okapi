package net.sf.okapi.apptest.skeleton;

import java.util.ArrayList;

import net.sf.okapi.apptest.common.INameable;
import net.sf.okapi.apptest.common.IReferenceable;
import net.sf.okapi.apptest.common.IResource;
import net.sf.okapi.apptest.common.ISkeleton;
import net.sf.okapi.apptest.resource.Property;
import net.sf.okapi.apptest.resource.StartGroup;

public class StorageList extends ArrayList<IResource>
	implements IResource, INameable, IReferenceable {

	private static final long serialVersionUID = 1L;
	
	private StartGroup startGroup;

	public StorageList (StartGroup startGroup) {
		this.startGroup = startGroup;
	}
	
	/*
	public String toString (IWriterHelper writerHelper) {
		return mergeList(this, writerHelper);
	}*/

	public String getId () {
		return startGroup.getId();
	}

	public void setId (String id) {
		// Not implemented: read-only info
	}

	public boolean isReferent () {
		return startGroup.isReferent();
	}

	public void setIsReferent (boolean value) {
		// Not implemented: read-omly info
	}

	public ISkeleton getSkeleton () {
		return startGroup.getSkeleton();
	}

	public void setSkeleton (ISkeleton skeleton) {
		// Not implemented: read-only info
	}

	public String getName () {
		return startGroup.getName();
	}

	public Property getProperty (String name) {
		return startGroup.getProperty(name);
	}

	public void setName (String name) {
		// Not implemented: read-only info
	}

	public void setProperty (Property property) {
		// Not implemented: read-only info
	}

	/*
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
	}*/

}
