/*
 * ===========================================================================
 * Copyright (C) 2012 by the Okapi Framework contributors
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

package net.sf.okapi.lib.verification;

import static org.junit.Assert.assertEquals;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;

import org.junit.Before;
import org.junit.Test;

public class SpaceCheckerTest {

	private LocaleId locEN = LocaleId.ENGLISH;
	private LocaleId locFR = LocaleId.FRENCH;
	private GenericContent fmt;
	private SpaceChecker checker;

	@Before
	public void setUp() {

		fmt = new GenericContent();
		checker = new SpaceChecker();
	}

	@Test
	public void testEmptyCase() {

		TextFragment srcTf = new TextFragment("t1 ");
		srcTf.append(TagType.OPENING, "bold", "<b>");
		srcTf.append("t2");
		srcTf.append(TagType.CLOSING, "bold", "</b>");
		srcTf.append(" t3.");

		TextFragment trgTf = new TextFragment();

		checker.checkSpaces(srcTf, trgTf);

		assertEquals("", fmt.setContent(trgTf).toString());
		assertEquals("", fmt.setContent(trgTf).toString(true));
	}

	@Test
	public void testMatchingCase() {

		TextFragment srcTf = new TextFragment("t1 ");
		srcTf.append(TagType.OPENING, "bold", "<b>");
		srcTf.append("t2");
		srcTf.append(TagType.CLOSING, "bold", "</b>");
		srcTf.append(" t3.");

		TextFragment trgTf = new TextFragment("t1 ");
		trgTf.append(TagType.OPENING, "bold", "<b>");
		trgTf.append("t2");
		trgTf.append(TagType.CLOSING, "bold", "</b>");
		trgTf.append(" t3.");

		checker.checkSpaces(srcTf, trgTf);

		assertEquals("t1 <1>t2</1> t3.", fmt.setContent(trgTf).toString());
		assertEquals("t1 <b>t2</b> t3.", fmt.setContent(trgTf).toString(true));
	}

	@Test
	public void testStartCase() {

		// <b>t1</b> t2
		TextFragment srcTf = new TextFragment();
		srcTf.append(TagType.OPENING, "bold", "<b>");
		srcTf.append("t1");
		srcTf.append(TagType.CLOSING, "bold", "</b>");
		srcTf.append(" t2.");

		// <b> t1 </b> t2
		TextFragment trgTf = new TextFragment();
		trgTf.append(TagType.OPENING, "bold", "<b>");
		trgTf.append(" t1 ");
		trgTf.append(TagType.CLOSING, "bold", "</b>");
		trgTf.append(" t2.");

		checker.checkSpaces(srcTf, trgTf);

		assertEquals("<1>t1</1> t2.", fmt.setContent(trgTf).toString());
		assertEquals("<b>t1</b> t2.", fmt.setContent(trgTf).toString(true));
	}

	@Test
	public void testEndCase() {

		// t1 <b>t2</b>
		TextFragment srcTf = new TextFragment("t1 ");
		srcTf.append(TagType.OPENING, "bold", "<b>");
		srcTf.append("t2");
		srcTf.append(TagType.CLOSING, "bold", "</b>");

		// t1 <b> t2 </b>
		TextFragment trgTf = new TextFragment("t1 ");
		trgTf.append(TagType.OPENING, "bold", "<b>");
		trgTf.append(" t2 ");
		trgTf.append(TagType.CLOSING, "bold", "</b>");

		checker.checkSpaces(srcTf, trgTf);

		assertEquals("t1 <1>t2</1>", fmt.setContent(trgTf).toString());
		assertEquals("t1 <b>t2</b>", fmt.setContent(trgTf).toString(true));
	}

	@Test
	public void testSrcMultipleSpaceBefore() {

		// t1 <b>t2</b>
		TextFragment srcTf = new TextFragment("t1  ");
		srcTf.append(TagType.OPENING, "bold", "<b>");
		srcTf.append("t2");
		srcTf.append(TagType.CLOSING, "bold", "</b>");

		// t1 <b> t2 </b>
		TextFragment trgTf = new TextFragment("t1 ");
		trgTf.append(TagType.OPENING, "bold", "<b>");
		trgTf.append(" t2 ");
		trgTf.append(TagType.CLOSING, "bold", "</b>");

		checker.checkSpaces(srcTf, trgTf);

		assertEquals("t1  <1>t2</1>", fmt.setContent(trgTf).toString());
		assertEquals("t1  <b>t2</b>", fmt.setContent(trgTf).toString(true));
	}

	@Test
	public void testTrgMultipleSpaceBefore() {

		// t1 <b>t2</b>
		TextFragment srcTf = new TextFragment("t1 ");
		srcTf.append(TagType.OPENING, "bold", "<b>");
		srcTf.append("t2");
		srcTf.append(TagType.CLOSING, "bold", "</b>");

		// t1 <b> t2 </b>
		TextFragment trgTf = new TextFragment("t1  ");
		trgTf.append(TagType.OPENING, "bold", "<b>");
		trgTf.append(" t2 ");
		trgTf.append(TagType.CLOSING, "bold", "</b>");

		checker.checkSpaces(srcTf, trgTf);

		assertEquals("t1 <1>t2</1>", fmt.setContent(trgTf).toString());
		assertEquals("t1 <b>t2</b>", fmt.setContent(trgTf).toString(true));
	}

	@Test
	public void testMultipleSpaceAfter() {

		// t1 <b>t2</b>
		TextFragment srcTf = new TextFragment("t1 ");
		srcTf.append(TagType.OPENING, "bold", "<b>");
		srcTf.append("  t2");
		srcTf.append(TagType.CLOSING, "bold", "</b>");

		// t1 <b> t2 </b>
		TextFragment trgTf = new TextFragment("t1 ");
		trgTf.append(TagType.OPENING, "bold", "<b>");
		trgTf.append(" t2 ");
		trgTf.append(TagType.CLOSING, "bold", "</b>");

		checker.checkSpaces(srcTf, trgTf);

		assertEquals("t1 <1>  t2</1>", fmt.setContent(trgTf).toString());
		assertEquals("t1 <b>  t2</b>", fmt.setContent(trgTf).toString(true));
	}

	@Test
	public void testNoSpaceSrc() {

		// t1<b>t2</b>t3
		TextFragment srcTf = new TextFragment("t1");
		srcTf.append(TagType.OPENING, "bold", "<b>");
		srcTf.append("t2");
		srcTf.append(TagType.CLOSING, "bold", "</b>");
		srcTf.append("t3");

		// t1 <b>t2 </b> t3
		TextFragment trgTf = new TextFragment("t1  ");
		trgTf.append(TagType.OPENING, "bold", "<b>");
		trgTf.append("t2 ");
		trgTf.append(TagType.CLOSING, "bold", "</b>");
		trgTf.append("   t3");

		checker.checkSpaces(srcTf, trgTf);

		assertEquals("t1<1>t2</1>t3", fmt.setContent(trgTf).toString());
		assertEquals("t1<b>t2</b>t3", fmt.setContent(trgTf).toString(true));
	}

	@Test
	public void testNoSpaceTrg() {

		// t1 <b>t2</b> t3
		TextFragment srcTf = new TextFragment("t1 ");
		srcTf.append(TagType.OPENING, "bold", "<b>");
		srcTf.append("t2");
		srcTf.append(TagType.CLOSING, "bold", "</b>");
		srcTf.append(" t3");

		// t1<b>t2</b>t3
		TextFragment trgTf = new TextFragment("t1");
		trgTf.append(TagType.OPENING, "bold", "<b>");
		trgTf.append("t2");
		trgTf.append(TagType.CLOSING, "bold", "</b>");
		trgTf.append("t3");

		checker.checkSpaces(srcTf, trgTf);

		assertEquals("t1 <1>t2</1> t3", fmt.setContent(trgTf).toString());
		assertEquals("t1 <b>t2</b> t3", fmt.setContent(trgTf).toString(true));
	}

	@Test
	public void testTrgMultiInsert() {

		// t1 <b> t2</b> t3
		TextFragment srcTf = new TextFragment("t1  ");
		srcTf.append(TagType.OPENING, "bold", "<b>");
		srcTf.append("  t2");
		srcTf.append(TagType.CLOSING, "bold", "</b>");
		srcTf.append(" t3");

		// t1<b>t2</b>t3
		TextFragment trgTf = new TextFragment("t1");
		trgTf.append(TagType.OPENING, "bold", "<b>");
		trgTf.append("t2");
		trgTf.append(TagType.CLOSING, "bold", "</b>");
		trgTf.append("t3");

		checker.checkSpaces(srcTf, trgTf);

		assertEquals("t1  <1>  t2</1> t3", fmt.setContent(trgTf).toString());
		assertEquals("t1  <b>  t2</b> t3", fmt.setContent(trgTf).toString(true));
	}

	@Test
	public void testSimpleRemoval() {

		// t1 <b>t2</b> t3
		TextFragment srcTf = new TextFragment("t1 ");
		srcTf.append(TagType.OPENING, "bold", "<b>");
		srcTf.append("t2");
		srcTf.append(TagType.CLOSING, "bold", "</b>");
		srcTf.append(" t3.");

		// t1 <b> t2 </b> t3
		TextFragment trgTf = new TextFragment("t1 ");
		trgTf.append(TagType.OPENING, "bold", "<b>");
		trgTf.append(" t2 ");
		trgTf.append(TagType.CLOSING, "bold", "</b>");
		trgTf.append(" t3.");

		checker.checkSpaces(srcTf, trgTf);

		assertEquals("t1 <1>t2</1> t3.", fmt.setContent(trgTf).toString());
		assertEquals("t1 <b>t2</b> t3.", fmt.setContent(trgTf).toString(true));
	}

	@Test
	public void testMultiCode() {
		
		// t1 <b>t2</b> t3 <b>t4</b> t5
		TextFragment srcTf = new TextFragment("t1 ");
		srcTf.append(TagType.OPENING, "bold", "<b>");
		srcTf.append("t2");
		srcTf.append(TagType.CLOSING, "bold", "</b>");
		srcTf.append(" t3 ");
		srcTf.append(TagType.OPENING, "italic", "<i>");
		srcTf.append("t4");
		srcTf.append(TagType.CLOSING, "italic", "</i>");
		srcTf.append(" t5");

		// t1 <b> t2 </b> t3  <b> t4    </b>t5
		TextFragment trgTf = new TextFragment("t1 ");
		trgTf.append(TagType.OPENING, "bold", "<b>");
		trgTf.append(" t2 ");
		trgTf.append(TagType.CLOSING, "bold", "</b>");
		trgTf.append(" t3  ");
		trgTf.append(TagType.OPENING, "italic", "<i>");
		trgTf.append(" t4    ");
		trgTf.append(TagType.CLOSING, "italic", "</i>");
		trgTf.append("t5");

		checker.checkSpaces(srcTf, trgTf);

		assertEquals("t1 <1>t2</1> t3 <2>t4</2> t5", fmt.setContent(trgTf).toString());
		assertEquals("t1 <b>t2</b> t3 <i>t4</i> t5", fmt.setContent(trgTf).toString(true));		
	}

	@Test
	public void testMultiNested() {

		// t1 <b><i>t2</i> t3</b> t4 <b>t5</b>
		TextFragment srcTf = new TextFragment("t1 ");
		srcTf.append(TagType.OPENING, "bold", "<b>");
		srcTf.append(TagType.OPENING, "italic", "<i>");
		srcTf.append("t2");
		srcTf.append(TagType.CLOSING, "italic", "</i>");
		srcTf.append(" t3");
		srcTf.append(TagType.CLOSING, "bold", "</b>");
		srcTf.append(" t4 ");
		srcTf.append(TagType.OPENING, "bold", "<b>");
		srcTf.append("t5");
		srcTf.append(TagType.CLOSING, "bold", "</b>");

		// t1<b> <i> t2 </i> t3 </b>t4 <b>  t5</b>
		TextFragment trgTf = new TextFragment("t1 ");
		trgTf.append(TagType.OPENING, "bold", "<b>");
		trgTf.append(" ");
		trgTf.append(TagType.OPENING, "italic", "<i>");
		trgTf.append(" t2 ");
		trgTf.append(TagType.CLOSING, "italic", "</i>");
		trgTf.append(" t3  ");
		trgTf.append(TagType.CLOSING, "bold", "</b>");
		trgTf.append("t4 ");
		trgTf.append(TagType.OPENING, "bold", "<b>");
		trgTf.append("  t5");
		trgTf.append(TagType.CLOSING, "bold", "</b>");
		
		checker.checkSpaces(srcTf, trgTf);

		assertEquals("t1 <1><2>t2</2> t3</1> t4 <3>t5</3>", fmt.setContent(trgTf).toString());
		assertEquals("t1 <b><i>t2</i> t3</b> t4 <b>t5</b>", fmt.setContent(trgTf).toString(true));
	}
	
	@Test
	public void testMultiCodeWithReorg() {
		
		// t1 <b><i>t2</i> t3</b> t4
		TextFragment srcTf = new TextFragment("t1 ");
		srcTf.append(TagType.OPENING, "bold", "<b>");
		srcTf.append(TagType.OPENING, "italic", "<i>");
		srcTf.append("t2");
		srcTf.append(TagType.CLOSING, "italic", "</i>");
		srcTf.append(" t3");
		srcTf.append(TagType.CLOSING, "bold", "</b>");
		srcTf.append(" t4");

		// <b> <i>t2  </i>t3</b>  t1 t4
		TextFragment trgTf = new TextFragment();
		trgTf.append(TagType.OPENING, "bold", "<b>");
		trgTf.append(" ");
		trgTf.append(TagType.OPENING, "italic", "<i>");
		trgTf.append("t2  ");
		trgTf.append(TagType.CLOSING, "italic", "</i>");
		trgTf.append("t3");
		trgTf.append(TagType.CLOSING, "bold", "</b>");
		trgTf.append(" ");
		trgTf.append(" t1 ");
		trgTf.append("t4");
		
		checker.checkSpaces(srcTf, trgTf);

		assertEquals("<1><2>t2</2> t3</1> t1 t4", fmt.setContent(trgTf).toString());
		assertEquals("<b><i>t2</i> t3</b> t1 t4", fmt.setContent(trgTf).toString(true));
	}
	
	@Test
	public void testInitialSpaceSrc() {
		
		// <b>t1 <i>t2</i> t3</b> t4
		TextFragment srcTf = new TextFragment(" ");
		srcTf.append(TagType.OPENING, "bold", "<b>");
		srcTf.append("t1 ");
		srcTf.append(TagType.OPENING, "italic", "<i>");
		srcTf.append("t2");
		srcTf.append(TagType.CLOSING, "italic", "</i>");
		srcTf.append(" t3");
		srcTf.append(TagType.CLOSING, "bold", "</b>");
		srcTf.append(" t4");

		// <b>t1<i>t2  </i>t3</b> t4
		TextFragment trgTf = new TextFragment();
		trgTf.append(TagType.OPENING, "bold", "<b>");
		trgTf.append("t1");
		trgTf.append(TagType.OPENING, "italic", "<i>");
		trgTf.append("t2  ");
		trgTf.append(TagType.CLOSING, "italic", "</i>");
		trgTf.append("t3");
		trgTf.append(TagType.CLOSING, "bold", "</b>");
		trgTf.append("t4");
		
		checker.checkSpaces(srcTf, trgTf);

		assertEquals(" <1>t1 <2>t2</2> t3</1> t4", fmt.setContent(trgTf).toString());
		assertEquals(" <b>t1 <i>t2</i> t3</b> t4", fmt.setContent(trgTf).toString(true));
	}
	
	@Test
	public void testTrailingSpaceSrc() {
		
		// <b>t1 <i>t2</i> t3</b> t4
		TextFragment srcTf = new TextFragment("");
		srcTf.append(TagType.OPENING, "bold", "<b>");
		srcTf.append("t1 ");
		srcTf.append(TagType.OPENING, "italic", "<i>");
		srcTf.append("t2");
		srcTf.append(TagType.CLOSING, "italic", "</i>");
		srcTf.append(" t3");
		srcTf.append(TagType.CLOSING, "bold", "</b>");
		srcTf.append(" t4 ");

		// <b>t1<i>t2  </i>t3</b> t4
		TextFragment trgTf = new TextFragment();
		trgTf.append(TagType.OPENING, "bold", "<b>");
		trgTf.append("t1");
		trgTf.append(TagType.OPENING, "italic", "<i>");
		trgTf.append("t2  ");
		trgTf.append(TagType.CLOSING, "italic", "</i>");
		trgTf.append("t3");
		trgTf.append(TagType.CLOSING, "bold", "</b>");
		trgTf.append("t4");
		
		checker.checkSpaces(srcTf, trgTf);

		assertEquals("<1>t1 <2>t2</2> t3</1> t4 ", fmt.setContent(trgTf).toString());
		assertEquals("<b>t1 <i>t2</i> t3</b> t4 ", fmt.setContent(trgTf).toString(true));
	}
	
}
