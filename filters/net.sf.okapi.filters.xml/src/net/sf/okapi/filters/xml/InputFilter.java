/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel                                          */
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

package net.sf.okapi.filters.xml;

import java.io.InputStream;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.filters.IInputFilter;
import net.sf.okapi.common.pipeline.IResourceBuilder;

public class InputFilter implements IInputFilter {

	private InputStream      input;
	private IResourceBuilder output;
	private XMLReader        reader;
	

	public InputFilter () {
		reader = new XMLReader();
	}
	
	public void close () {
	}

	public IParameters getParameters () {
		return null; //TODO reader.resource.getParameters();
	}

	public void initialize (InputStream input,
		String name,
		String filterSettings,
		String encoding,
		String sourceLanguage,
		String targetLanguage)
	{
		close();
		this.input = input;
		reader.resource.setName(name);
		reader.resource.setFilterSettings(filterSettings);
		reader.resource.setSourceEncoding(encoding);
		reader.resource.setSourceLanguage(sourceLanguage);
		reader.resource.setTargetLanguage(targetLanguage);
		//TODO: Get the real target/output encoding from parameters
		reader.resource.setTargetEncoding(encoding);
	}

	public boolean supports (int feature) {
		switch ( feature ) {
		case FEATURE_TEXTBASED:
		//case FEATURE_BILINGUAL:
			return true;
		default:
			return false;
		}
	}

	public void process () {
		try {
			close();
			Resource res = (Resource)reader.resource;
			reader.open(input, res.getName());
			
			// Get started
			output.startResource(reader.resource);
			
			// Process
			int n;
			do {
				//TODO: groups
				switch ( (n = reader.read()) ) {
				case XMLReader.RESULT_STARTTRANSUNIT:
					// Do nothing: Both events to be sent when end trans-unit comes
					// We do this because of the condition based on state attribute.
					break;
				case XMLReader.RESULT_ENDTRANSUNIT:
					output.startExtractionItem(reader.getItem());
					output.endExtractionItem(reader.getItem());
					break;
				}
			}
			while ( n > XMLReader.RESULT_ENDINPUT );

			output.endResource(reader.resource);
		}
		finally {
			close();
		}
	}

	public void setOutput (IResourceBuilder builder) {
		this.output = builder;
	}

	public void cancel () {
		// TODO implement cancel()
	}
}
