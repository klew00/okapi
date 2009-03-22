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
import java.util.List;

import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.common.pipeline.IPipeline;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.common.pipeline.Pipeline;

public class PipelineWrapper {
	
	private String path;
	private ArrayList<Step> steps;

	public PipelineWrapper () {
		steps = new ArrayList<Step>();
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
				writer.writeAttributeString("utilId", step.utilId);
				writer.writeAttributeString("className", step.className);
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
		IPipeline pipeline = null;
		try {
			// Create the real pipeline from the info
			pipeline = new Pipeline();
			for ( Step stepInfo : steps ) {
				IPipelineStep step = (IPipelineStep)Class.forName(stepInfo.className).newInstance();
				pipeline.addStep(step);
			}

			// Execute the steps
			//pipeline.process(input)
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
		finally {
			if ( pipeline != null ) pipeline.destroy();
		}
		
	}

	public void addStep (Step step) {
		insertStep(-1, step); // insert at the end
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
