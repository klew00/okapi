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

import net.sf.okapi.Filter.IFilterItem;

public interface IMatch {
	
	public int getKind ();
	
	public void setKind (int p_nKind);
	
	public int getScore ();
	
	public void setScore (int p_nScore);
	
	public IFilterItem getSource ();
	
	public void setSource (IFilterItem p_Item);
	
	public IFilterItem getTarget ();
	
	public void setTarget (IFilterItem p_Item);
	
	public String getSourceText ();
	
	public void setSourceText (String p_sText);
	
	public String getTargetText ();
	
	public void setTargetText (String p_sText);
}