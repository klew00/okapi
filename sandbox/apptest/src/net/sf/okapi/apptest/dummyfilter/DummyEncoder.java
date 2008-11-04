package net.sf.okapi.apptest.dummyfilter;

import net.sf.okapi.apptest.filters.IEncoder;
import net.sf.okapi.common.Util;

public class DummyEncoder implements IEncoder {

	public String encode (String text) {
		return Util.escapeToXML(text, 1, false);
	}
	
	public String encode (char value) {
		switch ( value ) {
		case '<':
			return "&lt";
		case '\"':
			return "&quot;";
		case '\'':
			return "&apos;";
		case '&':
			return "&amp;";
		default:
			return String.valueOf(value);
		}
	}
	
}
