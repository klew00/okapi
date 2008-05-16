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
/* Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA              */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.common.filters;

import java.util.ArrayList;
import java.util.List;

/**
 * Reference implementation of IExtractionItem.
 */
public class ExtractionItem implements IExtractionItem {

	private IContainer                 main;
	private ArrayList<IExtractionItem> children;
	private ArrayList<IContainer>      segments;
	
	public void addChild (IExtractionItem child) {
		if ( children == null ) {
			children = new ArrayList<IExtractionItem>();
		}
		children.add(child);
	}

	public List<IExtractionItem> getChildren () {
		if ( children == null ) {
			children = new ArrayList<IExtractionItem>();
		}
		return children;
	}

	public IContainer getContent () {
		return main;
	}

	public List<IContainer> getSegments () {
		if ( segments == null ) {
			segments = new ArrayList<IContainer>();
		}
		return segments;
	}

	public void setContent (IContainer data) {
		main = data;
	}

}
