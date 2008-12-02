package net.sf.okapi.apptest.common;

public interface IEncoder {

	String encode (String text, int context);
	
	String encode (char value, int context);
	
}
