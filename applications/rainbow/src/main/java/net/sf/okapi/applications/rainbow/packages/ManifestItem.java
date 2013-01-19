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

package net.sf.okapi.applications.rainbow.packages;

public class ManifestItem {

	public static final String POSPROCESSING_TYPE_DEFAULT = "default";
	public static final String POSPROCESSING_TYPE_RTF = "rtf";
	
	private String relativeWorkPath;
	private String relativeInputPath;
	private String relativeOutputPath;
	private String inputEncoding;
	private String outputEncoding;
	private String filterID;
	private boolean selected;
	private String postProcessingType;
	private boolean exists;

	public ManifestItem (String relativeWorkPath,
		String relativeInputPath,
		String relativeOutputPath,
		String inputEncoding,
		String outputEncoding,
		String filterID,
		String postProcessingType,
		boolean selected)
	{
		if ( relativeWorkPath == null ) throw new NullPointerException();
		if ( relativeInputPath == null ) throw new NullPointerException();
		if ( relativeOutputPath == null ) throw new NullPointerException();
		if ( inputEncoding == null ) throw new NullPointerException();
		if ( outputEncoding == null ) throw new NullPointerException();
		if ( filterID == null ) throw new NullPointerException();
		if ( postProcessingType == null ) throw new NullPointerException();
		
		this.relativeWorkPath = relativeWorkPath;
		this.relativeInputPath = relativeInputPath;
		this.relativeOutputPath = relativeOutputPath;
		this.inputEncoding = inputEncoding;
		this.outputEncoding = outputEncoding;
		this.filterID = filterID;
		this.selected = selected;
		this.postProcessingType = postProcessingType;
		exists = true;
	}

	public String getRelativeWorkPath () {
		return relativeWorkPath;
	}
	
	public String getRelativeInputPath () {
		return relativeInputPath;
	}
	
	public String getRelativeOutputPath () {
		return relativeOutputPath;
	}
	
	public String getInputEncoding () {
		return inputEncoding;
	}

	public String getOutputEncoding () {
		return outputEncoding;
	}

	public String getFilterID () {
		return filterID;
	}

	public boolean selected () {
		return selected;
	}
	
	public void setSelected (boolean value) {
		selected = value;
	}
	
	public boolean exists () {
		return exists;
	}
	
	public void setExists (boolean value) {
		exists = value;
	}
	
	public String getPostProcessingType () {
		return postProcessingType;
	}
	
}
