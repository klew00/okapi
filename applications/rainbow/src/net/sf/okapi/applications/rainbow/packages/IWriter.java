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

package net.sf.okapi.applications.rainbow.packages;

import net.sf.okapi.common.resource.IExtractionItem;

/**
 * Provides a common way create a translation package. 
 */
public interface IWriter {

	public String getPackageType ();
	
	public void setParameters (String p_sSourceLanguage,
		String p_sTargetLanguage,
		String p_sProjectID,
		String p_sOutputDir,
		String p_sPackageID);
	
	public void writeStartPackage ();
	
	public void writeEndPackage (boolean p_bCreateZip);
	
	public void createDocument (int p_nDKey,
		String p_sRelativePath);
	
	public void writeStartDocument (String p_sOriginal);
	
	/**
	 * Writes an item in the current document. 
	 * @param p_Source The source item.
	 * @param p_Target The target item.
	 * @param p_nStatus The current target status (same values as Borneo DB TSTATUS_*)
	 */
	public void writeItem (IExtractionItem p_Source,
		IExtractionItem p_Target,
		int p_nStatus);
	
	public void writeEndDocument ();

}
