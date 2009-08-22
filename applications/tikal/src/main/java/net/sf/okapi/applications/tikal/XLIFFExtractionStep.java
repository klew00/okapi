/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.applications.tikal;

import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.resource.RawDocument;

// Temporary extraction step. This should eventually be a normal step
public class XLIFFExtractionStep {
	
	private IFilter filter;
	private IFilterConfigurationMapper fcMapper;
	private XLIFFWriter writer;
	private String outputPath;
	
	public XLIFFExtractionStep (IFilterConfigurationMapper fcMapper) {
		this.fcMapper = fcMapper;
	}
	
	public String getOutputPath () {
		return outputPath;
	}

	public void setOutputPath (String outputPath) {
		this.outputPath = outputPath;
	}

	public void handleRawDocument (RawDocument rd) {
		try {
			filter = fcMapper.createFilter(rd.getFilterConfigId(), filter);
			if ( filter == null ) {
				throw new RuntimeException(String.format(
					"Cannot create a filter for the configuration '%s'", rd.getFilterConfigId()));
			}
	
			filter.open(rd);
			
			writer = new XLIFFWriter();
			writer.setOptions(rd.getTargetLanguage(), rd.getEncoding());
			
			if ( outputPath == null ) {
				outputPath = rd.getInputURI().getPath();
				outputPath = outputPath + ".xlf";
			}
			writer.setOutput(outputPath);
			
			while ( filter.hasNext() ) {
				writer.handleEvent(filter.next());
			}
		}
		finally {
			if ( filter != null ) filter.close();
			if ( writer != null ) writer.close();
		}
	}

}
