/*===========================================================================
  Copyright (C) 2010-2011 by the Okapi Framework contributors
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

package net.sf.okapi.lib.omegat;

import java.awt.Dialog;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.InvalidContentException;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextFragment.TagType;

import org.omegat.filters2.FilterContext;
import org.omegat.filters2.IAlignCallback;
import org.omegat.filters2.IParseCallback;
import org.omegat.filters2.ITranslateCallback;
import org.omegat.filters2.TranslationException;

abstract class AbstractOkapiFilter implements org.omegat.filters2.IFilter {

	protected IParseCallback parseCallback;
    protected ITranslateCallback translateCallback;
    protected IAlignCallback alignCallback;
    protected String defaultInEncoding = "UTF-8";
    protected String defaultOutEncoding = "UTF-8";
    protected String supportedExtensions;

	private final Pattern patternOpening = Pattern.compile("\\<g(\\d+?)\\>");
	private final Pattern patternClosing = Pattern.compile("\\</g(\\d+?)\\>");
	private final Pattern patternIsolated = Pattern.compile("\\<x(\\d+?)/\\>");
	private final Pattern patternIsolatedB = Pattern.compile("\\<b(\\d+?)/\\>");
	private final Pattern patternIsolatedE = Pattern.compile("\\<e(\\d+?)/\\>");

	private String filterConfigId;
	private FilterConfigurationMapper fcMapper;
	private String name;

	protected void initialize (String name,
		String filterClassName,
		String filterConfigId,
		String supportedExtensions)
	{
		fcMapper = new FilterConfigurationMapper();
		fcMapper.addConfigurations(filterClassName);
		this.filterConfigId = filterConfigId;
		this.name = name;
		this.supportedExtensions = supportedExtensions;
	}

	@Override
	public void alignFile(File inFile,
		File outFile,
		Map<String, String> config,
		FilterContext context,
		IAlignCallback callback)
		throws Exception
    {
		// Do nothing
    	//throw new RuntimeException("Not implemented");
	}

	@Override
	public Map<String, String> changeOptions (Dialog parent,
		Map<String, String> config)
	{
		return null;
	}

	@Override
	public String getFileFormatName () {
		return name;
	}

	@Override
	public String getFuzzyMark () {
		return "fuzzy";
	}

	@Override
	public String getHint () {
		return "";
	}

	@Override
	public boolean hasOptions () {
		return false;
	}

	@Override
	public boolean isFileSupported (File inFile,
		Map<String, String> config,
		FilterContext context)
	{
		String ext = Util.getExtension(inFile.getAbsolutePath());
		if ( Util.isEmpty(ext) ) return false;
		return supportedExtensions.contains(ext);
	}

	@Override
	public boolean isSourceEncodingVariable () {
		return false;
	}

	@Override
	public boolean isTargetEncodingVariable () {
		return true;
	}

	@Override
	public void parseFile (File inFile,
		Map<String, String> config,
		FilterContext context,
		IParseCallback callback)
		throws Exception
	{
		parseCallback = callback;
		translateCallback = null;
		alignCallback = null;
        try {
            processFile(inFile, null, context);
        }
        finally {
        	parseCallback = null;
        }
	}

	@Override
	public void translateFile (File inFile,
		File outFile,
		Map<String, String> config,
		FilterContext context,
		ITranslateCallback callback)
		throws Exception
	{
		parseCallback = null;
		translateCallback = callback;
		alignCallback = null;
        try {
            processFile(inFile, outFile, context);
        }
        finally {
        	translateCallback = null;
        }
	}

    protected void processFile (File inFile,
       	File outFile,
		FilterContext context)
    	throws IOException, TranslationException
    {
    	// Get the source and target locales
    	LocaleId srcLoc = LocaleId.ENGLISH;
    	if ( context.getSourceLang() != null ) {
    		srcLoc = LocaleId.fromString(context.getSourceLang().getLanguage());
    	}
    	LocaleId trgLoc = LocaleId.FRENCH;
    	if ( context.getTargetLang() != null ) {
    		trgLoc = LocaleId.fromString(context.getTargetLang().getLanguage());
    	}

    	// Get the source and target encoding
    	String inEncoding = context.getInEncoding();
    	if ( inEncoding == null ) { // auto
    		inEncoding = defaultInEncoding;
    	}
    	String outEncoding = context.getOutEncoding();
    	if ( outEncoding == null ) { // auto
    		outEncoding = defaultOutEncoding;
    	}
    	
		Segment trgSeg;
		IFilter filter = null;
		IFilterWriter writer = null;
		try {
			filter  = fcMapper.createFilter(filterConfigId);
			if ( filter == null ){
				throw new RuntimeException(String.format("Could not create a filter for the configuration '%s'.", filterConfigId));
			}
		
			RawDocument rd = new RawDocument(inFile.toURI(), inEncoding, srcLoc, trgLoc);
			filter.open(rd);
			
			if ( outFile != null ) {
				writer = filter.createFilterWriter();
				writer.setOptions(trgLoc, outEncoding);
				writer.setOutput(outFile.getAbsolutePath());
			}
			
			while ( filter.hasNext() ) {
				Event event = filter.next();
				if ( event.isTextUnit() ) {
					ITextUnit tu = event.getTextUnit();
					// Skip non-translatable entries
					if ( !tu.isTranslatable() ) continue;
					
					// Get target and properties
					boolean isFuzzy = false;
					TextContainer tc = null;
					if ( tu.hasTarget(trgLoc) ) {
						tc = tu.getTarget(trgLoc);
						// Check the approved property for fuzzy flag
						if ( tc.hasProperty(Property.APPROVED) ) {
							isFuzzy = !tc.getProperty(Property.APPROVED).getValue().equals("yes");
						}
						if ( tc.isEmpty() ) tc = null;
					}

					trgSeg = null;
					ISegments trgSegs = null;
					if ( tc == null ) {
						// If there is no target we create one empty but segmented
						// It'll be used for merging back
						tc = tu.createTarget(trgLoc, false, IResource.COPY_SEGMENTATION);
					}
					trgSegs = tc.getSegments();
					
					// Go through the segments
					for ( Segment srcSeg : tu.getSource().getSegments() ) {
						
						// Get the corresponding target (could be null)
						trgSeg = trgSegs.get(srcSeg.id);
						
						// Exchange the data with OmegaT
						if ( writer == null ) {
							// Populate OmegaT
							if (( trgSeg == null ) || trgSeg.text.isEmpty() ) {
								// No existing translation
								parseCallback.addEntry(
									tu.getId()+"_"+srcSeg.id,
									toOmegat(srcSeg.text),
									null,
									false,
									null, this);
							}
							else {
								// There is an existing translation
								parseCallback.addEntry(
									tu.getId()+"_"+srcSeg.id,
									toOmegat(srcSeg.text),
									toOmegat(trgSeg.text),
									isFuzzy,
									null, this);
							}
						}
						else { // Translation coming back from OmegaT
							String trans = translateCallback.getTranslation(
								tu.getId()+"_"+srcSeg.id, toOmegat(srcSeg.text));
							// Put it back in the Okapi resource
							if ( !Util.isEmpty(trans) ) {
								if ( trgSeg == null ) {
									// No corresponding target: That should not really occur
								}
								else {
									// Else: make sure we have the inline codes in the target fragment
									trgSeg.text = srcSeg.text.clone();
									// And create the new target content
									fromOmegat(trans, trgSeg.text);
								}
							}
							else { // Empty translation from OmegaT
								trgSeg.text = new TextFragment(); 
							}
						}
					}
				}
				// Write out the translation if needed
				if ( writer != null ) {
					writer.handleEvent(event);
				}
			}
		}
		finally {
			if ( filter != null ) filter.close();
			if ( writer != null ) writer.close();
		}
    	
    }
	
	private String toOmegat (TextFragment tf) {
		// Use directly the coded text if there is no codes
		if ( !tf.hasCode() ) {
			return tf.getCodedText();
		}

		// Else: represent the code the OmegaT way
		StringBuilder tmp = new StringBuilder();
		String ctext = tf.getCodedText();
		List<Code> codes = tf.getCodes();
		Code code;
		int index;
		
		for ( int i = 0; i<ctext.length(); i++ ) {
			if ( TextFragment.isMarker(ctext.charAt(i)) ) {
				index = TextFragment.toIndex(ctext.charAt(++i));
				code = codes.get(index);
				switch ( code.getTagType() ) {
				case OPENING:
					tmp.append(String.format("<g%d>", code.getId()));
					break;
				case CLOSING:
					tmp.append(String.format("</g%d>", code.getId()));
					break;
				case PLACEHOLDER:
					if ( code.getTagType() == TagType.OPENING ) {
						tmp.append(String.format("<b%d/>", code.getId()));
					}
					else if ( code.getTagType() == TagType.CLOSING ) {
						tmp.append(String.format("<e%d/>", code.getId()));
					}
					else {
						tmp.append(String.format("<x%d/>", code.getId()));
					}
					break;
				}
			}
			else {
				tmp.append(ctext.charAt(i));
			}
		}
		return tmp.toString();
	}

	private void fromOmegat (String text,
		TextFragment frag)
	{
		// Case with no in-line codes
		if ( !frag.hasCode() && ( text.indexOf('<') == -1 )) {
			frag.setCodedText(text);
			return;
		}
		
		// Otherwise: we have in-line codes
		StringBuilder tmp = new StringBuilder(text);
		
		int n;
		int start = 0;
		int diff = 0;
		int index;
		Matcher m = patternOpening.matcher(text);
		while ( m.find(start) ) {
			n = m.start();
			index = frag.getIndex(Integer.valueOf(m.group(1)));
			if ( index == -1 )
				throw new InvalidContentException(String.format("Invalid code: '%s'", m.group()));
			tmp.replace(n+diff, (n+diff)+m.group().length(), String.format("%c%c",
				(char)TextFragment.MARKER_OPENING, TextFragment.toChar(index)));
			diff += (2-m.group().length());
			start = n+m.group().length();
		}
		start = diff = 0;
		m = patternClosing.matcher(tmp.toString());
		while ( m.find(start) ) {
			n = m.start();
			index = frag.getIndexForClosing(Integer.valueOf(m.group(1)));
			frag.getCode(index).setId(-1); // For re-balancing
			if ( index == -1 )
				throw new InvalidContentException(String.format("Invalid code: '%s'", m.group()));
			tmp.replace(n+diff, (n+diff)+m.group().length(), String.format("%c%c",
				(char)TextFragment.MARKER_CLOSING, TextFragment.toChar(index)));
			diff += (2-m.group().length());
			start = n+m.group().length();
		}
		start = diff = 0;
		m = patternIsolated.matcher(tmp.toString());
		while ( m.find(start) ) {
			n = m.start();
			index = frag.getIndex(Integer.valueOf(m.group(1)));
			if ( index == -1 )
				throw new InvalidContentException(String.format("Invalid code: '%s'", m.group()));
			tmp.replace(n+diff, (n+diff)+m.group().length(), String.format("%c%c",
				(char)TextFragment.MARKER_ISOLATED, TextFragment.toChar(index)));
			diff += (2-m.group().length());
			start = n+m.group().length();
		}
		start = diff = 0;
		m = patternIsolatedB.matcher(tmp.toString());
		while ( m.find(start) ) {
			n = m.start();
			index = frag.getIndex(Integer.valueOf(m.group(1)));
			if ( index == -1 )
				throw new InvalidContentException(String.format("Invalid code: '%s'", m.group()));
			tmp.replace(n+diff, (n+diff)+m.group().length(), String.format("%c%c",
				(char)TextFragment.MARKER_ISOLATED, TextFragment.toChar(index)));
			diff += (2-m.group().length());
			start = n+m.group().length();
		}
		start = diff = 0;
		m = patternIsolatedE.matcher(tmp.toString());
		while ( m.find(start) ) {
			n = m.start();
			index = frag.getIndexForClosing(Integer.valueOf(m.group(1)));
			frag.getCode(index).setId(-1); // For re-balancing
			if ( index == -1 )
				throw new InvalidContentException(String.format("Invalid code: '%s'", m.group()));
			tmp.replace(n+diff, (n+diff)+m.group().length(), String.format("%c%c",
				(char)TextFragment.MARKER_ISOLATED, TextFragment.toChar(index)));
			diff += (2-m.group().length());
			start = n+m.group().length();
		}
		
		// Allow deletion of codes
		frag.setCodedText(tmp.toString(), false);
	}

}
