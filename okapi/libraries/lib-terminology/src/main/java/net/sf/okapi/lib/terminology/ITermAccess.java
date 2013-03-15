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

package net.sf.okapi.lib.terminology;

import java.util.List;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.TextFragment;

public interface ITermAccess {

	/**
	 * Sets the parameters for this termbase connector.
	 * @param params the new parameter
	 */
	public void setParameters (IParameters params);
	
	/**
	 * Gets the current parameters for this termbase connector.
	 * @return the current parameters for this termbase connector.
	 */
	public IParameters getParameters ();

	/**
	 * Opens the connection to the termbase.
	 * You may need to call {@link #setParameters(IParameters)} before this method.
	 */
	public void open ();
	
	/**
	 * Closes the connection to the termbase. 
	 */
	public void close ();
	
	/**
	 * Gets the list of all terms of the termbase that exist in a given fragment
	 * for a given source/target pair of locales. 
	 * @param fragment the fragment to examine.
	 * @param fragmentLoc the locale of the fragment.
	 * @param otherLoc the other (source or target) locale.
	 * @return the list of all terms of the termbase that exist in the given fragment.
	 */
	public List<TermHit> getExistingTerms (TextFragment fragment,
		LocaleId fragmentLoc,
		LocaleId otherLoc);

	public List<TermHit> getExistingStrings (TextFragment fragment,
		LocaleId fragmentLoc,
		LocaleId otherLoc);

}
