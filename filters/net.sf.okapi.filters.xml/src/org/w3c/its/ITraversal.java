package org.w3c.its;

import org.w3c.dom.Attr;
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
	 * Starts the traversal of the document. This method must be called
	 * once before you call {@link #nextNode()}.
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
	 * Indicates if a given attribute of the current element of the traversal is
	 * translatable.
	 * @param attribute The attribute to query. The attribute must be in the current
	 * node of the traversal.
	 * @return True if the attribute is translatable, false otherwise.
	 */
	boolean translate (Attr attribute);
	
	/**
	 * Gets the directionality for the text of the current node of the
	 * traversal.
	 * @return One of the DIR_* values.
	 */
	int getDirectionality ();
	
	/**
	 * Gets the directionality for the text of a given attribute of the current 
	 * node of the traversal.
	 * @param attribute The attribute to query. The attribute must be in the current
	 * node of the traversal.
	 * @return One of the DIR_* values.
	 */
	int getDirectionality (Attr attribute);
	
	/**
	 * Gets the element-withinText-related information for the current element.
	 * @return One of the WINTINTEXT_* values.
	 */
	int getWithinText ();

	/**
	 * Indicates if the current node of the traversal is a term.
	 * @return True if the current node is a tern, false otherwise.
	 */
	boolean isTerm ();
	
	/**
	 * Indicates if a given attribute of the current element of the traversal is
	 * a term.
	 * @param attribute The attribute to query. The attribute must be in the current
	 * node of the traversal.
	 * @return True if the attribute is a term, false otherwise.
	 */
	boolean isTerm (Attr attribute);
	
	/**
	 * Gets the localization notes of the current element of the traversal, or null
	 * if the node has no localization notes.
	 * @return The localization note of the current element, or null if the
	 * node has no localization note.
	 */
	String getNote ();
	
	/**
	 * Get the localization note of the given attribute of the current element.
	 * @param attribute The attribute to query. The attribute must be in the current
	 * node of the traversal.
	 * @return The localization note of the attribute, or null if the
	 * attribute has no localization note.
	 */
	String getNote (Attr attribute);
	
}
