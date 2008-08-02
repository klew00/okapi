package net.sf.okapi.common.resource2;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class TextUnit implements ITranslatable, IAnnotatable {

	protected String                        id;
	protected String                        name;
	protected boolean                       isTranslatable;
	protected boolean                       preserveWS;
	protected SkeletonUnit                  sklBefore;
	protected SkeletonUnit                  sklAfter;
	protected ArrayList<ITranslatable>      children;
	protected ITranslatable                 parent;
	protected String                        source; //Using a String until we get the "LocaleUnit" done
	protected ArrayList<String>             targets; //Using Strings until we get the "LocaleUnit" done
	protected Hashtable<String, String>     propList;
	protected Hashtable<String, IExtension> extList;


	public TextUnit () {
		targets = new ArrayList<String>();
		targets.add(null);
	}

	public String getName () {
		return name;
	}

	public void setName (String value) {
		name = value;
	}

	public String getProperty (String name) {
		if ( propList == null ) return null;
		return propList.get(name);
	}

	public void setProperty (String name,
		String value)
	{
		if ( propList == null ) propList = new Hashtable<String, String>();
		propList.put(name, value);
	}

	public Hashtable<String, String> getProperties () {
		if ( propList == null ) propList = new Hashtable<String, String>();
		return propList;
	}

	public IExtension getExtension (String name) {
		if ( extList == null ) return null;
		return extList.get(name);
	}

	public void setExtension (String name,
		IExtension value)
	{
		if ( extList == null ) extList = new Hashtable<String, IExtension>();
		extList.put(name, value);
	}

	public Hashtable<String, IExtension> getExtensions () {
		if ( extList == null ) extList = new Hashtable<String, IExtension>();
		return extList;
	}

	public boolean preserveWhitespaces() {
		return preserveWS;
	}

	public void setPreserveWhitespaces (boolean value) {
		preserveWS = value;
	}

	public String getID () {
		return id;
	}

	public void setID (String value) {
		id = value;
	}

	public boolean isEmpty () {
		//TODO: Change for "LocaleUnit" later
		return (( source != null ) && ( source.length() > 0 ));
	}

	public boolean isTranslatable () {
		return isTranslatable;
	}

	public void setIsTranslatable (boolean value) {
		isTranslatable = value;
	}

	public ITranslatable getParent () {
		return parent;
	}

	public void setParent (ITranslatable value) {
		parent = value;
	}

	public SkeletonUnit getSkeletonBefore () {
		return sklBefore;
	}
	
	public void setSkeletonBefore (SkeletonUnit value) {
		sklBefore = value;
	}

	public SkeletonUnit getSkeletonAfter () {
		return sklAfter;
	}
	
	public void setSkeletonAfter (SkeletonUnit value) {
		sklAfter = value;
	}

	public String getSource () {
		//TODO: change for "LocaleUnit" later
		return source;
	}
	
	public void setSource (String value) {
		//TODO: change for "LocaleUnit" later
		source = value;
	}

	/**
	 * Indicates if the text unit has a (first) target.
	 * @return True if the unit has a (first) target, false otherwise.
	 */
	public boolean hasTarget () {
		//TODO: change for "LocaleUnit" later
		return (( targets.get(0) != null )
			&& ( targets.get(0).length() > 0 ));
	}
	
	public String getTarget () {
		//TODO: change for "LocaleUnit" later
		return targets.get(0); 
	}

	/**
	 * Sets the (first) target object.
	 * @param value The object to assign.
	 */
	public void setTarget (String value) {
		//TODO: change for "LocaleUnit" later
		targets.set(0, value);
	}

	//For now the only way to access all targets
	public List<String> getTargets () {
		//TODO: change for "LocaleUnit" later
		return targets;
	}
	
	public void addChild (ITranslatable child) {
		if ( children == null ) {
			children = new ArrayList<ITranslatable>();
		}
		child.setParent(this);
		children.add(child);
	}

	public boolean hasChild () {
		if ( children == null ) return false;
		else return !children.isEmpty();
	}

	public List<ITranslatable> getChildren () {
		if ( children == null ) {
			children = new ArrayList<ITranslatable>();
		}
		return children;
	}


}
