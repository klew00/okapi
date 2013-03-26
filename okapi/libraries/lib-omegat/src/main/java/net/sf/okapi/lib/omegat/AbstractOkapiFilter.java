/*===========================================================================
  Copyright (C) 2010-2013 by the Okapi Framework contributors
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
import java.util.Map;
import java.util.ResourceBundle;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.GenericAnnotations;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
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
    private Boolean processOmegaT2_5 = null;

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

            // 2.5 version also needs to link previous/next
            if ( requirePrevNextFields() ) {
                // parsing - need to link prev/next
                parseCallback.linkPrevNextSegments();
            }
            
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
        	// For 2.5
        	if ( doProcessFor2_5() ) {
        		translateCallback.setPass(1);
        	}
            processFile(inFile, outFile, context);

        	// For 2.5 only too
            if ( requirePrevNextFields() ) {
            	translateCallback.linkPrevNextSegments();
            	translateCallback.setPass(2);
                processFile(inFile, outFile, context);
            }
        }
        finally {
        	translateCallback = null;
        }
	}

    /**
     * Method can be overridden for return true. It means what two-pass parsing and translating will be
     * processed and previous/next segments will be linked.
     */
    protected boolean requirePrevNextFields () {
        return false; // Default: all Okapi filters have IDs
        // Take doProcessFor2_5() into account if this changes
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
    		if ( filterConfigId.startsWith("okf_ttx") ) outEncoding = "UTF-16";
    		else outEncoding = defaultOutEncoding;
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
							String comments = processComments(tu, srcSeg, trgSeg);
							if (( trgSeg == null ) || trgSeg.text.isEmpty() ) {
								// No existing translation
								parseCallback.addEntry(
									tu.getId()+"_"+srcSeg.id,
									toOmegat(srcSeg.text),
									null,
									false, comments, this);
							}
							else {
								// There is an existing translation
								parseCallback.addEntry(
									tu.getId()+"_"+srcSeg.id,
									toOmegat(srcSeg.text),
									toOmegat(trgSeg.text),
									isFuzzy, comments, this);
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
    
    /**
     * Gather annotations to display them as comments
     * @param tu the text unit to process.
     * @param srcSeg the source segment to process.
     * @param trgSeg the corresponding target segment (can be null).
     * @return The comments or null if there are none.
     */
    protected String processComments (ITextUnit tu,
    	Segment srcSeg,
    	Segment trgSeg)
    {
    	TextFragment tf = srcSeg.getContent();
    	if ( !tf.hasCode() ) return null;
    	
    	String ct = tf.getCodedText();
		StringBuilder tmp = new StringBuilder();
    	for ( int i=0; i<ct.length(); i++ ) {
    		if ( TextFragment.isMarker(ct.charAt(i)) ) {
    			Code c = tf.getCode(ct.charAt(++i));
    			if ( c.getTagType() == TagType.CLOSING ) continue;
    			GenericAnnotations anns = c.getGenericAnnotations();
    			if ( anns == null ) continue;
    			
    			// else: process the annotations
    			if ( tmp == null ) tmp = new StringBuilder();

    			// Terminology
    			GenericAnnotation ann = anns.getFirstAnnotation(GenericAnnotationType.TERM);
    			if ( ann != null ) {
    				if ( tmp.length() > 0 ) tmp.append("\n");
    				tmp.append("Term: \'"+getSpan(ct, i+1, c, tf)+"'");
    				String info = ann.getString(GenericAnnotationType.TERM_INFO);
    				if ( info != null ) tmp.append(" "+info);
    				Double conf = ann.getDouble(GenericAnnotationType.TERM_CONFIDENCE);
    				if ( conf != null ) tmp.append(" Confidence="+Util.formatDouble(conf));
    			}
    			
    			// Text Analysis
    			ann = anns.getFirstAnnotation(GenericAnnotationType.TA);
    			if ( ann != null ) {
    				if ( tmp.length() > 0 ) tmp.append("\n");
    				tmp.append("TA: \'"+getSpan(ct, i+1, c, tf)+"'");
    				String str = ann.getString(GenericAnnotationType.TA_CLASS);
    				if ( str != null ) tmp.append(" Class:"+str);
    				str = ann.getString(GenericAnnotationType.TA_IDENT);
    				if ( str != null ) tmp.append(" Ident:"+str);
    				str = ann.getString(GenericAnnotationType.TA_SOURCE);
    				if ( str != null ) tmp.append(" Src:"+str);
    				Double conf = ann.getDouble(GenericAnnotationType.TERM_CONFIDENCE);
    				if ( conf != null ) tmp.append(" Confidence="+Util.formatDouble(conf));
    			}

    			// Localization Quality Issue
    			//TODO
    		}
    	}
    	return ( Util.isEmpty(tmp) ? null : tmp.toString());
    }
	
    /**
     * Gets the text-only span of content for a given open/close code.
     * @param ct the coded text to lookup.
     * @param start the start index in the coded text.
     * @param code the start code. 
     * @param tf the text fragment corresponding to the coded text.
     * @return the span of content (stripped of it's inline codes) that is between
     * the given start code and its ending code.
     */
    private String getSpan (String ct,
    	int start,
    	Code code,
    	TextFragment tf)
    {
    	// Get the index of the closing code
    	int index = tf.getIndexForClosing(code.getId());
    	// If none: return empty span
    	if ( index == -1 ) return "";
    	// Else: look for the corresponding code
    	for ( int i=start; i<ct.length(); i++ ) {
    		if ( TextFragment.isMarker(ct.charAt(i)) ) {
    			if ( index == TextFragment.toIndex(ct.charAt(++i)) ) {
    				// Ending found
    				StringBuilder tmp = new StringBuilder(ct.substring(start, i-1));
    				for ( i=0; i<tmp.length(); i++ ) {
    					if ( TextFragment.isMarker(tmp.charAt(i)) ) {
    						tmp.delete(i, i+1);
    						i--; // To offset the next +1
    					}
    				}
    				return tmp.toString();
    			}
    		}
    	}
    	return ""; // Just in case
    }
    
	private String toOmegat (TextFragment tf) {
		
		return GenericContent.fromFragmentToLetterCoded(tf, true);
//		
//		// Use directly the coded text if there is no codes
//		if ( !tf.hasCode() ) {
//			return tf.getCodedText();
//		}
//
//		// Else: represent the code the OmegaT way
//		StringBuilder tmp = new StringBuilder();
//		String ctext = tf.getCodedText();
//		List<Code> codes = tf.getCodes();
//		Code code;
//		int index;
//		
//		for ( int i = 0; i<ctext.length(); i++ ) {
//			if ( TextFragment.isMarker(ctext.charAt(i)) ) {
//				index = TextFragment.toIndex(ctext.charAt(++i));
//				code = codes.get(index);
//				switch ( code.getTagType() ) {
//				case OPENING:
//					tmp.append(String.format("<g%d>", code.getId()));
//					break;
//				case CLOSING:
//					tmp.append(String.format("</g%d>", code.getId()));
//					break;
//				case PLACEHOLDER:
//					if ( code.getTagType() == TagType.OPENING ) {
//						tmp.append(String.format("<b%d/>", code.getId()));
//					}
//					else if ( code.getTagType() == TagType.CLOSING ) {
//						tmp.append(String.format("<e%d/>", code.getId()));
//					}
//					else {
//						tmp.append(String.format("<x%d/>", code.getId()));
//					}
//					break;
//				}
//			}
//			else {
//				tmp.append(ctext.charAt(i));
//			}
//		}
//		return tmp.toString();
	}

	private void fromOmegat (String text,
		TextFragment frag)
	{
		GenericContent.fromLetterCodedToFragment(text, frag, true, true);
//		
//		// Case with no in-line codes
//		if ( !frag.hasCode() && ( text.indexOf('<') == -1 )) {
//			frag.setCodedText(text);
//			return;
//		}
//		
//		// Otherwise: we have in-line codes
//		StringBuilder tmp = new StringBuilder(text);
//		
//		int n;
//		int start = 0;
//		int diff = 0;
//		int index;
//		Matcher m = patternOpening.matcher(text);
//		while ( m.find(start) ) {
//			n = m.start();
//			index = frag.getIndex(Integer.valueOf(m.group(1)));
//			if ( index == -1 )
//				throw new InvalidContentException(String.format("Invalid code: '%s'", m.group()));
//			tmp.replace(n+diff, (n+diff)+m.group().length(), String.format("%c%c",
//				(char)TextFragment.MARKER_OPENING, TextFragment.toChar(index)));
//			diff += (2-m.group().length());
//			start = n+m.group().length();
//		}
//		start = diff = 0;
//		m = patternClosing.matcher(tmp.toString());
//		while ( m.find(start) ) {
//			n = m.start();
//			index = frag.getIndexForClosing(Integer.valueOf(m.group(1)));
//			frag.getCode(index).setId(-1); // For re-balancing
//			if ( index == -1 )
//				throw new InvalidContentException(String.format("Invalid code: '%s'", m.group()));
//			tmp.replace(n+diff, (n+diff)+m.group().length(), String.format("%c%c",
//				(char)TextFragment.MARKER_CLOSING, TextFragment.toChar(index)));
//			diff += (2-m.group().length());
//			start = n+m.group().length();
//		}
//		start = diff = 0;
//		m = patternIsolated.matcher(tmp.toString());
//		while ( m.find(start) ) {
//			n = m.start();
//			index = frag.getIndex(Integer.valueOf(m.group(1)));
//			if ( index == -1 )
//				throw new InvalidContentException(String.format("Invalid code: '%s'", m.group()));
//			tmp.replace(n+diff, (n+diff)+m.group().length(), String.format("%c%c",
//				(char)TextFragment.MARKER_ISOLATED, TextFragment.toChar(index)));
//			diff += (2-m.group().length());
//			start = n+m.group().length();
//		}
//		start = diff = 0;
//		m = patternIsolatedB.matcher(tmp.toString());
//		while ( m.find(start) ) {
//			n = m.start();
//			index = frag.getIndex(Integer.valueOf(m.group(1)));
//			if ( index == -1 )
//				throw new InvalidContentException(String.format("Invalid code: '%s'", m.group()));
//			tmp.replace(n+diff, (n+diff)+m.group().length(), String.format("%c%c",
//				(char)TextFragment.MARKER_ISOLATED, TextFragment.toChar(index)));
//			diff += (2-m.group().length());
//			start = n+m.group().length();
//		}
//		start = diff = 0;
//		m = patternIsolatedE.matcher(tmp.toString());
//		while ( m.find(start) ) {
//			n = m.start();
//			index = frag.getIndexForClosing(Integer.valueOf(m.group(1)));
//			frag.getCode(index).setId(-1); // For re-balancing
//			if ( index == -1 )
//				throw new InvalidContentException(String.format("Invalid code: '%s'", m.group()));
//			tmp.replace(n+diff, (n+diff)+m.group().length(), String.format("%c%c",
//				(char)TextFragment.MARKER_ISOLATED, TextFragment.toChar(index)));
//			diff += (2-m.group().length());
//			start = n+m.group().length();
//		}
//		
//		// Allow deletion of codes
//		frag.setCodedText(tmp.toString(), false);
	}

	private boolean doProcessFor2_5 () {
		if ( processOmegaT2_5 == null ) {
			try {
				String tmp = ResourceBundle.getBundle("org/omegat/Version").getString("version");
				processOmegaT2_5 = (tmp.compareTo("2.5.0") >= 0);
			}
			catch ( Throwable e ) {
				processOmegaT2_5 = false;
			}
		}
		return processOmegaT2_5;
	}
}
