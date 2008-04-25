/*===========================================================================*/
/* Copyright (C) 2008 ENLASO Corporation, Okapi Development Team             */
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

package net.sf.okapi.Translation;

import net.sf.okapi.Filter.FilterItem;
import net.sf.okapi.Filter.FilterItemText;
import net.sf.okapi.Filter.IFilterItem;

public class Match implements IMatch {
	
	private int         kind = 0;
	private int         score = 0;
	private FilterItem  fitemSrc;
	private FilterItem  fitemTrg;

	public Match () {
		fitemSrc = new FilterItem();
		fitemTrg = new FilterItem();
	}
	
	public int getKind () {
		return kind;
	}

	public int getScore () {
		return score;
	}

	public IFilterItem getSource () {
		return fitemSrc;
	}

	public String getSourceText () {
		return fitemSrc.getText(FilterItemText.ORIGINAL);
	}

	public IFilterItem getTarget () {
		return fitemTrg;
	}

	public String getTargetText () {
		return fitemTrg.getText(FilterItemText.ORIGINAL);
	}

	public void setKind (int kind) {
		this.kind = kind;
	}

	public void setScore (int score) {
		this.score = score;
	}

	public void setSource (IFilterItem fitem) {
		fitemSrc.copyFrom(fitem);
	}

	public void setSourceText (String text) {
		fitemSrc.setText(text);
	}

	public void setTarget (IFilterItem fitem) {
		fitemTrg.copyFrom(fitem);
	}

	public void setTargetText (String text) {
		fitemTrg.setText(text);
	}

}
