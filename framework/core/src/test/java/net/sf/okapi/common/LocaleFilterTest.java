/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class LocaleFilterTest {

	private LocaleId locENUS = LocaleId.fromString("en-us");
	private LocaleId locESUS = LocaleId.fromString("es-us");
	private LocaleId locFRCA = LocaleId.fromString("fr-ca");
	
	@Test
	public void testStatics() {
		
		LocaleFilter filter = LocaleFilter.any();
		assertTrue(filter.matches(LocaleId.EMPTY));
		
		assertFalse(LocaleFilter.anyExcept(locFRCA, locENUS).matches(locFRCA));
		assertFalse(LocaleFilter.anyExcept(locFRCA, locENUS).matches(locENUS));
		assertTrue(LocaleFilter.anyExcept(locFRCA, locENUS).matches(locESUS));
	}
}
