/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.termextraction;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.PathInputPart;
import net.sf.okapi.common.uidescription.SeparatorPart;
import net.sf.okapi.common.uidescription.SpinInputPart;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {
	
	private static final String OUTPUTPATH = "outputPath";
	private static final String MINWORDSPERTERM = "minWordsPerTerm";
	private static final String MAXWORDSPERTERM = "maxWordsPerTerm";
	private static final String MINOCCURRENCES = "minOccurrences";
	private static final String STOPWORDSPATH = "stopWordsPath";
	private static final String NOSTARTWORDSPATH = "noStartWordsPath";
	private static final String NOENDWORDSPATH = "noEndWordsPath";
	
	private String outputPath;
	private int minWordsPerTerm;
	private int maxWordsPerTerm;
	private int minOccurrences;
	private String stopWordsPath;
	private String noStartWordsPath;
	private String noEndWordsPath;

	public Parameters () {
		reset();
	}
	
	public String getOutputPath () {
		return outputPath;
	}

	public void setOutputPath (String outputPath) {
		this.outputPath = outputPath;
	}

	public int getMinWordsPerTerm () {
		return minWordsPerTerm;
	}

	public void setMinWordsPerTerm (int minWordsPerTerm) {
		this.minWordsPerTerm = minWordsPerTerm;
	}

	public int getMaxWordsPerTerm () {
		return maxWordsPerTerm;
	}

	public void setMaxWordsPerTerm (int maxWordsPerTerm) {
		this.maxWordsPerTerm = maxWordsPerTerm;
	}

	public int getMinOccurrences () {
		return minOccurrences;
	}

	public void setMinOccurrences (int minOccurrences) {
		this.minOccurrences = minOccurrences;
	}

	public String getStopWordsPath () {
		return stopWordsPath;
	}

	public void setStopWordsPath (String stopWordsPath) {
		this.stopWordsPath = stopWordsPath;
	}

	public String getNoStartWordsPath () {
		return noStartWordsPath;
	}

	public void setNoStartWordsPath (String noStartWordsPath) {
		this.noStartWordsPath = noStartWordsPath;
	}

	public String getNoEndWordsPath () {
		return noEndWordsPath;
	}

	public void setNoEndWordsPath (String noEndWordsPath) {
		this.noEndWordsPath = noEndWordsPath;
	}

	@Override
	public void reset () {
		outputPath = "terms.txt";
		minWordsPerTerm = 1;
		maxWordsPerTerm = 3;
		minOccurrences = 2;
		stopWordsPath = "";
		noStartWordsPath = "";
		noEndWordsPath = "";
	}

	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		outputPath = buffer.getString(OUTPUTPATH, outputPath);
		minWordsPerTerm = buffer.getInteger(MINWORDSPERTERM, minWordsPerTerm);
		maxWordsPerTerm = buffer.getInteger(MAXWORDSPERTERM, maxWordsPerTerm);
		minOccurrences = buffer.getInteger(MINOCCURRENCES, minOccurrences);
		stopWordsPath = buffer.getString(STOPWORDSPATH, stopWordsPath);
		noStartWordsPath = buffer.getString(NOSTARTWORDSPATH, noStartWordsPath);
		noEndWordsPath = buffer.getString(NOENDWORDSPATH, noEndWordsPath);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setString(OUTPUTPATH, outputPath);
		buffer.setInteger(MINWORDSPERTERM, minWordsPerTerm);
		buffer.setInteger(MAXWORDSPERTERM, maxWordsPerTerm);
		buffer.setInteger(MINOCCURRENCES, minOccurrences);
		buffer.setString(STOPWORDSPATH, stopWordsPath);
		buffer.setString(NOSTARTWORDSPATH, noStartWordsPath);
		buffer.setString(NOENDWORDSPATH, noEndWordsPath);
		return buffer.toString();
	}
	
	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(OUTPUTPATH, "Output path", "Full path of the output file");
		desc.add(MINWORDSPERTERM, "Minimum number of words per term", "A term will be made up at least of that many words");
		desc.add(MAXWORDSPERTERM, "Maximum number of words per term", "A term will be made up at the most of that many words");
		desc.add(MINOCCURRENCES, "Minimum number of occurrences", "A term will have at least that many occurrences");
		desc.add(STOPWORDSPATH, "Path of the file with stop words", "Full path of the file containing stop words");
		desc.add(NOSTARTWORDSPATH, "Path of the file with no-start words", "Full path of the file containing no-start words");
		desc.add(NOENDWORDSPATH, "Path of the file with no-end words", "Full path of the file containing no-end words");
		return desc;
	}
	
	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Term Extraction");

		PathInputPart pip = desc.addPathInputPart(paramsDesc.get(OUTPUTPATH), "Output File to Generate", true);
		pip.setBrowseFilters("Text Files (*.txt)\tAll Files (*.*)", "*.txt\t*.*");
		pip.setVertical(true);

		SpinInputPart sip = desc.addSpinInputPart(paramsDesc.get(MINWORDSPERTERM));
		sip.setRange(1, 999);
		sip = desc.addSpinInputPart(paramsDesc.get(MAXWORDSPERTERM));
		sip.setRange(1, 999);
		sip = desc.addSpinInputPart(paramsDesc.get(MINOCCURRENCES));
		sip.setRange(1, 999);
		
		SeparatorPart sp = desc.addSeparatorPart();
		sp.setVertical(true);
		
		pip = desc.addPathInputPart(paramsDesc.get(STOPWORDSPATH), "Stop Words File", false);
		pip.setBrowseFilters("Text Files (*.txt)\tAll Files (*.*)", "*.txt\t*.*");
		pip.setVertical(true);

		pip = desc.addPathInputPart(paramsDesc.get(NOSTARTWORDSPATH), "No-Start Words File", false);
		pip.setBrowseFilters("Text Files (*.txt)\tAll Files (*.*)", "*.txt\t*.*");
		pip.setVertical(true);

		pip = desc.addPathInputPart(paramsDesc.get(NOENDWORDSPATH), "No-End Words File", false);
		pip.setBrowseFilters("Text Files (*.txt)\tAll Files (*.*)", "*.txt\t*.*");
		pip.setVertical(true);

		return desc;
	}

}
