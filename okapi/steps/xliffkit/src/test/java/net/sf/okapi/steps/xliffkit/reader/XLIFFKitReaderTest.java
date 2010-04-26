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

package net.sf.okapi.steps.xliffkit.reader;

import static org.junit.Assert.assertNull;

import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.Test;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.steps.xliffkit.common.persistence.BeanMapper;
import net.sf.okapi.steps.xliffkit.common.persistence.json.jackson.JSONPersistenceSession;
import net.sf.okapi.steps.xliffkit.common.persistence.versioning.TestEvent;
import net.sf.okapi.steps.xliffkit.common.persistence.versioning.TestEventBean;
import net.sf.okapi.steps.xliffkit.sandbox.pipelinebuilder.Batch;
import net.sf.okapi.steps.xliffkit.sandbox.pipelinebuilder.BatchItem;
import net.sf.okapi.steps.xliffkit.sandbox.pipelinebuilder.Parameter;
import net.sf.okapi.steps.xliffkit.sandbox.pipelinebuilder.Pipeline;
import net.sf.okapi.steps.xliffkit.sandbox.pipelinebuilder.PipelineStep;

public class XLIFFKitReaderTest {
	
	private static final LocaleId ENUS = new LocaleId("en", "us");
	private static final LocaleId FRFR = new LocaleId("fr", "fr");
	private static final LocaleId DEDE = new LocaleId("de", "de");
//	private static final LocaleId ITIT = new LocaleId("it", "it");
	
	// DEBUG 		@Test
	public void testReader() {
		
		new Pipeline(
				"Test pipeline for XLIFFKitReaderStep",
				new Batch(
						new BatchItem(
								this.getClass().getResource("testPackageFormat.xliff.kit"),
								"UTF-8",
								Util.getTempDirectory() + "/testPackageFormat",
								"UTF-8",
								ENUS,
								ENUS)
						),
				new XLIFFKitReaderStep()
				,				
				new EventLogger()
//				,
//				
//				new FilterEventsToRawDocumentStep()
		).execute();
	}
	
	// DEBUG 		@Test
	public void testReader2() {
		
		new Pipeline(
				"Test pipeline for XLIFFKitReaderStep",
				new Batch(
						new BatchItem(
								this.getClass().getResource("testPackageFormat2.xliff.kit"),
								"UTF-8",
								Util.getTempDirectory() + "/testPackageFormat2",
								"UTF-8",
								ENUS,
								DEDE)
						),
				new XLIFFKitReaderStep()
				,				
				new EventLogger()
//				,
//				
//				new FilterEventsToRawDocumentStep()
		).execute();
	}

	// DEBUG 		@Test
	public void testReader4() {
		
		new Pipeline(
				"Test pipeline for XLIFFKitReaderStep",
				new Batch(
						new BatchItem(
								this.getClass().getResource("testPackageFormat4.xliff.kit"),
								"UTF-8",
								Util.getTempDirectory() + "/testPackageFormat4",
								"UTF-8",
								ENUS,
								ENUS)
						),
				new XLIFFKitReaderStep()
				,				
				new EventLogger()
//				,
//				
//				new FilterEventsToRawDocumentStep()
		).execute();
	}

	// DEBUG 		@Test
	public void testReferences() {
		
		new Pipeline(
				"Test pipeline for XLIFFKitReaderStep",
				new Batch(
						new BatchItem(
								this.getClass().getResource("testReferences.xliff.kit"),
								"UTF-8",
								Util.getTempDirectory() + "/testReferences.xliff.kit",
								"UTF-8",
								ENUS,
								ENUS)
						),
				new PipelineStep(
						new XLIFFKitReaderStep(),
						new Parameter("generateTargets", false))
				,				
				new EventLogger()
//				,
//				
//				new FilterEventsToRawDocumentStep()
		).execute();
	}

// DEBUG 		@Test
	public void testReferences2() {
		
		new Pipeline(
				"Test pipeline for XLIFFKitReaderStep",
				new Batch(
						new BatchItem(
								this.getClass().getResource("testReferences2.xliff.kit"),
								"UTF-8",
								Util.getTempDirectory() + "/testReferences2.xliff.kit",
								"UTF-8",
								ENUS,
								ENUS)
						),
				new PipelineStep(
						new XLIFFKitReaderStep(),
						new Parameter("generateTargets", false))
				,				
				new EventLogger()
//				,
//				
//				new FilterEventsToRawDocumentStep()
		).execute();
	}
	
	@SuppressWarnings("unused")
	// DEBUG 		
	@Test
	public void testReferences3() {
		
		JSONPersistenceSession session = new JSONPersistenceSession();
		session.setItemClass(TestEvent.class);
		session.setItemLabel("event");
		
		BeanMapper.registerBean(TestEvent.class, TestEventBean.class);
		InputStream inStream = this.getClass().getResourceAsStream("test_refs3.txt.json"); 
		session.start(inStream);		
		TestEvent sd = session.deserialize(TestEvent.class); // StartDocument
		
		TestEvent e1 = session.deserialize(TestEvent.class);
		TestEvent e2 = session.deserialize(TestEvent.class);
		
		TestEvent ed = session.deserialize(TestEvent.class); // Ending
		TestEvent e4 = session.deserialize(TestEvent.class);
		assertNull(e4);
		TestEvent e5 = session.deserialize(TestEvent.class);
		assertNull(e5);
		TestEvent e6 = session.deserialize(TestEvent.class);
		assertNull(e6);
		session.end();
	}
}
