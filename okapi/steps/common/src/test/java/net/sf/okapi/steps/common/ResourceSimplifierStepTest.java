/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.common;

import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.InputDocument;
import net.sf.okapi.common.filters.RoundTripComparison;
import net.sf.okapi.filters.html.HtmlFilter;
import net.sf.okapi.steps.common.ResourceSimplifierStep;
import org.junit.Test;

public class ResourceSimplifierStepTest {
	private static final LocaleId ENUS = new LocaleId("en", "us");
	
	@Test
	public void testDoubleExtraction () throws URISyntaxException, IOException {
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();				
		String pathBase = Util.getDirectoryName(this.getClass().getResource("aa324.html").getPath()) + "/";
		
		list.add(new InputDocument(pathBase + "aa324.html", null));
		list.add(new InputDocument(pathBase + "form.html", null));
		list.add(new InputDocument(pathBase + "W3CHTMHLTest1.html", null));
		
		RoundTripComparison rtc = new RoundTripComparison();
		ResourceSimplifierStep rss = new ResourceSimplifierStep(false);
		
		assertTrue(rtc.executeCompare(new HtmlFilter(), list, "UTF-8", ENUS, ENUS, "skeleton", rss));
		//assertTrue(rtc.executeCompare(new HtmlFilter(), list, "UTF-8", ENUS, ENUS, "skeleton"));
	}
}
