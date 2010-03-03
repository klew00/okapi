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

package net.sf.okapi.steps.xliffkit.writer;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.steps.common.RawDocumentToFilterEventsStep;
import net.sf.okapi.steps.xliffkit.sandbox.pipelinebuilder.Batch;
import net.sf.okapi.steps.xliffkit.sandbox.pipelinebuilder.BatchItem;
import net.sf.okapi.steps.xliffkit.sandbox.pipelinebuilder.Pipeline;

import org.junit.Test;

public class XLIFFKitWriterTest {

	private XLIFFKitWriterStep step1;
	private final String IN_NAME1 = "Gate Openerss.htm";
	private final String IN_NAME2 = "TestDocument01.odt";
	
	private Pipeline buildPipeline(String inPath) {
		
		step1 = new XLIFFKitWriterStep();
		// Output files are created in /target/test-classes/net/sf/okapi/steps/xliffkit/writer
		String outPath = Util.getDirectoryName(this.getClass().getResource(inPath).getPath()) + "/" + inPath + ".xlf";
		step1.setOutput(outPath);
		
		return
			new Pipeline(
					"Test pipeline for XLIFFKitWriterStep",
					new Batch(
							new BatchItem(
									this.getClass().getResource(inPath),
									"UTF-8",
									LocaleId.ENGLISH
							)
					),
					new RawDocumentToFilterEventsStep(),
					step1
			);
	}
	
	@Test
	public void testOutputFile() {
		
		buildPipeline(IN_NAME1).execute();
		buildPipeline(IN_NAME2).execute();
		
		
	}
		
}
