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

package net.sf.okapi.steps.xliffkit.sandbox.pipelinebuilder;

import java.io.File;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.steps.common.FilterEventsWriterStep;
import net.sf.okapi.steps.common.RawDocumentToFilterEventsStep;
import net.sf.okapi.steps.gcaligner.SentenceAlignerStep;
import net.sf.okapi.steps.searchandreplace.SearchAndReplaceStep;
import net.sf.okapi.steps.textmodification.TextModificationStep;
import net.sf.okapi.steps.wordcount.WordCountStep;
import net.sf.okapi.steps.xliffkit.sandbox.pipelinebuilder.BatchItem;
import net.sf.okapi.steps.xliffkit.sandbox.pipelinebuilder.Pipeline;
import net.sf.okapi.steps.xliffkit.sandbox.pipelinebuilder.PipelineStep;
import net.sf.okapi.steps.xliffkit.sandbox.pipelinebuilder.PipelineType;

import org.junit.Test;

public class PipelineBuilderTest {
	
	@Test
	public void testPipeline() {
		
		//----------------------------------------------------------------
		Pipeline p1 = 
			new Pipeline(					
					"Pipeline template example.",
					new RawDocumentToFilterEventsStep(),
					new TextModificationStep(),
					new SearchAndReplaceStep(),
					new WordCountStep(),
					new FilterEventsWriterStep());
		
		//----------------------------------------------------------------
		Pipeline p2 = 
			new Pipeline(					
					"Pipeline initialized to process 2 input documents with TextModificationStep.",
					new Batch(
							new BatchItem(
								(new File("input1.html")).toURI(),
								"UTF-8",
								"okf_html",
								(new File("output1.html")).toURI(),
								"UTF-8",
								LocaleId.ENGLISH,
								LocaleId.FRENCH),
								
							new BatchItem(
								(new File("input2.html")).toURI(),
								"UTF-8",
								"okf_html",
								(new File("output2.html")).toURI(),
								"UTF-16",
								LocaleId.ENGLISH,
								LocaleId.CHINA_CHINESE)),
								
					new RawDocumentToFilterEventsStep(),
					new TextModificationStep(),
					new FilterEventsWriterStep());
		
		//----------------------------------------------------------------		
		Pipeline p3 = 
			new Pipeline(					
					"Pipeline initialized to process a batch defined in another pipeline. " +
					"TextModificationStep is created and initialized wit parameters from an external resource. " +
					"The p1 pipeline is inserted as a step, and a parallel pipeline with 2 steps inserted as a step.",
					p1.getBatch(),
					new RawDocumentToFilterEventsStep(),
					new PipelineStep(
							TextModificationStep.class,
							this.getClass().getResource("test.txt"), 
							true),
					p1,
					new Pipeline(
							"Parallel pipeline with SearchAndReplaceStep and WordCountStep steps.",
							PipelineType.PARALLEL,
							new SearchAndReplaceStep(),
							new WordCountStep()),
					new FilterEventsWriterStep());
		
		//----------------------------------------------------------------				
		Pipeline p4 = 
			new Pipeline(
					"Pipeline initialized to process nested batches.",
					new Batch(
							// bic 1 with 2 documents
							p2.getBatch(),
							
							// bic 2 with 1 document
							new BatchItem(									
									(new File("input3.html")).toURI(),
									"UTF-8",
									"okf_html",
									(new File("output3.html")).toURI(),
									"UTF-8",
									LocaleId.ENGLISH,
									LocaleId.FRENCH),
							
							// bic 3 with 2 documents	
							new Batch(
									new BatchItem(
											(new File("input4.html")).toURI(),
											"UTF-8",
											"okf_html",
											(new File("output4.html")).toURI(),
											"UTF-8",
											LocaleId.ENGLISH,
											LocaleId.FRENCH),
									new BatchItem(
											(new File("input5.html")).toURI(),
											"UTF-8",
											"okf_html",
											(new File("output5.html")).toURI(),
											"UTF-8",
											LocaleId.ENGLISH,
											LocaleId.FRENCH)),
							// bic 4 with 2 documents
							new BatchItem(
									(new File("input6.html")).toURI(),
									"UTF-8",
									"okf_html",
									(new File("output6.html")).toURI(),
									"UTF-16",
									LocaleId.ENGLISH,
									LocaleId.CHINA_CHINESE),
							
							new BatchItem(
									(new File("input7.html")).toURI(),
									"UTF-8",
									"okf_html",
									(new File("output7.html")).toURI(),
									"UTF-16",
									LocaleId.ENGLISH,
									LocaleId.CHINA_CHINESE)),
								
					new RawDocumentToFilterEventsStep(),
					new TextModificationStep(),
					new FilterEventsWriterStep());
		
		//----------------------------------------------------------------
		Pipeline p5 =
			new Pipeline(
					"Alignment pipeline. Source and target documents are processed by separate " +
					"pipelines connected in parallel. Events from both pipelines are anilized in TestAlignerStep.",
					new Pipeline(
							"Parallel pipeline for parrallel handling of source and target documents.",
							PipelineType.PARALLEL,
							new Pipeline(
									"Source document translatable text extraction",
									new Batch(
											new BatchItem(
													(new File("source.html")).toURI(),
													 "UTF-8",
													 LocaleId.ENGLISH)),
									new RawDocumentToFilterEventsStep()),
							new Pipeline(
									"Target document translatable text extraction",
									new Batch(
											new BatchItem(
													(new File("target.doc")).toURI(),
													 "UTF-16",
													 LocaleId.CHINA_CHINESE)),
									new RawDocumentToFilterEventsStep())
					),					
					new SentenceAlignerStep(),
					new FilterEventsWriterStep()
			);
					
											
		
//		p1.execute();
//		p2.execute();
//		p3.execute();
//		p4.execute();
//		p5.execute();
	}	
}
