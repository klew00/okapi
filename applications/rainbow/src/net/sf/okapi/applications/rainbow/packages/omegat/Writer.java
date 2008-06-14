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

import net.sf.okapi.common.Util;
import net.sf.okapi.common.XMLWriter;

public class Writer extends net.sf.okapi.applications.rainbow.packages.xliff.Writer {

	@Override
	public String getPackageType () {
		return "omegat";
	}
	
	@Override
	public String getReaderClass () {
		//TODO: Use dynamic name
		return "net.sf.okapi.applications.rainbow.packages.omegat.Reader";
	}
	
	@Override
	public void writeStartPackage ()
	{
		// Set any non-default folders before calling the base class method.
		manifest.setSourceLocation("source");
		manifest.setTargetLocation("target");
		tmxPath = manifest.getRoot() + File.separator + "tm" + File.separator
			+ "fromOriginal.tmx";
		// call the base class method
		super.writeStartPackage();
		
		// Create the OmegaT-specific directories
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
			XR.writeEndElementLineBreak(); // source_dir
			
			XR.writeStartElement("target_dir");
			XR.writeRawXML("__DEFAULT__");
			XR.writeEndElementLineBreak(); // target_dir
			
			XR.writeStartElement("tm_dir");
			XR.writeRawXML("__DEFAULT__");
			XR.writeEndElementLineBreak(); // tm_dir
			
			XR.writeStartElement("glossary_dir");
			XR.writeRawXML("__DEFAULT__");
			XR.writeEndElementLineBreak(); // glossary_dir
			
			XR.writeStartElement("source_lang");
			XR.writeRawXML(manifest.getSourceLanguage());
			XR.writeEndElementLineBreak(); // source_lang

			XR.writeStartElement("target_lang");
			XR.writeRawXML(manifest.getTargetLanguage());
			XR.writeEndElementLineBreak(); // target_lang

			XR.writeStartElement("sentence_seg");
			XR.writeRawXML("true");
			XR.writeEndElementLineBreak(); // sentence_seg

			XR.writeEndElementLineBreak(); // project
			XR.writeEndElement(); // omegat
		}
		finally {
			if ( XR != null ) {
				XR.writeEndDocument();
				XR.close();
			}
		}
	}
}
