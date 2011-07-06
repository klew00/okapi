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

package net.sf.okapi.lib.xliff;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.LinkedList;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sf.okapi.lib.xliff.XLIFFEvent.XLIFFEventType;

public class XLIFFReader {
	
	private final static String NS_XLIFF20 = "urn:oasis:names:tc:xliff:document:2.0";

	private XMLStreamReader reader;
	private boolean hasNext;
	private LinkedList<XLIFFEvent> queue;
	private Unit unit;
	private Segment segment;
	private Part ignorable;
	

	public void open (URI inputURI) {
		try {
			BufferedInputStream bis = new BufferedInputStream(inputURI.toURL().openStream());
			open(bis);
		}
		catch ( MalformedURLException e ) {
			throw new XLIFFReaderException("Cannot open the XLIFF stream. "+e.getMessage(), e);
		}
		catch ( IOException e ) {
			throw new XLIFFReaderException("Cannot open the XLIFF stream. "+e.getMessage(), e);
		}
	}
	
	public void open (String input) {
		open(new ByteArrayInputStream(input.getBytes()));
	}
	
	public void open (InputStream inputStream) {
		try {
			XMLInputFactory fact = XMLInputFactory.newInstance();
			fact.setProperty(XMLInputFactory.IS_COALESCING, true);
			fact.setProperty(XMLInputFactory.SUPPORT_DTD, false);

			reader = fact.createXMLStreamReader(inputStream);
			queue = new LinkedList<XLIFFEvent>();
			hasNext = true;
		}
		catch ( XMLStreamException e ) {
			throw new XLIFFReaderException("Cannot open the XLIFF stream. "+e.getMessage(), e);
		}
	}
	
	public void close () {
		try {
			if ( reader != null ) {
				reader.close();
				reader = null;
			}
			hasNext = false;
		}
		catch ( XMLStreamException e ) {
			throw new XLIFFReaderException("Closing error. "+e.getMessage(), e);
		}
	}

	public boolean hasNext () {
		return hasNext;
	}
	
	public XLIFFEvent next () {
		if ( queue.isEmpty() ) {
			readNext();
		}
		if ( queue.peek().getType() == XLIFFEventType.END_DOCUMENT ) {
			hasNext = false;
		}
		return queue.poll();
	}
	
	private void readNext () {
		try {
			String tmp;
			while ( reader.hasNext() ) {
				int type = reader.next();
				switch ( type ) {
				case XMLStreamReader.START_DOCUMENT:
					queue.add(new XLIFFEvent(XLIFFEventType.START_DOCUMENT, null));
					return;
				case XMLStreamReader.END_DOCUMENT:
					queue.add(new XLIFFEvent(XLIFFEventType.END_DOCUMENT, null));
					return;
				case XMLStreamReader.START_ELEMENT:
					tmp = reader.getLocalName();
					if ( tmp.equals("xliff") ) {
						processXliff();
						return;
					}
					if ( tmp.equals("unit") ) {
						processUnit();
						return;
					}
					break;
				case XMLStreamReader.END_ELEMENT:
					tmp = reader.getLocalName();
					break;
				}
			}
			hasNext = false;
		}
		catch ( XMLStreamException e ) {
			throw new XLIFFReaderException("Reading error. "+e.getMessage(), e);
		}
	}

	private void processXliff () {
		
		queue.add(new XLIFFEvent(XLIFFEventType.START_DOCUMENT, null));
	}

	private void processUnit ()
		throws XMLStreamException
	{
		// New unit
		String tmp = reader.getAttributeValue(null, "id");
		if ( Util.isNullOrEmpty(tmp) ) {
			throw new XLIFFReaderException("Missing or empty attribute 'id'");
		}
		unit = new Unit(tmp);
		
		while ( reader.hasNext() ) {
			switch ( reader.next() ) {
			case XMLStreamReader.START_ELEMENT:
				tmp = reader.getLocalName();
				if ( tmp.equals("segment") ) {
					processPart(true);
				}
				else if ( tmp.equals("ignorable") ) {
					processPart(false);
				}
				break;
				
			case XMLStreamReader.END_ELEMENT:
				tmp = reader.getLocalName();
				if ( tmp.equals("unit") ) { // End of this unit
					queue.add(new XLIFFEvent(XLIFFEventType.EXTRACTION_UNIT, unit));
					return;
				}
				break;
			}
		}
	}
	
	private void processPart (boolean isSegment)
		throws XMLStreamException
	{
		String tmp;
		if ( isSegment ) {
			segment = unit.appendNewSegment();
			tmp = reader.getAttributeValue(null, "id");
			if ( tmp != null ) {
				if ( tmp.isEmpty() ) {
					throw new XLIFFReaderException("Empty attribute 'id'");					
				}
				segment.setId(tmp);
			}
		}
		else {
			ignorable = unit.appendNewIgnorable();
		}

		while ( reader.hasNext() ) {
			switch ( reader.next() ) {
			case XMLStreamReader.END_ELEMENT:
				tmp = reader.getLocalName();
				if ( tmp.equals("segment") ) {
					return;
				}
				if ( tmp.equals("ignorable") ) {
					return;
				}
				break;
			}
		}
	}
}
