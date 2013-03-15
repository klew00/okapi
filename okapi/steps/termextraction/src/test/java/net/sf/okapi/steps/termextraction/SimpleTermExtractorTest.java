/*===========================================================================
  Copyright (C) 2010-2011 by the Okapi Framework contributors
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

package net.sf.okapi.steps.termextraction;

import java.util.Map;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.TestUtil;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class SimpleTermExtractorTest {
	
	private SimpleTermExtractor extr = new SimpleTermExtractor();
	private Parameters params;
	
	@Before
	public void setUp() {
		params = new Parameters();
		String root = TestUtil.getParentDir(this.getClass(), "/");
		params.setOutputPath(root + "/terms.txt");
	}

	@Test
	public void testSimpleCase () {
		extr.initialize(params, LocaleId.ENGLISH, null);
		extr.processTextUnit(new TextUnit("id", "This is a test, a rather simple test."));
		extr.completeExtraction();
		Map<String, Integer> res = extr.getTerms();
		assertEquals("{test=2}", res.toString());
	}

	@Test
	public void testLongTextCaseWithMinOcc3 () {
		params.setMinOccurrences(3);
		extr.initialize(params, LocaleId.ENGLISH, null);
		extr.processTextUnit(createLongTU());
		extr.completeExtraction();
		Map<String, Integer> res = extr.getTerms();
		assertEquals("{complex=4, complex expression=3, expression=3}", res.toString());
	}

	private ITextUnit createLongTU () {
		ITextUnit tu = new TextUnit("id");
		tu.setSourceContent(new TextFragment("This is a test with a complex expression. A complex expression that "
			+ "occurs often. This is important for this test. A complex term like [complex expression] is also "
			+ "a term with several words. Things like $#@ or & should not be seen as words."
		));
		return tu;
	}
}
