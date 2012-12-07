/*===========================================================================
  Copyright (C) 2012 by the Okapi Framework contributors
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

package net.sf.okapi.common.annotation;

/**
 * Types of Generic annotation.
 */
public class GenericAnnotationType {

	/**
	 * Annotation identifier for the
	 * <a href='http://www.w3.org/TR/its20/#lqissue'>ITS Localization Quality Issue</a> data category.
	 * 
	 */
	public static final String LQI = "its-lqi";
	public static final String LQI_ISSUESREF = "lqiIssuesRef";
	public static final String LQI_TYPE = "lqiType";
	public static final String LQI_COMMENT = "lqiComment";
	public static final String LQI_SEVERITY = "lqiSeverity"; // Float
	public static final String LQI_PROFILEREF = "lqiProfileRef";
	public static final String LQI_ENABLED = "lqiEnabled"; // Boolean

	/**
	 * Annotation identifier for the
	 * <a href='http://www.w3.org/TR/its20/#Disambiguation'>ITS Disambiguation</a> data category.
	 */
	public static final String DISAMB = "its-disamb";
	public static final String DISAMB_CLASS = "disambClass";
	public static final String DISAMB_SOURCE = "disambSource";
	public static final String DISAMB_IDENT = "disambIdent";
	public static final String DISAMB_CONFIDENCE = "disambConfidence"; // Float
	public static final String DISAMB_GRANULARITY = "disambGranularity";
	
	public static final String DISAMB_GRANULARITY_LEXICAL = "lexical-concept";
	public static final String DISAMB_GRANULARITY_ONTOLOGY = "ontology-concept";
	public static final String DISAMB_GRANULARITY_ENTITY = "entity";

	/**
	 * Annotation identifier for the 
	 * <a href='http://www.w3.org/TR/its20/#terminology'>ITS Terminology</a> data category.
	 */
	public static final String TERM = "its-term";
	public static final String TERM_INFO = "termInfo";
	public static final String TERM_CONFIDENCE = "termConfidence";
}
