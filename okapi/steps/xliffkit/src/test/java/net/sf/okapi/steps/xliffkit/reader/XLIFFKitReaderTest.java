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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.lib.extra.pipelinebuilder.XBatch;
import net.sf.okapi.lib.extra.pipelinebuilder.XBatchItem;
import net.sf.okapi.lib.extra.pipelinebuilder.XParameter;
import net.sf.okapi.lib.extra.pipelinebuilder.XPipeline;
import net.sf.okapi.lib.extra.pipelinebuilder.XPipelineStep;
import net.sf.okapi.lib.extra.steps.EventLogger;
import net.sf.okapi.lib.persistence.BeanMapper;
import net.sf.okapi.lib.persistence.NamespaceMapper;
import net.sf.okapi.lib.persistence.beans.FactoryBean;
import net.sf.okapi.lib.beans.v0.TestEvent;
import net.sf.okapi.lib.beans.v0.TestEventBean;
import net.sf.okapi.lib.beans.v0.TestEventBean2;
import net.sf.okapi.persistence.json.jackson.JSONPersistenceSession;
import net.sf.okapi.lib.beans.sessions.OkapiJsonSession;

import org.junit.Test;

@SuppressWarnings("unused")
public class XLIFFKitReaderTest {
	
	private static final LocaleId ENUS = new LocaleId("en", "us");
	private static final LocaleId FRFR = new LocaleId("fr", "fr");
	private static final LocaleId DEDE = new LocaleId("de", "de");
//	private static final LocaleId ITIT = new LocaleId("it", "it");
	
