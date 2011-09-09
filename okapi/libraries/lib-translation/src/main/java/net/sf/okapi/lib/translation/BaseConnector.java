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

package net.sf.okapi.lib.translation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.annotation.AltTranslationsAnnotation;
import net.sf.okapi.common.query.IQuery;
import net.sf.okapi.common.query.QueryResult;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextUnitUtil;

/**
 * Abstract implementation of the {@link IQuery} interface.
 */
public abstract class BaseConnector implements IQuery {

	private static final Logger LOGGER = Logger.getLogger(BaseConnector.class.getName());

	protected LocaleId srcLoc;
	protected String srcCode;
	protected LocaleId trgLoc;
	protected String trgCode;
	protected QueryResult result;
	protected int current = -1;
	private int weight;

	@Override
	public LocaleId getSourceLanguage () {
		return srcLoc;
	}

	@Override
	public LocaleId getTargetLanguage () {
		return trgLoc;
	}

	@Override
	public void setLanguages (LocaleId sourceLocale,
		LocaleId targetLocale)
	{
		// We keep a copy of the original locale so getSource/TargetLocale() return an unaltered value.
		srcLoc = sourceLocale;
		trgLoc = targetLocale;
		srcCode = toInternalCode(srcLoc);
		trgCode = toInternalCode(trgLoc);
	}

	@Override
	public boolean hasNext () {
		return (current > -1);
	}

	@Override
	public QueryResult next () {
		// By default supports only one result
		if ( current > -1 ) {
			current = -1;
			return result;
		}
		return null;
	}

	@Override
	public void clearAttributes () {
		// No attribute support by default
	}

	@Override
	public void removeAttribute (String name) {
		// No attribute support by default
	}

	@Override
	public void setAttribute (String name,
		String value)
	{
		// No attribute support by default
	}

	@Override
	public void setRootDirectory (String rootDir) {
		// No use of root directory by default
	}

	@Override
	public IParameters getParameters () {
		// No parameters by default
		return null;
	}

	@Override
	public void setParameters (IParameters params) {
		// No parameters by default
	}

	@Override
	public int getWeight () {
		return weight;
	}

	@Override
	public void setWeight (int weight) {
		this.weight = weight;
	}

	/**
	 * Slow default implementation using query!!
	 * Override to take advantage of servers batch API
	 */
	@Override
	public List<List<QueryResult>> batchQuery (List<TextFragment> fragments) {
		List<List<QueryResult>> queriesResults = new LinkedList<List<QueryResult>>();
		for (TextFragment fragment : fragments) {
			query(fragment);
			List<QueryResult> results = new LinkedList<QueryResult>();
			while (hasNext()) {
				QueryResult qr = next();
				results.add(qr);
			}
			queriesResults.add(results);
		}				
		return queriesResults;
	}

	@Override
	public void leverage (ITextUnit tu) {
		if (( tu == null ) || !tu.isTranslatable() ) {
			return; // No need to query
		}
		QueryResult qr;
		AltTranslationsAnnotation at = null;

		// We assume here that if there is a target content it match the segmentation of the source
		// Create an empty target (or return existing target)
		TextContainer trgCont = tu.createTarget(getTargetLanguage(), false, IResource.COPY_SEGMENTATION);
		ISegments trgSegs = trgCont.getSegments();
		
		// For each segment
		for ( Segment srcSeg : tu.getSource().getSegments() ) {
			// Skip segments with no text
			if ( !srcSeg.text.hasText(false) ) continue;
			
			// Do the query for the source segment
			query(srcSeg.text);
			// Then process each result
			while ( hasNext() ) {
				qr = next();
					
				// Adjust codes so that leveraged target matches the source
				// !!! We assume codes have been aligned - use TextFragment::alignCodeIds if needed
				TextUnitUtil.copySrcCodeDataToMatchingTrgCodes(srcSeg.text, qr.target, true, false, null, tu);

				if ( trgCont.hasBeenSegmented() ) {
					// Get corresponding target segment
					Segment ts = trgSegs.get(srcSeg.getId());
					if ( ts == null ) {
						ts = new Segment(srcSeg.id, new TextFragment(""));
						trgSegs.append(ts);
						LOGGER.warning(String.format("Cannot find matching target segment for source id: %s."
							+ "Creating a new target segment at the end of the target.", srcSeg.getId()));
					}
					at = TextUnitUtil.addAltTranslation(ts,
						qr.toAltTranslation(srcSeg.text, getSourceLanguage(), getTargetLanguage()));
				}
				else { // Add to the text container 
					at = TextUnitUtil.addAltTranslation(trgCont,
						qr.toAltTranslation(srcSeg.text, getSourceLanguage(), getTargetLanguage()));
				}
			}
			// Then sort AltTranslations into ranked order
			if ( at != null ) {
				at.sort();
			}
		}
	}
	
	/**
	 * Slow default implementation using leverage(TextUnit).
	 * Override in sub-class if you want a custom batchLeverage
	 * @param tus list of the text units to process.
	 */
	@Override	
	public void batchLeverage(List<ITextUnit> tus) {
		for (ITextUnit tu : tus) {
			leverage(tu);
		}
	}
	
