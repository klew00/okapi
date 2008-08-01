package net.sf.okapi.common.resource2;

import java.util.ArrayList;
import java.util.List;

public class TextUnit implements ITranslationResource {

	protected String                             id;
	protected String                             name;
	protected boolean                            isTranslatable;
	protected boolean                            preserveWS;
	protected SkeletonUnit                       sklBefore;
	protected SkeletonUnit                       sklAfter;
	protected ArrayList<ITranslationResource>    children;
	protected ITranslationResource               parent;
	protected String                             source; //Using a String until we get the "LocaleUnit" done
	protected ArrayList<String>                  targets; //Using Strings until we get the "LocaleUnit" done


	public TextUnit () {
		targets = new ArrayList<String>();
		targets.add(null);
	}

	public String getName () {
		return name;
	}

	public boolean isTranslatable () {
		return isTranslatable;
	}

	public boolean preserveWhitespaces () {
		return preserveWS;
	}

	public void setIsTranslatable (boolean value) {
		isTranslatable = value;
	}

	public void setName (String value) {
		name = value;
	}

	public void setPreserveWhitespaces (boolean value) {
		preserveWS = value;
	}

	public String getID () {
		return id;
	}

	public boolean isEmpty () {
		//TODO: Change for "LocaleUnit" later
		return (( source != null ) && ( source.length() > 0 ));
	}

	public void setID (String value) {
		id = value;
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
	
	public void addChild (ITranslationResource child) {
		if ( children == null ) {
			children = new ArrayList<ITranslationResource>();
		}
		child.setParent(this);
		children.add(child);
	}

	public boolean hasChild () {
		if ( children == null ) return false;
		else return !children.isEmpty();
	}

	public List<ITranslationResource> getChildren () {
		if ( children == null ) {
			children = new ArrayList<ITranslationResource>();
		}
		return children;
	}

	public ITranslationResource getParent () {
		return parent;
	}

	public void setParent (ITranslationResource value) {
		parent = value;
	}

}
