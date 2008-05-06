/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel (at ENLASO Corporation)                  */
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

package net.sf.okapi.utilities.textrewriting;

import net.sf.okapi.Filter.FilterItemText;
import net.sf.okapi.Filter.FilterItemType;
import net.sf.okapi.Filter.IFilter;
import net.sf.okapi.Filter.IFilterItem;
import net.sf.okapi.Library.Base.IParameters;
import net.sf.okapi.utility.IUtility;

public class Utility implements IUtility {
	
	IFilter             filter;
	Parameters          params;

	public Utility () {
		params = new Parameters();
	}
	
	public String getIdentifier() {
		return "textrewriting";
	}

	public boolean hasParameters () {
		return false;
	}
	
	public IParameters getParameters () {
		// Not used
		return null;
	}

	public String getRoot () {
		// Not used
		return null;
	}

	public boolean needRoot () {
		return false;
	}

	public void processEndDocument () {
		filter.closeOutput();
	}

	public void processItem (IFilterItem filterItem) {
		if ( filterItem.getItemType() == FilterItemType.TEXT ) {
			if ( filterItem.isTranslatable() ) {
				switch ( params.action ) {
				case Parameters.ACTION_MASK:
					String tmp = filterItem.getText(FilterItemText.CODED).replaceAll("\\p{L}", "X");
					filterItem.modifyText(tmp.replaceAll("\\d", "N"));
					break;
				}
			}
		}
		filter.writeItem();
	}

	public void processStartDocument (IFilter newFilter,
		String outputPath,
		String outputLanguage,
		String outputEncoding)
	{
		filter = newFilter;
		filter.setOutputOptions(outputLanguage, outputEncoding);
		filter.openOutputFile(outputPath);
	}

	public void setParameters (IParameters paramsObject) {
		// Not used
	}

	public void setRoot (String root) {
		// Not used
	}

}
