/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.applications.rainbow.utilities;

import java.util.ArrayList;

import javax.swing.event.EventListenerList;

import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.applications.rainbow.lib.FilterAccess;

public abstract class BaseUtility implements IUtility {

	protected final Logger logger = LoggerFactory.getLogger("net.sf.okapi.logging");
	protected EventListenerList listenerList = new EventListenerList();
	protected FilterAccess fa;
	protected String paramsFolder;
	protected Shell shell;
	protected ArrayList<InputData> inputs;
	protected ArrayList<OutputData> outputs;
	protected String inputRoot;
	protected String outputRoot;
	protected String srcLang;
	protected String trgLang;

	public BaseUtility () {
		inputs = new ArrayList<InputData>();
		outputs = new ArrayList<OutputData>();
	}
	
	public void addCancelListener (CancelListener listener) {
		listenerList.add(CancelListener.class, listener);
	}

	public void removeCancelListener (CancelListener listener) {
		listenerList.remove(CancelListener.class, listener);
	}

	public void setContextUI (Object contextUI) {
		shell = (Shell)contextUI;
	}

	public void setOptions (String sourceLanguage,
		String targetLanguage)
	{
		srcLang = sourceLanguage;
		trgLang = targetLanguage;
	}

	public void setFilterAccess (FilterAccess filterAccess,
		String paramsFolder)
	{
		fa = filterAccess;
		this.paramsFolder = paramsFolder;
	}

	protected void fireCancelEvent (CancelEvent event) {
		Object[] listeners = listenerList.getListenerList();
		for ( int i=0; i<listeners.length; i+=2 ) {
			if ( listeners[i] == CancelListener.class ) {
				((CancelListener)listeners[i+1]).cancelOccurred(event);
			}
		}
	}

	public void addInputData (String path,
		String encoding,
		String filterSettings)
	{
		inputs.add(new InputData(path, encoding, filterSettings));
	}

	public void addOutputData (String path,
		String encoding)
	{
		outputs.add(new OutputData(path, encoding));
	}

	public String getInputRoot () {
		return inputRoot;
	}

	public String getOutputRoot () {
		return outputRoot;
	}

	public void resetLists () {
		inputs.clear();
		outputs.clear();
	}

	public void setRoots (String inputRoot,
		String outputRoot)
	{
		this.inputRoot = inputRoot;
		this.outputRoot = outputRoot;
	}

	public String getInputPath (int index) {
		return inputs.get(index).path;
	}
	
	public String getInputEncoding (int index) {
		return inputs.get(index).encoding;
	}

	public String getInputFilterSettings (int index) {
		return inputs.get(index).filterSettings;
	}

	public String getOutputPath (int index) {
		return outputs.get(index).path;
	}
	
	public String getOutputEncoding (int index) {
		return outputs.get(index).encoding;
	}

	public void cancel () {
		// TODO Auto-generated method stub
		
	}

	public void pause () {
		// TODO Auto-generated method stub
		
	}

	public void resume () {
		// TODO Auto-generated method stub
		
	}

}
