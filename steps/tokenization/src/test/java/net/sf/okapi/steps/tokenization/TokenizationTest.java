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

package net.sf.okapi.steps.tokenization;

import static org.junit.Assert.assertEquals;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.plaintext.common.TextUnitUtils;
import net.sf.okapi.steps.tokenization.engine.rbbi.WordBreakIteratorStep;
import net.sf.okapi.steps.tokenization.locale.LocaleUtils;
import net.sf.okapi.steps.tokenization.tokens.Tokens;

import org.junit.Before;
import org.junit.Test;

import com.ibm.icu.text.BreakIterator;
import com.ibm.icu.text.RuleBasedBreakIterator;

public class TokenizationTest {

	private String text = "Jaguar will sell its new XJ-6 model in the U.S. for " +
    "a small fortune :-). Expect to pay around USD 120ks. Custom options " +
    "can set you back another few 10,000 dollars. For details, go to " +
    "<a href=\"http://www.jaguar.com/sales\" alt=\"Click here\">" +
    "Jaguar Sales</a> or contact xj-6@jaguar.com.";
	
	private TokenizationStep ts;
	
	@Before
	public void setUp() {
		
	}

	@Test
	public void testDefRules() {
		
		RuleBasedBreakIterator iterator = (RuleBasedBreakIterator) BreakIterator.getWordInstance();
		System.out.println(iterator.toString().replace(";", ";\n"));
	}
	
	@Test
	public void testLocaleUtils() {
		
		assertEquals("EN-US", LocaleUtils.normalizeLanguageCode_Okapi("en_US"));
		assertEquals("en_US", LocaleUtils.normalizeLanguageCode_ICU("EN-US"));
	}
	
	@Test
	public void testTS() {
		
		ts = new TokenizationStep();
		
		TextUnit tu = TextUnitUtils.buildTU(text);
		Event event = new Event(EventType.TEXT_UNIT, tu);
		
		ts.handleEvent(new Event(EventType.START_BATCH));
		ts.handleEvent(event);
		ts.handleEvent(new Event(EventType.END_BATCH));
	}
	
	@Test
	public void testRBBI() {
		/* TODO debug
	    String text2 = "Test word count is correct.";
	    String text3 = "The quick (\"brown\") fox can't jump 32.3 feet, right?";
	    String text4 = "The quick (“brown”) fox can’t jump 32.3 feet, right?";

		WordBreakIteratorStep rbbi = new WordBreakIteratorStep();
		
		System.out.println("---------------------------");
		rbbi.tokenize(text, new Tokens(), "en-us", new String[] {});
		System.out.println("---------------------------");
		rbbi.tokenize(text2, new Tokens(), "en-us", new String[] {});
		System.out.println("---------------------------");
		rbbi.tokenize(text3, new Tokens(), "en-us", new String[] {});
		System.out.println("---------------------------");
		rbbi.tokenize(text4, new Tokens(), "en-us", new String[] {});
		*/
	}
}
