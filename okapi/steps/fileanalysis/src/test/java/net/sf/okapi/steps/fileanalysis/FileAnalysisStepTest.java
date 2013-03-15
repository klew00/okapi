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

package net.sf.okapi.steps.fileanalysis;

//import static org.junit.Assert.*;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.TestUtil;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.pipelinedriver.BatchItemContext;
import net.sf.okapi.common.pipelinedriver.IPipelineDriver;
import net.sf.okapi.common.pipelinedriver.PipelineDriver;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.steps.common.RawDocumentToFilterEventsStep;

import org.junit.Before;
import org.junit.Test;

public class FileAnalysisStepTest {
	
	private String root;
	private LocaleId locEN = LocaleId.fromString("en");
	
	@Before
	public void setUp() {
		root = TestUtil.getParentDir(this.getClass(), "/test01.properties");
	}

	@Test
	public void testSimpleStep ()
		throws URISyntaxException
	{
		IPipelineDriver pdriver = new PipelineDriver();
		pdriver.setRootDirectories(Util.deleteLastChar(root), Util.deleteLastChar(root)); // Don't include final separator
		pdriver.addStep(new RawDocumentToFilterEventsStep());
		pdriver.addStep(new FileAnalysisStep());
	
		URI inputURI = new File(root+"/test01.properties").toURI();
		RawDocument rd = new RawDocument(inputURI, "UTF-8", locEN);
		pdriver.addBatchItem(new BatchItemContext(rd, null, null));
		
		pdriver.processBatch();

		//TODO: test results
	}

}
