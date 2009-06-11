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

package net.sf.okapi.filters.plaintext.common;

import java.util.LinkedList;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.IParameters;

/**
 * Compound Filter parameters.
 * 
 * @version 0.1, 10.06.2009
 * @author Sergei Vasilyev
 */

public class CompoundFilterParameters extends BaseParameters{
		 
	private LinkedList<IParameters> parameters = new LinkedList<IParameters>();
	private IParameters activeParameters = null;

	public String parametersClass = "";
	
	public CompoundFilterParameters() {
		
		super();
		
		reset();
		toString(); 
		
	}

	protected boolean addParameters(Class<?> parametersClass) {
		
		if (parameters == null) return false;
		boolean res = false;
	
		IParameters params = null;
		
		try {
			res = parameters.add((IParameters)parametersClass.newInstance());
			if (!res) return false;
			
			params = parameters.getLast();
			if (params == null) return false;
						
		} catch (InstantiationException e) {
			
			return false;
			
		} catch (IllegalAccessException e) {
			
			return false;
		}
		
		if (activeParameters == null)
			activeParameters = params;
				
		return res;
	}

	public boolean setActiveParameters(String parametersClass) {
		
		IParameters params = findParameters(parametersClass);
		if (params == null) return false; 
		
		if (activeParameters != params) {
			
			// Some finalization of the previous one might be needed
			activeParameters = params;
		}
		
		return true;
	}
	
	private IParameters findParameters(String parametersClass) {
					
		if (parameters == null) return null;
		
		for (IParameters params : parameters) {
			
			if (params == null) continue;
			if (params.getClass() == null) continue;
			
			if (params.getClass().getName().equalsIgnoreCase(parametersClass)) 
				return params;
		}
		
		return null;
	}

	public void reset() {
		
		parametersClass = "";
	}
	
	public void fromString(String data) {
		
		reset();
		
		buffer.fromString(data);
		
		parametersClass = buffer.getString("parametersClass", "");
		setActiveParameters(parametersClass);
		
		// Load active parameters
		if (activeParameters != null)			
			activeParameters.fromString(data);
	}
	
	@Override
	public String toString () {
		
		buffer.reset();
		
		// Store active parameters		
		if (activeParameters != null)			
			buffer.fromString(activeParameters.toString());
		
		if (activeParameters == null)
			parametersClass = "";
		else
			parametersClass = activeParameters.getClass().getName(); 
				
		buffer.setString("parametersClass", parametersClass);				
		
		return buffer.toString();
	}
}
