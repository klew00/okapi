/*
 * ===========================================================================
 * Copyright (C) 2013 by the Okapi Framework contributors
 * -----------------------------------------------------------------------------
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
 * ===========================================================================
 */

package net.sf.okapi.steps.languagetool;

import static org.junit.Assert.assertNotNull;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.annotation.GenericAnnotations;
import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextFragment.TagType;

import org.junit.Before;
import org.junit.Test;

public class LanguageToolTest {

	private GenericContent fmt;
	private LanguageTool lt;
	private Parameters params;

	@Before
	public void setUp () {
		params = new Parameters();
		fmt = new GenericContent();
		lt = new LanguageTool(null, LocaleId.ENGLISH, LocaleId.FRENCH);
	}

	@Test
	public void simpleTest () {
		ITextUnit tu = new TextUnit("id", "original teext");
		tu.setTargetContent(LocaleId.FRENCH, new TextFragment("texte original"));
		lt.run(tu);
		GenericAnnotations anns = tu.getAnnotation(GenericAnnotations.class);
		assertNotNull(anns);
	}

	@Test
	public void testWithCodes () {
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("text");
		tf.append(TagType.CLOSING, "b", "</b>");
		ITextUnit tu = new TextUnit("id");
		tu.setSourceContent(tf);
		tu.setTargetContent(LocaleId.FRENCH, tf.clone());
		lt.run(tu);
		GenericAnnotations anns = tu.getAnnotation(GenericAnnotations.class);
		assertNotNull(anns);
	}

}
