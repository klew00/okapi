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

package net.sf.okapi.steps.tradosutils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Variant;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.RawDocument;

@UsingParameters(ParametersAnalysis.class)
public class TradosAnalysisStep extends BasePipelineStep{

	private final Logger logger = LoggerFactory.getLogger(getClass().getName());
	
	private ParametersAnalysis params;
	private ArrayList<String> inputFiles = new ArrayList<String>();
	private String logToOpen;
	private LocaleId sourceLocale;
	private LocaleId targetLocale;
	private String rootDir;
	private String inputRootDir;
	private boolean sendTmx;
	private int batchInputCount;
	private int count;
	
	public TradosAnalysisStep () {
		params = new ParametersAnalysis();
	}
	
	@StepParameterMapping(parameterType = StepParameterType.SOURCE_LOCALE)
	public void setSourcetLocale (LocaleId sourceLocale) {
		this.sourceLocale = sourceLocale;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALE)
	public void setTargetLocale (LocaleId targetLocale) {
		this.targetLocale = targetLocale;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.ROOT_DIRECTORY)
	public void setRootDirectory (String rootDir) {
		this.rootDir = rootDir;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.INPUT_ROOT_DIRECTORY)
	public void setInputRootDirectory (String inputRootDir) {
		this.inputRootDir = inputRootDir;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.BATCH_INPUT_COUNT)
	public void setBatchInputCount (int batchInputCount) {
		this.batchInputCount = batchInputCount;
	}
	
	@Override
	protected Event handleRawDocument (Event event) {
		RawDocument rawDoc = event.getRawDocument();
		inputFiles.add(new File(rawDoc.getInputURI()).getPath());
		count++;
		if ( count >= batchInputCount ) {
			Event newEvent = execute();
			if ( sendTmx ) return newEvent;
			else return event;
		}
		else {
			if ( sendTmx ) return Event.NOOP_EVENT;
			else return event;
		}
	}

	@Override
	protected Event handleStartBatch (final Event event) {
		inputFiles.clear();
		logToOpen = null;
		count = 0;
		TradosUtils.verifyJavaLibPath(new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile()));
		// If we create a TMX file of unknown segments and the send-tmx option is on
		// then this step should send only one useful event to the next step 
		sendTmx = params.getSendTmx() && params.getExportUnknown();
		return event;
	}

	@Override
	protected Event handleStartBatchItem (Event event) {
		if ( sendTmx ) return Event.NOOP_EVENT;
		else return event;
	}

	@Override
	protected Event handleEndBatchItem (Event event) {
		return Event.NOOP_EVENT;
	}

	private Event execute () {
		StringBuffer job = new StringBuffer();
		job.append("[Analyse]\n");
		
		logToOpen = Util.fillRootDirectoryVariable(params.getLogPath(), rootDir);
		logToOpen = Util.fillInputRootDirectoryVariable(logToOpen, inputRootDir);
		logToOpen = LocaleId.replaceVariables(logToOpen, sourceLocale, targetLocale);
		
		// Delete existing log and csv file if requested
		TradosUtils.deleteLogIfRequested( (!params.getAppendToLog()), logToOpen);
		Util.createDirectories(logToOpen);
		
		job.append("LogFile="+logToOpen+"\n");
		job.append("Tasks="+(params.getExportUnknown() ? 2 : 1)+"\n");
		job.append("[Task1]\n");
		job.append("Task=Analyse\n");
		job.append("Files="+inputFiles.size()+"\n");

		int i = 1;
		for (String file : inputFiles) {
			job.append("File"+i+"="+file+"\n");
			i++;
		}
		
		//--add ExportUnknown task if selected--
		String tmxOutput = Util.fillRootDirectoryVariable(params.getTmxPath(), rootDir);
		tmxOutput = Util.fillInputRootDirectoryVariable(tmxOutput, inputRootDir);
		tmxOutput = LocaleId.replaceVariables(tmxOutput, sourceLocale, targetLocale);
		Util.createDirectories(tmxOutput);
		
		if ( params.getExportUnknown() ) {
			job.append("[Task2]\n");
			job.append("Task=ExportUnknown\n");
			job.append("MaxMatch="+params.getMaxMatch()+"\n");
			job.append("File="+tmxOutput+"\n");
			job.append("FileType=5\n");					
		}
		
		File jobFile;
		try {
			jobFile = File.createTempFile("tradosjobfile_", ".tmp");
			jobFile.deleteOnExit();

			// Write job file
		    BufferedWriter out = new BufferedWriter(new FileWriter(jobFile));
		    out.write(job.toString());
		    out.close();
		}
		catch ( Throwable e ) {
			throw new OkapiIOException("Cannot create temporary output.", e);
		}
		
		// Get name of temporary tm
		if ( !params.getUseExisting() ) {
			String tempName = TradosUtils.generateTempTmName(); 
			analyzeWithTM(tempName,jobFile.getPath(), true);
			TradosUtils.deleteTM(tempName);
		}
		else {
			String tmPath = Util.fillRootDirectoryVariable(params.getExistingTm(), rootDir);
			tmPath = Util.fillInputRootDirectoryVariable(tmPath, inputRootDir);
			tmPath = LocaleId.replaceVariables(tmPath, sourceLocale, targetLocale);
			analyzeWithTM(tmPath,jobFile.getPath(), false);
		}
		
		if ( sendTmx ) {
			return TradosUtils.generateAltOutput(tmxOutput, "UTF-8", sourceLocale, targetLocale, "okf_tmx");
		}

		return null;
	}
	
	@Override
	public String getName () {
		return "Trados Analysis";
	}

	@Override
	public String getDescription () {
		return "Analyses a set of input files with a Trados TM."
			+ " Expects: raw document. Sends back: raw document.";
	}
	
	@Override
	public IParameters getParameters () {
		return params;
	}

	@Override
	public void setParameters (IParameters params) {
		this.params = (ParametersAnalysis)params;
	}
	
	/**
	 * TM specific analysis.
	 * @param tm The path and filename for the TM.
	 * @param job the path of the job file
	 * @param createTm true to create the TM
	 */    
    private void analyzeWithTM (String tm,
    	String job,
    	boolean createTm)
    {
    	ActiveXComponent xl = new ActiveXComponent("TW4Win.Application");
    	try {
    		//--create a new tm--
    		if ( createTm ) {
    			TradosUtils.createTM(xl, tm, sourceLocale, targetLocale, logger);
    		}
    		
    		ActiveXComponent o = xl.getPropertyAsComponent("TranslationMemory");
    		
    		if ( !params.getPass().isEmpty() ) {
            	o.invoke("Open", new Variant(tm), new Variant(params.getUser()), new Variant(params.getPass()));
    		}
    		else {
            	o.invoke("Open", new Variant(tm), new Variant(params.getUser()));    			
    		}

        	o.invoke("AnalyseFiles", new Variant(job));
        	xl.invoke("quit", new Variant[]{});

    		if ( params.getAutoOpenLog() && ( logToOpen != null )) {
    			Util.openURL((new File(logToOpen)).getAbsolutePath());
    		}

    	}
    	catch ( Exception e ) {
        	throw new OkapiIOException("Trados Analysis failed.", e);
        }
    }

}
