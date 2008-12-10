/*===========================================================================*/
/* Copyright (C) 2008 by the Okapi Framework contributors                    */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.filters.xliff;

import java.io.InputStream;
import java.net.URL;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.resource.IResource;

public class XLIFFFilter implements IFilter {

	public void cancel () {
		// TODO Auto-generated method stub
		
	}

	public void close () {
		// TODO Auto-generated method stub
		
	}

	public String getName () {
		return "XLIFFFilter";
	}

	public IParameters getParameters () {
		return null;
	}

	public IResource getResource () {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasNext () {
		// TODO Auto-generated method stub
		return false;
	}

	public FilterEvent next () {
		// TODO Auto-generated method stub
		return null;
	}

	public void open(InputStream input) {
		// TODO Auto-generated method stub
		
	}

	public void open(CharSequence inputText) {
		// TODO Auto-generated method stub
		
	}

	public void open(URL inputURL) {
		// TODO Auto-generated method stub
		
	}

	public void setOptions(String sourceLanguage,
		String targetLanguage,
		String defaultEncoding,
		boolean generateSkeleton)
	{
		// TODO Auto-generated method stub
	}

	public void setOptions(String sourceLanguage,
		String defaultEncoding,
		boolean generateSkeleton)
	{
		setOptions(sourceLanguage, null, defaultEncoding, generateSkeleton);
	}

	public void setParameters (IParameters params) {
		// TODO Auto-generated method stub
		
	}

}
