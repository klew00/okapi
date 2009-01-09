package net.sf.okapi.applications.rainbow.packages.rtf;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.writer.ILayerProvider;

public class LayerProvider implements ILayerProvider {

	private CharsetEncoder outputEncoder;

	public String endCode () {
		return "}";
	}

	public String endInline () {
		return "}";
	}

	public String startCode () {
		return "{\\cs5\\f1\\cf15\\lang1024 ";
	}

	public String startInline () {
		return "{\\cs6\\f1\\cf6\\lang1024 ";
	}

	// context: 0=in text, 1=in skeleton, 2=in inline
	public String encode (String text, int context) {
		//TODO: change to better faster support
		return Util.escapeToRTF(text, true, context, outputEncoder);
	}

	public String encode (char value, int context) {
		//TODO: change to better faster support
		return Util.escapeToRTF(String.valueOf(value), true, context, outputEncoder);
	}

	public void setOptions (IParameters params,
		String encoding)
	{
		outputEncoder = Charset.forName(encoding).newEncoder();
	}

	// Not used for a layer provider
	public String toNative(String propertyName, String value) {
		return value; // No modification
	}

}
