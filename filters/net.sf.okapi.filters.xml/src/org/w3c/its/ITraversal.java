package org.w3c.its;

import org.w3c.dom.Node;

public interface ITraversal {
	
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
}
