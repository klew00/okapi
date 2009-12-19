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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;


public class LocaleFilterTest {

	private LocaleId locENUS = LocaleId.fromString("en-us");
	private LocaleId locENGB = LocaleId.fromString("en-gb");
	private LocaleId locESEQ = LocaleId.fromString("es-eq");
	private LocaleId locESUS = LocaleId.fromString("es-us");	
	private LocaleId locFRFR = LocaleId.fromString("fr-fr");
	private LocaleId locFRCA = LocaleId.fromString("fr-ca");
	private LocaleId locFRCH = LocaleId.fromString("fr-ch");
	private LocaleId locFRBE = LocaleId.fromString("fr-be");
	private LocaleId locDEDE = LocaleId.fromString("de-de");
	private LocaleId locDECH = LocaleId.fromString("de-ch");
	
	@Test
	public void testMatches() {
		
		LocaleFilter filter = LocaleFilter.any();
		assertTrue(filter.matches(LocaleId.EMPTY));
		assertTrue(filter.matches(locENUS));
		
		assertFalse(LocaleFilter.anyExcept(locFRCA, locENUS).matches(locFRCA));
		assertFalse(LocaleFilter.anyExcept(locFRCA, locENUS).matches(locENUS));
		assertTrue(LocaleFilter.anyExcept(locFRCA, locENUS).matches(locESUS));
		
		assertTrue(LocaleFilter.anyOf(locFRCA, locENUS).matches(locFRCA));
		assertFalse(LocaleFilter.anyOf(locFRCA, locENUS).matches(locESUS));
		
		assertFalse(LocaleFilter.none().matches(locESUS));
		assertFalse(LocaleFilter.none().matches(locENUS));
	}
	
	@Test
	public void testFilter() {
		
		Set<LocaleId> filtered = LocaleFilter.anyOf(locFRFR, locFRCA, locFRCH).filter(locFRCA, locFRBE, locENUS);
		assertEquals(1, filtered.size());
		assertTrue(filtered.contains(locFRCA));
		assertFalse(filtered.contains(locFRBE));
	}
	
	@Test
	public void testConstructor() {
		
		LocaleFilter filter = new LocaleFilter();
		assertTrue(filter.matches(locENUS));
		assertTrue(filter.matches(locFRCA));
	}
	
	@Test
	public void testInclude() {
		
		LocaleFilter filter = LocaleFilter.none();
		
		assertFalse(filter.matches(locENUS));		
		filter.include(locENUS);
		assertTrue(filter.matches(locENUS));
		
		assertFalse(filter.matches(locFRCA));		
		filter.include(locFRFR, locFRCA, locESUS);
		assertTrue(filter.matches(locFRFR));
		assertFalse(filter.matches(locFRCH));
		
		assertFalse(filter.matches(locFRBE));
		Set<LocaleId> set = new HashSet<LocaleId>();
		set.add(locFRBE);
		filter.include(set);
		assertTrue(filter.matches(locFRBE));
		
		filter.reset();
		
		filter.include("en-.*");
		assertTrue(filter.matches(locENUS));		
		assertTrue(filter.matches(locENGB));
		assertFalse(filter.matches(locFRCA));
		assertFalse(filter.matches(locESUS));
		assertFalse(filter.matches(locESEQ));
		assertEquals(1, filter.getIncludePatterns().size());
		
		filter.include(".*-us");
		assertTrue(filter.matches(locENUS));
		assertTrue(filter.matches(locESUS));
		assertTrue(filter.matches(locENGB)); // From the previous regex
		assertFalse(filter.matches(locESEQ));
		assertEquals(2, filter.getIncludePatterns().size());
		
		filter.reset();
		assertEquals(0, filter.getIncludePatterns().size());
		
		filter.include("e.*-us");
		assertTrue(filter.matches(locENUS));
		assertTrue(filter.matches(locESUS));
		assertFalse(filter.matches(locENGB));
		assertFalse(filter.matches(locESEQ));
		assertFalse(filter.matches(locFRCA));
		assertEquals(1, filter.getIncludePatterns().size());
		
		filter.reset();
		filter.includeLanguage("en");
		assertTrue(filter.matches(locENUS));
		assertFalse(filter.matches(locESUS));
		assertTrue(filter.matches(locENGB));
		assertFalse(filter.matches(locESEQ));
		assertFalse(filter.matches(locFRCA));
		assertEquals(1, filter.getLanguageIncludes().size());
		
		filter.includeLanguage("es", "fr");
		assertTrue(filter.matches(locENUS));
		assertTrue(filter.matches(locESUS));
		assertTrue(filter.matches(locENGB));
		assertTrue(filter.matches(locESEQ));
		assertTrue(filter.matches(locFRCA));
		assertFalse(filter.matches(locDEDE));
		assertFalse(filter.matches(locDECH));
		assertEquals(3, filter.getLanguageIncludes().size());
		
		filter.reset();
		filter.includeRegion("us");
		assertEquals(1, filter.getRegionIncludes().size());
		assertTrue(filter.matches(locENUS));
		assertTrue(filter.matches(locESUS));
		assertFalse(filter.matches(locENGB));
		assertFalse(filter.matches(locESEQ));
		assertFalse(filter.matches(locFRCA));
		
		filter.includeRegion("ch", "gb");
		assertEquals(3, filter.getRegionIncludes().size());
		assertTrue(filter.matches(locFRCH));
		assertTrue(filter.matches(locDECH));
		assertTrue(filter.matches(locENUS)); // From includeRegion("us") 
		assertTrue(filter.matches(locESUS)); // From includeRegion("us")
		assertTrue(filter.matches(locENGB));
		assertFalse(filter.matches(locESEQ));		
		
		filter.reset();
		filter.includeUserPart("ats");
		assertEquals(1, filter.getUserPartIncludes().size());
		assertTrue(filter.matches(LocaleId.fromPOSIXLocale("de_AT.UTF-8@ATS")));
		assertFalse(filter.matches(LocaleId.fromPOSIXLocale("sr@latin")));
		
		filter.includeUserPart("mac", "latin");
		assertEquals(3, filter.getUserPartIncludes().size());
		assertTrue(filter.matches(LocaleId.fromPOSIXLocale("es_us@mac")));
		assertTrue(filter.matches(LocaleId.fromPOSIXLocale("sr@latin")));
		assertFalse(filter.matches(LocaleId.fromPOSIXLocale("en_us@win")));
		
		filter.reset();
		LocaleFilter filter2 = LocaleFilter.none();
		filter2.include(locENUS);
		assertFalse(filter.matches(locENUS));
		filter.include(filter2);
		assertTrue(filter.matches(locENUS));
	}
	
