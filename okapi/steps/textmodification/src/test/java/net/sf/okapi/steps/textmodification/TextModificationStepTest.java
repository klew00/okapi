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

package net.sf.okapi.steps.textmodification;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.steps.tests.StepTestDriver;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TextModificationStepTest {

	private StepTestDriver driver;
	private LocaleId locEN = LocaleId.fromString("EN");
	private LocaleId locFR = LocaleId.fromString("Fr");

	//TODO: Fix those test using a dummy filter, when we have stable step/pipeline architecture.
	
	@Before
	public void setUp() {
		driver = new StepTestDriver();
	}

	@Test
	public void testTargetDefaults () {
		String original = "This is the content #1 with %s.";
		driver.prepareFilterEventsStep(original, original, locEN, locFR);
		TextModificationStep step = new TextModificationStep(); // Defaults
		Parameters params = (Parameters)step.getParameters();
		params.type = Parameters.TYPE_XNREPLACE;
		driver.testFilterEventsStep(step);
		ITextUnit res = driver.getResult();
		assertNotNull(res);
		assertTrue(res.hasTarget(locFR));
		assertEquals(original, res.getTarget(locFR).toString());
	}

	@Test
	public void testDefaults () {
		String original = "This is the content.";
		driver.prepareFilterEventsStep(original, null, locEN, locFR);
		TextModificationStep step = new TextModificationStep(); // Defaults
		driver.testFilterEventsStep(step);
		ITextUnit res = driver.getResult();
		assertNotNull(res);
		assertTrue(res.hasTarget(locFR));
		assertEquals(original, res.getTarget(locFR).toString());
	}

	@Test
	public void testWithPrefixSuffixMarkers () {
		String original = "This is the content.";
		String expected = "{_[This is the content.]_id1_}";
		driver.prepareFilterEventsStep(original, null, locEN, locFR);
		TextModificationStep step = new TextModificationStep();
		Parameters params = (Parameters)step.getParameters();
		params.addPrefix = true;
		params.prefix = "{_";
		params.addSuffix = true;
		params.suffix = "_}";
		params.markSegments = true;
		params.addID = true;
		driver.testFilterEventsStep(step);
		ITextUnit res = driver.getResult();
		assertNotNull(res);
		assertTrue(res.hasTarget(locFR));
		assertEquals(expected, res.getTarget(locFR).toString());
	}

	@Test
	public void testWithXandNs () {
		String original = "This is the content #1 with %s.";
		String expected = "Xxxx xx xxx xxxxxxx #N xxxx %x.";
		driver.prepareFilterEventsStep(original, null, locEN, locFR);
		TextModificationStep step = new TextModificationStep();
		Parameters params = (Parameters)step.getParameters();
		params.type = Parameters.TYPE_XNREPLACE;
		driver.testFilterEventsStep(step);
		ITextUnit res = driver.getResult();
		assertNotNull(res);
		assertTrue(res.hasTarget(locFR));
		assertEquals(expected, res.getTarget(locFR).toString());
	}

	@Test
	public void testWithPseudoTrans () {
		// "A     a     E     e     I     i     O     o     U     u     Y     y     C     c     D     d     N     n";
		// "\u00c2\u00e5\u00c9\u00e8\u00cf\u00ec\u00d8\u00f5\u00db\u00fc\u00dd\u00ff\u00c7\u00e7\u00d0\u00f0\u00d1\u00f1";
		String original = "This is the content #1 with %s.";
		String expected = "Th\u00ecs \u00ecs th\u00e8 \u00e7\u00f5\u00f1t\u00e8\u00f1t #1 w\u00ecth %s.";
		driver.prepareFilterEventsStep(original, null, locEN, locFR);
		TextModificationStep step = new TextModificationStep();
		Parameters params = (Parameters)step.getParameters();
		params.type = Parameters.TYPE_EXTREPLACE;
		driver.testFilterEventsStep(step);
		ITextUnit res = driver.getResult();
		assertNotNull(res);
		assertTrue(res.hasTarget(locFR));
		assertEquals(expected, res.getTarget(locFR).toString());
	}

	@Test
	public void testKeepInlineCodes () {
		String original = "This is the content #1 with '@#$0'.";
		String expected = "@#$0";
		driver.prepareFilterEventsStep(original, null, locEN, locFR);
		TextModificationStep step = new TextModificationStep();
		Parameters params = (Parameters)step.getParameters();
		params.type = Parameters.TYPE_KEEPINLINE;
		driver.testFilterEventsStep(step);
		ITextUnit res = driver.getResult();
		assertNotNull(res);
		assertTrue(res.hasTarget(locFR));
		assertEquals(expected, res.getTarget(locFR).toString());
	}

	@Test
	public void testExpansion () {
		TextModificationStep step = new TextModificationStep(); // Defaults
		Parameters params = (Parameters)step.getParameters();
		params.type = Parameters.TYPE_KEEPORIGINAL;
		params.expand = true; // Overwrite existing target

		String original = "Original.";
		String expected = "Original. zzzz";
		driver.prepareFilterEventsStep(original, null, locEN, locFR);
		driver.testFilterEventsStep(step);
		ITextUnit res = driver.getResult();
		assertEquals(expected, res.getTarget(locFR).toString());

		original = "O";
		expected = "Oz";
		driver.prepareFilterEventsStep(original, null, locEN, locFR);
		driver.testFilterEventsStep(step);
		res = driver.getResult();
		assertEquals(expected, res.getTarget(locFR).toString());

		original = "";
		expected = "";
		driver.prepareFilterEventsStep(original, null, locEN, locFR);
		driver.testFilterEventsStep(step);
		res = driver.getResult();
		assertEquals(expected, res.getTarget(locFR).toString());

		original = "This is a longer text with a lot more words and characters.";
		expected = "This is a longer text with a lot more words and characters. zzzzz zzzzz zzzzz zzzzz zzzzz zzzzz zzzzz zzzzz zzzzz zzzz";
		driver.prepareFilterEventsStep(original, null, locEN, locFR);
		driver.testFilterEventsStep(step);
		res = driver.getResult();
		assertEquals(expected, res.getTarget(locFR).toString());
	}

	@Test
	public void testTargetOverwriting () {
		String original = "This is the content #1 with @#$0.";
		String expected = "Xxxx xx xxx xxxxxxx #N xxxx @#$0.";
		driver.prepareFilterEventsStep(original, original, locEN, locFR);
		TextModificationStep step = new TextModificationStep(); // Defaults
		Parameters params = (Parameters)step.getParameters();
		params.type = Parameters.TYPE_XNREPLACE;
		params.applyToExistingTarget = true; // Overwrite existing target
		driver.testFilterEventsStep(step);
		ITextUnit res = driver.getResult();
		assertNotNull(res);
		assertTrue(res.hasTarget(locFR));
		assertEquals(expected, res.getTarget(locFR).toString());
	}

}
