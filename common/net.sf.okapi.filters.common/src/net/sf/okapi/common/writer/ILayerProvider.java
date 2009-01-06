/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.common.writer;

import net.sf.okapi.common.filters.IEncoder;

/**
 * Provides common methods for encoding a layer on top of a text
 * extracted with a filter.
 */
public interface ILayerProvider extends IEncoder {

	/**
	 * Gets the string denoting the start of external codes.
	 * @return The string denoting the start of external codes.
	 */
	public String startCode ();
	
	/**
	 * Gets the string denoting the end of external codes.
	 * @return The string denoting the end of external codes.
	 */
	public String endCode ();
	
	/**
	 * Gets the string denoting the start of inline codes.
	 * @return The string denoting the start of inlinel codes.
	 */
	public String startInline ();
	
	/**
	 * Gets the string denoting the end of inline codes.
	 * @return The string denoting the end of inlinel codes.
	 */
	public String endInline ();
	
}
