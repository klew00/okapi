package net.sf.okapi.applications.rainbow.lib;

import java.util.List;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment;

/**
 * Handles the conversion between a abstract content (IContainer)
 * and TMX notation.
 */
public class TMXContent {

	private String      codedText;
	private List<Code>  codes;
	
	public TMXContent () {
		codedText = "";
	}
	
	public TMXContent (TextFragment content) {
		setContent(content);
	}
	
	public TMXContent setContent (TextFragment content) {
		codedText = content.getCodedText();
		codes = content.getCodes();
		return this;
	}
	
	@Override
	public String toString () {
		return toString(1, true);
	}

	/**
	 * Generates a TMX string from the content.
	 * @param quoteMode 0=no quote escaped, 1=apos and quot, 2=#39 and quot,
	 * and 3=quot only.
	 * @param escapeGT True to always escape '>' to gt.
	 * @return
	 */
	public String toString (int quoteMode,
		boolean escapeGT)
	{
		StringBuilder tmp = new StringBuilder();
		int index;
		int id;
		for ( int i=0; i<codedText.length(); i++ ) {
			switch ( codedText.codePointAt(i) ) {
			case TextFragment.MARKER_OPENING:
				index = TextFragment.toIndex(codedText.charAt(++i));
				id = codes.get(index).getID();
				tmp.append(String.format("<bpt i=\"%d\">", id));
				tmp.append(Util.escapeToXML(codes.get(index).toString(), quoteMode, escapeGT));
				tmp.append("</bpt>");
				break;
			case TextFragment.MARKER_CLOSING:
				index = TextFragment.toIndex(codedText.charAt(++i));
				id = codes.get(index).getID();
				tmp.append(String.format("<ept i=\"%d\">", id));
				tmp.append(Util.escapeToXML(codes.get(index).toString(), quoteMode, escapeGT));
				tmp.append("</ept>");
				break;
			case TextFragment.MARKER_ISOLATED:
				index = TextFragment.toIndex(codedText.charAt(++i));
				id = codes.get(index).getID();
				tmp.append(String.format("<ph i=\"%d\">", id));
				tmp.append(Util.escapeToXML(codes.get(index).toString(), quoteMode, escapeGT));
				tmp.append("</ph>");
				break;
			case '>':
				if ( escapeGT ) tmp.append("&gt;");
				else {
					if (( i > 0 ) && ( codedText.charAt(i-1) == ']' )) 
						tmp.append("&gt;");
					else
						tmp.append('>');
				}
				break;
			case '<':
				tmp.append("&lt;");
				break;
			case '&':
				tmp.append("&amp;");
				break;
			case '"':
				if ( quoteMode > 0 ) tmp.append("&quot;");
				else tmp.append('"');
				break;
			case '\'':
				switch ( quoteMode ) {
				case 1:
					tmp.append("&apos;");
					break;
				case 2:
					tmp.append("&#39;");
					break;
				default:
					tmp.append(codedText.charAt(i));
					break;
				}
				break;
			default:
				tmp.append(codedText.charAt(i));
				break;
			}
		}
		return tmp.toString();
	}
}
