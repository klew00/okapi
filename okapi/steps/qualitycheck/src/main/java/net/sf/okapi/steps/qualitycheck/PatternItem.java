/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.qualitycheck;

import java.util.List;
import java.util.regex.Pattern;

public class PatternItem {
	
	public static final String SAME = "<same>";

	public String source;
	public String target;
	public boolean enabled;
	public String message;
	
	private Pattern srcPat;
	private Pattern trgPat;

	public static List<PatternItem> loadFile () {
		return null;
	}
	
	public PatternItem (String source,
		String target,
		boolean enabled)
	{
		create(source, target, enabled, null);
	}

	public PatternItem (String source,
		String target,
		boolean enabled,
		String message)
	{
		create(source, target, enabled, message);
	}

	private void create (String source,
		String target,
		boolean enabled,
		String message)
	{
		this.source = source;
		this.target = target;
		this.enabled = enabled;
		this.message = message;
	}

	public void compile () {
		srcPat = Pattern.compile(source);
		if ( !target.equals(SAME) ) {
			trgPat = Pattern.compile(target);
		}
	}

	public Pattern getSourcePattern () {
		return srcPat; 
	}

	public Pattern getTargetPattern () {
		return trgPat; 
	}

}
