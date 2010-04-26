/*===========================================================================
  Copyright (C) 2009-2010 by the Okapi Framework contributors
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.Range;
import net.sf.okapi.common.exceptions.OkapiFilterCreationException;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.xliff.XLIFFFilter;

// Temporary merging step. This should eventually be a normal step
public class XLIFFMergingStep {
	
	private final Logger logger = Logger.getLogger(getClass().getName());

	private IFilter filter;
	private IFilterWriter writer;
	private IFilterConfigurationMapper fcMapper;
	private XLIFFFilter xlfReader;
	private String xliffPath;
	private String outputPath;
	private String outputEncoding;
	private LocaleId trgLoc;
	
	public XLIFFMergingStep (IFilterConfigurationMapper fcMapper) {
		this.fcMapper = fcMapper;
	}
	
	public String getOutputPath () {
		return outputPath;
	}

	public void setOutputPath (String outputPath) {
		this.outputPath = outputPath;
	}

	public String getXliffPath () {
		return xliffPath;
	}

	public void setXliffPath (String xliffPath) {
		this.xliffPath = xliffPath;
	}

	public String getOutputEncoding () {
		return outputEncoding;
	}

	public void setOutputEncoding (String outputEncoding) {
		this.outputEncoding = outputEncoding;
	}

	/*
	 * For now, take all the info from argument rather than directly the XLIFF file.
	 */
	public void handleRawDocument (RawDocument skelRawDoc) {
		try {
			trgLoc = skelRawDoc.getTargetLocale();
			xlfReader = new XLIFFFilter();
			File f = new File(xliffPath);
			RawDocument xlfRawDoc = new RawDocument(f.toURI(), "UTF-8",
				skelRawDoc.getSourceLocale(), trgLoc);
			xlfReader.open(xlfRawDoc);
	
			filter = fcMapper.createFilter(skelRawDoc.getFilterConfigId(), filter);
			if ( filter == null ) {
				throw new OkapiFilterCreationException(String.format("Cannot create the filter or load the configuration for '%s'",
					skelRawDoc.getFilterConfigId()));
			}
			filter.open(skelRawDoc);

			writer = filter.createFilterWriter();
			writer.setOptions(trgLoc, outputEncoding);
			writer.setOutput(outputPath);
			
			Event event;
			while ( filter.hasNext() ) {
				event = filter.next();
				switch ( event.getEventType() ) {
				case TEXT_UNIT:
					processTextUnit((TextUnit)event.getResource());
				}
				writer.handleEvent(event);
			}
		}
		finally {
			if ( xlfReader != null ) xlfReader.close();
			if ( filter != null ) filter.close();
			if ( writer != null ) writer.close();
		}
	}

	/**
	 * Gets the next text unit in the XLIFF document.
	 * @return the next text unit or null.
	 */
	private TextUnit getTextUnitFromXLIFF () {
		Event event;
		while ( xlfReader.hasNext() ) {
			event = xlfReader.next();
			if ( event.getEventType() == EventType.TEXT_UNIT ) {
				return (TextUnit)event.getResource();
			}
		}
		return null;
	}
	
	private void processTextUnit (TextUnit tu) {
		// Skip the non-translatable
		// This means the translate attributes must be the same
		// in the original and the merging files
		if ( !tu.isTranslatable() ) return;

		// Get item from the package document
		// Skip also the read-only ones
		TextUnit tuFromTrans;
		while ( true ) {
			tuFromTrans = getTextUnitFromXLIFF();
			if ( tuFromTrans == null ) {
				// Problem: 
				logger.log(Level.WARNING,
					String.format("There is no more items in the package to merge with id=\"%s\".", tu.getId()));
				// Keep the source
				return;
			}
			if ( !tuFromTrans.isTranslatable() ) continue;
			else break; // Found next translatable (and likely translated) item
		}
		
		if ( !tu.getId().equals(tuFromTrans.getId()) ) {
			// Problem: different IDs
			logger.warning(String.format("ID mismatch: Original item id=\"%s\" package item id=\"%s\".",
				tu.getId(), tuFromTrans.getId()));
			return; // Use the source
		}

		if ( !tuFromTrans.hasTarget(trgLoc) ) {
			// No translation in package
			if ( !tu.getSource().isEmpty() ) {
				logger.log(Level.WARNING,
					String.format("Item id=\"%s\": No translation provided; using source instead.", tu.getId()));
				return; // Use the source
			}
		}

//		boolean isTransApproved = false;
//		Property prop = tuFromTrans.getTargetProperty(trgLang, Property.APPROVED);
//		if ( prop != null ) {
//			isTransApproved = prop.getValue().equals("yes");
//		}
//		if ( manifest.useApprovedOnly() && !isTransApproved ) {
//			// Not approved: use the source
//			logger.log(Level.WARNING,
//				String.format("Item id='%s': Target is not approved; using source instead.", tu.getId()));
//			return; // Use the source
//		}

		// Get the translated target
		TextContainer fromTrans = tuFromTrans.getTarget(trgLoc);
		if ( fromTrans == null ) {
			if ( tuFromTrans.getSource().isEmpty() ) return;
			// Else: Missing target in the XLIFF
			logger.log(Level.WARNING,
				String.format("Item id='%s': No target in XLIFF; using source instead.", tu.getId()));
			return; // Use the source
		}
		
		// Do we need to preserve the segmentation for merging (e.g. TTX case)
		boolean mergeAsSegments = (( tu.getMimeType() != null ) 
			&& ( tu.getMimeType().equals(MimeTypeMapper.TTX_MIME_TYPE) ));
		
		// Un-segment if needed (and remember the ranges if we will need to re-split after)
		// Merging the segments allow to check/transfer the codes at the text unit level
		ArrayList<Range> ranges = null;
		if ( mergeAsSegments ) ranges = new ArrayList<Range>();
		if ( !fromTrans.contentIsOneSegment() ) {
			fromTrans.joinAllSegments(ranges);
		}
		
		// Get the source (as a clone if we need to change the segments)
		TextContainer srcCont;
		if ( !tu.getSource().contentIsOneSegment() ) {
			srcCont  = tu.getSource().clone();
			srcCont.joinAllSegments();
		}
		else {
			srcCont = tu.getSource();
		}

		// Adjust the codes to use the appropriate ones
		List<Code> transCodes = transferCodes(fromTrans, srcCont, tu);

		// We create a new target if needed
		TextContainer trgCont = tu.createTarget(trgLoc, false, IResource.COPY_ALL);
		
		// Now set the target coded text and the target codes
		try {
			// Use first part because it not segmented here
			trgCont.getFirstPartContent().setCodedText(fromTrans.getCodedText(), transCodes, false);
			// Re-set the ranges on the translated entry
			if ( mergeAsSegments ) {
				trgCont.getSegments().create(ranges);
			}
		}
		catch ( RuntimeException e ) {
			logger.log(Level.SEVERE,
				String.format("Inline code error with item id=\"%s\".\n" + e.getLocalizedMessage(), tu.getId()));
			// Use the source instead, continue the merge
			tu.setTarget(trgLoc, tu.getSource());
		}
		
	}

	/*
	 * Checks the codes in the translated entry, uses the original data if there is
	 * none in the code coming from XLIFF, and generates a non-stopping error if
	 * a non-deletable code is missing. Assumes the containers are NOT segmented.
	 */
	private List<Code> transferCodes (TextContainer fromTrans,
		TextContainer srcCont, // Can be a clone of the original content
		TextUnit tu)
	{
		// We assume the container are NOT segmented
		List<Code> transCodes = fromTrans.getFirstPartContent().getCodes();
		List<Code> oriCodes = srcCont.getFirstPartContent().getCodes();
		
		// Check if we have at least one code
		if ( transCodes.size() == 0 ) {
			if ( oriCodes.size() == 0 ) return transCodes;
			// Else: fall thru and get missing codes errors
		}
		
		int[] oriIndices = new int[oriCodes.size()];
		for ( int i=0; i<oriIndices.length; i++ ) oriIndices[i] = i;
		int done = 0;
		
		Code transCode, oriCode;
		for ( int i=0; i<transCodes.size(); i++ ) {
			transCode = transCodes.get(i);
			transCode.setOuterData(null); // Remove XLIFF outer codes

			// Get the data from the original code (match on id)
			oriCode = null;
			for ( int j=0; j<oriIndices.length; j++ ) {
				if ( oriIndices[j] == -1) continue; // Used already
				if ( oriCodes.get(oriIndices[j]).getId() == transCode.getId() ) {
					oriCode = oriCodes.get(oriIndices[j]);
					oriIndices[j] = -1;
					done++;
					break;
				}
			}
			if ( oriCode == null ) { // Not found in original (extra in target)
				if (( transCode.getData() == null )
					|| ( transCode.getData().length() == 0 )) {
					// Leave it like that
					logger.warning(String.format("The extra target code id='%d' does not have corresponding data (item id='%s', name='%s')",
						transCode.getId(), tu.getId(), (tu.getName()==null ? "" : tu.getName())));
				}
			}
			else { // Get the data from the original
				if ( transCode.getOuterData() != null ) {
					transCode.setOuterData(oriCode.getOuterData());
				}
				else if (( transCode.getData() == null )
					|| ( transCode.getData().length() == 0 )) {
					transCode.setData(oriCode.getData());
				}
				transCode.setReferenceFlag(oriCode.hasReference());
			}
		}
		
		// If needed, check for missing codes in translation
		if ( oriCodes.size() > done ) {
			// Any index > -1 in source means it was was deleted in target
			for ( int i=0; i<oriIndices.length; i++ ) {
				if ( oriIndices[i] != -1 ) {
					Code code = oriCodes.get(oriIndices[i]);
					if ( !code.isDeleteable() ) {
						logger.warning(String.format("The code id='%d' (%s) is missing in target (item id='%s', name='%s')",
							code.getId(), code.getData(), tu.getId(), (tu.getName()==null ? "" : tu.getName())));
						logger.info("Source='"+tu.getSource().toString()+"'");
					}
				}
			}
		}
		
		return transCodes;
	}
	
