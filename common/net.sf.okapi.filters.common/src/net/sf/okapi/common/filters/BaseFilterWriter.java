/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
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

package net.sf.okapi.common.filters;

import java.io.OutputStream;

import net.sf.okapi.common.IParameters;

public abstract class BaseFilterWriter implements IFilterWriter {

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.filters.IFilterWriter#close()
	 */
	public void close() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.filters.IFilterWriter#getName()
	 */
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.filters.IFilterWriter#getParameters()
	 */
	public IParameters getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.filters.IFilterWriter#handleEvent(net.sf.okapi.common.filters.FilterEvent)
	 */
	public FilterEvent handleEvent(FilterEvent event) {
		// TODO Auto-generated method stub
		return event;
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.filters.IFilterWriter#setOptions(java.lang.String, java.lang.String)
	 */
	public void setOptions(String language, String defaultEncoding) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.filters.IFilterWriter#setOutput(java.lang.String)
	 */
	public void setOutput(String path) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.filters.IFilterWriter#setOutput(java.io.OutputStream)
	 */
	public void setOutput(OutputStream output) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.filters.IFilterWriter#setParameters(net.sf.okapi.common.IParameters)
	 */
	public void setParameters(IParameters params) {
		// TODO Auto-generated method stub

	}
}
