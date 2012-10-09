/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.filters.doxygen;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.RawDocument;

import org.junit.Before;
import org.junit.Test;

public class DoxygenWriterTest {
	private DoxygenFilter filter;
	private IFilterWriter writer;
		
	@Before
	public void setUp() {
		filter = new DoxygenFilter();
		writer = new DoxygenWriter();
		filter.setOptions(LocaleId.ENGLISH, LocaleId.SPANISH, "UTF-8", true);
	}
	
	@Test
	public void testOutputMultilineComment() throws IOException {
		String snippet = "foo foo foo /// This is \n"
				   + "bar bar bar /// a test.\n"
				   + "baz baz baz /// ";
		String expected = "foo foo foo /// This is a test.\n"
						+ "bar bar bar /// \n"
						+ "baz baz baz ///\n";
		String result = eventWriter(snippet);
		assertEquals(expected, result);
	}
	
	@Test
	public void testOutputJavadocComment() throws IOException {
		String snippet = "/**\n"
				   + " * This is \n"
				   + " * a test.\n"
				   + " */\n"
				   + "baz baz baz";
		String expected = "/**\n"
						+ " * This is a test.\n"
						+ " * \n"
						+ " */\n"
						+ "baz baz baz\n";
		String result = eventWriter(snippet);
		assertEquals(expected, result);
	}
	
	private String eventWriter(String input) throws IOException {
		try {
			// Open the input
			filter.open(new RawDocument(input, LocaleId.ENGLISH, LocaleId.SPANISH));

			// Prepare the output
			writer.setOptions(LocaleId.SPANISH, "UTF-8");
			ByteArrayOutputStream writerBuffer = new ByteArrayOutputStream();
			writer.setOutput(writerBuffer);

			// Process the document
			Event event;
			while (filter.hasNext()) {
				event = filter.next();
				writer.handleEvent(event);
			}			
			writerBuffer.close();
			return new String(writerBuffer.toByteArray(), "UTF-8");
		} finally {
			if (filter != null)
				filter.close();
			if (writer != null)
				writer.close();
		}
	}
}
