package net.sf.okapi.common.resource;

import java.util.List;

public interface IContainer extends List<IContent>, IContent {

	public IContent addPart (boolean isSegment);
	
	public IContent addPart (boolean isSegment, String text);
	
	public int getLastCodeID ();
	
	public int setLastCodeID (int value);

	public void joinParts();

	public List<IContent> getSegments ();
	
	public IContent getSegment (int index);
	
	public void setSegment (int index, IContent content);

	public void removeSegment (int index);

}
