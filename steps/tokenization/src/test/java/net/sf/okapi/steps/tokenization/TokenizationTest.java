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

// This test case uses parts of the code presented by Sujit Pal at http://sujitpal.blogspot.com/2008/05/tokenizing-text-with-icu4js.html

package net.sf.okapi.steps.tokenization;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextUnitUtil;
import net.sf.okapi.steps.tokenization.common.Token;
import net.sf.okapi.steps.tokenization.engine.RbbiLexer;
import net.sf.okapi.steps.tokenization.locale.LocaleUtil;
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
    "Jaguar Sales</a> or contact xj-6@jaguar.com. See http://www.jaguar.com/sales, www.jaguar.com, 192.168.0.5 for info.";

	//private String text = "http://www.jaguar.com/sales";
	//private String text = "<a href=\"http://www.jaguar.com/sales\" alt=\"Click here\">";
	
	private TokenizationStep ts;
	
	@Before
	public void setUp() {
				
	}

	@Test
	public void testDefRules() {
		
		@SuppressWarnings("unused")
		RuleBasedBreakIterator iterator = (RuleBasedBreakIterator) BreakIterator.getWordInstance();
		// System.out.println(iterator.toString().replace(";", ";\n"));
	}
	
	@Test
	public void testLocaleUtil() {
		
		assertEquals("EN-US", LocaleUtil.normalizeLanguageCode_Okapi("en_US"));
		assertEquals("en_US", LocaleUtil.normalizeLanguageCode_ICU("EN-US"));
	}
	
	@Test
	public void testTS() {
		
		ts = new TokenizationStep();
		
		TextUnit tu = TextUnitUtil.buildTU(text);
		Event event = new Event(EventType.TEXT_UNIT, tu);
		
		ts.handleEvent(new Event(EventType.START_BATCH));
		ts.handleEvent(event);
		ts.handleEvent(new Event(EventType.END_BATCH));
	}
	
	private void listTokens(Tokens tokens) {
		
		if (tokens == null) return;
		for (Token token : tokens) {	
			
			System.out.println(token.toString());
		}
	}
	
	@Test
	public void testRBBI() {
		/* 
	    String text2 = "Test word count is correct.";
	    String text3 = "The quick (\"brown\") fox can't jump 32.3 feet, right?";
	    String text4 = "The quick (“brown”) fox can’t jump 32.3 feet, right?";
		*/
		
		Tokens tokens = Tokenizer.tokenize(text, "en-us"); // All tokens
		//assertEquals(127, tokens.size());
		
		//System.out.println(tokens.size());		
		listTokens(tokens);
	}
	
	@Test
	public void testRetainRemove() {
		
		List<String> list = new ArrayList<String> ();
		list.add("A");
		list.add("B");
		list.add("C");
		
		List<String> whiteList = new ArrayList<String> ();
		whiteList.add("A");
		whiteList.add("B");
						
		List<String> blackList = new ArrayList<String> ();
		blackList.add("B");

		assertEquals(3, list.size());
		assertEquals("A", list.get(0));
		assertEquals("B", list.get(1));
		assertEquals("C", list.get(2));
		
		list.retainAll(whiteList);
		assertEquals(2, list.size());
		assertEquals("A", list.get(0));
		assertEquals("B", list.get(1));
		
		list.removeAll(blackList);
		assertEquals(1, list.size());
		assertEquals("A", list.get(0));
	}
	
	@Test
	public void testFormRbbiRules() {
		
		String rules = null;
		String expected = null;
		
		try {
			expected = Util.normalizeNewlines(streamAsString(this.getClass().getResourceAsStream("/rbbi_custom.txt")));
			rules = streamAsString(this.getClass().getResourceAsStream("/rbbi_default.txt"));
			
			//rules = RbbiLexer.formatCaption(rules, "Custom rules");
			
			rules = RbbiLexer.formatRule(rules, 
					"Abbreviation", 
					"Abbreviation: Uppercase alpha chars separated by period and optionally followed by a period", 
					"[A-Z0-9](\\.[A-Z0-9])+(\\.)*",
					500);
			rules = RbbiLexer.formatRule(rules, 
					"HyphenatedWord", 
					"Hyphenated Word : sequence of letter or digit, (punctuated by - or _, with following letter or digit sequence)+", 
					"[A-Za-z0-9]+([\\-_][A-Za-z0-9]+)+", 
					501);
			rules = RbbiLexer.formatRule(rules, 
					"EmailAddress", 
					"Email address: sequence of letters, digits and punctuation followed by @ and followed by another sequence", 
					"[A-Za-z0-9_\\-\\.]+\\@[A-Za-z][A-Za-z0-9_]+\\.[a-z]+",
					502);
			rules = RbbiLexer.formatRule(rules, 
					"InternetAddress", 
					"Internet Addresses: http://www.foo.com(/bar)", 
					"[a-z]+\\:\\/\\/[a-z0-9]+(\\.[a-z0-9]+)+(\\/[a-z0-9][a-z0-9\\.]+)", 
					503);
			rules = RbbiLexer.formatRule(rules, 
					"XmlMarkup", 
					"XML markup: A run begins with < and ends with the first matching >", 
					"\\<[^\\>]+\\>", 
					504);
			rules = RbbiLexer.formatRule(rules, 
					"Emoticon", 
					"Emoticon: A run that starts with :;B8{[ and contains only one or more of the following -=/{})(", 
					"[B8\\:\\;\\{\\[][-=\\/\\{\\}\\)\\(]+",
					505);
			
		} catch (IOException e) {

			e.printStackTrace();
		}
		
		assertEquals(expected, rules);
	}
	
	private String streamAsString(InputStream input) throws IOException {
		BufferedReader reader = null;
		reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));

		StringBuilder tmp = new StringBuilder();
		char[] buf = new char[2048];
		int count = 0;
		while (( count = reader.read(buf)) != -1 ) {
			tmp.append(buf, 0, count);
		}
		
        return tmp.toString();
    }
}