//	/*
//	 * Checks the codes in the translated entry, uses the original data if there is
//	 * none in the code coming from XLIFF, and generates a non-stopping error if
//	 * a non-deletable code is missing.
//	 */
//	private List<Code> transferCodes (List<Code> transCodes,
//		List<Code> oriCodes,
//		TextUnit tu)
//	{
//		// Check if we have at least one code
//		if ( transCodes.size() == 0 ) {
//			if ( oriCodes.size() == 0 ) return transCodes;
//			// Else: fall thru and get missing codes errors
//		}
//		
//		int[] oriIndices = new int[oriCodes.size()];
//		for ( int i=0; i<oriIndices.length; i++ ) oriIndices[i] = i;
//		int done = 0;
//		boolean orderWarning;
//		
//		Code transCode, oriCode;
//		for ( int i=0; i<transCodes.size(); i++ ) {
//			transCode = transCodes.get(i);
//			transCode.setOuterData(null); // Remove XLIFF outer codes
//
//			// Get the data from the original code (match on id)
//			oriCode = null;
//			orderWarning = true;
//			for ( int j=0; j<oriIndices.length; j++ ) {
//				if ( oriIndices[j] == -1) continue; // Used already
//				if ( oriCodes.get(oriIndices[j]).getId() == transCode.getId() ) {
//					oriCode = oriCodes.get(oriIndices[j]);
//					oriIndices[j] = -1;
//					done++;
//					break;
//				}
//				if ( orderWarning ) {
//					logger.warning(String.format("The target code id='%d' has been moved (item id='%s', name='%s')",
//						transCode.getId(), tu.getId(), (tu.getName()==null ? "" : tu.getName())));
//					orderWarning = false; 
//				}
//			}
//			if ( oriCode == null ) { // Not found in original (extra in target)
//				if (( transCode.getData() == null )
//					|| ( transCode.getData().length() == 0 )) {
//					// Leave it like that
//					logger.warning(String.format("The extra target code id='%d' does not have corresponding data (item id='%s', name='%s')",
//						transCode.getId(), tu.getId(), (tu.getName()==null ? "" : tu.getName())));
//				}
//			}
//			else { // Get the data from the original
//				if ( transCode.getOuterData() != null ) {
//					transCode.setOuterData(oriCode.getOuterData());
//				}
//				else if (( transCode.getData() == null )
//					|| ( transCode.getData().length() == 0 )) {
//					transCode.setData(oriCode.getData());
//				}
//				transCode.setReferenceFlag(oriCode.hasReference());
//			}
//		}
//		
//		// If needed, check for missing codes in translation
//		if ( oriCodes.size() > done ) {
//			// Any index > -1 in source means it was was deleted in target
//			for ( int i=0; i<oriIndices.length; i++ ) {
//				if ( oriIndices[i] != -1 ) {
//					Code code = oriCodes.get(oriIndices[i]);
//					if ( !code.isDeleteable() ) {
//						logger.warning(String.format("The code id='%d' (%s) is missing in target (item id='%s', name='%s')",
//							code.getId(), code.getData(), tu.getId(), (tu.getName()==null ? "" : tu.getName())));
//						logger.info("Source='"+tu.getSource().toString()+"'");
//					}
//				}
//			}
//		}
//		
//		return transCodes;
//	}
	
}