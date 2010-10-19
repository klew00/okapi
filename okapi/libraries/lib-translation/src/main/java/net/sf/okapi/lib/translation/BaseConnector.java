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

package net.sf.okapi.lib.translation;

import java.util.List;
import java.util.logging.Logger;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.annotation.AltTranslationsAnnotation;
import net.sf.okapi.common.exceptions.OkapiNotImplementedException;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextUnitUtil;

/**
 * Abstract implementation of the {@link IQuery} interface.
 */
public abstract class BaseConnector implements IQuery {
	private static final Logger LOGGER = Logger.getLogger(BaseConnector.class.getName());

	protected String srcCode;
	protected String trgCode;
	protected QueryResult result;
	protected int current = -1;
	private int weight;

	@Override
	public LocaleId getSourceLanguage() {
		return LocaleId.fromString(srcCode);
	}

	@Override
	public LocaleId getTargetLanguage() {
		return LocaleId.fromString(trgCode);
	}

	@Override
	public void setLanguages(LocaleId sourceLocale, LocaleId targetLocale) {
		srcCode = toInternalCode(sourceLocale);
		trgCode = toInternalCode(targetLocale);
	}

	@Override
	public boolean hasNext() {
		return (current > -1);
	}

	@Override
	public QueryResult next() {
		// By default supports only one result
		if (current > -1) {
			current = -1;
			return result;
		}
		return null;
	}

	@Override
	public void clearAttributes() {
		// No attribute support by default
	}

	@Override
	public void removeAttribute(String name) {
		// No attribute support by default
	}

	@Override
	public void setAttribute(String name, String value) {
		// No attribute support by default
	}

	@Override
	public void setRootDirectory(String rootDir) {
		// No use of root directory by default
	}

	@Override
	public IParameters getParameters() {
		// No parameters by default
		return null;
	}

	@Override
	public void setParameters(IParameters params) {
		// No parameters by default
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	@Override
	public List<List<QueryResult>> batchQuery(List<TextFragment> fragments) {
		throw new OkapiNotImplementedException();
	}

	@Override
	public void leverage(TextUnit tu) {
		if (tu == null || !tu.isTranslatable()) {
			return;
		}

		QueryResult qr;
		AltTranslationsAnnotation at = null;

		// For each segment
		for (Segment seg : tu.getSource().getSegments()) {
			// Query if needed
			if (seg.text.hasText(false)) {
				query(seg.text);
				while (hasNext()) {
					qr = next();
					
					// set weight based on connector weight
					qr.weight = getWeight();
					
					// adjust codes so that leveraged target matches the source
					TextUnitUtil.adjustTargetCodes(seg.text, qr.target, true, false, null, tu);

					// create an empty target (or return existing target) if we need to to hold the annotations
					TextContainer tc = tu.createTarget(getTargetLanguage(), false, IResource.CREATE_EMPTY);

					if (tc.hasBeenSegmented()) {
						// get corresponding target segment
						ISegments segments = tc.getSegments();
						Segment ts = segments.get(seg.getId());

						if (ts == null) {
							ts = new Segment(seg.id, new TextFragment(""));
							tc.append(ts);
							LOGGER.warning("Can't find matching target segment for source id: "
									+ seg.id + ". Creating new target segment and appending it to target container.");
						}

						at = TextUnitUtil.addAltTranslation(ts,
								qr.toAltTranslation(seg.text, getSourceLanguage(), getTargetLanguage()));
					} else {
						// paragraph
						at = TextUnitUtil.addAltTranslation(tc,
								qr.toAltTranslation(seg.text, getSourceLanguage(), getTargetLanguage()));
					}
				}
				// sort AltTranslations into ranked order
				if (at != null) {
					at.sort();
				}
			}
		}
	}
	
	/**
	 * Converts a locale identifier to the internal string value for a language/locale code for this connector. By
	 * default, this simply return the string of the given LocaleId.
	 * 
	 * @param locId
	 *            the locale identifier to convert.
	 * @return the internal string code for language/locale code for this connector.
	 */
	protected String toInternalCode(LocaleId locId) {
		return locId.toString();
	}
}
