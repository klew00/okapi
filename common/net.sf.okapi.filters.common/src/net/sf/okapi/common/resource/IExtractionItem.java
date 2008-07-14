package net.sf.okapi.common.resource;

import java.util.List;

public interface IExtractionItem extends ICommonResource {

	/**
	 * Indicates if this extraction item has any content.
	 * @return True if there is text or codes, or both, false otherwise.
	 */
	boolean isEmpty ();
	
	/**
	 * Indicates if the item has a corresponding target item. The target item may or may not
	 * be a translation (it could be a copy of the source for example).
	 * @return True if there is a corresponding target item available, false otherwise.
	 */
	boolean hasTarget ();
	
	/**
	 * Gets the target of the item.
	 * @return The target of this item, or null.
	 */
	IContainer getTarget ();
	
	/**
	 * Sets the target for this item. Use setTarget(null) to remove an
	 * existing target.
	 * @param target The target for this item.
	 */
	void setTarget (IContainer target);
	
	/**
	 * Gets the source container of the item.
	 * @return The IContainer object for the source.
	 */
	IContainer getSource();
	
	/**
	 * Sets the content of the source of the item.
	 * @param data An IContainer object with the new source content.
	 */
	void setSource (IContainer data);
	
	/**
	 * Gets the list of the segments for the source.
	 * @return A list of IPart object corresponding to each source segment.
	 */
	List<IPart> getSourceSegments();

	/**
	 * Gets the list of the segments for the target.
	 * @return A list of IPart object corresponding to each target segment.
	 */
	List<IPart> getTargetSegments();

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
	 * Indicates whether this item has at least one child.
	 * @return True if the item has at least one child, false otherwise.
	 */
	boolean hasChild ();
	
	/**
	 * Gets the parent item of this item.
	 * @return The parent item of this item, or null if this item has no parent.
	 */
	IExtractionItem getParent ();
	
	/**
	 * Gets the first item in this item. It is guaranteed that this call
	 * will not return null, as there is at least the parent item itself.
	 * If the item has no child, this method returns the item itself, then
	 * {@link #getNextItem()} return null. If the item has children
	 * this method returns the first item in the branch, then calls to
	 * {@link #getNextItem()} return the other items, or null when there
	 * are no more.
	 * @return The next item in this item. Never is never returned.
	 */
	IExtractionItem getFirstItem ();
	
	/**
	 * Gets the next item in this item.
	 * @return The next item in this item, or null if there is no more
	 * children items.
	 */
	IExtractionItem getNextItem ();
}