	@Test
	public void testExclude() {
		
		LocaleFilter filter = LocaleFilter.any();
		
		assertTrue(filter.matches(locENUS));		
		filter.exclude(locENUS);
		assertFalse(filter.matches(locENUS));
		
		assertTrue(filter.matches(locFRCA));		
		filter.exclude(locFRFR, locFRCA, locESUS);
		assertFalse(filter.matches(locFRFR));
		assertTrue(filter.matches(locFRCH));
		
		assertTrue(filter.matches(locFRBE));
		Set<LocaleId> set = new HashSet<LocaleId>();
		set.add(locFRBE);
		filter.exclude(set);
		assertFalse(filter.matches(locFRBE));
		
		filter.reset();
		
		filter.exclude("en-.*");
		assertFalse(filter.matches(locENUS));		
		assertFalse(filter.matches(locENGB));
		assertTrue(filter.matches(locFRCA));
		assertTrue(filter.matches(locESUS));
		assertTrue(filter.matches(locESEQ));
		assertEquals(1, filter.getExcludePatterns().size());
		
		filter.exclude(".*-us");
		assertFalse(filter.matches(locENUS));
		assertFalse(filter.matches(locESUS));
		assertFalse(filter.matches(locENGB)); // From the previous regex
		assertTrue(filter.matches(locESEQ));
		assertEquals(2, filter.getExcludePatterns().size());
		
		filter.reset();
		assertEquals(0, filter.getExcludePatterns().size());
		
		filter.exclude("e.*-us");
		assertFalse(filter.matches(locENUS));
		assertFalse(filter.matches(locESUS));
		assertTrue(filter.matches(locENGB));
		assertTrue(filter.matches(locESEQ));
		assertTrue(filter.matches(locFRCA));
		assertEquals(1, filter.getExcludePatterns().size());
		
		filter.reset();
		filter.excludeLanguage("en");
		assertFalse(filter.matches(locENUS));
		assertTrue(filter.matches(locESUS));
		assertFalse(filter.matches(locENGB));
		assertTrue(filter.matches(locESEQ));
		assertTrue(filter.matches(locFRCA));
		assertEquals(1, filter.getLanguageExcludes().size());
		
		filter.excludeLanguage("es", "fr");
		assertFalse(filter.matches(locENUS));
		assertFalse(filter.matches(locESUS));
		assertFalse(filter.matches(locENGB));
		assertFalse(filter.matches(locESEQ));
		assertFalse(filter.matches(locFRCA));
		assertTrue(filter.matches(locDEDE));
		assertTrue(filter.matches(locDECH));
		assertEquals(3, filter.getLanguageExcludes().size());
		
		filter.reset();
		filter.excludeRegion("us");
		assertEquals(1, filter.getRegionExcludes().size());
		assertFalse(filter.matches(locENUS));
		assertFalse(filter.matches(locESUS));
		assertTrue(filter.matches(locENGB));
		assertTrue(filter.matches(locESEQ));
		assertTrue(filter.matches(locFRCA));
		
		filter.excludeRegion("ch", "gb");
		assertEquals(3, filter.getRegionExcludes().size());
		assertFalse(filter.matches(locFRCH));
		assertFalse(filter.matches(locDECH));
		assertFalse(filter.matches(locENUS)); // From excludeRegion("us") 
		assertFalse(filter.matches(locESUS)); // From excludeRegion("us")
		assertFalse(filter.matches(locENGB));
		assertTrue(filter.matches(locESEQ));		
		
		filter.reset();
		filter.excludeUserPart("ats");
		assertEquals(1, filter.getUserPartExcludes().size());
		assertFalse(filter.matches(LocaleId.fromPOSIXLocale("de_AT.UTF-8@ATS")));
		assertTrue(filter.matches(LocaleId.fromPOSIXLocale("sr@latin")));
		
		filter.excludeUserPart("mac", "latin");
		assertEquals(3, filter.getUserPartExcludes().size());
		assertFalse(filter.matches(LocaleId.fromPOSIXLocale("es_us@mac")));
		assertFalse(filter.matches(LocaleId.fromPOSIXLocale("sr@latin")));
		assertTrue(filter.matches(LocaleId.fromPOSIXLocale("en_us@win")));
		
		filter.reset();
		LocaleFilter filter2 = LocaleFilter.none();
		filter2.include(locENUS);
		assertTrue(filter.matches(locENUS));
		filter.exclude(filter2);
		assertFalse(filter.matches(locENUS));
	}
}
