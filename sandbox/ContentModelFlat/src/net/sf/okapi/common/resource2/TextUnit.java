package net.sf.okapi.common.resource2;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class TextUnit implements ITranslatable, IAnnotatable {

	protected String                        id;
	protected String                        name;
	protected String                        type;
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
	private ArrayList<TextUnit>             allUnits;
	private int                             currentIndex;


	public TextUnit () {
		source = ""; //TODO: Change for "LocaleUnit" later
		targets = new ArrayList<String>();
		targets.add(null);
	}

	public TextUnit (String id,
		String sourceText)
	{
		this.id = id;
		source = sourceText; //TODO: Change for "LocaleUnit" later
		targets = new ArrayList<String>();
		targets.add(null);
	}
	
	@Override
	public String toString () {
		//TODO: Modify for real output, this is test only
		//TODO: support for children
		return (sklBefore==null ? "" : sklBefore.toString())
			+ source +
			(sklAfter==null ? "" : sklAfter.toString());
	}
	
	public String getID () {
		return id;
	}

	public void setID (String value) {
		id = value;
	}

	public boolean isEmpty () {
		//TODO: Change for "LocaleUnit" later
		return (source.length() > 0);
	}

	public String getName () {
		return name;
	}

	public void setName (String value) {
		name = value;
	}

	public String getType () {
		return type;
	}

	public void setType (String value) {
		type = value;
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

	/**
	 * Gets the skeleton unit just before the resource. For example a "<p>"
	 * in a HTML paragraph. 
	 * @return The skeleton unit before the resource or null.
	 */
	public SkeletonUnit getSkeletonBefore () {
		return sklBefore;
	}
	
	/**
	 * Sets the skeleton unit just before the resource. For example a "<p>"
	 * in a HTML paragraph. 
	 * @param value The skeleton unit to set.
	 */
	public void setSkeletonBefore (SkeletonUnit value) {
		sklBefore = value;
	}

	/**
	 * Gets the skeleton unit just after the resource. For example a "</p>"
	 * in a HTML paragraph. 
	 * @return The skeleton unit after the resource or null.
	 */
	public SkeletonUnit getSkeletonAfter () {
		return sklAfter;
	}
	
	/**
	 * Sets the skeleton unit just after the resource. For example a "</p>"
	 * in a HTML paragraph. 
	 * @param value The skeleton unit to set.
	 */
	public void setSkeletonAfter (SkeletonUnit value) {
		sklAfter = value;
	}

	/**
	 * Gets the source object of the resource.
	 * @return The source object of the resource, never null.
	 */
	public String getSource () {
		//TODO: change for "LocaleUnit" later
		return source;
	}
	
	/** Sets the source object of the resource.
	 * @param value The object to set (must not be null).
	 */
	public void setSource (String value) {
		if ( value == null )
			throw new IllegalArgumentException("Cannot set a source to null.");
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

	/**
	 * Gets the (first) target object of the resource. You can use
	 * {@link #hasTarget()} to know if there is a target available.
	 * @return The current (first) target object, or null.
	 */
	public String getTarget () {
		//TODO: change for "LocaleUnit" later
		return targets.get(0); 
	}

	/**
	 * Sets the (first) target object of the resource.
	 * @param value The object to assign (can be null).
	 */
	public void setTarget (String value) {
		//TODO: change for "LocaleUnit" later
		targets.set(0, value);
	}

	/**
	 * Gets the list of the target objects of the resource.
	 * @return The list of the targets.
	 */
	//For now the only way to access all targets
	public List<String> getTargets () {
		//TODO: change for "LocaleUnit" later
		return targets;
	}
	
	/**
	 * Adds a child resource to the resource. For example the text unit of an "alt"
	 * attribute in a HTML paragraph.
	 * @param child The child resource to add.
	 */
	public void addChild (ITranslatable child) {
		if ( children == null ) {
			children = new ArrayList<ITranslatable>();
		}
		child.setParent(this);
		children.add(child);
	}

	/**
	 * Indicates if the resource has at least one child.
	 * @return True if the resource has one child or more, false if it has none.
	 */
	public boolean hasChild () {
		if ( children == null ) return false;
		else return !children.isEmpty();
	}

	/**
	 * Gets the list of all the children of the resource.
	 * @return The list of all the children of the resource.
	 */
	public List<ITranslatable> getChildren () {
		if ( children == null ) {
			children = new ArrayList<ITranslatable>();
		}
		return children;
	}

	/**
	 * Stores recursively all TextUnit items in the children for the given object.
	 * Only TextUnit objects are stored, Group and SkeletonUnit objects are not.
	 * @param parent The parent object.
	 */
	private void storeItems (ITranslatable parent) {
		// Check if it's a TextUnit
		if ( parent instanceof TextUnit ) {
			// Inner-most is stored first, for a reverse effect
			if ( parent.hasChild() ) {
				for ( ITranslatable item : ((TextUnit)parent).getChildren() ) {
					storeItems(item);
				}
			}
			allUnits.add((TextUnit)parent);
			return;
		}
		// Else: it is a Group
		if ( parent.hasChild() ) {
			for ( IContainable item : (Group)parent ) {
				if ( item instanceof ITranslatable ) { 
					storeItems((ITranslatable)item);
				}
			}
		}
	}
	
	/**
	 * Reset the list of all children TextUnit items for this TextUnit, then
	 * return the first of the them.
	 * @return The first children TextUnit object for this TextUnit, or itself
	 * if it has not children.
	 */
	public TextUnit getFirstUnit () {
		if ( this.hasChild() ) {
			allUnits = new ArrayList<TextUnit>();
			storeItems(this);
			currentIndex = -1;
			return getNextUnit();
		}
		else {
			allUnits = null;
			return this;
		}
	}
	
	/**
	 * Gets the next TextUnit object for this TextUnit. It can be a children, itself,
	 * or null when all possibilities have been exhausted.
	 * @return A TextUnit object or null.
	 */
	public TextUnit getNextUnit () {
		if ( allUnits == null ) return null;
		if ( ++currentIndex < allUnits.size() ) return allUnits.get(currentIndex);
		else return null;
	}

}
