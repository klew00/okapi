package net.sf.okapi.common.resource;

import java.util.List;

public interface IContainer extends IPart {

	public static final int CODE_OPENING    = 0xE101;
	public static final int CODE_CLOSING    = 0xE102;
	public static final int CODE_ISOLATED   = 0xE103;
	public static final int CHARBASE        = 0xE200;


	public void append (IContainer container);
	
	public void append (IPart part);
	
	public void setContent (IPart part);
	
	public List<IPart> getParts ();
	
	public List<IPart> getSegments ();

	public void joinParts ();
	
	public boolean isSegmented ();
	
}
