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
============================================================================*/

package net.sf.okapi.filters.ts.stax;

import javax.xml.stream.XMLStreamReader;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.filters.ts.stax.StaxObject;

public class Characters implements StaxObject{
	protected String rawText;
	protected String escapedText;
	
	public Characters(XMLStreamReader reader){
		readObject(reader);
	}

	public Characters(String str){
		this.rawText = str;
		this.escapedText = str;
	}
	
	public Characters(){
		this.rawText = "";
		this.escapedText = "";
	}
	
	public void readObject(XMLStreamReader reader){
		this.rawText = reader.getText();
		this.escapedText = Util.escapeToXML(reader.getText(), 0, true, null); 
	}
	
	public void append(XMLStreamReader reader){
		this.rawText = this.rawText.concat(reader.getText()); 
		this.escapedText = this.rawText.concat(Util.escapeToXML(reader.getText(), 0, true, null));
	}
	
	public void append(String s){
		this.rawText = this.rawText.concat(s); 
		this.escapedText = this.rawText.concat(Util.escapeToXML(s, 0, true, null));
	}	
	
	public String toString(){
		return escapedText;
	}
	
	public GenericSkeleton getSkeleton(){
		return new GenericSkeleton(escapedText);
	}
}