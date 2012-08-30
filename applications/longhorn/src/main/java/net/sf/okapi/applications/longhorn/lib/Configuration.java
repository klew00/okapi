/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.applications.longhorn.lib;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Configuration {
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	private static final String DEF_WORKING_DIR = System.getProperty("user.home") + File.separator + "Okapi-Longhorn-Files";
	
	private String workingDirectory;

	public Configuration() {
		LOGGER.info("The default working directory for Okapi Longhorn will be used, " +
				"because no other was specified: " + DEF_WORKING_DIR);
		
		workingDirectory = DEF_WORKING_DIR;
	}

	public Configuration(String workingDir) {
		workingDir = workingDir.replace("\\", File.separator);
		workingDir = workingDir.replace("/", File.separator);
		workingDirectory = workingDir;
	}
	
	public Configuration(InputStream confXml) {
		loadFromFile(confXml);
	}

	public void loadFromFile(InputStream confXml) {
		workingDirectory = null;
		try {
			DocumentBuilderFactory Fact = DocumentBuilderFactory.newInstance();
			Fact.setValidating(false);
			Document Doc = Fact.newDocumentBuilder().parse(confXml);
			
			NodeList NL = Doc.getElementsByTagName("working-directory");
			
			for ( int i=0; i<NL.getLength(); i++ ) {
				String tc = NL.item(i).getTextContent();
				workingDirectory = tc;
			}
		}
		catch (DOMException e) {
			throw new RuntimeException(e);
		}
		catch (SAXException e) {
			throw new RuntimeException(e);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		
		if (workingDirectory == null)
			throw new IllegalArgumentException("Working directory not specified in configuration file");
	}
	
	public String getWorkingDirectory() {
		return workingDirectory;
	}
}
