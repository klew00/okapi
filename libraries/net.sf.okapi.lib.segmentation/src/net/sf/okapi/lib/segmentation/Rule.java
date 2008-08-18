/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel                                          */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.lib.segmentation;

public class Rule {

	protected String    before;
	protected String    after;
	protected boolean   isBreak;
	
	
	public Rule () {
		before = "";
		after = "";
		isBreak = false;
	}
	
	public Rule (String before,
		String after,
		boolean isBreak)
	{
		if ( before == null ) this.before = "";
		else this.before = before;
		
		if ( after == null ) this.after = "";
		else this.after = after;
		
		this.isBreak = isBreak;
	}
	
	public String getBefore () {
		return before;
	}
	
	public void setBefore (String value) {
		before = value;
	}
	
	public String getAfter () {
		return after;
	}
	
	public void setAfter (String value) {
		after = value;
	}
	
	public boolean isBreak () {
		return isBreak;
	}
	
	public void setIsBreak (boolean value) {
		isBreak = value;
	}
}
