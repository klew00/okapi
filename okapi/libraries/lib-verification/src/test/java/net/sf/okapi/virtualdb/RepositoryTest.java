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

package net.sf.okapi.virtualdb;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.virtualdb.IVDocument;
import net.sf.okapi.virtualdb.IVRepository;
import net.sf.okapi.virtualdb.IVTextUnit;
import net.sf.okapi.virtualdb.jdbc.Repository;
import net.sf.okapi.virtualdb.jdbc.h2.H2Access;

import static org.junit.Assert.*;
import org.junit.Test;

public class RepositoryTest {
	
	private FilterConfigurationMapper fcMapper;
	private LocaleId locEN = LocaleId.fromBCP47("en");
	private LocaleId locFR = LocaleId.fromBCP47("fr");
	private String root;

	public RepositoryTest () throws URISyntaxException {
		URL url = RepositoryTest.class.getResource("/test01.xlf");
		root = Util.getDirectoryName(url.toURI().getPath());
	}
	
	@Test
	public void testImportTwoFiles () {
		fcMapper = new FilterConfigurationMapper();
		fcMapper.addConfigurations("net.sf.okapi.filters.xliff.XLIFFFilter");
		// Create the repository object
		IVRepository repo = new Repository(new H2Access(root, fcMapper));
		// Create the repository database
		repo.create("myRepo");

		// Import file 1
		RawDocument rd = new RawDocument((new File(root+"/test01.xlf")).toURI(), "UTF-8", locEN, locFR);
		rd.setFilterConfigId("okf_xliff");
		repo.importDocument(rd);
		// Import file 2
		rd = new RawDocument((new File(root+"/test02.xlf")).toURI(), "UTF-8", locEN, locFR);
		rd.setFilterConfigId("okf_xliff");
		repo.importDocument(rd);
		// Import file 3
		rd = new RawDocument((new File(root+"/test03.xlf")).toURI(), "UTF-8", locEN, locFR);
		rd.setFilterConfigId("okf_xliff");
		repo.importDocument(rd);
		
		// Check first document
		IVDocument vdoc1 = repo.getFirstDocument();
		IVTextUnit vtu = vdoc1.getTextUnit("1");
		assertEquals("Texte de l'attribute", vtu.getTextUnit().getTarget(locFR).toString());
		
		// Check next document
		IVDocument vdoc2 = (IVDocument)vdoc1.getNextSibling();
		vtu = vdoc2.getTextUnit("1");
		assertNotNull(vtu);
		assertEquals("test02 - Texte de l'attribute", vtu.getTextUnit().getTarget(locFR).toString());
		// Test previous doc
//		assertEquals(vdoc1, vdoc2.getPreviousSibling());
		
		// Check third document
		IVDocument vdoc3 = (IVDocument)vdoc2.getNextSibling();
		vtu = vdoc3.getTextUnit("1");
		assertNotNull(vtu);
		assertEquals("test03 - Texte de l'attribute", vtu.getTextUnit().getTarget(locFR).toString());
		// Test previous doc
//		assertEquals(vdoc2, vdoc3.getPreviousSibling());
		
		// Delete the second document
		repo.removeDocument(vdoc2);
		
		// First document is the same
		IVDocument nvdoc1 = repo.getFirstDocument();
		vtu = nvdoc1.getTextUnit("1");
		assertNotNull(vtu);
		assertEquals("Texte de l'attribute", vtu.getTextUnit().getTarget(locFR).toString());
		// Next document should be the test03 one
		IVDocument nvdoc2 = (IVDocument)nvdoc1.getNextSibling();
		vtu = nvdoc2.getTextUnit("1");
		assertNotNull(vtu);
		assertEquals("test03 - Texte de l'attribute", vtu.getTextUnit().getTarget(locFR).toString());
		// Test previous doc
//		assertEquals(nvdoc1, nvdoc2.getPreviousSibling());

		repo.close();
	}
	
	@Test
	public void testCreate () {
		fcMapper = new FilterConfigurationMapper();
		fcMapper.addConfigurations("net.sf.okapi.filters.xliff.XLIFFFilter");
		// Create the repository object
		IVRepository repo = new Repository(new H2Access(root, fcMapper));
		// Create the repository database
		repo.create("myRepo");
		// Import data
		RawDocument rd = new RawDocument((new File(root+"/test01.xlf")).toURI(), "UTF-8", locEN, locFR);
		rd.setFilterConfigId("okf_xliff");
		repo.importDocument(rd);

		// Get the documents
		ArrayList<IVDocument> docs = new ArrayList<IVDocument>();
		for ( IVDocument doc : repo.documents() ) {
			docs.add(doc);
		}
		assertEquals(1, docs.size());
		
		IVDocument doc = repo.getFirstDocument();
		assertNotNull(doc);
		
		ArrayList<IVTextUnit> vtus = new ArrayList<IVTextUnit>();
		for ( IVTextUnit vtu : doc.textUnits() ) {
			vtus.add(vtu);
		}
		assertEquals(8, vtus.size());
		
		ITextUnit tu = vtus.get(0).getTextUnit();
		assertEquals("1", tu.getId());
		assertEquals("Texte de l'attribute", tu.getTarget(locFR).toString());
		
		repo.close();
	}

	@Test
	public void testRetrieve () {
		fcMapper = new FilterConfigurationMapper();
		fcMapper.addConfigurations("net.sf.okapi.filters.xliff.XLIFFFilter");
		// Create the repository object
		IVRepository repo = new Repository(new H2Access(root, fcMapper));
		// Create the repository database
		repo.create("myRepo");
		// Import data
		RawDocument rd = new RawDocument((new File(root+"/test01.xlf")).toURI(), "UTF-8", locEN, locFR);
		rd.setFilterConfigId("okf_xliff");
		repo.importDocument(rd);
		
		// Get the documents
		ArrayList<IVDocument> docs = new ArrayList<IVDocument>();
		for ( IVDocument doc : repo.documents() ) {
			docs.add(doc);
		}
		assertEquals(1, docs.size());
		IVDocument doc = repo.getFirstDocument();
		
		ArrayList<IVTextUnit> vtus = new ArrayList<IVTextUnit>();
		for ( IVTextUnit vtu : doc.textUnits() ) {
			vtus.add(vtu);
		}
		assertEquals(8, vtus.size());
		
		ITextUnit tu = vtus.get(0).getTextUnit();
		assertEquals("1", tu.getId());
		assertEquals("Texte de l'attribute", tu.getTarget(locFR).toString());

		repo.close();
	}
	
	@Test
	public void testSaveAndRetrieve () {
		fcMapper = new FilterConfigurationMapper();
		fcMapper.addConfigurations("net.sf.okapi.filters.xliff.XLIFFFilter");
		// Create the repository object
		IVRepository repo = new Repository(new H2Access(root, fcMapper));
		// Create the repository database
		repo.create("myRepo");
		// Import data
		RawDocument rd = new RawDocument((new File(root+"/test01.xlf")).toURI(), "UTF-8", locEN, locFR);
		rd.setFilterConfigId("okf_xliff");
		repo.importDocument(rd);

		IVDocument doc = repo.getFirstDocument();

		IVTextUnit vtu = (IVTextUnit)doc.getItem("1");
		ITextUnit tu = vtu.getTextUnit();
		assertEquals("Texte de l'attribute", tu.getTarget(locFR).toString());
		
		tu.setTarget(locFR, new TextContainer("new target text"));
		vtu.save();

		vtu = (IVTextUnit)doc.getItem("1");
		tu = vtu.getTextUnit();
		assertEquals("new target text", tu.getTarget(locFR).toString());

		repo.close();
	}

}