/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

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
	public void startTraversal ();
	
	/**
	 * Moves to the next node in the traversal of the document.
	 * @return The current node of the traversal. Null if the document is traversed.
	 */
	public Node nextNode ();
	
	/**
	 * Indicates whether the current node is found while backtracking. For example,
	 * for an element node, this indicate the equivalent of a closing tag.
	 * @return True if the current node is found while backtracking, false otherwise. 
	 */
	public boolean backTracking ();

	/**
	 * Indicates if the current node of the traversal is translatable.
	 * @return True if the current node is translatable, false otherwise.
	 */
 	public boolean translate ();
	
	/**
	 * Indicates if a given attribute of the current element of the traversal is
	 * translatable.
	 * @param attribute The attribute to query. The attribute must be in the current
	 * node of the traversal.
	 * @return True if the attribute is translatable, false otherwise.
	 */
	public boolean translate (Attr attribute);

	/**
	 * Gets the target pointer for the current element of the traversal. This method
	 * is used for an extension to ITS 1.0.
	 * @return The XPath relative to the current element to the node where the
	 * translation should be placed.
	 */
	public String getTargetPointer ();
	
	/**
	 * Gets the id value for the current element of the traversal.
	 * This method is used for both the ITS 2.0 feature and the deprecated extension to ITS 1.0.
	 * @return The value of the identifier for this node.
	 */
	public String getIdValue ();
	
	/**
	 * Gets the directionality for the text of the current node of the
	 * traversal.
	 * @return One of the DIR_* values.
	 */
	public int getDirectionality ();
	
	/**
	 * Gets the directionality for the text of a given attribute of the current 
	 * node of the traversal.
	 * @param attribute The attribute to query. The attribute must be in the current
	 * node of the traversal.
	 * @return One of the DIR_* values.
	 */
	public int getDirectionality (Attr attribute);
	
	/**
	 * Gets the element-withinText-related information for the current element.
	 * @return One of the WINTINTEXT_* values.
	 */
	public int getWithinText ();

	/**
	 * Indicates if the current node of the traversal is a term.
	 * @return True if the current node is a tern, false otherwise.
	 */
	public boolean isTerm ();

	/**
	 * Gets the information associated with a given term node.
	 * @return the information associated with a given term node.
	 */
	public String getTermInfo ();
	
	/**
	 * Indicates if a given attribute of the current element of the traversal is
	 * a term.
	 * @param attribute The attribute to query. The attribute must be in the current
	 * node of the traversal.
	 * @return True if the attribute is a term, false otherwise.
	 */
	public boolean isTerm (Attr attribute);
	
	/**
	 * Gets the localization notes of the current element of the traversal, or null
	 * if the node has no localization notes.
	 * @return the localization note of the current element, or null if the
	 * node has no localization note.
	 */
	public String getNote ();
	
	/**
	 * Gets the localization note of the given attribute of the current element.
	 * @param attribute the attribute to query. The attribute must be in the current
	 * node of the traversal.
	 * @return The localization note of the attribute, or null if the
	 * attribute has no localization note.
	 */
	public String getNote (Attr attribute);
	
	/**
	 * Gets the domain of the current element of the traversal, or null
	 * if the node has no domain.
	 * @return the domain of the current element of the traversal, or null
	 * if the node has no domain.
	 */
	public String getDomain ();
	
	/**
	 * Gets the domain of the given attribute of the current element.
	 * @param attribute the attribute to query. the attribute must be in the current
	 * node of the traversal.
	 * @return the domain of the given attribute of the current element, or null if the
	 * attribute has no domain.
	 */
	public String getDomain (Attr attribute);

	/**
	 * Indicates if the white spaces of the current element of the traversal
	 * must be preserved. 
	 * @return True if the white spaces of the current element must be preserve,
	 * false if they may or may not be preserved.
	 */
	public boolean preserveWS ();

	/**
	 * Gets the language for the current element of the traversal.
	 * @return The language code for the current element. 
	 */
	public String getLanguage ();
	
}
