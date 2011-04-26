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

package net.sf.okapi.steps.xliffkit.reader;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.Range;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.TextContainer;

public class TextUnitMerger {
	
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	private boolean useApprovedOnly;
	private boolean updateApprovedFlag;
	private LocaleId trgLoc;

	public void mergeTargets(ITextUnit tu, ITextUnit tuFromTrans) {		
		// Skip the non-translatable
		// This means the translate attributes must be the same
		// in the original and the merging files
		if ( !tu.isTranslatable() ) return;

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

		// Process the "approved" property
		boolean isTransApproved = false;
		Property prop = tuFromTrans.getTargetProperty(trgLoc, Property.APPROVED);
		if ( prop != null ) {
			isTransApproved = prop.getValue().equals("yes");
		}
		if (useApprovedOnly && !isTransApproved ) {
			// Not approved: use the source
			logger.log(Level.WARNING,
				String.format("Item id='%s': Target is not approved; using source instead.", tu.getId()));
			return; // Use the source
		}

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
		boolean mergeAsSegments = false;
		if ( tu.getMimeType() != null ) { 
			if ( tu.getMimeType().equals(MimeTypeMapper.TTX_MIME_TYPE)
				|| tu.getMimeType().equals(MimeTypeMapper.XLIFF_MIME_TYPE) ) {
				mergeAsSegments = true;
			}
		}
		
		// Un-segment if needed (and remember the ranges if we will need to re-split after)
		// Merging the segments allow to check/transfer the codes at the text unit level
		List<Range> ranges = null;
		List<Range> srcRanges = null;
		if ( mergeAsSegments ) {
			ranges = new ArrayList<Range>();
			srcRanges = tuFromTrans.getSourceSegments().getRanges(); //.saveCurrentSourceSegmentation();
		}
		if ( !fromTrans.contentIsOneSegment() ) {
			fromTrans.getSegments().joinAll(ranges);
		}
		
		// Get the source (as a clone if we need to change the segments)
		TextContainer srcCont;
		if ( !tu.getSource().contentIsOneSegment() ) {
			srcCont  = tu.getSource().clone();
			srcCont.getSegments().joinAll();
		}
		else {
			srcCont = tu.getSource();
		}

		// Adjust the codes to use the appropriate ones
		List<Code> transCodes = transferCodes(fromTrans, srcCont, tu);
		
		// We create a new target if needed
		TextContainer trgCont = tu.createTarget(trgLoc, false, IResource.COPY_ALL);
		if ( !trgCont.contentIsOneSegment() ) {
			trgCont.getSegments().joinAll();
		}

		// Update 'approved' flag is requested
		if (updateApprovedFlag) {
			prop = trgCont.getProperty(Property.APPROVED);
			if ( prop == null ) {
				prop = trgCont.setProperty(new Property(Property.APPROVED, "no"));
			}
			//TODO: Option to set the flag based on isTransApproved
			prop.setValue("yes");
		}

		// Now set the target coded text and the target codes
		try {
			// trgCont is un-segmented at this point and will be re-segmented if needed
			trgCont.getFirstContent().setCodedText(fromTrans.getCodedText(), transCodes, false);
			// Re-set the ranges on the translated entry
			if ( mergeAsSegments ) {
				trgCont.getSegments().create(ranges);
				tu.getSource().getSegments().create(srcRanges);
				//tu.setSourceSegmentationForTarget(trgLoc, srcRanges);
				//tu.synchronizeSourceSegmentation(trgLoc);
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
	 * a non-deletable code is missing.
	 */
	private List<Code> transferCodes (TextContainer fromTrans,
		TextContainer srcCont, // Can be a clone of the original content
		ITextUnit tu)
	{
		List<Code> transCodes = fromTrans.getFirstContent().getCodes();
		List<Code> oriCodes = srcCont.getFirstContent().getCodes();
		
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
				if ( !transCode.hasData() ) {
					// Leave it like that
					logger.warning(String.format("The extra target code id='%d' does not have corresponding data (item id='%s', name='%s')",
						transCode.getId(), tu.getId(), (tu.getName()==null ? "" : tu.getName())));
				}
			}
			else { // Get the data from the original
				if ( oriCode.hasOuterData() ) {
					transCode.setOuterData(oriCode.getOuterData());
				}
				else if ( !transCode.hasData() ) {
					transCode.setData(oriCode.getData());
				}
				transCode.setReferenceFlag(oriCode.hasReference());
				//transCode.setType(oriCode.getType()); // sv
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

	public boolean isUseApprovedOnly() {
		return useApprovedOnly;
	}

	public void setUseApprovedOnly(boolean useApprovedOnly) {
		this.useApprovedOnly = useApprovedOnly;
	}

	public boolean isUpdateApprovedFlag() {
		return updateApprovedFlag;
	}

	public void setUpdateApprovedFlag(boolean updateApprovedFlag) {
		this.updateApprovedFlag = updateApprovedFlag;
	}

	public LocaleId getTrgLoc() {
		return trgLoc;
	}

	public void setTrgLoc(LocaleId trgLoc) {
		this.trgLoc = trgLoc;
	}

}
