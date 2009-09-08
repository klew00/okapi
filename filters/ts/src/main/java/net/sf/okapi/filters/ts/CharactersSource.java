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

package net.sf.okapi.filters.ts;

import javax.xml.stream.XMLStreamReader;

import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.filters.ts.stax.Characters;

public class CharactersSource extends Characters{
	
	TextUnit tu;
	
	public CharactersSource(XMLStreamReader reader){
		super(reader);
	}
	
	public CharactersSource(String str){
		super(str);
	}
	
	public void setTu(TextUnit tu){
		this.tu = tu;
	}

	public GenericSkeleton getSkeleton(){
		if(tu != null){
			String tmpRawText = rawText;
			TextFragment tf = new TextFragment("");
			int index = tmpRawText.indexOf("<byte value=\"");
			int index2 = tmpRawText.indexOf("\"/>");
			while(index != -1 && index2 != -1){
				tf.append(tmpRawText.substring(0,index));
				tf.append(TagType.PLACEHOLDER, "byte", "<byte value=\""+tmpRawText.substring(index+13, index2)+ "\"/>");
				tmpRawText = tmpRawText.substring(index2+3);
				index = tmpRawText.indexOf("<byte value=\"");
				index2 = tmpRawText.indexOf("\"/>");
			}
			tf.append(tmpRawText);
			//TextContainer tc = new TextContainer(rawText);
			//tc.setContent(tf);
			//tu.setSource(tc);
			tu.setSourceContent(tf);
			GenericSkeleton skel = new GenericSkeleton();
			skel.addContentPlaceholder(tu);
			return skel;				
		}else{
			return super.getSkeleton();
		}
	}
}