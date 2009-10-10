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

package net.sf.okapi.steps.wordcount.common;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextUnitUtil;

/**
 * 
 * 
 * @version 0.1 07.07.2009
 */

abstract public class BaseCounter {
	
	private static BaseCounter counter = null;
	protected static StructureParameters params;
	
	abstract protected long doGetCount(String text, String language);
	abstract protected String getResourceName();
	
	static protected long getCount(Class<?> classRef, Object text, String language) {
	
		if (classRef == null) return 0L;
		if (text == null) return 0L;
		if (Util.isEmpty(language)) return 0L;
		
		if (text instanceof TextUnit) {
		
			TextUnit tu = (TextUnit) text;
			
			if (tu.hasTarget(language))
				return getCount(classRef,tu.getTarget(language), language);
			else
				return getCount(classRef, tu.getSource(), language);
		} 
		else if (text instanceof TextContainer) {
			
			TextContainer tc = (TextContainer) text;
			
			return getCount(classRef, tc.getContent(), language);
			
		}
		else if (text instanceof TextFragment) {
			
			TextFragment tf = (TextFragment) text;
			
			return getCount(classRef, TextUnitUtil.getText(tf), language);
		}
		else if (text instanceof String) {
			
			if (counter == null)				
				try {
					counter = (BaseCounter) classRef.newInstance();
					params = new StructureParameters();
					params.loadFromResource(counter.getResourceName());
					
				} catch (InstantiationException e) {
					
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					
					e.printStackTrace();
				}
				
			if (counter == null) return 0L;
			
			return counter.doGetCount((String) text, language);
		}
		
		return 0;		
	}
	
	public static String getTokenName() {
		
		return params.getTokenName();
	}
}
