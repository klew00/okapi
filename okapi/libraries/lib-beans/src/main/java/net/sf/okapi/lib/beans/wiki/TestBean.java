/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

package net.sf.okapi.lib.beans.wiki;

public class TestBean {
	
	private int data;
	private boolean ready;
	
	public TestBean() { // No-arguments constructor
		super();
		data = -1;
		ready = false;
	}
	
	public int getData() { // Getter for the private data field
		return data;
	}
	
	public void setData(int data) { // Setter for the private data field
		this.data = data;
	}
	
	public boolean isReady() { // Getter for the private ready field
		return ready;
	}
	
	public void setReady(boolean ready) { // Setter for the private ready field
		this.ready = ready;
	}
}
