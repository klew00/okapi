package net.sf.okapi.apptest.resource;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class Group extends ArrayList<IReferenceable>
	implements IReferenceable {

	private static final long serialVersionUID = 1L;
	
	private String id;
	private boolean isReference;
	private String name;
	private String parentId;
	private LocaleProperties srcProp;
	private ArrayList<LocaleProperties> trgPropList;
	
	public Group (String parentId) {
		this.parentId = parentId;
	}

	public Group (String parentId,
		String id)
	{
		this.parentId = parentId;
		this.id = id;
	}

	public Group (String parentId,
		String id,
		boolean isReference)
	{
		this.parentId = parentId;
		this.id = id;
		this.isReference = isReference;
	}

	public String getName () {
		return name;
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
	public String getParentID () {
		return parentId;
	}
	
	public void setParentID (String parentId) {
		this.parentId = parentId;
	}
	
	public boolean isReference () {
		return isReference;
	}

	public void setIsReference (boolean value) {
		isReference = value;
	}

	public String getID () {
		return id;
	}

	public void setID (String id) {
		this.id = id;
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
