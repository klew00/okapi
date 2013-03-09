/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
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

package net.sf.okapi.applications.rainbow.packages;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.ITextUnit;

/**
 * Provides a common way to read a translation package generated with an 
 * implementation of IWriter. 
 */
public interface IReader {

	/**
	 * Opens the translated document.
	 * @param path The full path of the document to post-process.
	 * @param sourceLanguage The code of the source language.
	 * @param targetLanguage The code of the target language.
	 */
	public void openDocument (String path,
		LocaleId sourceLanguage,
		LocaleId targetLanguage);
	
	/**
	 * Closes the document.
	 */
	public void closeDocument ();

	/**
	 * Reads the next item to post-process.
	 * @return True if an item is available.
	 */
	public boolean readItem ();
	
	/**
	 * Gets the last TextUnit object read.
	 * @return The last TextUnit object read.
	 */
	public ITextUnit getItem ();
	
}
