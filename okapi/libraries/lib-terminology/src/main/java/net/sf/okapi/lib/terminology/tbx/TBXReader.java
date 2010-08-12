/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.lib.terminology.tbx;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.lib.terminology.GlossaryEntry;
import net.sf.okapi.lib.terminology.IGlossaryReader;
import net.sf.okapi.lib.terminology.LangEntry;
import net.sf.okapi.lib.terminology.TermEntry;

public class TBXReader implements IGlossaryReader {

	private GlossaryEntry entry;
	private GlossaryEntry gent;
	private LangEntry lent;
	private XMLStreamReader reader;

	@Override
	public void open (File file) {
		try {
			open(new FileInputStream(file));
		}
		catch ( Throwable e) {
			throw new OkapiIOException("Error opening the URI.\n" + e.getLocalizedMessage());
		}
	}

	@Override
	public void open (InputStream input) {
		try {
			close();
			XMLInputFactory fact = XMLInputFactory.newInstance();
			fact.setProperty(XMLInputFactory.IS_COALESCING, true);
			fact.setProperty(XMLInputFactory.SUPPORT_DTD, false);
			reader = fact.createXMLStreamReader(input);
			
			// Read the first entry
			readNext();
		}
		catch ( Throwable e) {
			throw new OkapiIOException("Error opening the URI.\n" + e.getLocalizedMessage());
		}
	}

	@Override
	public void close () {
		entry = null;
		try {
			if ( reader != null ) {
				reader.close();
				reader = null;
			}
		}
		catch ( XMLStreamException e) {
			throw new OkapiIOException(e);
		}
	}

	@Override
	public boolean hasNext () {
		return (entry != null);
	}

	@Override
	public GlossaryEntry next () {
		GlossaryEntry toSend = entry;
		readNext(); // Parse the next entry
		return toSend;
	}

	private void readNext () {
		try {
			entry = gent = null;
			while ( reader.hasNext() ) {
				int eventType = reader.next();
				switch ( eventType ) {
				case XMLStreamConstants.START_ELEMENT:
					String name = reader.getLocalName();
					if ( "termEntry".equals(name) ) {
						processTermEntry();
						return; // Done for this entry
					}
					break;
				}
			}
		}
		catch ( Throwable e ) {
			throw new OkapiIOException("Error when reading." + e.getLocalizedMessage(), e);
		}
	}

	private void processTermEntry () throws XMLStreamException {
		gent = new GlossaryEntry();
		
		String name;
		while ( reader.hasNext() ) {
			int eventType = reader.next();
			switch ( eventType ) {
			case XMLStreamConstants.START_ELEMENT:
				name = reader.getLocalName();
				if ( "langSet".equals(name) ) {
					processLangSet();
				}
				break;
			case XMLStreamConstants.END_ELEMENT:
				name = reader.getLocalName();
				if ( "termEntry".equals(name) ) {
					entry = gent; // No error, we can set the real entry
					return; // This termEntry is done
				}
				break;
			}
		}
	}

	private void processLangSet () throws XMLStreamException {
		// Get the language information
		String tmp = reader.getAttributeValue(XMLConstants.XML_NS_URI, "lang");
		if ( tmp == null ) {
			throw new OkapiIOException("Missing xml;lang attribute.");
		}
		// Create the new language entry
		lent = new LangEntry(LocaleId.fromString(tmp));

		String name;
		while ( reader.hasNext() ) {
			int eventType = reader.next();
			switch ( eventType ) {
			case XMLStreamConstants.START_ELEMENT:
				name = reader.getLocalName();
				if ( "tig".equals(name) ) {
					processTig();
				}
				else if ( "ntig".equals(name) ) {
					processNtig();
				}
				break;
			case XMLStreamConstants.END_ELEMENT:
				name = reader.getLocalName();
				if ( "langSet".equals(name) ) {
					gent.addLangEntry(lent);
					return; // This langSet is done
				}
				break;
			}
		}
	}

	private void processTig () throws XMLStreamException {
		String name;
		while ( reader.hasNext() ) {
			int eventType = reader.next();
			switch ( eventType ) {
			case XMLStreamConstants.START_ELEMENT:
				name = reader.getLocalName();
				if ( "term".equals(name) ) {
					processTerm();
				}
				break;
			case XMLStreamConstants.END_ELEMENT:
				name = reader.getLocalName();
				if ( "tig".equals(name) ) {
					return; // This tig is done
				}
				break;
			}
		}
	}

	private void processNtig () throws XMLStreamException {
		String name;
		while ( reader.hasNext() ) {
			int eventType = reader.next();
			switch ( eventType ) {
			case XMLStreamConstants.START_ELEMENT:
				name = reader.getLocalName();
				if ( "termGrp".equals(name) ) {
					processTermGrp();
				}
				break;
			case XMLStreamConstants.END_ELEMENT:
				name = reader.getLocalName();
				if ( "ntig".equals(name) ) {
					return; // This ntig is done
				}
				break;
			}
		}
	}

	private void processTermGrp () throws XMLStreamException {
		String name;
		while ( reader.hasNext() ) {
			int eventType = reader.next();
			switch ( eventType ) {
			case XMLStreamConstants.START_ELEMENT:
				name = reader.getLocalName();
				if ( "term".equals(name) ) {
					processTerm();
				}
				break;
			case XMLStreamConstants.END_ELEMENT:
				name = reader.getLocalName();
				if ( "termGrp".equals(name) ) {
					return; // This termGrp is done
				}
				break;
			}
		}
	}

	private void processTerm () throws XMLStreamException {
		// We do not read the <hi> element, but just get its content
		StringBuilder tmp = new StringBuilder();
		while ( reader.hasNext() ) {
			int eventType = reader.next();
			switch ( eventType ) {
			case XMLStreamConstants.END_ELEMENT:
				if ( "term".equals(reader.getLocalName()) ) {
					TermEntry term = new TermEntry(tmp.toString());
					lent.addTerm(term);
					return;
				}
				break;
			case XMLStreamConstants.CHARACTERS:
				tmp.append(reader.getText());
				break;
			}
		}
	}

}
