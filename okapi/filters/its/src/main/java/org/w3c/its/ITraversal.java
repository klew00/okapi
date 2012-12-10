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
	 * Indicates if the current element or one of its attributes is
	 * translatable.
	 * @param attribute The attribute to query or null to query the element.
	 * @return True if the queried element or attribute is translatable, false otherwise.
	 */
	public boolean getTranslate (Attr attribute);

	/**
	 * Gets the target pointer for the current element of the traversal or one of its attributes.
	 * @return The XPath relative to the current element to the node where the
	 * translation should be placed.
	 */
	public String getTargetPointer (Attr attribute);
	
	/**
	 * Gets the id value for the current element of the traversal or one of its attributes.
	 * @param attribute The attribute to query or null to query the element.
	 * This method is used for both the ITS 2.0 feature and the deprecated extension to ITS 1.0.
	 * @return The value of the identifier for the queried part.
	 */
	public String getIdValue (Attr attribute);
	
	/**
	 * Gets the directionality for the text of a given attribute of the current 
	 * node of the traversal.
	 * @param attribute The attribute to query. The attribute must be in the current
	 * node of the traversal.
	 * @return One of the DIR_* values.
	 */
	public int getDirectionality (Attr attribute);
	
	public String getExternalResourceRef (Attr attribute);
	
	public String getLocQualityIssuesRef (Attr attribute);
	
	public int getLocQualityIssueCount (Attr attribute);
	
	public String getLocQualityIssueType (Attr attribute,
		int index);
	
	public String getLocQualityIssueComment (Attr attribute,
		int index);
	
	public Float getLocQualityIssueSeverity (Attr attribute,
		int index);
	
	public String getLocQualityIssueProfileRef (Attr attribute,
		int index);
	
	public Boolean getLocQualityIssueEnabled (Attr attribute,
		int index);
	
	/**
	 * Gets the element-withinText-related information for the current element.
	 * This data category applies only to elements.
	 * @return One of the WINTINTEXT_* values.
	 */
	public int getWithinText ();

	/**
	 * Indicates if a given attribute of the current element of the traversal or
	 * one of its attributes is a term.
	 * @param attribute The attribute to query or null for the element.
	 * @return True if the queried part is a term, false otherwise.
	 */
	public boolean getTerm (Attr attribute);
	
	/**
	 * Gets the information associated with a given term node or one of its
	 * attributes.
	 * @param attribute The attribute to query or null for the element.
	 * @return the information associated with the queried part.
	 */
	public String getTermInfo (Attr attribute);
	
	/**
	 * Gets the confidence associated with a given term node or one of its
	 * attributes.
	 * @param attribute The attribute to query or null for the element.
	 * @return the confidence associated with the queried part.
	 */
	public Float getTermConfidence (Attr attribute);
	
	/**
	 * Gets the localization note of the current element of the traversal or
	 * one of its attributes.
	 * @param attribute the attribute to query or null for the element.
	 * @return The localization note of the queried part.
	 */
	public String getLocNote (Attr attribute);
	
	public String getLocNoteType (Attr attribute);
	
	public String getDomains (Attr attribute);

	/**
	 * Gets the locale filter information.
	 * @return A a comma-separated list of extended language ranges as defined in 
	 * BCP-47 (and possibly empty).
	 */
	public String getLocaleFilter ();
	
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
	
	public String getStorageSize(Attr attribute);
	
	public String getStorageEncoding (Attr attribute);
	
	public String getLineBreakType (Attr attribute);
	
	public String getAllowedCharacters (Attr attribute);

	/**
	 * Gets the tools references associated with the current element of the traversal (and its attributes).
	 * <p>The returned value is sorted by data category and hold all data categories within scope
	 * (not just the ones set on the given node).
	 * @return the tools references associated with the queried part.
	 */
	public String getAnnotatorsRef ();
	
	/**
	 * Gets the MT Confidence value for the current element of the traversal or one
	 * of its attributes.
	 * @param attribute the attribute to query or null for the element.
	 * @return the MT Confidence value or null if none is set.
	 */
	public Float getMtConfidence (Attr attribute);

	public String getDisambigGranularity (Attr attribute);

	public String getDisambigClass (Attr attribute);
	
	public String getDisambigSource (Attr attribute);
	
	public String getDisambigIdent (Attr attribute);
	
	public Float getDisambigConfidence (Attr attribute);

	public Float getLocQualityRatingScore ();
	
	public Integer getLocQualityRatingVote ();
	
	public Float getLocQualityRatingScoreThreshold ();
	
	public Integer getLocQualityRatingVoteThreshold ();
	
	public String getLocQualityRatingProfileRef ();
	
}
