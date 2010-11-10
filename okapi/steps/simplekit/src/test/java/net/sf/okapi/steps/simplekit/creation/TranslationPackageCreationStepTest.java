/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.simplekit.creation;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.TestUtil;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.pipelinedriver.BatchItemContext;
import net.sf.okapi.common.pipelinedriver.IPipelineDriver;
import net.sf.okapi.common.pipelinedriver.PipelineDriver;
import net.sf.okapi.filters.openoffice.OpenOfficeFilter;
import net.sf.okapi.filters.properties.PropertiesFilter;
import net.sf.okapi.steps.common.RawDocumentToFilterEventsStep;
import net.sf.okapi.steps.simplekit.creation.TranslationPackageCreationStep;

import org.junit.Before;
import org.junit.Test;

public class TranslationPackageCreationStepTest {
	
	private String root;
	private LocaleId locEN = LocaleId.fromString("en");
	private LocaleId locFR = LocaleId.fromString("fr");
	
	@Before
	public void setUp() {
		root = TestUtil.getParentDir(this.getClass(), "/test01.properties");
	}

	@Test
	public void testSimpleStep ()
		throws URISyntaxException
	{
		// Ensure output is deleted
		assertTrue(deleteOutputDir("pack1", true));
		
		IPipelineDriver pdriver = new PipelineDriver();
		FilterConfigurationMapper fcMapper = new FilterConfigurationMapper();
		fcMapper.addConfigurations(PropertiesFilter.class.getName());
		fcMapper.addConfigurations(OpenOfficeFilter.class.getName());
		pdriver.setFilterConfigurationMapper(fcMapper);
		pdriver.setRootDirectory(Util.deleteLastChar(root)); // Don't include final separator
		pdriver.addStep(new RawDocumentToFilterEventsStep());
		pdriver.addStep(new TranslationPackageCreationStep());
		
		URI inputURI = this.getClass().getResource("/test01.properties").toURI();
		URI outputURI = new URI(inputURI.getPath().replace("test01.", "test01.out."));
		pdriver.addBatchItem(new BatchItemContext(inputURI, "UTF-8", "okf_properties", outputURI, "UTF-8", locEN, locFR));
		
		inputURI = this.getClass().getResource("/subDir/test01.odt").toURI();
		outputURI = new URI(inputURI.getPath().replace("test01.", "test01.out."));
		pdriver.addBatchItem(new BatchItemContext(inputURI, "UTF-8", "okf_openoffice", outputURI, "UTF-8", locEN, locFR));
		
//		pdriver.processBatch();
//
//		File file = new File(root+"pack1/work/test01.properties.xlf");
//		assertTrue(file.exists());
//		file = new File(root+"pack1/work/subDir/test01.odt.xlf");
//		assertTrue(file.exists());
		
	}
	
    public boolean deleteOutputDir (String dirname, boolean relative) {
    	File d;
    	if ( relative ) d = new File(root + File.separator + dirname);
    	else d = new File(dirname);
    	if ( d.isDirectory() ) {
    		String[] children = d.list();
    		for ( int i=0; i<children.length; i++ ) {
    			boolean success = deleteOutputDir(d.getAbsolutePath() + File.separator + children[i], false);
    			if ( !success ) {
    				return false;
    			}
    		}
    	}
    	if ( d.exists() ) return d.delete();
    	else return true;
    }
    

}
