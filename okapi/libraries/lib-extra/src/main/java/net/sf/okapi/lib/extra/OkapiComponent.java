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

package net.sf.okapi.lib.extra;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.exceptions.OkapiBadFilterParametersException;

/**
 * Configurable Okapi component like a filter or pipeline step
 * 
 * @version 0.1 13.07.2009
 */

public abstract class OkapiComponent extends Component implements IConfigurable {

	private IParameters params;
	private String parametersClassName;
	
	abstract protected void component_init();
	
	protected void component_done() {
		
	}

	public IParameters getParameters() {
		return params;
	}
	
	public void setParameters(IParameters params) {
		this.params = (BaseParameters) params;
		
		if (params instanceof INotifiable)
			((INotifiable) params).exec(this, Notification.PARAMETERS_SET_OWNER, this);
		
		if (this instanceof INotifiable)
			((INotifiable) this).exec(this, Notification.PARAMETERS_CHANGED, null);
		
//		if (!Util.isEmpty(parametersClassName)) return; // This name is set by the first call from the filter's constructor
		if (params == null) return;
		if (params.getClass() == null) return;
		
		parametersClassName = params.getClass().getName();
	}

	protected <A> A getParameters(Class<A> expectedClass) {
		if (params == null) {
			throw new OkapiBadFilterParametersException("Filter parameters object is null.");			
		}
		
		if (!expectedClass.isInstance(params)) {
			
			String st = "null";
			if (params.getClass() != null) st = params.getClass().getName();
			
			throw new OkapiBadFilterParametersException(
					String.format("Parameters of class <%s> expected, but are <%s>",
							expectedClass.getName(), st));			
		}
		
		return expectedClass.cast(params);
	}

	public String getParametersClassName() {
		return parametersClassName;
	}

}
