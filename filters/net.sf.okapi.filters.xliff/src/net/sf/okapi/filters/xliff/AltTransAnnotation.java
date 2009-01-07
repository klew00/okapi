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
===========================================================================*/

package net.sf.okapi.filters.xliff;

import java.util.LinkedList;

import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;

//TODO: This class probably needs to be more generic and not in the XLIFF filter package
/**
 * Implements an annotation for XLIFF alt-trans.
 */
public class AltTransAnnotation implements IAnnotation {
	
	private class AltTrans {

		String srcLang;
		TextUnit tu;
		
		public AltTrans (String sourceLanguage,
			TextUnit textUnit)
		{
			srcLang = sourceLanguage;
			tu = textUnit;
		}
	}

	private LinkedList<AltTrans> list;
	private int id;
	
	public AltTransAnnotation () {
		list = new LinkedList<AltTrans>();
	}
	
	public void addNew (String sourceLanguage,
		TextContainer sourceText)
	{
		TextUnit tu = new TextUnit(String.valueOf(++id));
		if ( sourceText != null ) tu.setSource(sourceText);
		list.add(new AltTrans(sourceLanguage, tu));
	}

	public void setTarget (String targetLanguage,
		TextContainer targetText)
	{
		if ( list.size() == 0 ) {
			addNew(null, null);
		}
		list.getLast().tu.setTarget(targetLanguage, targetText);
	}

}
