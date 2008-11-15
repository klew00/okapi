/*===========================================================================*/
/* Copyright (C) 2008 By the Okapi Framework contributors                    */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.lib.translation;

import net.sf.okapi.common.resource.TextFragment;

public interface IQuery {
	
	public static final int HAS_FILEPATH         = 0x0001;
	public static final int HAS_LOGIN            = 0x0002;
	public static final int HAS_LOGINPASSWORD    = 0x0004;
	public static final int HAS_USER             = 0x0008;
	public static final int HAS_USERPASSWORD     = 0x0010;
	public static final int HAS_SERVER           = 0x0020;
	public static final int HAS_PORT             = 0x0040;
	public static final int SUPPORT_EXPORT       = 0x0080;

	public boolean hasOption (int option);
	
	public void setLanguages (String sourceLang,
		String targetLang);
	
	public String getSourceLanguage ();
	
	public String getTargetLanguage ();
	
	public void setAttribute (String name,
		String value);
	
	public void removeAttribute (String name);

	public void open (String connectionString);
	
	public void close ();
	
	public int query (String plainText);
	
	/**
	 * Starts a query for a given text.
	 * @param text The text to query.
	 * @return The number of hits for the given query.
	 */
	public int query (TextFragment text);
	
	public boolean hasNext ();

	/**
	 * Gets the next hit for the current query.
	 * @return The source and target text of the hit,
	 * or null if there is no more hit. 
	 */
	public QueryResult next ();

	/**
	 * Exports all the items of the resource to TMX. This method
	 * may be un-supported: Use {@link #getOptions()} to verify.
	 * @param outputPath The full path of the TMX file to create.
	 */
	public void export (String outputPath);

}
