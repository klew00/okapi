/* Copyright (C) 2008 Jim Hargrave
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA              */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.filters.html.tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import net.sf.okapi.common.filters.IParser;
import net.sf.okapi.common.resource.Group;
import net.sf.okapi.common.resource.IContainable;
import net.sf.okapi.common.resource.SkeletonUnit;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.html.HtmlParser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author HargraveJE
 * 
 */
public class HtmlParserTest {
	private HtmlParser htmlParser;

	@Before
	public void setUp() {
		htmlParser = new HtmlParser();
	}

	@Test
	public void parse() {
		try {
			IParser.ParserTokenType tokenType;
			InputStream htmlStream = HtmlParserTest.class.getResourceAsStream("simpleTest.html");
			InputStreamReader htmlReader = new InputStreamReader(htmlStream, "utf-8");
			htmlParser.open(htmlStream);
			while ((tokenType = htmlParser.parseNext()) != IParser.ParserTokenType.ENDINPUT) {
				IContainable item = htmlParser.getResource();
				if (tokenType == IParser.ParserTokenType.TRANSUNIT) {
					assertTrue(item instanceof TextUnit);
					System.out.println("Text:");
				} else if (tokenType == IParser.ParserTokenType.SKELETON) {
					assertTrue(item instanceof SkeletonUnit);
					System.out.println("Skeleton:");
				} else if (tokenType == IParser.ParserTokenType.STARTGROUP
						|| tokenType == IParser.ParserTokenType.ENDGROUP) {
					assertTrue(item instanceof Group);
					System.out.println("Group:");
				}
				assertNotNull(item);
				System.out.println(item.toString());
			}
			htmlParser.close();
		} catch (UnsupportedEncodingException e) {
		} catch (IOException e) {
		}
	}
}
