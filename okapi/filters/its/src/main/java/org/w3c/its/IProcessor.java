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

import java.net.URI;

import org.w3c.dom.Document;

public interface IProcessor {
	
	public static final int DC_LANGINFO          = 0x0001;
	public static final int DC_TRANSLATE         = 0x0002;
	public static final int DC_WITHINTEXT        = 0x0004;
	public static final int DC_LOCNOTE           = 0x0008;
	public static final int DC_TERMINOLOGY       = 0x0010;
	public static final int DC_DIRECTIONALITY    = 0x0020;
	public static final int DC_RUBY              = 0x0040;
	public static final int DC_IDVALUE           = 0x0080; // ITS 2.0
	public static final int DC_DOMAIN            = 0x0100; // ITS 2.0
	public static final int DC_TARGETPOINTER     = 0x0200; // ITS 2.0
	public static final int DC_EXTERNALRES       = 0x0400; // ITS 2.0
	public static final int DC_LOCFILTER         = 0x0800; // ITS 2.0
	public static final int DC_PRESERVESPACE     = 0x1000; // ITS 2.0
	public static final int DC_LOCQUALITYISSUE   = 0x2000; // ITS 2.0
	public static final int DC_STORAGESIZE       = 0x4000; // ITS 2.0
	public static final int DC_ALLOWEDCHARS      = 0x8000; // ITS 2.0
	public static final int DC_ALL               = 0xFFFF;
	
	/**
	 * Adds a set of global rules to the document to process. The rules are added
	 * to the internal storage of the document, not to the document tree.
	 * Use this method to add one rule set or more before calling applyRules().
	 * @param docRules Document where the global rules are declared.
	 * @param docURI URI of the document. This is needed because xlink:href need
	 * a initial location.
	 */
	void addExternalRules (Document rulesDoc,
		URI docURI);

	/**
	 * Adds a set of global rules to the document to process.
	 * See {@link #addExternalRules(Document, String)} for more details.
	 * @param docURI URI of the document that contains the rules to add.
	 */
	void addExternalRules (URI docURI);

	/**
	 * Applies the current ITS rules to the document. This method decorates
	 * the document tree with special flags that are used for getting the
	 * different ITS information later.
	 * @param dataCategories Flag indicating what data categories to apply.
	 * The value must be one of the DC_* values or several combined with 
	 * a OR operator. For example:
	 * applyRules(DC_TRANSLATE | DC_LOCNOTE);
	 */
	void applyRules (int dataCategories);
	
	/**
	 * Removes all the special flags added when applying the ITS rules.
	 * Once you have called this method you should call applyRules() again to be able
	 * to use ITS-aware methods again.
	 */
	void disapplyRules ();

}
