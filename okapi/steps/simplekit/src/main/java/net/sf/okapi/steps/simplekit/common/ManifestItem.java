/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.simplekit.common;

public class ManifestItem {

	private int id;
	private String originalRelativePath;
	private String sourceRelativePath;
	private String encoding;
	private String filterId;
	private String formatType;

	public ManifestItem (int id,
		String originalRelativePath,
		String sourceRelativePath,
		String encoding,
		String filterId,
		String formatType)
	{
		if ( originalRelativePath == null ) throw new NullPointerException();
		if ( sourceRelativePath == null ) throw new NullPointerException();
		if ( encoding == null ) throw new NullPointerException();
		if ( encoding == null ) throw new NullPointerException();
		if ( filterId == null ) throw new NullPointerException();
		if ( formatType == null ) throw new NullPointerException();
		
		this.id = id;
		this.originalRelativePath = originalRelativePath;
		this.sourceRelativePath = sourceRelativePath;
		this.encoding = encoding;
		this.filterId = filterId;
		this.formatType = formatType;
	}

	public String getSourceRelativePath () {
		return sourceRelativePath;
	}
	
	public String getOriginalRelativePath () {
		return originalRelativePath;
	}
	
	public String getEncoding () {
		return encoding;
	}

	public String getFilterId () {
		return filterId;
	}

	public String getFormatType () {
		return formatType;
	}
	
	public int getId () {
		return id;
	}

}
