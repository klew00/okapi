package net.sf.okapi.apptest.resource;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.apptest.filters.IWriterHelper;

public class StartGroup extends BaseResource {

	private LocaleProperties srcProp;
	private ArrayList<LocaleProperties> trgPropList;
	
	public StartGroup (String parentId) {
		this.parentId = parentId;
	}

	public StartGroup (String parentId,
		String id)
	{
		this.parentId = parentId;
		this.id = id;
	}

	public StartGroup (String parentId,
		String id,
		boolean isReference)
	{
		this.parentId = parentId;
		this.id = id;
		this.isReferent = isReference;
	}

	public String toString (IWriterHelper writerHelper) {
		if ( skeleton != null ) return skeleton.toString(writerHelper);
		else return "";
	}
	
	public LocaleProperties getSourceProperties () {
		if ( srcProp == null ) srcProp = new LocaleProperties();
		return srcProp;
	}
	
	public void setSourceProperties (LocaleProperties value) {
		if ( value == null ) throw new InvalidParameterException();
		srcProp = value;
	}

	public boolean hasTargetProperties () {
		if ( trgPropList == null ) return false;
		return (trgPropList.get(0) != null);
	}

	public LocaleProperties getTargetProperties () {
		if ( trgPropList == null ) trgPropList = new ArrayList<LocaleProperties>();
		if ( trgPropList.get(0) == null ) trgPropList.add(new LocaleProperties());
		return trgPropList.get(0);
	}
	
	public void setTarget (LocaleProperties value) {
		if ( value == null ) throw new InvalidParameterException();
		if ( trgPropList == null ) trgPropList = new ArrayList<LocaleProperties>();
		if ( trgPropList.size() == 0 ) trgPropList.add(value);
		else trgPropList.set(0, value);
	}

	public List<LocaleProperties> getTargetsProperties () {
		if ( trgPropList == null ) trgPropList = new ArrayList<LocaleProperties>();
		if ( trgPropList.get(0) == null ) trgPropList.add(new LocaleProperties());
		return trgPropList;
	}

}
