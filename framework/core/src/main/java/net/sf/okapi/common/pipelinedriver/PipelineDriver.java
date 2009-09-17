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

package net.sf.okapi.common.pipelinedriver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import net.sf.okapi.common.exceptions.OkapiBadStepInputException;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.pipeline.IPipeline;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.common.pipeline.Pipeline;
import net.sf.okapi.common.pipeline.annotations.ConfigurationParameter;
import net.sf.okapi.common.pipeline.annotations.StepIntrospector;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.RawDocument;

/**
 * Default implementation of the {@link IPipelineDriver} interface.
 */
public class PipelineDriver implements IPipelineDriver {

	/**
	 * Logger for this driver.
	 */
	protected final Logger logger = Logger.getLogger(getClass().getName());
	
	private IPipeline pipeline;
	private List<IBatchItemContext> batchItems;
	private LinkedList<List<ConfigurationParameter>> paramList;
	private IPipelineStep lastOutputStep;
	private int maxInputCount;
	private IFilterConfigurationMapper fcMapper;
	
	/**
	 * Creates an new PipelineDriver object with an empty pipeline.
	 */
	public PipelineDriver () {
		pipeline = new Pipeline();
		batchItems = new ArrayList<IBatchItemContext>();
		paramList = new LinkedList<List<ConfigurationParameter>>();
		maxInputCount = 1;
	}

	public void setFilterConfigurationMapper (IFilterConfigurationMapper fcMapper) {
		this.fcMapper = fcMapper;
	}

	public void setPipeline (IPipeline pipeline) {
		this.pipeline = pipeline;
	}

	public IPipeline getPipeline () {
		return pipeline;
	}

	public void addStep (IPipelineStep step) {
		pipeline.addStep(step);
		List<ConfigurationParameter> pList = StepIntrospector.getStepParameters(step);
		paramList.add(pList);

		for ( ConfigurationParameter p : pList ) {
			if ( p.getParameterType() == StepParameterType.OUTPUT_URI ) {
				if ( lastOutputStep != null ) {
					lastOutputStep.setLastOutputStep(false);
				}
				lastOutputStep = step;
				lastOutputStep.setLastOutputStep(true);
			}
			else if ( p.getParameterType() == StepParameterType.SECONDARY_INPUT_RAWDOC ) {
				maxInputCount = 2;
			}
		}
	}

	public void processBatch (List<IBatchItemContext> batchItems) {
		this.batchItems = batchItems;
		processBatch();
	}
	
	public void processBatch () {
		pipeline.startBatch();
		for ( IBatchItemContext item : batchItems ) {
			displayInput(item);
			// Set the runtime parameters
			assignRuntimeParameters(item);
			// Process this input
			pipeline.process(item.getRawDocument(0));
		}
		pipeline.endBatch();
	}
	
	public void addBatchItem (IBatchItemContext item) {
		batchItems.add(item);
	}

	public void addBatchItem (RawDocument... rawDocs)
	{
		BatchItemContext item = new BatchItemContext();
		for ( RawDocument rawDoc : rawDocs ) {
			DocumentData ddi = new DocumentData();
			ddi.rawDocument = rawDoc;
			item.add(ddi);
		}
		batchItems.add(item);
	}
	
	public void addBatchItem (RawDocument rawDoc,
		URI outputURI,
		String outputEncoding)
	{
		DocumentData ddi = new DocumentData();
		ddi.rawDocument = rawDoc;
		ddi.outputURI = outputURI;
		ddi.outputEncoding = outputEncoding;
		BatchItemContext item = new BatchItemContext();
		item.add(ddi);
		batchItems.add(item);
	}
	
	public void addBatchItem (URI inputURI,
		String defaultEncoding,
		String filterConfigId,
		String srcLang,
		String trgLang)
	{
		DocumentData ddi = new DocumentData();
		ddi.rawDocument = new RawDocument(inputURI, defaultEncoding, srcLang, trgLang);
		ddi.rawDocument.setFilterConfigId(filterConfigId);
		BatchItemContext item = new BatchItemContext();
		item.add(ddi);
		batchItems.add(item);
	}
	
	public void clearItems () {
		batchItems.clear();
	}

	/**
	 * Logs the information about which batch item is about to be processed. This
	 * method is called inside the loop that process the batch.
	 * @param item the batch item that is about to be processed.
	 */
	protected void displayInput (IBatchItemContext item) {
		if ( item.getRawDocument(0).getInputURI() != null ) {
			logger.info(String.format("\n-- Input: %s",
				item.getRawDocument(0).getInputURI().getPath()));
		}
		else {
			logger.info("\n-- Input (No path available)");
		}
	}

	public void clearSteps () {
		pipeline.clearSteps();		
		paramList.clear();
		lastOutputStep = null;
		maxInputCount = 1;
	}

	public int getRequestedInputCount () {
		return maxInputCount;
	}

	private void assignRuntimeParameters (IBatchItemContext item) {
		try {
			RawDocument input = item.getRawDocument(0);
			// Set the runtime parameters using the method annotations
			// For each step
			for ( List<ConfigurationParameter> pList : paramList ) {
				// For each exposed parameter
				for ( ConfigurationParameter p : pList ) {
					Method method = p.getMethod();
					if ( method == null ) continue;
					switch ( p.getParameterType() ) {
					case OUTPUT_URI:
						if ( lastOutputStep == p.getStep() ) {
							method.invoke(p.getStep(), item.getOutputURI(0));
						}
						break;
					case TARGET_LANGUAGE:
						method.invoke(p.getStep(), input.getTargetLanguage());
						break;
					case SOURCE_LANGUAGE:
						method.invoke(p.getStep(), input.getSourceLanguage());
						break;
					case OUTPUT_ENCODING:
						method.invoke(p.getStep(), item.getOutputEncoding(0));
						break;
					case INPUT_URI:
						method.invoke(p.getStep(), input.getInputURI());
						break;
					case FILTER_CONFIGURATION_ID:
						method.invoke(p.getStep(), input.getFilterConfigId());
						break;
					case FILTER_CONFIGURATION_MAPPER:
						method.invoke(p.getStep(), fcMapper);
						break;
					case INPUT_RAWDOC:
						method.invoke(p.getStep(), input);
						break;
					case SECONDARY_INPUT_RAWDOC:
						method.invoke(p.getStep(), item.getRawDocument(1));
						break;
					default:
						throw new OkapiBadStepInputException(String.format(
							"The step '%s' is using a runtime parameter not supported by this driver.",
							p.getStep().getName()));
					}
				}
			}
		}
		catch ( IllegalArgumentException e ) {
			throw new RuntimeException("Error when assigning runtime parameters.", e);
		}
		catch ( IllegalAccessException e ) {
			throw new RuntimeException("Error when assigning runtime parameters.", e);
		}
		catch ( InvocationTargetException e ) {
			throw new RuntimeException("Error when assigning runtime parameters.", e);
		}
	}

}