	// DEBUG 		
	@Test
	public void testReader() throws URISyntaxException {
		
		new XPipeline(
				"Test pipeline for XLIFFKitReaderStep",
				new XBatch(
						new XBatchItem(
								this.getClass().getResource("testPackageFormat.xliff.kit").toURI(),
								"UTF-8",
								new File(Util.getTempDirectory() + "/testPackageFormat").toURI(),
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
	
	// DEBUG 		
	@Test
	public void testReader2() throws URISyntaxException {
		
		new XPipeline(
				"Test pipeline for XLIFFKitReaderStep",
				new XBatch(
						new XBatchItem(
								this.getClass().getResource("testPackageFormat2.xliff.kit").toURI(),
								"UTF-8",
								new File(Util.getTempDirectory() + "/testPackageFormat2").toURI(),
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

	// DEBUG 		
	@Test
	public void testReader4() throws URISyntaxException {
		
		new XPipeline(
				"Test pipeline for XLIFFKitReaderStep",
				new XBatch(
						new XBatchItem(
								this.getClass().getResource("testPackageFormat4.xliff.kit").toURI(),
								"UTF-8",
								new File(Util.getTempDirectory() + "/testPackageFormat4").toURI(),
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

	// DEBUG 		
	@Test
	public void testReader5() throws URISyntaxException {
		
		new XPipeline(
				"Test pipeline for XLIFFKitReaderStep",
				new XBatch(
						new XBatchItem(
								this.getClass().getResource("testPackageFormat5.xliff.kit").toURI(),
								"UTF-8",
								new File(Util.getTempDirectory() + "/testPackageFormat4").toURI(),
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

	// DEBUG 		
	@Test
	public void testReader6() throws URISyntaxException {
		
		new XPipeline(
				"Test pipeline for XLIFFKitReaderStep",
				new XBatch(
						new XBatchItem(
								this.getClass().getResource("testPackageFormat6.xliff.kit").toURI(),
								"UTF-8",
								new File(Util.getTempDirectory() + "/testPackageFormat2").toURI(),
								"UTF-8",
								ENUS,
								FRFR)
						),
				new XLIFFKitReaderStep()
				,				
				new EventLogger()
//				,
//				
//				new FilterEventsToRawDocumentStep()
		).execute();
	}

	
	// DEBUG 	@Test
	public void testReferences() {
		
		new XPipeline(
				"Test pipeline for XLIFFKitReaderStep",
				new XBatch(
						new XBatchItem(
								this.getClass().getResource("testReferences.xliff.kit"),
								"UTF-8",
								Util.getTempDirectory() + "/testReferences.xliff.kit",
								"UTF-8",
								ENUS,
								ENUS)
						),
				new XPipelineStep(
						new XLIFFKitReaderStep(),
						new XParameter("generateTargets", false))
				,				
				new EventLogger()
//				,
//				
//				new FilterEventsToRawDocumentStep()
		).execute();
	}

// DEBUG @Test
	public void testReferences2() {
		
		new XPipeline(
				"Test pipeline for XLIFFKitReaderStep",
				new XBatch(
						new XBatchItem(
								this.getClass().getResource("testReferences2.xliff.kit"),
								"UTF-8",
								Util.getTempDirectory() + "/testReferences2.xliff.kit",
								"UTF-8",
								ENUS,
								ENUS)
						),
				new XPipelineStep(
						new XLIFFKitReaderStep(),
						new XParameter("generateTargets", false))
				,				
				new EventLogger()
//				,
//				
//				new FilterEventsToRawDocumentStep()
		).execute();
	}
	
	// DEBUG 		
	@Test
	public void testReferences3() {
		
		OkapiJsonSession session = new OkapiJsonSession();
		session.setItemClass(TestEvent.class);
		session.setItemLabel("event");
				
		InputStream inStream = this.getClass().getResourceAsStream("test_refs3.txt.json"); 
		session.start(inStream);
		session.registerBean(TestEvent.class, TestEventBean.class);
		
		TestEvent sd = session.deserialize(TestEvent.class); // StartDocument
		
		TestEvent e1 = session.deserialize(TestEvent.class);
		TestEvent e2 = session.deserialize(TestEvent.class);
		
		assertTrue("e1".equals(e1.getId()));
		assertTrue("e2".equals(e2.getId()));
		
		assertTrue("e2".equals(e1.getParent().getId()));
		assertTrue("e1".equals(e2.getParent().getId()));
		
		TestEvent ed = session.deserialize(TestEvent.class); // Ending
		TestEvent e4 = session.deserialize(TestEvent.class);
		assertNull(e4);
		TestEvent e5 = session.deserialize(TestEvent.class);
		assertNull(e5);
		TestEvent e6 = session.deserialize(TestEvent.class);
		assertNull(e6);
		session.end();
	}
	
	@Test
	public void testReferences4() {
		
		OkapiJsonSession session = new OkapiJsonSession();
		session.setItemClass(TestEvent.class);
		session.setItemLabel("event");
				
		InputStream inStream = this.getClass().getResourceAsStream("test_refs4.txt.json"); 
		session.start(inStream);
		session.registerBean(TestEvent.class, TestEventBean.class);
		TestEvent sd = session.deserialize(TestEvent.class); // StartDocument
		
		TestEvent e1 = session.deserialize(TestEvent.class);
		TestEvent e2 = session.deserialize(TestEvent.class);
		TestEvent e3 = session.deserialize(TestEvent.class);
		TestEvent e4 = session.deserialize(TestEvent.class);
		TestEvent e5 = session.deserialize(TestEvent.class);
		TestEvent e6 = session.deserialize(TestEvent.class);
		TestEvent e7 = session.deserialize(TestEvent.class);
		
		assertTrue("e1".equals(e1.getId()));
		assertTrue("e2".equals(e2.getId()));
		assertTrue("e3".equals(e3.getId()));
		assertTrue("e4".equals(e4.getId()));
		assertTrue("e5".equals(e5.getId()));
		assertTrue("e6".equals(e6.getId()));
		assertTrue("e7".equals(e7.getId()));
		
		assertTrue("e3".equals(e1.getParent().getId()));
		assertTrue("e4".equals(e3.getParent().getId()));
		assertTrue("e6".equals(e2.getParent().getId()));
		assertTrue("e6".equals(e7.getParent().getId()));
		assertTrue("e2".equals(e5.getParent().getId()));
		
		TestEvent ed = session.deserialize(TestEvent.class); // Ending
		TestEvent e8 = session.deserialize(TestEvent.class);
		assertNull(e8);
		TestEvent e9 = session.deserialize(TestEvent.class);
		assertNull(e9);
		TestEvent e10 = session.deserialize(TestEvent.class);
		assertNull(e10);
		session.end();
	}
		
	@Test
	public void testReferences5() {
		
		OkapiJsonSession session = new OkapiJsonSession();
		session.setItemClass(TestEvent.class);
		session.setItemLabel("event");
				
		InputStream inStream = this.getClass().getResourceAsStream("test_refs5.txt.json"); 
		session.start(inStream);
		session.registerBean(TestEvent.class, TestEventBean2.class);
		TestEvent sd = session.deserialize(TestEvent.class); // StartDocument
		
		TestEvent e1 = session.deserialize(TestEvent.class);
		TestEvent e2 = session.deserialize(TestEvent.class);
		
		assertEquals("e1", e1.getId());
		assertEquals("e2", e2.getId());
		
		TestEvent p1 = e1.getParent();
		TestEvent p2 = e2.getParent();
		
		assertEquals(e2, p1);
		assertEquals(e1, p2);
		
		TestEvent ed = session.deserialize(TestEvent.class); // Ending
		TestEvent e8 = session.deserialize(TestEvent.class);
		assertNull(e8);
		TestEvent e9 = session.deserialize(TestEvent.class);
		assertNull(e9);
		TestEvent e10 = session.deserialize(TestEvent.class);
		assertNull(e10);
		session.end();
	}
	
	@Test
	public void testReferences6() {
		
		OkapiJsonSession session = new OkapiJsonSession();
		session.setItemClass(TestEvent.class);
		session.setItemLabel("event");
				
		InputStream inStream = this.getClass().getResourceAsStream("test_refs6.txt.json"); 
		session.start(inStream);
		session.registerBean(TestEvent.class, TestEventBean2.class);
		TestEvent sd = session.deserialize(TestEvent.class); // StartDocument
		
		TestEvent e1 = session.deserialize(TestEvent.class);
		TestEvent e2 = session.deserialize(TestEvent.class);
		TestEvent e3 = session.deserialize(TestEvent.class);
		TestEvent e4 = session.deserialize(TestEvent.class);
		TestEvent e5 = session.deserialize(TestEvent.class);
		TestEvent e6 = session.deserialize(TestEvent.class);
		TestEvent e7 = session.deserialize(TestEvent.class);
		
		assertEquals("e1", e1.getId());
		assertEquals("e2", e2.getId());
		assertEquals("e3", e3.getId());
		assertEquals("e4", e4.getId());
		assertEquals("e5", e5.getId());
		assertEquals("e6", e6.getId());
		assertEquals("e7", e7.getId());
		
		TestEvent p1 = e1.getParent();
		TestEvent p2 = e2.getParent();
		TestEvent p3 = e3.getParent();
		TestEvent p4 = e4.getParent();
		TestEvent p5 = e5.getParent();
		TestEvent p6 = e6.getParent();
		TestEvent p7 = e7.getParent();
		
		assertEquals(e3, p1);
		assertEquals(e6, p2);
		assertEquals(e4, p3);
		assertEquals(null, p4);
		assertEquals(e2, p5);
		assertEquals(null, p6);
		assertEquals(e6, p7);
		
		TestEvent ed = session.deserialize(TestEvent.class); // Ending
		TestEvent e8 = session.deserialize(TestEvent.class);
		assertNull(e8);
		TestEvent e9 = session.deserialize(TestEvent.class);
		assertNull(e9);
		TestEvent e10 = session.deserialize(TestEvent.class);
		assertNull(e10);
		session.end();
	}
}
