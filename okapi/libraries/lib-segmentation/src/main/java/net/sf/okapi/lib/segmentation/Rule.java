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

package net.sf.okapi.lib.segmentation;

/**
 * Stores the data for a SRX &lt;rule> element.
 */
public class Rule {

	/**
	 * Pattern for before the break point.
	 */
	protected String before;
	
	/**
	 * Pattern for after the break point.
	 */
	protected String after;
	
	/**
	 * Flag indicating if the rule is a breaking rule.
	 */
	protected boolean isBreak;
	
	/**
	 * Flag indicating if the rule is active.
	 */
	protected boolean isActive;

	/**
	 * Optional comment placed just before the rule.
	 */
	protected String comment;
	
	/**
	 * Creates an empty breaking and active Rule object. 
	 */
	public Rule () {
		before = "";
		after = "";
		isBreak = true;
		isActive = true;
		comment = null;
	}
	
	/**
	 * Creates a Rule object with given patterns and a flag indicating if the rule
	 * is a breaking one or a breaking exception.
	 * @param before the pattern for before the break point.
	 * @param after the pattern for after the break point.
	 * @param isBreak true if the rule is a breaking rule, false if it is a
	 * breaking exception.
	 */
	public Rule (String before,
		String after,
		boolean isBreak)
	{
		if ( before == null ) this.before = "";
		else this.before = before;
		
		if ( after == null ) this.after = "";
		else this.after = after;
		
		this.isBreak = isBreak;
		isActive = true;
	}
	
	/**
	 * Gets the pattern before the break point for this rule.
	 * @return the pattern before the break point for this rule.
	 */
	public String getBefore () {
		return before;
	}
	
	/**
	 * Sets the pattern before the break point for this rule.
	 * @param value the new pattern before the break point for this rule.
	 */
	public void setBefore (String value) {
		before = value;
	}
	
	/**
	 * Gets the pattern after the break point for this rule.
	 * @return the pattern after the break point for this rule.
	 */
	public String getAfter () {
		return after;
	}
	
	/**
	 * Sets the pattern after the break point for this rule.
	 * @param value the new pattern after the break point for this rule.
	 */
	public void setAfter (String value) {
		after = value;
	}
	
	/**
	 * Indicates if this rule is a breaking rule.
	 * @return true if this rule is a breaking rule, false if it is a
	 * breaking exception.
	 */
	public boolean isBreak () {
		return isBreak;
	}
	
	/**
	 * Sets the flag indicating if this rule is a breaking rule.
	 * @param value true if this rule is a breaking rule, false if it is a
	 * breaking exception.
	 */
	public void setBreak (boolean value) {
		isBreak = value;
	}
	
	/**
	 * Indicates if this rule is active.
	 * @return true if this rule is active, false otherwise.
	 */
	public boolean isActive () {
		return isActive;
	}
	
	/**
	 * Sets the flag indicating if this rule is active.
	 * @param value true if this rule is active, false otherwise.
	 */
	public void setActive (boolean value) {
		isActive = value;
	}

	/**
	 * Gets the optional comment for this rule.
	 * @return the comment for this rule, or null if there is none.
	 */
	public String getComment () {
		return comment;
	}
	
	/**
	 * Sets the comment for this rule.
	 * @param text the new comment. Use null or empty string to remove the comment.
	 */
	public void setComment (String text) {
		comment = text;
		if (( comment != null ) && ( comment.length() == 0 )) {
			comment = null;
		}
	}

}
