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

import java.io.InputStream;

import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.FilterEventType;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.html.HtmlFilter;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author HargraveJE
 * 
 */
public class HtmlParserTest {
	private HtmlFilter htmlParser;

	@Before
	public void setUp() {		
	}

	@Test
	public void excludeInclude() {
		htmlParser = new HtmlFilter();		
		InputStream htmlStream = HtmlParserTest.class.getResourceAsStream("/simpleTest.html");
		htmlParser.open(htmlStream);			
		while (htmlParser.hasNext()) {
			FilterEvent event = htmlParser.next();
			if (event.getEventType() == FilterEventType.TEXT_UNIT) {
				assertTrue(event.getResource() instanceof TextUnit);								
			} else if (event.getEventType() == FilterEventType.DOCUMENT_PART) {
				assertTrue(event.getResource() instanceof DocumentPart);				
			} else if (event.getEventType() == FilterEventType.START_GROUP || event.getEventType() == FilterEventType.END_GROUP) {
				assertTrue(event.getResource() instanceof StartGroup || event.getResource() instanceof Ending);				
			}			
			System.out.println(event.getEventType().toString());
			if (event.getResource() != null) {
				System.out.println(event.getResource().toString());
				if (event.getResource().getSkeleton() != null) {
					System.out.println(event.getResource().getSkeleton().toString());
				}
			}
		}
		htmlParser.close();
	}
}
