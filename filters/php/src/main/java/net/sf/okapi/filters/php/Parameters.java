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

package net.sf.okapi.filters.php;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.filters.InlineCodeFinder;
import net.sf.okapi.common.filters.LocalizationDirectives;

public class Parameters extends BaseParameters {

	public boolean useCodeFinder;
	public InlineCodeFinder codeFinder;
	public LocalizationDirectives locDir;
	private boolean concatenate;

	public Parameters () {
		locDir = new LocalizationDirectives();
		codeFinder = new InlineCodeFinder();
		reset();
		toString(); // fill the list
	}
	
	public boolean getConcatenate () {
		return concatenate;
	}

	public void setConcatenate (boolean concatenate) {
		this.concatenate = concatenate;
	}

	public void reset () {
		locDir.reset();
		useCodeFinder = true;
		codeFinder.reset();
		codeFinder.setSample("${abc} {$abc) $abc");
		codeFinder.setUseAllRulesWhenTesting(true);

		// HTML-like tags (including without start or end)
		codeFinder.addRule("(\\A[^<]*?>)|(<[\\w!?/].*?(>|\\Z))");
		// Basic escaped characters
		codeFinder.addRule("\\\\a|\\\\b|\\\\f|\\\\n|\\\\r|\\\\t|\\\\v");
		// Email address
		codeFinder.addRule("(\\w[-._\\w]*\\w@\\w[-._\\w]*\\w\\.\\w{2,3})");
		// [var] and {var} variables
		codeFinder.addRule("[\\[{][\\w_$]+?[}\\]]");
		concatenate = true;
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setBoolean("useLD", locDir.useLD());
		buffer.setBoolean("localizeOutside", locDir.localizeOutside());
		buffer.setBoolean("useCodeFinder", useCodeFinder);
		buffer.setGroup("codeFinderRules", codeFinder.toString());
		buffer.setBoolean("concatenate", concatenate);
		return buffer.toString();
	}
	
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		boolean tmpBool1 = buffer.getBoolean("useLD", locDir.useLD());
		boolean tmpBool2 = buffer.getBoolean("localizeOutside", locDir.localizeOutside());
		locDir.setOptions(tmpBool1, tmpBool2);
		useCodeFinder = buffer.getBoolean("useCodeFinder", useCodeFinder);
		codeFinder.fromString(buffer.getGroup("codeFinderRules", ""));
		buffer.getBoolean("concatenate", concatenate);
	}

}
