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

package net.sf.okapi.filters.openxml;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.filters.PropertyTextUnitPlaceholder;

/**
 * Holds text and placeholder information for a list of tags.  This
 * is used in OpenXMLContentFilter as a temporary holding place
 * for information that will be needed to create a single code
 * for a sequence of tags such as <w:r>...<w:t> or </w:t>...</w:r>.
 * 
 */
public class TextRun {
	
	private String text;
	private List<PropertyTextUnitPlaceholder> propertyTextUnitPlaceholders;

	public TextRun()
	{
		text = "";
		propertyTextUnitPlaceholders = null;
	}
	
	/**
	 * Appends a string to the text run. If the string is null, it is ignored.
	 * @param text The string to append.
	 */
	public void append (String text) {
		if ( text != null )
			this.text += text;
	}

	/**
	 * Appends an existing list placeholders to this text run.
	 * @param List<PropertyTextUnitPlaceholder> propertyTextUnitPlaceholders The existing placeholders to add.
	 */
	public void appendWithPropertyTextUnitPlaceholders(String text, int offset, List<PropertyTextUnitPlaceholder> propertyTextUnitPlaceholders) {
		if ( text != null )
			this.text += text;
		for(PropertyTextUnitPlaceholder p : propertyTextUnitPlaceholders)
		{
			p.setMainStartPos(p.getMainStartPos()+offset);
			p.setMainEndPos(p.getMainEndPos()+offset);
			p.setValueStartPos(p.getValueStartPos()+offset);
			p.setValueEndPos(p.getValueEndPos()+offset);
			if (this.propertyTextUnitPlaceholders==null)
				this.propertyTextUnitPlaceholders = new ArrayList<PropertyTextUnitPlaceholder>();
			this.propertyTextUnitPlaceholders.add(p);
		}
	}

	public String getText()
	{
		return text;
	}
	public void setText(String text)
	{
		this.text = text;
	}
	public List<PropertyTextUnitPlaceholder> getPropertyTextUnitPlaceholders()
	{
		return propertyTextUnitPlaceholders;
	}
	public void setPropertyTextUnitPlaceholders(List<PropertyTextUnitPlaceholder> propertyTextUnitPlaceholders)
	{
		this.propertyTextUnitPlaceholders = propertyTextUnitPlaceholders;
	}
}
