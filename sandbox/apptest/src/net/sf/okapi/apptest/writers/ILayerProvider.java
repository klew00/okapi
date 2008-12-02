package net.sf.okapi.apptest.writers;

import net.sf.okapi.apptest.common.IEncoder;

public interface ILayerProvider extends IEncoder {

	public String startCode ();
	
	public String endCode ();
	
	public String startInline ();
	
	public String endInline ();
	
}
