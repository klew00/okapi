/*===========================================================================
  Copyright (C) 2009-2012 by the Okapi Framework contributors
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

package net.sf.okapi.filters.its;

import net.sf.okapi.common.annotation.GenericAnnotations;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.its.ITSEngine;

class ContextItem {
	
	Node node;
	boolean translate;
	String trgPointer;
	String idValue;
	String locNote;
	boolean preserveWS;
	String domains;
	String externalRes;
	String allowedChars;
	GenericAnnotations lqIssues;
	GenericAnnotations storageSize;
	GenericAnnotations lqRating;
	GenericAnnotations disambig;
	Double mtConfidence;
	GenericAnnotations prov;

	public ContextItem (Node node,
		ITSEngine trav)
	{
		this(node, trav, null);
	}
	
	public ContextItem (Node node,
		ITSEngine trav,
		Attr attribute)
	{
		this.node = node;
		// Context is always an element node
		this.translate = trav.getTranslate(attribute);
		this.trgPointer = trav.getTargetPointer(attribute);
		this.idValue = trav.getIdValue(attribute);
		this.locNote = trav.getLocNote(attribute);
		this.preserveWS = trav.preserveWS();
		this.domains = trav.getDomains(attribute);
		this.externalRes = trav.getExternalResourceRef(attribute);
		this.allowedChars = trav.getAllowedCharacters(attribute);
		this.storageSize = trav.getStorageSizeAnnotation(attribute);
		this.lqIssues = trav.getLocQualityIssueAnnotation(attribute);
		this.lqRating = trav.getLocQualityRatingAnnotation();
		this.mtConfidence = trav.getMtConfidence(attribute);
		this.disambig = trav.getDisambiguationAnnotation(attribute);
		this.prov = trav.getProvenanceAnnotation(attribute);
	}

}
