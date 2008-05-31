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

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.resource.IExtractionItem;

/**
 * Provides a common way create a translation package. 
 */
public interface IWriter {

	public String getPackageType ();
	
	/**
	 * Sets the global parameters of the package.
	 * @param sourceLanguage The source language.
	 * @param targetLanguage The target language.
	 * @param projectID The project identifier.
	 * @param outputDir The root folder for the output.
	 * @param packageID The package identifier.
	 * @param sourceRoot The root folder of the original inputs.
	 */
	public void setParameters (String sourceLanguage,
		String targetLanguage,
		String projectID,
		String outputDir,
		String packageID,
		String sourceRoot);
	
	public void writeStartPackage ();
	
	public void writeEndPackage (boolean createZip);
	
	public void createDocument (int docID,
		String relativePath,
		String inputEncoding,
		String outputEncoding,
		String filterSettings,
		IParameters filterParams);
	
	public void writeStartDocument ();
	
	/**
	 * Writes an item in the current document. 
	 * @param sourceItem The source item.
	 * @param targetItem The target item.
	 * @param p_nStatus The current target status (same values as Borneo DB TSTATUS_*)
	 */
	public void writeItem (IExtractionItem sourceItem,
		IExtractionItem targetItem,
		int p_nStatus);
	
	public void writeEndDocument ();

}
