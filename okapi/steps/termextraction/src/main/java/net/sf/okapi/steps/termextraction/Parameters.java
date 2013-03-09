/*===========================================================================
  Copyright (C) 2010-2011 by the Okapi Framework contributors
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
import net.sf.okapi.common.ReferenceParameter;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.PathInputPart;
import net.sf.okapi.common.uidescription.SpinInputPart;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {
	
	private static final String OUTPUTPATH = "outputPath";
	private static final String AUTOOPEN = "autoOpen";
	private static final String MINWORDSPERTERM = "minWordsPerTerm";
	private static final String MAXWORDSPERTERM = "maxWordsPerTerm";
	private static final String MINOCCURRENCES = "minOccurrences";
	private static final String STOPWORDSPATH = "stopWordsPath";
	private static final String NOTSTARTWORDSPATH = "notStartWordsPath";
	private static final String NOTENDWORDSPATH = "notEndWordsPath";
	private static final String KEEPCASE = "keepCase";
	private static final String REMOVESUBTERMS = "removeSubTerms";
	private static final String SORTBYOCCURRENCE = "sortByOccurrence";
	
	private String outputPath;
	private boolean autoOpen;
	private int minWordsPerTerm;
	private int maxWordsPerTerm;
	private int minOccurrences;
	private String stopWordsPath;
	private String notStartWordsPath;
	private String notEndWordsPath;
	private boolean keepCase;
	private boolean removeSubTerms;
	private boolean sortByOccurrence;

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

	@ReferenceParameter
	public String getStopWordsPath () {
		return stopWordsPath;
	}

	public void setStopWordsPath (String stopWordsPath) {
		this.stopWordsPath = stopWordsPath;
	}

	@ReferenceParameter
	public String getNotStartWordsPath () {
		return notStartWordsPath;
	}

	public void setNotStartWordsPath (String notStartWordsPath) {
		this.notStartWordsPath = notStartWordsPath;
	}

	@ReferenceParameter
	public String getNotEndWordsPath () {
		return notEndWordsPath;
	}

	public void setNotEndWordsPath (String notEndWordsPath) {
		this.notEndWordsPath = notEndWordsPath;
	}

	public boolean getKeepCase () {
		return keepCase;
	}

	public void setKeepCase (boolean keepCase) {
		this.keepCase = keepCase;
	}

	public boolean getRemoveSubTerms () {
		return removeSubTerms;
	}

	public void setRemoveSubTerms (boolean removeSubTerms) {
		this.removeSubTerms = removeSubTerms;
	}

	public boolean getSortByOccurrence () {
		return sortByOccurrence;
	}

	public void setSortByOccurrence (boolean sortByOccurrence) {
		this.sortByOccurrence = sortByOccurrence;
	}

	public boolean getAutoOpen () {
		return autoOpen;
	}

	public void setAutoOpen (boolean autoOpen) {
		this.autoOpen = autoOpen;
	}

	@Override
	public void reset () {
		outputPath = Util.ROOT_DIRECTORY_VAR+"/terms.txt";
		autoOpen = false;
		minWordsPerTerm = 1;
		maxWordsPerTerm = 3;
		minOccurrences = 2;
		stopWordsPath = "";
		notStartWordsPath = "";
		notEndWordsPath = "";
		keepCase = false;
		removeSubTerms = false;
		sortByOccurrence = false;
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
		notStartWordsPath = buffer.getString(NOTSTARTWORDSPATH, notStartWordsPath);
		notEndWordsPath = buffer.getString(NOTENDWORDSPATH, notEndWordsPath);
		keepCase = buffer.getBoolean(KEEPCASE, keepCase);
		removeSubTerms = buffer.getBoolean(REMOVESUBTERMS, removeSubTerms);
		sortByOccurrence = buffer.getBoolean(SORTBYOCCURRENCE, sortByOccurrence);
		autoOpen = buffer.getBoolean(AUTOOPEN, autoOpen);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setString(OUTPUTPATH, outputPath);
		buffer.setInteger(MINWORDSPERTERM, minWordsPerTerm);
		buffer.setInteger(MAXWORDSPERTERM, maxWordsPerTerm);
		buffer.setInteger(MINOCCURRENCES, minOccurrences);
		buffer.setString(STOPWORDSPATH, stopWordsPath);
		buffer.setString(NOTSTARTWORDSPATH, notStartWordsPath);
		buffer.setString(NOTENDWORDSPATH, notEndWordsPath);
		buffer.setBoolean(KEEPCASE, keepCase);
		buffer.setBoolean(REMOVESUBTERMS, removeSubTerms);
		buffer.setBoolean(SORTBYOCCURRENCE, sortByOccurrence);
		buffer.setBoolean(AUTOOPEN, autoOpen);
		return buffer.toString();
	}
	
	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(OUTPUTPATH, "Output path", "Full path of the output file");
		desc.add(MINWORDSPERTERM, "Minimum number of words per term", "A term will be made up at least of that many words");
		desc.add(MAXWORDSPERTERM, "Maximum number of words per term", "A term will be made up at the most of that many words");
		desc.add(MINOCCURRENCES, "Minimum number of occurrences per term", "A term will have at least that many occurrences");
		desc.add(STOPWORDSPATH, "Path of the file with stop words (leave empty for default)", "Full path of the file containing stop words");
		desc.add(NOTSTARTWORDSPATH, "Path of the file with not-start words (leave empty for default)", "Full path of the file containing not-start words");
		desc.add(NOTENDWORDSPATH, "Path of the file with not-end words (leave empty for default)", "Full path of the file containing not-end words");
		desc.add(KEEPCASE, "Preserve case differences", null);
		desc.add(REMOVESUBTERMS, "Remove entries that seem to be sub-strings of longer entries", null);
		desc.add(SORTBYOCCURRENCE, "Sort the results by the number of occurrences", null);
		desc.add(SORTBYOCCURRENCE, "Sort the results by the number of occurrences", null);
		desc.add(AUTOOPEN, "Open the result file after completion", null);
		return desc;
	}
	
	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Term Extraction", true, false);

		PathInputPart pip = desc.addPathInputPart(paramsDesc.get(OUTPUTPATH), "Output File to Generate", true);
		pip.setBrowseFilters("Text Files (*.txt)\tAll Files (*.*)", "*.txt\t*.*");
		desc.addCheckboxPart(paramsDesc.get(AUTOOPEN));

		desc.addSeparatorPart();
		
		SpinInputPart sip = desc.addSpinInputPart(paramsDesc.get(MINWORDSPERTERM));
		sip.setRange(1, 999);
		sip.setVertical(false);
		sip = desc.addSpinInputPart(paramsDesc.get(MAXWORDSPERTERM));
		sip.setRange(1, 999);
		sip.setVertical(false);
		sip = desc.addSpinInputPart(paramsDesc.get(MINOCCURRENCES));
		sip.setRange(1, 999);
		sip.setVertical(false);
		
		desc.addCheckboxPart(paramsDesc.get(KEEPCASE));
		desc.addCheckboxPart(paramsDesc.get(REMOVESUBTERMS));
		desc.addCheckboxPart(paramsDesc.get(SORTBYOCCURRENCE));
		
		desc.addSeparatorPart();
		
		pip = desc.addPathInputPart(paramsDesc.get(STOPWORDSPATH), "Stop Words File", false);
		pip.setBrowseFilters("Text Files (*.txt)\tAll Files (*.*)", "*.txt\t*.*");
		pip.setAllowEmpty(true);

		pip = desc.addPathInputPart(paramsDesc.get(NOTSTARTWORDSPATH), "Not-Start Words File", false);
		pip.setBrowseFilters("Text Files (*.txt)\tAll Files (*.*)", "*.txt\t*.*");
		pip.setAllowEmpty(true);

		pip = desc.addPathInputPart(paramsDesc.get(NOTENDWORDSPATH), "Not-End Words File", false);
		pip.setBrowseFilters("Text Files (*.txt)\tAll Files (*.*)", "*.txt\t*.*");
		pip.setAllowEmpty(true);

		return desc;
	}

}
