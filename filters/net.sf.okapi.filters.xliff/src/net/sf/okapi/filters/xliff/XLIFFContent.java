package net.sf.okapi.filters.xliff;

import java.util.List;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.CodeFragment;
import net.sf.okapi.common.resource.Container;
import net.sf.okapi.common.resource.IContainer;
import net.sf.okapi.common.resource.IFragment;

/**
 * Handles the conversion between a abstract content (IContainer)
 * and XLIFF notation.
 */
public class XLIFFContent {

	private String           codedText;
	private List<IFragment>  codes;
	
	public XLIFFContent () {
		codedText = "";
	}
	
	public XLIFFContent (IContainer content) {
		setContent(content);
	}
	
	public XLIFFContent setContent (IContainer content) {
		codedText = content.getCodedText();
		codes = content.getCodes();
		return this;
	}
	
	@Override
	public String toString () {
		return toString(1, true);
	}

	/**
	 * Generates an XLIFF string from the content.
	 * @param quoteMode 0=no quote escaped, 1=apos and quot, 2=#39 and quot,
	 * and 3=quot only.
	 * @param escapeGT True to always escape '>' to gt
	 * @return
	 */
	public String toString (int quoteMode,
		boolean escapeGT)
	{
		StringBuilder tmp = new StringBuilder();
		int index;
		int id;
		//TODO: change to index-based not id-based
		for ( int i=0; i<codedText.length(); i++ ) {
			switch ( codedText.codePointAt(i) ) {
			case IContainer.CODE_OPENING:
				index = Container.CtoI(codedText.charAt(++i));
				id = ((CodeFragment)codes.get(index)).getID();
				tmp.append(String.format("<bpt id=\"%d\">", id));
				tmp.append(Util.escapeToXML(codes.get(index).toString(), quoteMode, escapeGT));
				tmp.append("</bpt>");
				break;
			case IContainer.CODE_CLOSING:
				index = Container.CtoI(codedText.charAt(++i));
				id = ((CodeFragment)codes.get(index)).getID();
				tmp.append(String.format("<ept id=\"%d\">", id));
				tmp.append(Util.escapeToXML(codes.get(index).toString(), quoteMode, escapeGT));
				tmp.append("</ept>");
				break;
			case IContainer.CODE_ISOLATED:
				index = Container.CtoI(codedText.charAt(++i));
				id = ((CodeFragment)codes.get(index)).getID();
				tmp.append(String.format("<ph id=\"%d\">", id));
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
