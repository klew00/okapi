/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.filters.plaintext;

import net.sf.okapi.common.BaseParameters;

public class ParametersPT extends BaseParameters {

//----------------------------------------------------------------------------------------------------------------------------	
	/** Char sequence to be considered line break. If empty, line breaks are auto-detected from the input.
	 * If specified (for instance, "\r\n" or "\r\n\r\n"), then auto-detection is not performed.<p>Default: null (auto-detect). */	
	public String lineBreakPattern = null;	
		
	/** Generate text units for empty lines?<p>Default: false (no). */	
	public boolean sendEmptyLines = false;   

	/** 
	 * Text to be inserted in the beginning of every line.<p>Default: null (don't insert anything).<p>A %d specifier will be replaced with the line's number.<p><p>
	 * <b>Example:</b><p>The 15-th line is "<i>Penny Lane is in my ears and in my eyes...</i>", linePrefix is "%d.  ".<p>The filter will produce:<p>
	 *  "<i>15.  Penny Lane is in my ears and in my eyes...</i>"
	 * */	
	public String linePrefix = null;
	
	/** Start index to number lines of the given text.<p>Default: 1. */
	public int firstLineNumber = 1;
	
	/** You can specify a text to be added to every line's text. For example, you may want to add "\n" at the end of every line.
	 * <p>Default: null  (don't add anything). */	
	public String lineSuffix = null;
	
	/** Specify a non-empty string to replace Tab characters (\u0009) with the string (normally a run of 4-8 spaces). 
	 * Leave empty not to replace tabs.<p>Default: null (don't extend tabs). */	
	public String tabExtender = null;
		
	/** If true, Form feeds (\u000c) will act like line breaks, and be added to the first line of the two broken to. 
	 * If false, they will be considered part of the current non-broken line.<p>Default: true (break lines). */
	public boolean formFeedBreaksLine = true;
	
	/** Convert non-breaking spaces (\u00a0, \t) to regular spaces (\u0020).<p>Default: false (do not convert). */
	public boolean convertNonBreakingSpaces = false;

	/** Delete spaces (Space, Non-breaking Space, Tab) in the beginning of every line.<p>Default: false (don't delete). */	
	public boolean trimHeadingSpaces = false;

	/** Delete spaces (Space, Non-breaking Space, Tab) at the end of every line.<p>Default: false (don't delete). */
	public boolean trimTrailingSpaces = false;
	
	/** If a Backspace character (\u0008, \b) is found in the line, it is deleted along with its preceding character.<p>Default: false (leave backspaces as are). */
	public boolean backspaceDeletesPrevChar = false;
	
	/** Non-printable characters (\u0001 - \u001f) are converted to escape sequences (\a, \b, ...).<p>Default: false (don't convert). */
	public boolean convertNonPrintableToEscapes = false;
	
	/** Unicode characters are replaced with their escape sequences (\u221e).<p>Default: false (not replaced). */
	public boolean convertUnicodeToEscapes = false;
			
	/** Holds a reference to an external object whose breakLine() is called for every line to determine if the line needs to be further broken.<p>
	 * Default: null (all line-breaking decisions are made by the filter). */
	public ILineBreaker lineBreaker = null;
	
	/** Holds a reference to an external object whose processLine() is called BEFORE the filter's operations on the current line.<p>
	 * Default: null (no preprocessor). */
	public ILineProcessor linePreProcessor = null;
	
	/** Holds a reference to an external object whose processLine() is called AFTER the filter's operations on the current line.<p>
	 * Default: null (no postprocessor). */
	public ILineProcessor linePostProcessor = null;
	
//----------------------------------------------------------------------------------------------------------------------------	
	public void fromString(String data) {
		// TODO Auto-generated method stub
		
	}

	public ParametersPT() {
		super();
		
		reset();
	}

	public void reset() {
		
	}

	@Override
	public String toString () {
		buffer.reset();
		
		return buffer.toString();
	}
}

