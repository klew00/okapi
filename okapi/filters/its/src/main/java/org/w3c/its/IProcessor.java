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
	
	public static final long DC_LANGINFO          = 0x00000001;
	public static final long DC_TRANSLATE         = 0x00000002;
	public static final long DC_WITHINTEXT        = 0x00000004;
	public static final long DC_LOCNOTE           = 0x00000008;
	public static final long DC_TERMINOLOGY       = 0x00000010;
	public static final long DC_DIRECTIONALITY    = 0x00000020;
	public static final long DC_RUBY              = 0x00000040;
	public static final long DC_IDVALUE           = 0x00000080; // ITS 2.0
	public static final long DC_DOMAIN            = 0x00000100; // ITS 2.0
	public static final long DC_TARGETPOINTER     = 0x00000200; // ITS 2.0
	public static final long DC_EXTERNALRES       = 0x00000400; // ITS 2.0
	public static final long DC_LOCFILTER         = 0x00000800; // ITS 2.0
	public static final long DC_PRESERVESPACE     = 0x00001000; // ITS 2.0
	public static final long DC_LOCQUALITYISSUE   = 0x00002000; // ITS 2.0
	public static final long DC_STORAGESIZE       = 0x00004000; // ITS 2.0
	public static final long DC_ALLOWEDCHARS      = 0x00008000; // ITS 2.0
	public static final long DC_MTCONFIDENCE      = 0x00010000; // ITS 2.0
//TODO	public static final long DC_DISAMBIGUATION    = 0x00020000; // ITS 2.0
//TODO	public static final long DC_PROVENANCE        = 0x00040000; // ITS 2.0
	
	public static final long DC_SUBFILTER         = 0x00080000; // Extension

	public static final long DC_ALL               = 0xFFFFFFFF;
	
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
	 * a OR operator. For example: <code>applyRules(DC_TRANSLATE | DC_LOCNOTE);</code>
	 * <p>Use DC_ALL to apply all data categories.
	 */
	void applyRules (long dataCategories);
	
	/**
	 * Removes all the special flags added when applying the ITS rules.
	 * Once you have called this method you should call {@link #applyRules(long)} again to be able
	 * to use ITS-aware methods again.
	 */
	void disapplyRules ();

}
