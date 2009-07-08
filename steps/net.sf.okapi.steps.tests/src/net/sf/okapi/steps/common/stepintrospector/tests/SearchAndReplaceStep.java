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

package net.sf.okapi.steps.common.stepintrospector.tests;

import java.util.ArrayList;

import net.sf.okapi.steps.common.stepintrospector.StepConfigurationParameter;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.pipeline.BasePipelineStep;

public class SearchAndReplaceStep extends BasePipelineStep {	
	@SuppressWarnings("unused")
	@StepConfigurationParameter(description = "If true the input is a plain text file, not a filtered event", longDescription= "")
	private boolean plainText;
	
	@SuppressWarnings("unused")
	@StepConfigurationParameter(description = "Java Regex used to search", longDescription= "")
	private String regEx;
	
	@SuppressWarnings("unused")
	@StepConfigurationParameter(description = "?????", longDescription= "")
	private boolean dotAll;
	
	@SuppressWarnings("unused")
	@StepConfigurationParameter(description = "If true the search is case insensitive", longDescription= "")
	private boolean ignoreCase;
	
	@SuppressWarnings("unused")
	@StepConfigurationParameter(description = "Search across line boundries", longDescription= "")
	private  boolean multiLine;
	
	@SuppressWarnings("unused")
	@StepConfigurationParameter(description = "List of rules", longDescription= "")
	private ArrayList<String[]> rules;
	
	@Override
	public void destroy () {
	}

	public SearchAndReplaceStep() {
		plainText = false;
		regEx = "";
		dotAll = false;
		ignoreCase = false;
		multiLine = false;
		rules = new ArrayList<String[]>();
	}

	
	public String getDescription () {
		return "Performs search and replace on the entire file or the text units.";
	}

	public String getName () {
		return "Search and Replace";
	}

	@Override
	public boolean isDone () {
		return false;
	}
	 
	@Override
	public boolean needsOutput (int inputIndex) {
		return false;	
	}

	
	@Override
	protected void handleStartBatch (Event event) {
	}	
		
	
	@Override
	protected void handleStartBatchItem (Event event) {
	}	
	
	@Override
	protected void handleRawDocument (Event event) {
	}

	
	@Override
	protected void handleTextUnit (Event event) {
	}
}
