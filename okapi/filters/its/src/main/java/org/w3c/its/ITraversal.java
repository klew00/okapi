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
	
	public static final int DIR_LTR              = 0;
	public static final int DIR_RTL              = 1;
	public static final int DIR_LRO              = 2;
	public static final int DIR_RLO              = 3;
	
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
	
	public Double getLocQualityIssueSeverity (Attr attribute,
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
	public Double getTermConfidence (Attr attribute);
	
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
	 * or the given attribute must be preserved. 
	 * @return True if the white spaces of the current element or the given attribute must be preserve,
	 * false if they may or may not be preserved.
	 */
	public boolean preserveWS (Attr attribute);

	/**
	 * Gets the language for the current element of the traversal and its attributes.
	 * @return The language code for the current element and its attributes. 
	 */
	public String getLanguage ();
	
	public Integer getStorageSize(Attr attribute);
	
	public String getStorageEncoding (Attr attribute);
	
	public String getLineBreakType (Attr attribute);
	
	public String getAllowedCharacters (Attr attribute);

	/**
	 * Gets the tools references associated with the current element of the traversal and its attributes.
	 * <p>The returned value is sorted by data category and hold all data categories within scope
	 * (not just the ones set on the given node).
	 * @return the tools references associated with the current element of the traversal and its attributes.
	 */
	public String getAnnotatorsRef ();
	
	/**
	 * Gets the annotator reference for a given data category.
	 * @param dc the name of the data category to look up.
	 * @return the reference for the given data category, or null.
	 */
	public String getAnnotatorRef (String dc);
	
	/**
	 * Gets the MT Confidence value for the current element of the traversal or one
	 * of its attributes.
	 * @param attribute the attribute to query or null for the element.
	 * @return the MT Confidence value or null if none is set.
	 */
	public Double getMtConfidence (Attr attribute);

	public String getTextAnalysisClass (Attr attribute);
	
	public String getTextAnalysisSource (Attr attribute);
	
	public String getTextAnalysisIdent (Attr attribute);
	
	public Double getTextAnalysisConfidence (Attr attribute);

	public Double getLocQualityRatingScore (Attr attribute);
	
	public Integer getLocQualityRatingVote (Attr attribute);
	
	public Double getLocQualityRatingScoreThreshold (Attr attribute);
	
	public Integer getLocQualityRatingVoteThreshold (Attr attribute);
	
	public String getLocQualityRatingProfileRef (Attr attribute);
	
	public String getProvRecordsRef (Attr attribute);
	
	public int getProvRecordCount (Attr attribute);
	
	public String getProvPerson (Attr attribute,
		int index);
		
	public String getProvOrg (Attr attribute,
		int index);
		
	public String getProvTool (Attr attribute,
		int index);
		
	public String getProvRevPerson (Attr attribute,
		int index);
		
	public String getProvRevOrg (Attr attribute,
		int index);
		
	public String getProvRevTool (Attr attribute,
		int index);
		
	public String getProvRef (Attr attribute,
		int index);
			
		
}