	/**
	 * Call this method inside the overriding {@link #leverage(ITextUnit)} method
	 * of the derived class, if that class offers a fast {@link #batchQuery(List)} method.
	 * @param tu the text unit to leverage.
	 */
	protected void leverageUsingBatchQuery (ITextUnit tu) {
		if (( tu == null ) || !tu.isTranslatable() ) {
			return; // No need to query
		}
		List<ITextUnit> tuList = new ArrayList<ITextUnit>();
		tuList.add(tu);
		batchLeverageUsingBatchQuery(tuList);
	}
	
	/**
	 * Call this method inside the overriding {@link #batchLeverage(List)} method
	 * of the derived class, if that class offers a fast {@link #batchQuery(List)} method.
	 * @param tuList list of the text units to leverage.
	 */
	protected void batchLeverageUsingBatchQuery (List<ITextUnit> tuList) {
		// Gather all fragments in a list
		ArrayList<TextFragment> frags = new ArrayList<TextFragment>();
		for ( ITextUnit tu : tuList ) {
			// Skip non-translatable
			if ( !tu.isTranslatable() ) continue;
			// We assume here that if there is a target content it match the segmentation of the source
			// Create an empty target (or return existing target)
			for ( Segment srcSeg : tu.getSource().getSegments() ) {
				frags.add(srcSeg.text);
			}
		}
		
		// Do the query for the list of fragments
		List<List<QueryResult>> allResults = batchQuery(frags);

		// Place the translations
		int transIndex = -1;
		for ( ITextUnit tu : tuList ) {
			// Skip non-translatable
			if ( !tu.isTranslatable() ) continue;
			
			// Go through each segments in that text unit 
			TextContainer trgCont = tu.createTarget(getTargetLanguage(), false, IResource.COPY_SEGMENTATION);
			ISegments trgSegs = trgCont.getSegments();
			for ( Segment srcSeg : tu.getSource().getSegments() ) {
			
				// Get the list of translation for that segment
				List<QueryResult> resList = allResults.get(++transIndex);
				AltTranslationsAnnotation at = null;
				for ( QueryResult qr : resList ) {
					// Adjust codes so that leveraged target matches the source
					// !!! We assume codes have been aligned - use TextFragment::alignCodeIds if needed
					TextUnitUtil.copySrcCodeDataToMatchingTrgCodes(srcSeg.text, qr.target, true, false, null, tu);
					// Annotate
					if ( trgCont.hasBeenSegmented() ) {
						// Get corresponding target segment
						Segment ts = trgSegs.get(srcSeg.getId());
						if ( ts == null ) {
							ts = new Segment(srcSeg.id, new TextFragment(""));
							trgSegs.append(ts);
						}
						at = TextUnitUtil.addAltTranslation(ts,
							qr.toAltTranslation(srcSeg.text, getSourceLanguage(), getTargetLanguage()));
					}
					else { // Add to the text container 
						at = TextUnitUtil.addAltTranslation(trgCont,
							qr.toAltTranslation(srcSeg.text, getSourceLanguage(), getTargetLanguage()));
					}
				}
				// Then sort AltTranslations into ranked order
				if ( at != null ) {
					at.sort();
				}

			}
		}
		
	}
	
//	@Override
//	public void leverage (TextUnit tu) {
//		if (( tu == null ) || !tu.isTranslatable() ) {
//			return;
//		}
//
//		QueryResult qr;
//		AltTranslationsAnnotation at = null;
//
//		// For each segment
//		for ( Segment seg : tu.getSource().getSegments() ) {
//			// Query if needed
//			if ( seg.text.hasText(false) ) {
//				query(seg.text);
//				while ( hasNext() ) {
//					qr = next();
//					
//					// Set weight based on connector weight
//					qr.weight = getWeight();
//					
//					// Adjust codes so that leveraged target matches the source
//					TextUnitUtil.adjustTargetCodes(seg.text, qr.target, true, false, null, tu);
//
//					// Create an empty target (or return existing target) if we need to to hold the annotations
//					TextContainer tc = tu.createTarget(getTargetLanguage(), false, IResource.CREATE_EMPTY);
//
//					if ( tc.hasBeenSegmented() ) {
//						// Get corresponding target segment
//						ISegments segments = tc.getSegments();
//						Segment ts = segments.get(seg.getId());
//
//						if ( ts == null ) {
//							ts = new Segment(seg.id, new TextFragment(""));
//							tc.append(ts);
//							LOGGER.warning(String.format("Cannot find matching target segment for source id: %s."
//								+ "Creating a new target segment at the end of the target.", seg.id));
//						}
//
//						at = TextUnitUtil.addAltTranslation(ts,
//							qr.toAltTranslation(seg.text, getSourceLanguage(), getTargetLanguage()));
//					}
//					else {
//						// paragraph
//						at = TextUnitUtil.addAltTranslation(tc,
//							qr.toAltTranslation(seg.text, getSourceLanguage(), getTargetLanguage()));
//					}
//				}
//				// sort AltTranslations into ranked order
//				if ( at != null ) {
//					at.sort();
//				}
//			}
//		}
//	}
	
	/**
	 * Converts a locale identifier to the internal string value for a language/locale code for this connector.
	 * By default, this simply returns the string of the given LocaleId.
	 * 
	 * @param locId
	 *            the locale identifier to convert.
	 * @return the internal string code for language/locale code for this connector.
	 */
	protected String toInternalCode (LocaleId locId) {
		return locId.toString();
	}

}
