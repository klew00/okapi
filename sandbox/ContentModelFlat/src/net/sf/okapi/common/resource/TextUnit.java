package net.sf.okapi.common.resource;

import java.util.ArrayList;
import java.util.Collections;
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
	protected TextRootContainer             source;
	protected ArrayList<TextRootContainer>  targets;
	protected Hashtable<String, String>     propList;
	protected Hashtable<String, IExtension> extList;
	private ArrayList<TextUnit>             allUnits;
	private int                             currentIndex;


	public TextUnit () {
		source = new TextRootContainer(this);
		targets = new ArrayList<TextRootContainer>();
		targets.add(null);
	}

	public TextUnit (String id,
		String sourceText)
	{
		this.id = id;
		source = new TextRootContainer(this);
		if ( sourceText != null ) source.append(sourceText);
		source.id = id;
		targets = new ArrayList<TextRootContainer>();
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
		return source.isEmpty();
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
	public TextRootContainer getSource () {
		return source;
	}
	
	/** Sets the source object of the resource. This TextUnit becomes the parent
	 * of the given source object.
	 * @param value The object to set (must not be null).
	 */
	public void setSource (TextRootContainer value) {
		if ( value == null )
			throw new IllegalArgumentException("Cannot set a source to null.");
		source = value;
		source.setParent(this);
	}

	/**
	 * Indicates if the text unit has a (first) target.
	 * @return True if the unit has a (first) target, false otherwise.
	 */
	public boolean hasTarget () {
		return (( targets.get(0) != null )
			&& ( !targets.get(0).isEmpty() ));
	}

	/**
	 * Gets the (first) target object of the resource. You can use
	 * {@link #hasTarget()} to know if there is a target available.
	 * @return The current (first) target object, or null.
	 */
	public TextRootContainer getTarget () {
		return targets.get(0); 
	}

	/**
	 * Sets the (first) target object of the resource.
	 * @param value The object to assign (can be null).
	 */
	public void setTarget (TextRootContainer value) {
		targets.set(0, value);
	}

	/**
	 * Gets the list of the target objects of the resource.
	 * @return The list of the targets.
	 */
	//For now the only way to access all targets
	public List<TextRootContainer> getTargets () {
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
	private void storeTextUnits (ITranslatable parent) {
		// Check if it's a TextUnit
		if ( parent instanceof TextUnit ) {
			allUnits.add((TextUnit)parent);
			if ( parent.hasChild() ) {
				for ( ITranslatable item : ((TextUnit)parent).getChildren() ) {
					storeTextUnits(item);
				}
			}
			return;
		}
		// Else: it is a Group
		if ( parent.hasChild() ) {
			for ( IContainable item : (Group)parent ) {
				if ( item instanceof ITranslatable ) { 
					storeTextUnits((ITranslatable)item);
				}
			}
		}
	}
	
	/**
	 * Reset the list of all TextUnit items for this TextUnit, including itself,
	 * then return the first of the them.
	 * @return Itself (the parent TextUnit is always the first of the list).
	 */
	public TextUnit getFirstTextUnit () {
		if ( this.hasChild() ) {
			allUnits = new ArrayList<TextUnit>();
			storeTextUnits(this);
			currentIndex = -1;
			return getNextTextUnit();
		}
		else {
			allUnits = null;
			return this;
		}
	}
	
	/**
	 * Gets the next TextUnit object for this TextUnit. It can be a descendant,
	 * itself, or null when all possibilities have been exhausted.
	 * @return A TextUnit object or null.
	 */
	public TextUnit getNextTextUnit () {
		if ( allUnits == null ) return null;
		if ( ++currentIndex < allUnits.size() ) return allUnits.get(currentIndex);
		else return null;
	}

	/**
	 * Gets the child object with a given ID.
	 * @param id The ID value of the child to return.
	 * @return The child object for the given ID, or null if it has not
	 * been found.
	 */
	public ITranslatable getChild (String id) {
		if ( id == null ) return null;
		if ( !hasChild() ) return null;
		//TODO: Fix this so it cannot match the initial parent...
		return findChild(this, id);
	}
	
	private ITranslatable findChild (ITranslatable parent,
		String id)
	{
		if ( parent instanceof TextUnit ) {
			if ( parent.hasChild() ) {
				for ( ITranslatable item : ((TextUnit)parent).children ) {
					if ( id.equals(item.getID()) ) {
						return item;
					}
					ITranslatable res = findChild(item, id);
					if ( res != null ) return res;
				}
			}
			else if ( id.equals(parent.getID()) ) {
				return parent;
			}
		}
		else if ( parent instanceof Group ) {
			for ( IContainable item : (Group)parent ) {
				if ( item instanceof ITranslatable ) { 
					ITranslatable res = findChild((ITranslatable)item, id);
					if ( res != null ) return res;
				}
			}
			
		}
		return null;
	}
	
	public Iterable<TextUnit> childTextUnitIterator () {
		if (( allUnits == null ) || ( currentIndex > -1 )) {
			// currentIndex > -1 means allUnits is used by getFirst/Next
			// and includes the parent.
			currentIndex = -1;
			allUnits = new ArrayList<TextUnit>();
			if ( hasChild() ) {
				// Children only
				storeTextUnits(getChildren().get(0));
			}
		}
		return Collections.unmodifiableList(allUnits);
	}
	
}
