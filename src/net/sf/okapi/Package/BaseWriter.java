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

package net.sf.okapi.Package;

import net.sf.okapi.Library.Base.ILog;
import net.sf.okapi.Library.Base.Utils;

public abstract class BaseWriter implements IWriter {
	
	protected Manifest  m_Mnf;
	protected ILog      m_Log;
	
	public BaseWriter (ILog p_Log) {
		m_Log = p_Log;
		m_Mnf = new Manifest(m_Log);
	}
	
	public void setParameters (String p_sSourceLanguage,
		String p_sTargetLanguage,
		String p_sProjectID,
		String p_sOutputDir,
		String p_sPackageID)
	{
		m_Mnf.setSourceLanguage(p_sSourceLanguage);
		m_Mnf.setTargetLanguage(p_sTargetLanguage);
		m_Mnf.setProjectID(p_sProjectID);
		m_Mnf.setRoot(p_sOutputDir);
		m_Mnf.setPackageID(p_sPackageID);
		m_Mnf.setPackageType(getPackageType());
	}

	public void writeStartPackage ()
	{
		// Create the directory
		Utils.createDirectories(m_Mnf.getRoot());
	}

	public void writeEndPackage (boolean p_bCreateZip) {
		try {
			// Save the manifest
			if ( m_Mnf != null ) {
				m_Mnf.Save();
			}
			
			if ( p_bCreateZip ) {
				// Zip the package if needed
				Compression.zipDirectory(m_Mnf.getRoot(), m_Mnf.getRoot() + ".zip");
			}
		}
		catch ( Exception E ) {
			m_Log.error(E.getLocalizedMessage());
		}
	}
}
