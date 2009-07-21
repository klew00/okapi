/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.applications.rainbow.packages;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.annotation.ScoresAnnotation;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.TextUnit;

/**
 * Provides a common way create a translation package. 
 */
public interface IWriter extends IFilterWriter {

	public String getPackageType ();
	
	public String getReaderClass ();
	
	/**
	 * Sets the global parameters of the package.
	 * @param sourceLanguage the source language.
	 * @param targetLanguage the target language.
	 * @param projectID the project identifier.
	 * @param outputDir the root folder for the output.
	 * @param packageID the package identifier.
	 * @param sourceRoot the root folder of the original inputs.
	 * @param preSegmented indicates if the files are pre-segmented.
	 */
	public void setInformation (String sourceLanguage,
		String targetLanguage,
		String projectID,
		String outputDir,
		String packageID,
		String sourceRoot,
		boolean preSegmented);
	
	public void writeStartPackage ();
	
	public void writeEndPackage (boolean createZip);
	
	public void createOutput (int docID,
		String relativeSourcePath,
		String relativeTargetPath,
		String sourceEncoding,
		String targetEncoding,
		String filterSettings,
		IParameters filterParams);

	public void createCopies (int docID,
		String relativeSourcePath);

	/**
	 * Helper method to output the TMX entries.
	 * @param tu the text unit to look at for possible output.
	 */
	public void writeTMXEntries (TextUnit tu);

	/**
	 * Helper method to output scored entries. This method is called by {@link #writeTMXEntries(TextUnit)}.
	 * @param item the text unit to process.
	 * @param scores the optional annotation holding the scores (can be null).
	 */
	public void writeScoredItem (TextUnit item,
		ScoresAnnotation scores);

}
