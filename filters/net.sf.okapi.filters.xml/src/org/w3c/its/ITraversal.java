package org.w3c.its;

import org.w3c.dom.Node;

public interface ITraversal {
	
	public static final int DIR_RTL              = 0;
	public static final int DIR_LTR              = 1;
	public static final int DIR_RLO              = 2;
	public static final int DIR_LRO              = 3;
	
	public static final int WITHINTEXT_NO        = 0;
	public static final int WITHINTEXT_YES       = 1;
	public static final int WITHINTEXT_NESTED    = 2;
	
	
	/**
	 * Starts the traversal of the document.
	 */
	void startTraversal ();
	
	/**
	 * Moves to the next node in the traversal of the document.
	 * @return The current node of the traversal. Null if the document is traversed.
	 */
	Node nextNode ();
	
	/**
	 * Indicates whether the current node is found while backtracking. For example,
	 * for an element node, this indicate the equivalent of a closing tag.
	 * @return True if the current node is found while backtracking, false otherwise. 
	 */
 	boolean backTracking ();

	/**
	 * Indicates if the current node of the traversal is translatable.
	 * @return True if the current node is translatable, false otherwise.
	 */
	boolean translate ();
	
	/**
	 * Indicates if a given attribute of the current node of the traversal is
	 * translatable.
	 * @param attrName The name of the attribute to query.
	 * @return True if the attribute is translatable, false otherwise.
	 */
	boolean translate (String attrName);
	
	/**
	 * Gets the directionality for the text of the current node of the
	 * traversal.
	 * @return One of the DIR_* values.
	 */
	int getDirectionality ();
	
	/**
	 * Gets the directionality for the text of a given attribute of the current 
	 * node of the traversal.
	 * @param attrName The name of the attribute to query.
	 * @return One of the DIR_* values.
	 */
	int getDirectionality (String attrName);
	
	/**
	 * Gets the withinText-related information for the current element.
	 * @return One of the WINTINTEXT_* values.
	 */
	int getWithinText ();
}
