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

package net.sf.okapi.applications.rainbow.packages.omegat;

import java.io.File;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.XMLWriter;

public class Writer extends net.sf.okapi.applications.rainbow.packages.xliff.Writer {

	@Override
	public String getPackageType () {
		return "omegat";
	}
	
	@Override
	public void writeStartPackage ()
	{
		// Set the source and target before calling the base class.
		manifest.setSourceLocation("source");
		manifest.setTargetLocation("target");
		super.writeStartPackage();
		
		// Create the OmegaT directories
		Util.createDirectories(manifest.getRoot() + File.separator + "glossary" + File.separator);
		Util.createDirectories(manifest.getRoot() + File.separator + "omegat" + File.separator);
		Util.createDirectories(manifest.getRoot() + File.separator + "tm" + File.separator);
	}

	@Override
	public void writeEndPackage (boolean p_bCreateZip) {
		// Write the OmegaT project file
		createOmegaTProject();
		// And then, normal ending
		super.writeEndPackage(p_bCreateZip);
	}

	private void createOmegaTProject () {
		XMLWriter XR = null;
		try {
			XR = new XMLWriter();
			XR.create(manifest.getRoot() + File.separator + "omegat.project");
			XR.writeStartDocument();
			XR.writeStartElement("omegat");
			XR.writeStartElement("project");
			XR.writeAttributeString("version", "1.0");

			XR.writeStartElement("source_dir");
			XR.writeRawXML("__DEFAULT__");
			XR.writeEndElement(); // source_dir
			
			XR.writeStartElement("target_dir");
			XR.writeRawXML("__DEFAULT__");
			XR.writeEndElement(); // target_dir
			
			XR.writeStartElement("tm_dir");
			XR.writeRawXML("__DEFAULT__");
			XR.writeEndElement(); // tm_dir
			
			XR.writeStartElement("glossary_dir");
			XR.writeRawXML("__DEFAULT__");
			XR.writeEndElement(); // glossary_dir
			
			XR.writeStartElement("source_lang");
			XR.writeRawXML(manifest.getSourceLanguage());
			XR.writeEndElement(); // source_lang

			XR.writeStartElement("target_lang");
			XR.writeRawXML(manifest.getTargetLanguage());
			XR.writeEndElement(); // target_lang

			XR.writeStartElement("sentence_seg");
			XR.writeRawXML("true");
			XR.writeEndElement(); // sentence_seg

			XR.writeEndElement(); // project
			XR.writeEndElement(); // omegat
		}
		catch ( Exception e ) {
			throw new RuntimeException(e);
		}
		finally {
			if ( XR != null ) {
				XR.writeEndDocument();
				XR.close();
			}
		}
	}
}
