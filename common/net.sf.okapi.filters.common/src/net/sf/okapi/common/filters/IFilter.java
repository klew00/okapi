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

import java.io.InputStream;
import java.net.URL;

import net.sf.okapi.common.IParameters;

public interface IFilter {	

	public String getName ();

	public void setOptions (String sourceLanguage,
			String defaultEncoding,
			boolean generateSkeleton);

	public void setOptions (String sourceLanguage,
			String targetLanguage,
			String defaultEncoding,
			boolean generateSkeleton);

	public void open (InputStream input);

	public void open (CharSequence inputText);

	public void open (URL inputURL);

	public void close ();

	public boolean hasNext ();

	public FilterEvent next ();	

	public void cancel ();

	public IParameters getParameters ();

	public void setParameters (IParameters params);

}
