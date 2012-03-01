/*===========================================================================
  Copyright (C) 2012 by the Okapi Framework contributors
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

package net.sf.okapi.filters.drupal;

public class NodeInfo {
	
	private String nid;
	private String title;
	private String type;
	private boolean selected;
	private String status;
	
	public NodeInfo (String nid,
		boolean selected)
	{
		this.nid = nid;
		this.selected = selected;
		this.title = "";
		this.status = "";
		this.type = "";
	}

	@Override
	public String toString(){
		return "nid: "+nid+"\ttitle: "+title+"\ttype: "+type;
	}

	public String getNid () {
		return nid;
	}
	
	public void setNid (String nid) {
		this.nid = nid;
	}
	
	public String getTitle () {
		return title;
	}
	
	public void setTitle (String title) {
		if ( title == null ) this.title = "";
		else this.title = title;
	}
	
	public String getType () {
		return type;
	}
	
	public void setType (String type) {
		this.type = type;
	}

	public boolean getSelected () {
		return selected;
	}
	
	public void setSelected (boolean selected) {
		this.selected = selected;
	}

	public String getStatus () {
		return status;
	}
	
	public void setStatus (String status) {
		this.status = status;
	}
	
}
