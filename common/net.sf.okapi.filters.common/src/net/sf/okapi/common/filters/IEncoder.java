package net.sf.okapi.common.filters;

public interface IEncoder {

	String encode (String text, int context);
	
	String encode (char value, int context);
	
}
