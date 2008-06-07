package net.sf.okapi.common.resource;

import java.util.List;

public interface IExtractionItem {

	/**
	 * Gets the content of the item in its original format.
	 * @return The content of the object in its original format.
	 */
	String toString ();
	
	/**
	 * Indicates if this extraction item has any content.
	 * @return True if there is text or codes, or both, false otherwise.
	 */
	boolean isEmpty ();
	
	/**
	 * Gets the resource name of the item. This corresponds to the resname attribute
	 * in XLIFF. Uniqueness of resname depends on each filter.
	 * @return The resource name of the item, or null if there is none.
	 */
	//TODO: Return null or empty on no-resname???
	String getName ();
	
	/**
	 * Sets the resource name of the item.
	 * @param resname The new resource name to set.
	 */
	void setName (String resname);
	
	/**
	 * Gets the resource type of the item. This corresponds to the restype attribute
	 * in XLIFF.
	 * @return The resource type of the item.
	 */
	String getType ();
	
	/**
	 * Sets the resource type of the item.
	 * @param restype The resource type to set.
	 */
	void setType (String restype);
	
	/**
	 * Gets the identifier of the item in this input. This value is unique within the current
	 * input. It may be sequential or not, it may change depending on the parameters.
	 * It must be the same for two identical input processed with the same parameters. 
	 * @return The identifier of the item.
	 */
	String getID ();
	
	/**
	 * Sets the identifier of the item.
	 * @param id The identifier value to set.
	 */
	void setID (String id);
	
	/**
	 * Indicates if the item is translatable. Some item may be part of the extraction scope
	 * but because of specific parameters set in the filter, may be seen as non-translatable.
	 * @return True if the content of the item is translatable, false otherwise.
	 */
	boolean isTranslatable ();
	
	/**
	 * Sets the flag that indicates if the item is translatable.
	 * @param isTranslatable The new value to set.
	 */
	void setIsTranslatable (boolean isTranslatable);
	
	/**
	 * Indicates if the item has a corresponding target item. The target item may or may not
	 * be a translation (it could be a copy of the source for example).
	 * @return True if there is a corresponding target item available, false otherwise.
	 */
	boolean hasTarget ();
	
	/**
	 * Gets the target item of a source item.
	 * @return The target item of this item, or null.
	 */
	IExtractionItem getTarget ();
	
	/**
	 * Sets the target item for a source item. Uset setTarget(null) to remove an
	 * existing target.
	 * @param item The target item for this source item.
	 */
	void setTarget (IExtractionItem item);
	
	/**
	 * Indicates if the content of this item should have its white-spaces preserved.
	 * @return True if the formatting should be preserved, false otherwise.
	 */
	boolean preserveFormatting ();
	
	/**
	 * Sets the flag that indicates if the formatting of the item should be
	 * preserved.
	 * @param preserve The new value to set.
	 */
	void setPreserveFormatting (boolean preserve);
	
	/**
	 * Gets the unsegmented content of the item.
	 * @return The IContainer object with the unsegmented content.
	 */
	IContainer getContent();
	
	/**
	 * Sets the content of the item.
	 * @param data An IContainer object with the new content.
	 */
	void setContent (IContainer data);
	
	/**
	 * Gets the list of the segments for the item.
	 * @return A list of IContainer object corresponding to each segment.
	 */
	List<IContainer> getSegments();

	/**
	 * Gets the list of children items for this item.
	 * @return A list of IExtractionItem objects, one for each child item.
	 */
	List<IExtractionItem> getChildren();
	
	/**
	 * Adds a child item to this item.
	 * @param child The IExtractionItem object to add.
	 */
	void addChild (IExtractionItem child);

	/**
	 * Sets the property value object associated with a given property name.
	 * @param name The name of the property (case sensitive).
	 * @param value The new value to set.
	 */
	void setProperty (String name,
		Object value);
	
	/**
	 * Gets the value object associated with a given property name.
	 * @param name The name of the property (case sensitive).
	 * @return The current object associated with the given property name, this
	 * can be null. Null is also return if there is no property for the given name.
	 */
	Object getProperty (String name);
	
	/**
	 * Removes the list of properties associated with the object.
	 */
	void clearProperties ();
	
	/**
	 * Indicates if the object has an associated note.
	 * @return True if there is an associated note, false otherwise.
	 */
	boolean hasNote ();
	
	/**
	 * Gets the notes for the object.
	 * @return A string with the note(s).
	 */
	String getNote ();
	
	/**
	 * Sets the notes for the object.
	 * @param text The new text to set.
	 */
	void setNote (String text);
	
	/**
	 * Removes the segmentation breaks in an object. The object becomes
	 * a single segment.
	 */
	void removeSegmentation ();
	
	/**
	 * Adds a segment to the item. If the item is not already segmented
	 * the existing content is set as the first segment, and this new
	 * segment as the second.
	 * @param segment The segment to add.
	 */
	void addSegment (IContainer segment);
}
