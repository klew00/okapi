package net.sf.okapi.common.resource;

import java.util.List;

public interface IContainer extends List<IContent>, IContent {

	public IContent addContent (boolean isSegment);
	
	public IContent addContent (boolean isSegment, String text);
	
	// Maybe specific for flat model
	public int getLastCodeID ();
	
	// Maybe specific for flat model
	public int setLastCodeID (int value);

	public void joinAll ();
	
	public void joinSegments (int first, int last);

	public List<IContent> getSegments ();
	
	public IContent getSegment (int index);
	
	public void setSegment (int index, IContent content);

	public void removeSegment (int index);

}
