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

package net.sf.okapi.filters.plaintext;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.filters.AbstractBaseFilter;
import net.sf.okapi.common.resource.RawDocument;


public class PlainTextFilter extends AbstractBaseFilter{

	@Override
	protected boolean hasUtf8Bom() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean hasUtf8Encoding() {
		// TODO Auto-generated method stub
		return false;
	}

	public void close() {
		// TODO Auto-generated method stub
		
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public IParameters getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public void open(RawDocument input) {
		// TODO Auto-generated method stub
		
	}

	public void open(RawDocument input, boolean generateSkeleton) {
		// TODO Auto-generated method stub
		
	}

	public void setParameters(IParameters params) {
		// TODO Auto-generated method stub
		_doSomething();
	}

	private void _doSomething(){
		
	}
}	
	