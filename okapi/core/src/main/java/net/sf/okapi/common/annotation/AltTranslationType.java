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

package net.sf.okapi.common.annotation;

/**
 * Enumeration of the different types possible for an alternate translation entry.
 */
public enum AltTranslationType {
	/**
	 * Indicates an alternate translation coming from a TM engine.
	 */
	TM,
	
	/**
	 * Indicates an alternate translation coming from an MT engine.
	 */
	MT,
	
	/**
	 * Indicates an alternate translation coming from the source document.
	 * For example from an <code>&lt;alt-trans></code> element in an XLIFF source.
	 */
	FROM_DOCUMENT,
	
	/**
	 * Indicates an alternate translation coming from the DiffLeverageStep.
	 */
	DIFF_LEVERAGE
}
