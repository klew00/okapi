/*===========================================================================
  Copyright (C) 2010-2011 by the Okapi Framework contributors
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

package net.sf.okapi.virtualdb.jdbc.h2;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.virtualdb.IVDocument;
import net.sf.okapi.virtualdb.IVItem;
import net.sf.okapi.virtualdb.IVRepository;
import net.sf.okapi.virtualdb.IVTextUnit;
import net.sf.okapi.virtualdb.KeyAndSegId;
import net.sf.okapi.virtualdb.IVItem.ItemType;
import net.sf.okapi.virtualdb.jdbc.Repository;
import net.sf.okapi.virtualdb.jdbc.h2.H2Access;

import static org.junit.Assert.*;
import org.junit.Test;

public class H2ImplementationTest {
	
	private FilterConfigurationMapper fcMapper;
	private LocaleId locEN = LocaleId.fromBCP47("en");
	private LocaleId locFR = LocaleId.fromBCP47("fr");
	private String root;

	public H2ImplementationTest () throws URISyntaxException {
		URL url = H2ImplementationTest.class.getResource("/test01.xlf");
		root = Util.getDirectoryName(url.toURI().getPath());
	}

	@Test
	public void testExtraData1 () throws IOException {
		// Create the repository object
		IVRepository repo = new Repository(new H2Access(root, null));
		// Create the repository database
		repo.create("myRepo");

		String data = "this is a test";
		byte[] buffer = data.getBytes("UTF-8");
		repo.saveExtraData1(new ByteArrayInputStream(buffer));
		ByteArrayInputStream bais = (ByteArrayInputStream)repo.loadExtraData1();
		byte [] buf = new byte[bais.available()];
		bais.read(buf);
		String out = new String(buf);
		assertEquals(data, out);
		
		repo.close();
	}
	
	@Test
	public void testItemNavigation () {
		fcMapper = new FilterConfigurationMapper();
		fcMapper.addConfigurations("net.sf.okapi.filters.xliff.XLIFFFilter");
		// Create the repository object
		IVRepository repo = new Repository(new H2Access(root, fcMapper));
		// Create the repository database
		repo.create("myRepo");
		// Import file 1
		RawDocument rd = new RawDocument((new File(root+"/allItems.xlf")).toURI(), "UTF-8", locEN, locFR);
		rd.setFilterConfigId("okf_xliff");
		repo.importDocument(rd);

		IVDocument vdoc = repo.getFirstDocument();
		IVItem item1 = vdoc.getFirstChild(); // Get first sub-document
		assertEquals(ItemType.SUB_DOCUMENT, item1.getItemType());
		assertEquals("subdoc1", item1.getName());
		// Check that the parent is the document
		assertEquals(ItemType.DOCUMENT, item1.getParent().getItemType());

		IVItem item2 = item1.getNextSibling(); // Get second sub-document
		assertEquals(ItemType.SUB_DOCUMENT, item2.getItemType());
		assertEquals("subdoc2", item2.getName());
		// Check that the parent is the document
		assertEquals(ItemType.DOCUMENT, item2.getParent().getItemType());
		
		IVItem item3 = item2.getNextSibling(); // Get third sub-document
		assertEquals(ItemType.SUB_DOCUMENT, item3.getItemType());
		assertEquals("subdoc3", item3.getName());
		// Check that the parent is the document
		assertEquals(ItemType.DOCUMENT, item3.getParent().getItemType());

		item2 = item1.getFirstChild(); // Check first TU is first sub-document
		assertEquals(ItemType.TEXT_UNIT, item2.getItemType());
		assertEquals("f1-sd1-1", item2.getName());
		// Check that the parent
		assertEquals("subdoc1", item2.getParent().getName());
		
		item2 = item2.getNextSibling(); // Check second TU is first sub-document
		assertEquals(ItemType.TEXT_UNIT, item2.getItemType());
		assertEquals("f1-sd1-2", item2.getName());
		// Check that the parent
		assertEquals("subdoc1", item2.getParent().getName());
		
		item2 = item2.getNextSibling(); // Check third entry in first sub-document: should be a group
		assertEquals(ItemType.GROUP, item2.getItemType());
		assertEquals("f1-sd1-g1", item2.getName());
		// Check that the parent
		assertEquals("subdoc1", item2.getParent().getName());
		
		item3 = item2.getFirstChild(); // Check first TU in group
		assertEquals(ItemType.TEXT_UNIT, item3.getItemType());
		assertEquals("f1-sd1-g1-1", item3.getName());
		// Check that the parent is the group
		assertEquals("f1-sd1-g1", item3.getParent().getName());
		
		item2 = item2.getNextSibling(); // Check TU after the group
		assertEquals(ItemType.TEXT_UNIT, item2.getItemType());
		assertEquals("f1-sd1-3", item2.getName());
		// Check that the parent
		assertEquals("subdoc1", item2.getParent().getName());

		item2 = item1.getNextSibling().getFirstChild(); // Get first TU of second sub-document
		assertEquals(ItemType.TEXT_UNIT, item2.getItemType());
		assertEquals("f1-sd2-1", item2.getName());
		// Check that the parent
		assertEquals("subdoc2", item2.getParent().getName());
		
		item2 = item1.getNextSibling().getNextSibling().getFirstChild(); // Get the group of the third sub-document
		assertEquals(ItemType.GROUP, item2.getItemType());
		assertEquals("f1-sd3-g1", item2.getName());
		// Check that the parent
		assertEquals("subdoc3", item2.getParent().getName());
		
		item2 = item2.getFirstChild(); // Get group inside group
		assertEquals(ItemType.GROUP, item2.getItemType());
		assertEquals("f1-sd3-g1-g1", item2.getName());
		// Check that the parent
		assertEquals("f1-sd3-g1", item2.getParent().getName());
		
		item2 = item2.getFirstChild(); // Get TU of group
		assertEquals(ItemType.TEXT_UNIT, item2.getItemType());
		assertEquals("f1-sd3-g1-g1-1", item2.getName());
		// Check that the parent
		assertEquals("f1-sd3-g1-g1", item2.getParent().getName());
		
		repo.close();
	}

	@Test
	public void testDirectNavigation () {
		fcMapper = new FilterConfigurationMapper();
		fcMapper.addConfigurations("net.sf.okapi.filters.xliff.XLIFFFilter");
		// Create the repository object
		IVRepository repo = new Repository(new H2Access(root, fcMapper));
		// Create the repository database
		repo.create("myRepo");
		// Import file 1
		RawDocument rd = new RawDocument((new File(root+"/allItems.xlf")).toURI(), "UTF-8", locEN, locFR);
		rd.setFilterConfigId("okf_xliff");
		repo.importDocument(rd);
		
		IVDocument vdoc = repo.getFirstDocument();
		IVItem item = vdoc.getFirstChild(); // Get first sub-document
		assertEquals("subdoc1", item.getName());
		
		assertEquals("subdoc2", item.getNextSibling().getName());
		assertEquals("subdoc1", item.getNextSibling().getPreviousSibling().getName());
		assertEquals("subdoc3", item.getNextSibling().getNextSibling().getName());
		
		assertEquals("f1-sd1-1", item.getFirstChild().getName());
		assertEquals("f1-sd1-2", item.getFirstChild().getNextSibling().getName());
		assertEquals("subdoc1", item.getFirstChild().getNextSibling().getParent().getName());

		assertEquals("f1-sd1-g1", item.getFirstChild().getNextSibling().getNextSibling().getName());
		assertEquals("f1-sd1-2", item.getFirstChild().getNextSibling().getNextSibling().getPreviousSibling().getName());
		
		assertEquals("f1-sd3-g1-g1-1", item.getNextSibling().getNextSibling().getFirstChild()
			.getFirstChild().getFirstChild().getName());
		
		repo.close();
	}
	
	@Test
	public void testSameSourceDifferentTarget () {
		fcMapper = new FilterConfigurationMapper();
		fcMapper.addConfigurations("net.sf.okapi.filters.xliff.XLIFFFilter");
		// Create the repository object
		H2Access acc = new H2Access(root, fcMapper);
		IVRepository repo = new Repository(acc);
		// Create the repository database
		repo.create("myRepo");
		// Import file
		RawDocument rd = new RawDocument((new File(root+"/testWithDup.xlf")).toURI(), "UTF-8", locEN, locFR);
		rd.setFilterConfigId("okf_xliff");
		repo.importDocument(rd);
		IVDocument vdoc = repo.getFirstDocument();
		
		List<List<KeyAndSegId>> list = acc.getSameSourceWithDifferentTarget();
		assertEquals(2, list.size());
		
//		for ( List<Long> group : list ) {
//			System.out.println("===");
//			for ( long key : group ) {
//				TextUnit y = ((IVTextUnit)vdoc.getItem(key)).getTextUnit();
//				System.out.println(y.toString());
//			}
//		}

		// "source text"
		long key = list.get(0).get(0).key;
		ITextUnit tu = ((IVTextUnit)vdoc.getItem(key)).getTextUnit();
		assertEquals("source text", tu.toString());
		assertEquals(3, list.get(0).size());
		
		// "source text type 3"
		key = list.get(1).get(0).key;
		tu = ((IVTextUnit)vdoc.getItem(key)).getTextUnit();
		assertEquals("source text type 3", tu.toString());
		assertEquals(2, list.get(1).size());

		repo.close();
	}
	
	@Test
	public void testSameSourceDifferentTarget_ForSegment () {
		fcMapper = new FilterConfigurationMapper();
		fcMapper.addConfigurations("net.sf.okapi.filters.xliff.XLIFFFilter");
		// Create the repository object
		H2Access acc = new H2Access(root, fcMapper);
		IVRepository repo = new Repository(acc);
		// Create the repository database
		repo.create("myRepo");
		// Import file
		RawDocument rd = new RawDocument((new File(root+"/testWithDup.xlf")).toURI(), "UTF-8", locEN, locFR);
		rd.setFilterConfigId("okf_xliff");
		repo.importDocument(rd);
		IVDocument vdoc = repo.getFirstDocument();
		
		List<List<KeyAndSegId>> list = acc.getSegmentsWithSameSourceButDifferentTarget(locFR);
		assertEquals(3, list.size());
		
//		for ( List<KeyAndSegId> group : list ) {
//			System.out.println("===");
//			for ( KeyAndSegId tmp : group ) {
//				TextUnit y = ((IVTextUnit)vdoc.getItem(tmp.key)).getTextUnit();
//				System.out.println("---");
//				for ( Segment seg : y.getSource().getSegments() ) {
//					System.out.println(seg.toString());
//				}
//			}
//		}

		// "sourceA"
		int index = 0;
		KeyAndSegId ksid = list.get(index).get(0);
		ITextUnit tu = ((IVTextUnit)vdoc.getItem(ksid.key)).getTextUnit();
		Segment seg = tu.getSource().getSegments().get(ksid.segId);
		assertEquals("sourceA", seg.toString());
		assertEquals(2, list.get(index).size());

		// "source text type 3"
		index = 1;
		long key = list.get(index).get(0).key;
		tu = ((IVTextUnit)vdoc.getItem(key)).getTextUnit();
		assertEquals("source text type 3", tu.toString());
		assertEquals(2, list.get(index).size());

		// "source text"
		index = 2;
		key = list.get(index).get(0).key;
		tu = ((IVTextUnit)vdoc.getItem(key)).getTextUnit();
		assertEquals("source text", tu.toString());
		assertEquals(3, list.get(index).size());

		repo.close();
	}	
}