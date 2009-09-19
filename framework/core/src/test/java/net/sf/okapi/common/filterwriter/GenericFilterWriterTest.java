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

package net.sf.okapi.common.filterwriter;

import java.io.ByteArrayOutputStream;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.filterwriter.GenericFilterWriter;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class GenericFilterWriterTest {

	public Event startDocEvent;
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSourceTargetSkeleton () {
		TextUnit tu = new TextUnit("tu1");
		tu.setSourceContent(new TextFragment("src"));
		tu.setTargetContent("fr", new TextContainer("trg"));
		Event textUnitEvent = new Event(EventType.TEXT_UNIT, tu);
			
		GenericSkeleton skel = new GenericSkeleton();
		skel.add("[start]");
		skel.addContentPlaceholder(tu);
		skel.add("[middle]");
		skel.addContentPlaceholder(tu, "fr");
		skel.add("[end]");
		tu.setSkeleton(skel);
			
		GenericFilterWriter writer = new GenericFilterWriter(new GenericSkeletonWriter());
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		writer.setOptions("fr", "UTF-8");
		writer.setOutput(output);
		writer.handleEvent(createStartDocument(true));
		writer.handleEvent(textUnitEvent);
		writer.close();
		
		assertEquals("[start]src[middle]trg[end]", output.toString());
	}

	@Test
	public void testTextUnitReferenceInDocumentPart () {
		TextUnit tu = new TextUnit("tu1");
		tu.setSourceContent(new TextFragment("text"));
		tu.setIsReferent(true);
		Event textUnitEvent = new Event(EventType.TEXT_UNIT, tu);
			
		GenericSkeleton skel = new GenericSkeleton();
		skel.add("[bSkel]");
		skel.addReference(tu);
		skel.add("[eSkel]");
		DocumentPart dp = new DocumentPart("id1", false);
		dp.setSkeleton(skel);
		Event docPartEvent = new Event(EventType.DOCUMENT_PART, dp);
			
		GenericFilterWriter writer = new GenericFilterWriter(new GenericSkeletonWriter());
		writer.setOptions("en", "UTF-8");
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		writer.setOutput(output);
			
		writer.handleEvent(createStartDocument(false));
		writer.handleEvent(textUnitEvent);
		writer.handleEvent(docPartEvent);
		writer.close();
			
		assertEquals("[bSkel]text[eSkel]", output.toString());
	}

	private Event createStartDocument (boolean multilingual) {
		StartDocument sd = new StartDocument("sd");
		sd.setEncoding("UTF-8", false);
		sd.setLineBreak("\n");
		sd.setLanguage("en");
		sd.setMultilingual(multilingual);
		startDocEvent = new Event(EventType.START_DOCUMENT, sd);
		return startDocEvent;
	}
}
