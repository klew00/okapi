/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.scopingreport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.lib.extra.pipelinebuilder.XParameter;
import net.sf.okapi.lib.extra.pipelinebuilder.XPipelineStep;
import net.sf.okapi.lib.extra.steps.CompoundStep;
import net.sf.okapi.lib.reporting.ReportGenerator;
import net.sf.okapi.steps.wordcount.WordCountStep;
import net.sf.okapi.steps.wordcount.categorized.ExactMatchWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.FuzzyMatchWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.LeveragedWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.LocalContextExactMatchWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.PrevVersionExactMatchWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.RepeatedSegmentExactMatchWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.RepetitionWordCountStep;
import net.sf.okapi.steps.wordcount.common.BaseCounter;
import net.sf.okapi.steps.wordcount.common.GMX;

@UsingParameters(Parameters.class)
public class ScopingReportStep extends CompoundStep {

	private Parameters params;
	private ReportGenerator gen;
	private String rootDir;
	
	public ScopingReportStep() {
		super();
		params = new Parameters();
		setParameters(params);
		setName("Scoping Report");
		setDescription("Create a template-based scoping report based on word count and leverage annotations."
			+" Expects: filter events. Sends back: filter events.");
		gen = new ReportGenerator(getTemplateStream());
	}
	
	@StepParameterMapping(parameterType = StepParameterType.ROOT_DIRECTORY)
	public void setRootDirectory (String rootDir) {
		this.rootDir = rootDir;
	}

	protected InputStream getTemplateStream() {
		return this.getClass().getResourceAsStream("scoping_report.html");
	}

	@Override
	protected void addSteps(LinkedList<IPipelineStep> list) {
		list.add(
				new XPipelineStep(
						new WordCountStep(),
						new XParameter("countInBatchItems", true),
						new XParameter("countInBatch", true)
						)
				);
		list.add(
				new XPipelineStep(
						new LocalContextExactMatchWordCountStep(),
						new XParameter("countInBatchItems", true),
						new XParameter("countInBatch", true)
						)
				);
		list.add(
				new XPipelineStep(
						new PrevVersionExactMatchWordCountStep(),
						new XParameter("countInBatchItems", true),
						new XParameter("countInBatch", true)
						)
				);		
		list.add(
				new XPipelineStep(
						new ExactMatchWordCountStep(),
						new XParameter("countInBatchItems", true),
						new XParameter("countInBatch", true)
						)
				);
		list.add(
				new XPipelineStep(
						new LeveragedWordCountStep(),
						new XParameter("countInBatchItems", true),
						new XParameter("countInBatch", true)
						)
				);
		list.add(
				new XPipelineStep(
						new FuzzyMatchWordCountStep(),
						new XParameter("countInBatchItems", true),
						new XParameter("countInBatch", true)
						)
				);
		list.add(
				new XPipelineStep(
						new RepeatedSegmentExactMatchWordCountStep(),
						new XParameter("countInBatchItems", true),
						new XParameter("countInBatch", true)
						)
				);		
		list.add(
				new XPipelineStep(
						new RepetitionWordCountStep(),
						new XParameter("countInBatchItems", true),
						new XParameter("countInBatch", true)
						)
				);
	}

	@Override
	protected Event handleStartBatch(Event event) {
		params = getParameters(Parameters.class);
		gen.reset();
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
		gen.setField("DATE", df.format(new Date()));
		gen.setField("PROJECT_NAME", params.getProjectName());
		return super.handleStartBatch(event);
	}
	
	@Override
	protected Event handleEndBatch(Event event) {
		IResource res = event.getResource();
		if (res != null) {
			gen.setField("PROJECT_TOTAL", BaseCounter.getCount(res, GMX.TotalWordCount));
			gen.setField("PROJECT_LCEXACT", BaseCounter.getCount(res, LocalContextExactMatchWordCountStep.METRIC));
			gen.setField("PROJECT_EXACT", BaseCounter.getCount(res, GMX.LeveragedMatchedWordCount));
			gen.setField("PROJECT_FUZZY", BaseCounter.getCount(res, GMX.FuzzyMatchedWordCount));
			gen.setField("PROJECT_REP", BaseCounter.getCount(res, GMX.RepetitionMatchedWordCount));
			
			setEndBatchFields(gen, res);
		}		
		// Generate report
		String report = gen.generate();
		String outPath = Util.fillRootDirectoryVariable(params.getOutputPath(), rootDir);
		outPath = LocaleId.replaceVariables(outPath, getSourceLocale(), getTargetLocale());
		File outFile = new File(outPath);
		Util.createDirectories(outFile.getAbsolutePath());
		
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));
			writer.write(report);
			writer.close();
		} catch (UnsupportedEncodingException e) {
			throw new OkapiIOException(e);
		} catch (FileNotFoundException e) {
			throw new OkapiIOException(e);
		} catch (IOException e) {
			throw new OkapiIOException(e);
		}
		
		return super.handleEndBatch(event);
	}

	// To be overridden in subclasses
	protected void setEndBatchFields(ReportGenerator gen, IResource res) {
	}
	
	// To be overridden in subclasses
	protected void setEndBatchItemFields(ReportGenerator gen, IResource res) {
	}
	
	// To be overridden in subclasses
	protected void setStartDocumentFields(ReportGenerator gen, IResource res) {
	}

	@Override
	protected Event handleEndBatchItem(Event event) {
		IResource res = event.getResource();
		if (res != null)
			setEndBatchItemFields(gen, res);
		
		return super.handleEndBatchItem(event);
	}
	
	@Override
	protected Event handleStartDocument(Event event) {
		StartDocument sd = (StartDocument) event.getResource();
		if (sd != null) {
			String fname = sd.getName();
			gen.setField("ITEM_NAME", new File(fname).getAbsolutePath());
			
			setStartDocumentFields(gen, sd);
		}
		
		return super.handleStartDocument(event);
	}
}
