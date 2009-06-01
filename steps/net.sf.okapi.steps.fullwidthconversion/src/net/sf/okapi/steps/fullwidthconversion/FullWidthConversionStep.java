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

package net.sf.okapi.steps.fullwidthconversion;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;

public class FullWidthConversionStep extends BasePipelineStep {

	private Parameters params;
	private String trgLang;

	public FullWidthConversionStep () {
		params = new Parameters();
	}
	
	public String getName () {
		return "Full Width Conversion";
	}

	public String getDescription () {
		return "Convert the text units content of a document to or from full-width characters (zenkaku).";
	}

	@Override
	public IParameters getParameters () {
		return params;
	}

	@Override
	protected void handleStartBatchItem (Event event) {
		trgLang = getContext().getTargetLanguage(0);
	}
	
	@Override
	protected void handleTextUnit (Event event) {
		TextUnit tu = (TextUnit)event.getResource();
		// Skip non-translatable
		if ( !tu.isTranslatable() ) return;

		TextContainer tc = tu.createTarget(trgLang, false, IResource.COPY_ALL);
		String text = tc.getCodedText();
		StringBuilder sb = new StringBuilder(text);

		int ch;
		if ( params.toHalfWidth ) {
			for ( int i=0; i<text.length(); i++ ) {
				ch = text.charAt(i);
				if ( TextFragment.isMarker((char)ch) ) {
					i++; // Skip codes
					continue;
				}
				// ASCII
				if (( ch >= 0xFF01 ) && ( ch <= 0xFF5E )) {
					sb.setCharAt(i, (char)(ch-0xFEE0));
					continue;
				}
				// Stop here for ASCII only
				if ( params.asciiOnly ) continue;
				// Hangul
				if (( ch > 0x3131 ) && ( ch <= 0x314E )) {
					sb.setCharAt(i, (char)(ch+0xCE70));
					continue;
				}
				switch ( ch ) {
				// Katakana
				case 0x3002: sb.setCharAt(i, (char)0xFF61); break;
				case 0x300C: sb.setCharAt(i, (char)0xFF62); break;
				case 0x300D: sb.setCharAt(i, (char)0xFF63); break;
				case 0x3001: sb.setCharAt(i, (char)0xFF64); break;
				case 0x30FB: sb.setCharAt(i, (char)0xFF65); break;
				case 0x30F2: sb.setCharAt(i, (char)0xFF66); break;
				case 0x30A1: sb.setCharAt(i, (char)0xFF67); break;
				case 0x30A3: sb.setCharAt(i, (char)0xFF68); break;
				case 0x30A5: sb.setCharAt(i, (char)0xFF69); break;
				case 0x30A7: sb.setCharAt(i, (char)0xFF6A); break;
				case 0x30A9: sb.setCharAt(i, (char)0xFF6B); break;
				case 0x30E3: sb.setCharAt(i, (char)0xFF6C); break;
				case 0x30E5: sb.setCharAt(i, (char)0xFF6D); break;
				case 0x30E7: sb.setCharAt(i, (char)0xFF6E); break;
				case 0x30C3: sb.setCharAt(i, (char)0xFF6F); break;
				case 0x30FC: sb.setCharAt(i, (char)0xFF70); break;
				case 0x30A2: sb.setCharAt(i, (char)0xFF71); break;
				case 0x30A4: sb.setCharAt(i, (char)0xFF72); break;
				case 0x30A6: sb.setCharAt(i, (char)0xFF73); break;
				case 0x30A8: sb.setCharAt(i, (char)0xFF74); break;
				case 0x30AA: sb.setCharAt(i, (char)0xFF75); break;
				case 0x30AB: sb.setCharAt(i, (char)0xFF76); break;
				case 0x30AD: sb.setCharAt(i, (char)0xFF77); break;
				case 0x30AF: sb.setCharAt(i, (char)0xFF78); break;
				case 0x30B1: sb.setCharAt(i, (char)0xFF79); break;
				case 0x30B3: sb.setCharAt(i, (char)0xFF7A); break;
				case 0x30B5: sb.setCharAt(i, (char)0xFF7B); break;
				case 0x30B7: sb.setCharAt(i, (char)0xFF7C); break;
				case 0x30B9: sb.setCharAt(i, (char)0xFF7D); break;
				case 0x30BB: sb.setCharAt(i, (char)0xFF7E); break;
				case 0x30BD: sb.setCharAt(i, (char)0xFF7F); break;
				case 0x30BF: sb.setCharAt(i, (char)0xFF80); break;
				case 0x30C1: sb.setCharAt(i, (char)0xFF81); break;
				case 0x30C4: sb.setCharAt(i, (char)0xFF82); break;
				case 0x30C6: sb.setCharAt(i, (char)0xFF83); break;
				case 0x30C8: sb.setCharAt(i, (char)0xFF84); break;
				case 0x30CA: sb.setCharAt(i, (char)0xFF85); break;
				case 0x30CB: sb.setCharAt(i, (char)0xFF86); break;
				case 0x30CC: sb.setCharAt(i, (char)0xFF87); break;
				case 0x30CD: sb.setCharAt(i, (char)0xFF88); break;
				case 0x30CE: sb.setCharAt(i, (char)0xFF89); break;
				case 0x30CF: sb.setCharAt(i, (char)0xFF8A); break;
				case 0x30D2: sb.setCharAt(i, (char)0xFF8B); break;
				case 0x30D5: sb.setCharAt(i, (char)0xFF8C); break;
				case 0x30D8: sb.setCharAt(i, (char)0xFF8D); break;
				case 0x30DB: sb.setCharAt(i, (char)0xFF8E); break;
				case 0x30DE: sb.setCharAt(i, (char)0xFF8F); break;
				case 0x30DF: sb.setCharAt(i, (char)0xFF90); break;
				case 0x30E0: sb.setCharAt(i, (char)0xFF91); break;
				case 0x30E1: sb.setCharAt(i, (char)0xFF92); break;
				case 0x30E2: sb.setCharAt(i, (char)0xFF93); break;
				case 0x30E4: sb.setCharAt(i, (char)0xFF94); break;
				case 0x30E6: sb.setCharAt(i, (char)0xFF95); break;
				case 0x30E8: sb.setCharAt(i, (char)0xFF96); break;
				case 0x30E9: sb.setCharAt(i, (char)0xFF97); break;
				case 0x30EA: sb.setCharAt(i, (char)0xFF98); break;
				case 0x30EB: sb.setCharAt(i, (char)0xFF99); break;
				case 0x30EC: sb.setCharAt(i, (char)0xFF9A); break;
				case 0x30ED: sb.setCharAt(i, (char)0xFF9B); break;
				case 0x30EF: sb.setCharAt(i, (char)0xFF9C); break;
				case 0x30F3: sb.setCharAt(i, (char)0xFF9D); break;
				case 0x3099: sb.setCharAt(i, (char)0xFF9E); break;
				case 0x309A: sb.setCharAt(i, (char)0xFF9F); break;
				// Hangul
				case 0x3164: sb.setCharAt(i, (char)0xFFA0); break;
				case 0x3161: sb.setCharAt(i, (char)0xFFDA); break;
				case 0x3162: sb.setCharAt(i, (char)0xFFDB); break;
				case 0x3163: sb.setCharAt(i, (char)0xFFDC); break;
				// Others
				case 0x2502: sb.setCharAt(i, (char)0xFFE8); break;
				case 0x2190: sb.setCharAt(i, (char)0xFFE9); break;
				case 0x2191: sb.setCharAt(i, (char)0xFFEA); break;
				case 0x2192: sb.setCharAt(i, (char)0xFFEB); break;
				case 0x2193: sb.setCharAt(i, (char)0xFFEC); break;
				case 0x25A0: sb.setCharAt(i, (char)0xFFED); break;
				case 0x25CB: sb.setCharAt(i, (char)0xFFEE); break;
				}
			}
		}
		else { // To full-width
			for ( int i=0; i<text.length(); i++ ) {
				ch = text.charAt(i);
				if ( TextFragment.isMarker((char)ch) ) {
					i++; // Skip codes
					continue;
				}
				// ASCII
				if (( ch >= 0x0021 ) && ( ch <= 0x007E )) {
					sb.setCharAt(i, (char)(ch+0xFEE0));
					continue;
				}
				// Stop here for ASCII only
				if ( params.asciiOnly ) continue;
				// Hangul
				if (( ch > 0xFFA1 ) && ( ch <= 0xFFBE )) {
					sb.setCharAt(i, (char)(ch-0xCE70));
					continue;
				}
				switch ( ch ) {
				// Katakana
				case 0xFF61: sb.setCharAt(i, (char)0x3002); break;
				case 0xFF62: sb.setCharAt(i, (char)0x300C); break;
				case 0xFF63: sb.setCharAt(i, (char)0x300D); break;
				case 0xFF64: sb.setCharAt(i, (char)0x3001); break;
				case 0xFF65: sb.setCharAt(i, (char)0x30FB); break;
				case 0xFF66: sb.setCharAt(i, (char)0x30F2); break;
				case 0xFF67: sb.setCharAt(i, (char)0x30A1); break;
				case 0xFF68: sb.setCharAt(i, (char)0x30A3); break;
				case 0xFF69: sb.setCharAt(i, (char)0x30A5); break;
				case 0xFF6A: sb.setCharAt(i, (char)0x30A7); break;
				case 0xFF6B: sb.setCharAt(i, (char)0x30A9); break;
				case 0xFF6C: sb.setCharAt(i, (char)0x30E3); break;
				case 0xFF6D: sb.setCharAt(i, (char)0x30E5); break;
				case 0xFF6E: sb.setCharAt(i, (char)0x30E7); break;
				case 0xFF6F: sb.setCharAt(i, (char)0x30C3); break;
				case 0xFF70: sb.setCharAt(i, (char)0x30FC); break;
				case 0xFF71: sb.setCharAt(i, (char)0x30A2); break;
				case 0xFF72: sb.setCharAt(i, (char)0x30A4); break;
				case 0xFF73: sb.setCharAt(i, (char)0x30A6); break;
				case 0xFF74: sb.setCharAt(i, (char)0x30A8); break;
				case 0xFF75: sb.setCharAt(i, (char)0x30AA); break;
				case 0xFF76: sb.setCharAt(i, (char)0x30AB); break;
				case 0xFF77: sb.setCharAt(i, (char)0x30AD); break;
				case 0xFF78: sb.setCharAt(i, (char)0x30AF); break;
				case 0xFF79: sb.setCharAt(i, (char)0x30B1); break;
				case 0xFF7A: sb.setCharAt(i, (char)0x30B3); break;
				case 0xFF7B: sb.setCharAt(i, (char)0x30B5); break;
				case 0xFF7C: sb.setCharAt(i, (char)0x30B7); break;
				case 0xFF7D: sb.setCharAt(i, (char)0x30B9); break;
				case 0xFF7E: sb.setCharAt(i, (char)0x30BB); break;
				case 0xFF7F: sb.setCharAt(i, (char)0x30BD); break;
				case 0xFF80: sb.setCharAt(i, (char)0x30BF); break;
				case 0xFF81: sb.setCharAt(i, (char)0x30C1); break;
				case 0xFF82: sb.setCharAt(i, (char)0x30C4); break;
				case 0xFF83: sb.setCharAt(i, (char)0x30C6); break;
				case 0xFF84: sb.setCharAt(i, (char)0x30C8); break;
				case 0xFF85: sb.setCharAt(i, (char)0x30CA); break;
				case 0xFF86: sb.setCharAt(i, (char)0x30CB); break;
				case 0xFF87: sb.setCharAt(i, (char)0x30CC); break;
				case 0xFF88: sb.setCharAt(i, (char)0x30CD); break;
				case 0xFF89: sb.setCharAt(i, (char)0x30CE); break;
				case 0xFF8A: sb.setCharAt(i, (char)0x30CF); break;
				case 0xFF8B: sb.setCharAt(i, (char)0x30D2); break;
				case 0xFF8C: sb.setCharAt(i, (char)0x30D5); break;
				case 0xFF8D: sb.setCharAt(i, (char)0x30D8); break;
				case 0xFF8E: sb.setCharAt(i, (char)0x30DB); break;
				case 0xFF8F: sb.setCharAt(i, (char)0x30DE); break;
				case 0xFF90: sb.setCharAt(i, (char)0x30DF); break;
				case 0xFF91: sb.setCharAt(i, (char)0x30E0); break;
				case 0xFF92: sb.setCharAt(i, (char)0x30E1); break;
				case 0xFF93: sb.setCharAt(i, (char)0x30E2); break;
				case 0xFF94: sb.setCharAt(i, (char)0x30E4); break;
				case 0xFF95: sb.setCharAt(i, (char)0x30E6); break;
				case 0xFF96: sb.setCharAt(i, (char)0x30E8); break;
				case 0xFF97: sb.setCharAt(i, (char)0x30E9); break;
				case 0xFF98: sb.setCharAt(i, (char)0x30EA); break;
				case 0xFF99: sb.setCharAt(i, (char)0x30EB); break;
				case 0xFF9A: sb.setCharAt(i, (char)0x30EC); break;
				case 0xFF9B: sb.setCharAt(i, (char)0x30ED); break;
				case 0xFF9C: sb.setCharAt(i, (char)0x30EF); break;
				case 0xFF9D: sb.setCharAt(i, (char)0x30F3); break;
				case 0xFF9E: sb.setCharAt(i, (char)0x3099); break;
				case 0xFF9F: sb.setCharAt(i, (char)0x309A); break;
				// Hangul
				case 0xFFA0: sb.setCharAt(i, (char)0x3164); break;
				case 0xFFDA: sb.setCharAt(i, (char)0x3161); break;
				case 0xFFDB: sb.setCharAt(i, (char)0x3162); break;
				case 0xFFDC: sb.setCharAt(i, (char)0x3163); break;
				// Others
				case 0xFFE8: sb.setCharAt(i, (char)0x2502); break;
				case 0xFFE9: sb.setCharAt(i, (char)0x2190); break;
				case 0xFFEA: sb.setCharAt(i, (char)0x2191); break;
				case 0xFFEB: sb.setCharAt(i, (char)0x2192); break;
				case 0xFFEC: sb.setCharAt(i, (char)0x2193); break;
				case 0xFFED: sb.setCharAt(i, (char)0x25A0); break;
				case 0xFFEE: sb.setCharAt(i, (char)0x25CB); break;
				}
			}
		}

		// Set back the modified text
		tc.setCodedText(sb.toString());
	}

}
