/*===========================================================================
  Copyright (C) 2012-2013 by the Okapi Framework contributors
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
	 * Label to use for a generic annotation set (e.g. on an inline code).
	 */
	public static final String GENERIC = "generic";
	
	/**
	 * Prefix used to indicate a reference value.
	 */
	public static final String REF_PREFIX = "REF:";

	/**
	 * Annotation identifier for the ITS annotators reference.
	 */
	public static final String ANNOT = "its-annotators";
	public static final String ANNOT_VALUEREF = "annotatorsValue";
	
	/**
	 * Annotation identifier for the 
	 * <a href='http://www.w3.org/TR/its20/#terminology'>ITS Terminology</a> data category.
	 */
	public static final String TERM = "its-term";
	public static final String TERM_INFO = "termInfo";
	public static final String TERM_CONFIDENCE = "termConfidence";
	
	/**
	 * Annotation identifier for the 
	 * <a href='http://www.w3.org/TR/its20/#domain'>ITS Domain</a> data category.
	 */
	public static final String DOMAIN = "its-domain";
	public static final String DOMAIN_VALUE = "domainValue";
	
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
	// Values for the granularity information of the Disambiguation data category.
	public static final String DISAMB_GRANULARITY_LEXICAL = "lexical-concept";
	public static final String DISAMB_GRANULARITY_ONTOLOGY = "ontology-concept";
	public static final String DISAMB_GRANULARITY_ENTITY = "entity";

	/**
	 * Annotation identifier for the
	 * <a href='http://www.w3.org/TR/its20/#provenance'>ITS Provenance</a> data category.
	 * 
	 */
	public static final String PROV = "its-prov";
	public static final String PROV_RECSREF = "provRecsRef";
	public static final String PROV_PERSON = "provPerson";
	public static final String PROV_ORG = "provOrg";
	public static final String PROV_TOOL = "provTool";
	public static final String PROV_REVPERSON = "provRevPerson";
	public static final String PROV_REVORG = "provRevOrg";
	public static final String PROV_REVTOOL = "provRevTool";
	public static final String PROV_PROVREF = "provRef";

	/**
	 * Annotation identifier for the 
	 * <a href='http://www.w3.org/TR/its20/#externalresource'>ITS External resource</a> data category.
	 */
	public static final String EXTERNALRES = "its-externalres";
	public static final String EXTERNALRES_VALUE = "its-externalresValue";
	
	/**
	 * Annotation identifier for the
	 * <a href='http://www.w3.org/TR/its20/#lqissue'>ITS Localization Quality Issue</a> data category.
	 */
	public static final String LQI = "its-lqi";
	public static final String LQI_ISSUESREF = "lqiIssuesRef";
	public static final String LQI_TYPE = "lqiType";
	public static final String LQI_COMMENT = "lqiComment";
	public static final String LQI_SEVERITY = "lqiSeverity"; // Float
	public static final String LQI_PROFILEREF = "lqiProfileRef";
	public static final String LQI_ENABLED = "lqiEnabled"; // Boolean
	// Extensions
	public static final String LQI_XFORSOURCE = "lqiXForSource"; // Boolean
	public static final String LQI_XTYPE = "lqiXType"; // String
	public static final String LQI_XSEGID = "lqiXSegId"; // String
	public static final String LQI_XSTART = "lqiXStart"; // Integer
	public static final String LQI_XEND = "lqiXEnd"; // Integer
	public static final String LQI_XTRGSTART = "lqiXTrgStart"; // Integer
	public static final String LQI_XTRGEND = "lqiXTrgEnd"; // Integer
	public static final String LQI_XCODES = "lqiXCodes"; // String

	/**
	 * Annotation identifier for the
	 * <a href='http://www.w3.org/TR/its20/#lqrating'>ITS Localization Quality Rating</a> data category.
	 */
	public static final String LQR = "its-lqr";
	public static final String LQR_SCORE = "lqrScore";
	public static final String LQR_VOTE = "lqrVote";
	public static final String LQR_SCORETHRESHOLD = "lqrScoreThreshold";
	public static final String LQR_VOTETHRESHOLD = "lqrVoteThreshold";
	public static final String LQR_PROFILEREF = "lqrProfileRef";
	
	/**
	 * Annotation identifier for the
	 * <a href='http://www.w3.org/TR/its20/#mtconfidence'>ITS MT Confidence</a> data category.
	 */
	public static final String MTCONFIDENCE = "its-mtconfidence";
	public static final String MTCONFIDENCE_VALUE = "its-mtconfidenceValue";

	/**
	 * Annotation identifier for the
	 * <a href='http://www.w3.org/TR/its20/#allowedchars'>ITS Allowed Characters</a> data category.
	 */
	public static final String ALLOWEDCHARS = "its-allowedchars";
	public static final String ALLOWEDCHARS_VALUE = "allowedcharsValue";

	/**
	 * Annotation identifier for the
	 * <a href='http://www.w3.org/TR/its20/#storagesize'>ITS Storage Size</a> data category.
	 */
	public static final String STORAGESIZE = "its-storagesize";
	public static final String STORAGESIZE_SIZE = "storagesizeSize";
	public static final String STORAGESIZE_ENCODING = "storagesizeEncoding";
	public static final String STORAGESIZE_LINEBREAK = "storagesizeLinebreak";

}
