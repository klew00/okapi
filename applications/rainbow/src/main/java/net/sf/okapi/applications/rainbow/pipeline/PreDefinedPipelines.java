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
============================================================================*/

package net.sf.okapi.applications.rainbow.pipeline;

import java.util.HashMap;

public class PreDefinedPipelines {

	private HashMap<String, String> map;
	
	public PreDefinedPipelines () {
		map = new HashMap<String, String>();
		add("BatchTranslation");
		add("BOMConversion");
		add("CharListing");
		add("EncodingConversion");
		add("FormatConversion");
		add("ImageModification");
		add("ImportTM");
		add("LineBreakConversion");
		add("QualityCheck");
		add("RTFConversion");
		add("SnRWithFilter");
		add("SnRWithoutFilter");
		add("TermExtraction");
		add("TextRewriting");
		add("TranslationComparison");
		add("TranslationKitCreation");
		add("TranslationKitPostProcessing");
		add("URIConversion");
		add("XMLAnalysis");
		add("XMLCharactersFixing");
		add("XMLValidation");
		add("XSLTransform");
	}
	
	/**
	 * Adds a mapping. The added mapping uses the name in lowercase for the key
	 * and the cased name with the proper namespace for the class name.
	 * @param name the Correctly cased name of the predefined pipeline.
	 */
	private void add (String name) {
		map.put(name.toLowerCase(),
			"net.sf.okapi.applications.rainbow.pipeline." + name + "Pipeline");
	}
	
	/**
	 * Creates a predefined pipeline object for a given pre-defined pipeline name.
	 * @param name the name of the predefined pipeline to create (not case-sensitive)
	 * @return the predefined pipeline or null if it could not be created.
	 */
	public IPredefinedPipeline create (String name) {
		String className = map.get(name.toLowerCase());
		if ( className == null ) return null;
		IPredefinedPipeline pp;
		try {
			pp = (IPredefinedPipeline)Class.forName(className).newInstance();
		}
		catch ( Throwable e ) {
			return null; // TODO: warning
		}
		return pp;
	}

}
