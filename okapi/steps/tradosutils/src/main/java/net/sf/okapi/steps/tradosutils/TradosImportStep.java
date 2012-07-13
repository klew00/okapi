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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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

@UsingParameters(ParametersImport.class)
public class TradosImportStep extends BasePipelineStep{

	private final Logger logger = Logger.getLogger(getClass().getName());
	
	private ParametersImport params;
	private ArrayList<String> inputFiles = new ArrayList<String>();
	private LocaleId sourceLocale;
	private LocaleId targetLocale;
	private String rootDir;
	private String inputRootDir;
	private String tmPath;
	private boolean sendTm;
	private int batchInputCount;
	private int count;
	
	public TradosImportStep () {
		params = new ParametersImport();
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
			if ( sendTm ) return newEvent;
			else return event;
		}
		else {
			if ( sendTm ) return Event.NOOP_EVENT;
			else return event;
		}
	}

	@Override
	protected Event handleStartBatch (final Event event) {
		inputFiles.clear();
		count = 0;		
		TradosUtils.verifyJavaLibPath(new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile()));
		// If both the the use Existing option is on and the send TM option is on
		// then this step should send only one useful event to the next step 
		sendTm = params.getSendTm();
		return event;
	}

	@Override
	protected Event handleStartBatchItem (Event event) {
		if ( sendTm ) return Event.NOOP_EVENT;
		else return event;
	}
	
	@Override
	protected Event handleEndBatchItem (Event event) {
		return Event.NOOP_EVENT;
	}
	
	private Event execute() {
		
		tmPath = Util.fillRootDirectoryVariable(params.getExistingTm(), rootDir);
		tmPath = Util.fillInputRootDirectoryVariable(tmPath, inputRootDir);
		tmPath = LocaleId.replaceVariables(tmPath, sourceLocale, targetLocale);
		
		if ( TradosUtils.tmExists(tmPath) ){
			//Delete existing TM if requested
			if ( params.getOverwrite() ) {
				TradosUtils.deleteTM(tmPath);
				importIntoTM(tmPath, inputFiles, true);
			}
			else {
				importIntoTM(tmPath, inputFiles, false);
			}
		}
		else {
			importIntoTM(tmPath, inputFiles, true);
		}
		
		if ( sendTm ) {
			return TradosUtils.generateAltOutput(tmPath, "UTF-8", sourceLocale, targetLocale, "okf_tmx");
		}

		return null;
		
	}
	
	@Override
	public String getName () {
		return "Trados Import";
	}

	@Override
	public String getDescription() {
		return "Import a set of input files into a Trados TM."
				+ " Expects: raw document. Sends back: raw document.";
	}
	
	@Override
	public IParameters getParameters () {
		return params;
	}

	@Override
	public void setParameters (IParameters params) {
		this.params = (ParametersImport)params;
	}
	
	/**
	 * Import a list of files into TM 
	 * @param tm Trados TM
	 * @param files List of input files
	 * @param createTm Create TM or use existing
	 */
    private void importIntoTM (String tm,
    	List<String> files,
    	boolean createTm)
    {
		ActiveXComponent xl = new ActiveXComponent("TW4Win.Application");
		try {
    		//--create a new tm--
    		if ( createTm ) {
    			Util.createDirectories(tm);    			
    			TradosUtils.createTM(xl, tm, sourceLocale, targetLocale, logger);
    		}
			
			ActiveXComponent o = xl.getPropertyAsComponent("TranslationMemory");

			if ( !params.getPass().isEmpty() ) {
            	o.invoke("Open", new Variant(tm), new Variant(params.getUser()), new Variant(params.getPass()));
    		}
			else {
				o.invoke("Open", new Variant(tm), new Variant(params.getUser()));
			}

    		Variant[] importOptions = new Variant[6];
    		importOptions[1] = new Variant(params.getMode());
    		importOptions[2] = new Variant(params.getFormat());
    		importOptions[3] = new Variant(params.getReorganize());
    		importOptions[4] = new Variant(params.getIgnoreNewFields());
    		importOptions[5] = new Variant(params.getCheckLang());
			
			for (String file : files) {
				importOptions[0] = new Variant(file);
				o.invoke("Import", importOptions);
			} 
			
			xl.invoke("quit", new Variant[] {});
    		
		}
		catch ( Exception e ) {
			throw new OkapiIOException("Trados Import failed.", e);
		}
    }   
}
