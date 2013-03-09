/*===========================================================================
  Copyright (C) 2012 by the Okapi Framework contributors
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

package net.sf.okapi.applications.rainbow.batchconfig;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import net.sf.okapi.applications.rainbow.Input;
import net.sf.okapi.applications.rainbow.pipeline.PipelineWrapper;
import net.sf.okapi.common.filters.DefaultFilters;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.plugins.PluginsManager;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class BatchConfigurationTest {
    private static final String OKF_CUSTOM_HTML = "okf_custom_html";
    private static final String OKF_CUSTOM_XML = "okf_custom_xml";
    
    @Rule
	public TemporaryFolder folder = new TemporaryFolder();

    private static FilterConfigurationMapper fcMapper;
	
	@BeforeClass
	public static void setup() throws Exception {
        fcMapper = new FilterConfigurationMapper();
		// Get pre-defined configurations
		DefaultFilters.setMappings(fcMapper, false, true);
	}
	
	@Test
	public void testExtensionMappings() throws Exception {
        // creates a temp folder
        File tmpFolder = folder.newFolder("temp");

        // plugin manager
        PluginsManager pm = new PluginsManager();
        pm.discover(tmpFolder, false);

        // creates a simple PipelineWrapper
        PipelineWrapper wrapper = new PipelineWrapper(
            fcMapper, "", pm, "", "", null);

        // creates a list of Input files
        Input html = new Input();
        html.relativePath = "test.html";
        html.filterConfigId = OKF_CUSTOM_HTML;

        Input empty = new Input();
        empty.relativePath = "test";
        empty.filterConfigId = OKF_CUSTOM_XML;

        ArrayList<Input> inputFiles = new ArrayList<Input>();
        inputFiles.add(html);
        inputFiles.add(empty);

        File batchConfigFile = new File(tmpFolder, "exported.bconf");
        
        // export batch config file
        BatchConfiguration bc = new BatchConfiguration();
        bc.exportConfiguration(batchConfigFile.getAbsolutePath(), wrapper,
            fcMapper, inputFiles);

        // check if the batch config file has been created
        assertTrue(batchConfigFile.exists());
        
        // de-compose the batch config file
        bc.installConfiguration(batchConfigFile.getAbsolutePath(),
            tmpFolder.getAbsolutePath(), wrapper);

        // read the extension mapping file into a hash table
		BufferedReader fh = new BufferedReader(new FileReader(
                new File(tmpFolder, "extensions-mapping.txt")));
        
		HashMap<String, String> filterConfigByExtension
            = new HashMap<String, String>();
		
		String s;
		while ((s = fh.readLine()) != null) {
			String fields[] = s.split("\t");
			String ext = fields[0];
			String fc = fields[1];
			
			filterConfigByExtension.put(ext, fc);
		}
		fh.close();

        // check if the configured file extensions exist
        assertTrue(
            OKF_CUSTOM_HTML.equals(filterConfigByExtension.get(".html")));
        assertTrue(
            OKF_CUSTOM_XML.equals(filterConfigByExtension.get("")));
	}
}
