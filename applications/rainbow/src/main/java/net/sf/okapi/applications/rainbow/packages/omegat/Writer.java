/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
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

package net.sf.okapi.applications.rainbow.packages.omegat;

import java.io.File;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.XMLWriter;
//import net.sf.okapi.common.annotation.ScoresAnnotation;
import net.sf.okapi.common.annotation.AltTranslation;
import net.sf.okapi.common.annotation.AltTranslationsAnnotation;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.TextContainer;

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
	public void writeStartPackage () {
		// Set any non-default folders before calling the base class method.
		manifest.setSourceLocation("source");
		manifest.setTargetLocation("target");
		tmxPathApproved = manifest.getRoot() + File.separator + "omegat" + File.separator
			+ "project_save.tmx";
		tmxPathUnApproved = manifest.getRoot() + File.separator + "tm" + File.separator
			+ "unapproved.tmx";
		tmxPathAlternate = manifest.getRoot() + File.separator + "tm" + File.separator
			+ "alternate.tmx";
		tmxPathLeverage = manifest.getRoot() + File.separator + "tm" + File.separator
			+ "leverage.tmx";

		// Force the creation tool setting (needed for TMX with OmegaT workarounds)
		creationTool = "OmegaT";

		// Call the base class method
		super.writeStartPackage();
		
		tmxWriterApproved.setLetterCodedMode(true);
		tmxWriterUnApproved.setLetterCodedMode(true);
		tmxWriterAlternate.setLetterCodedMode(true);
		tmxWriterLeverage.setLetterCodedMode(true);

		// Force OmegaT-specific settings
		options.gMode = true;
		options.message = "This file is intended to be used with OmegaT. "
			+ "In order to provide better matching for exact matches, "
			+ "it may use specific XLIFF constructs in ways that may not work with other XLIFF tools.";
		options.includeNoTranslate = false;
		
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

	@Override
	public void writeScoredItem (ITextUnit item) {
		// In OmegaT we put both the approved and exact match of the project_save.tmx (the 'approved' one). 
		String tuid = item.getName();
		TextContainer srcTC = item.getSource();
		TextContainer trgTC = item.getTarget(trgLoc);

		ISegments trgSegs = trgTC.getSegments();
		for ( Segment srcSeg : srcTC.getSegments() ) {
			Segment trgSeg = trgSegs.get(srcSeg.id);
			if ( trgSeg != null ) {
				AltTranslationsAnnotation atAnn = trgSeg.getAnnotation(AltTranslationsAnnotation.class);
				if ( atAnn == null ) continue; // Nothing to do
				// Else: add the alt-trans
				for ( AltTranslation at : atAnn ) {
					if ( at.getScore() >= 100 ) {
						tmxWriterApproved.writeTU(srcSeg.getContent(),
							trgSeg.getContent(),
							((tuid==null) ? null : String.format("%s_s%s", tuid, trgSeg.id)), null);
					}
					else if ( at.getScore() > 0 ) {
						tmxWriterLeverage.writeTU(srcSeg.getContent(),
							trgSeg.getContent(),
							((tuid==null) ? null : String.format("%s_s%s", tuid, trgSeg.id)), null);
					}
				}
			}
			// Else: skip source without target
		}
	}
	
	private void createOmegaTProject () {
		XMLWriter XR = null;
		try {
			XR = new XMLWriter(manifest.getRoot() + File.separator + "omegat.project");
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
			XR.writeRawXML(manifest.getSourceLanguage().toBCP47());
			XR.writeEndElementLineBreak(); // source_lang

			XR.writeStartElement("target_lang");
			XR.writeRawXML(manifest.getTargetLanguage().toBCP47());
			XR.writeEndElementLineBreak(); // target_lang

			XR.writeStartElement("sentence_seg");
			XR.writeRawXML(preSegmented ? "false" : "true");
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
