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

package net.sf.okapi.applications.rainbow.pipeline;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.pipeline.IPipelineDriver;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.common.pipeline.Pipeline;
import net.sf.okapi.common.pipeline.PipelineDriver;

public class PipelineWrapper {
	
	public final Map<String, Step> availableSteps;
	
	private String path;
	private ArrayList<Step> steps;
	private IPipelineDriver driver;
	private IFilterConfigurationMapper fcMapper;

	// Temporary class to create a list of available steps
	private Map<String, Step> buildStepList () {
		Hashtable<String, Step> map = new Hashtable<String, Step>();
		try {
			IPipelineStep ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.common.RawDocumentToFilterEventsStep").newInstance();
			Step step = new Step(ps.getClass().getSimpleName(),
				ps.getName(), ps.getDescription(), null);
			IParameters params = ps.getParameters();
			if ( params != null ) {
				step.paramsData = params.toString();
			}
			map.put(step.id, step);
				
			ps = (IPipelineStep)Class.forName(
					"net.sf.okapi.steps.common.FilterEventsToRawDocumentStep").newInstance();
			step = new Step(ps.getClass().getSimpleName(),
				ps.getName(), ps.getDescription(), null);
			params = ps.getParameters();
			if ( params != null ) {
				step.paramsData = params.toString();
			}
			map.put(step.id, step);
							
			ps = (IPipelineStep)Class.forName(
					"net.sf.okapi.steps.common.FilterEventsWriterStep").newInstance();
			step = new Step(ps.getClass().getSimpleName(),
				ps.getName(), ps.getDescription(), null);
			params = ps.getParameters();
			if ( params != null ) {
				step.paramsData = params.toString();
			}
			map.put(step.id, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.common.RawDocumentWriterStep").newInstance();
			step = new Step(ps.getClass().getSimpleName(),
				ps.getName(), ps.getDescription(), null);
			params = ps.getParameters();
			if ( params != null ) {
				step.paramsData = params.toString();
			}
			map.put(step.id, step);
						
			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.xsltransform.XSLTransformStep").newInstance();
			step = new Step(ps.getClass().getSimpleName(),
				ps.getName(), ps.getDescription(), null);
			params = ps.getParameters();
			if ( params != null ) {
				step.paramsData = params.toString();
			}
			map.put(step.id, step);
					
			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.xsltransform.XSLTransformStep").newInstance();
			step = new Step(ps.getClass().getSimpleName(),
				ps.getName(), ps.getDescription(), null);
			params = ps.getParameters();
			if ( params != null ) {
				step.paramsData = params.toString();
			}
			map.put(step.id, step);
			
			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.bomconversion.BOMConversionStep").newInstance();
			step = new Step(ps.getClass().getSimpleName(),
				ps.getName(), ps.getDescription(), null);
			params = ps.getParameters();
			if ( params != null ) {
				step.paramsData = params.toString();
			}
			map.put(step.id, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.charlisting.CharListingStep").newInstance();
			step = new Step(ps.getClass().getSimpleName(),
				ps.getName(), ps.getDescription(), null);
			params = ps.getParameters();
			if ( params != null ) {
				step.paramsData = params.toString();
			}
			map.put(step.id, step);
	
			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.linebreakconversion.LineBreakConversionStep").newInstance();
			step = new Step(ps.getClass().getSimpleName(),
				ps.getName(), ps.getDescription(), null);
			params = ps.getParameters();
			if ( params != null ) {
				step.paramsData = params.toString();
			}
			map.put(step.id, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.segmentation.SegmentationStep").newInstance();
			step = new Step(ps.getClass().getSimpleName(),
				ps.getName(), ps.getDescription(), null);
			params = ps.getParameters();
			if ( params != null ) {
				step.paramsData = params.toString();
			}
			map.put(step.id, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.textmodification.TextModificationStep").newInstance();
			step = new Step(ps.getClass().getSimpleName(),
				ps.getName(), ps.getDescription(), null);
			params = ps.getParameters();
			if ( params != null ) {
				step.paramsData = params.toString();
			}
			map.put(step.id, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.translationcomparison.TranslationComparisonStep").newInstance();
			step = new Step(ps.getClass().getSimpleName(),
				ps.getName(), ps.getDescription(), null);
			params = ps.getParameters();
			if ( params != null ) {
				step.paramsData = params.toString();
			}
			map.put(step.id, step);

		}
		catch ( InstantiationException e ) {
			e.printStackTrace();
		}
		catch ( IllegalAccessException e ) {
			e.printStackTrace();
		}
		catch ( ClassNotFoundException e ) {
			e.printStackTrace();
		}		
		return map;
	}
	
	public PipelineWrapper (IFilterConfigurationMapper fcMapper) {
		this.fcMapper = fcMapper;
		steps = new ArrayList<Step>();
		driver = new PipelineDriver();
		//TODO: use register system for this
		availableSteps = buildStepList();
	}
	
	public void clear () {
		steps.clear();
	}
	
	public String getPath () {
		return path;
	}
	
	public void load (String path) {
		//TODO
		this.path = path;
	}
	
	public void save (String path) {
		XMLWriter writer = null;
		try {
			writer = new XMLWriter();
			writer.create(path);
			writer.writeStartDocument();
			writer.writeStartElement("pipeline");
			writer.writeLineBreak();
			for ( Step step : steps ) {
				writer.writeStartElement("step");
				writer.writeAttributeString("stepClass", step.stepClass);
				writer.writeEndElementLineBreak(); // step
			}
			writer.writeEndElementLineBreak(); // pipeline
			writer.writeEndDocument();
			this.path = path; 
		}
		finally {
			if ( writer != null ) writer.close();
		}
	}
	
	public void execute () {
		try {
			// Build the pipeline
			driver.setPipeline(new Pipeline());
			driver.getPipeline().getContext().setFilterConfigurationMapper(fcMapper);
			for ( Step stepInfo : steps ) {
				IPipelineStep step = (IPipelineStep)Class.forName(stepInfo.stepClass).newInstance();
				driver.addStep(step);
			}
			
			// Set the input
			//TODO

			// Execute
			driver.processBatch();
		}
		catch ( InstantiationException e ) {
			throw new RuntimeException(e);
		}
		catch ( IllegalAccessException e ) {
			throw new RuntimeException(e);
		}
		catch ( ClassNotFoundException e ) {
			throw new RuntimeException(e);
		}
	}

	public void addStep (Step step) {
		steps.add(step);
	}
	
	public void insertStep (int index,
		Step step)
	{
		if ( index == -1 ) {
			steps.add(step);
		}
		else {
			steps.add(index, step);
		}
	}
	
	public void removeStep (int index) {
		steps.remove(index);
	}
	
	public List<Step> getSteps () {
		return steps;
	}

}
