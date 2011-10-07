/*===========================================================================
  Copyright (C) 2009-2011 by the Okapi Framework contributors
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

package net.sf.okapi.steps.common.tufiltering;

import net.sf.okapi.common.resource.ITextUnit;

/**
 * Instances of classes that implement this interface are used to
 * filter text units that need to be processed by a filtering pipeline step. 
 */
public interface ITextUnitFilter {

	/**
	 * Tests if a given text unit should be processed by a filtering pipeline step.
	 * If a text unit is not accepted, then it is not processed by the step and
	 * sent unmodified further along the pipeline.
	 *  
	 * @param tu the given text unit
	 * @return <code>true</code> if the given text unit should be processed by the step
	 */
	boolean accept(ITextUnit tu);
}
