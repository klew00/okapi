/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.simplekit.common;

import net.sf.okapi.common.LocaleId;

/**
 * Provides a common way to read a translation package generated with an 
 * implementation of {@link IPackageWriter}. 
 */
public interface IPackageReader {

	/**
	 * Post-process a package.
	 * @param path The full path of the document to post-process.
	 * @param sourceLocale The code of the source locale.
	 * @param targetLocale The code of the target locale.
	 */
	public void postProcessPackage (String path,
		LocaleId sourceLocale,
		LocaleId targetLocale);

}
