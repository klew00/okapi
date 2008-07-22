package net.sf.okapi.common.resource;

import java.util.List;

public interface IContainer extends IContent {

	public IContent addPart (boolean isSegment);
	
	public IContent addPart (boolean isSegment, String text);
	
	public int getLastCodeID ();
	
	public int setLastCodeID (int value);

	public void joinParts();

	public List<IContent> getSegments ();
	
	public List<IContent> getParts ();
	
	public IContent getSegment (int index);
	
	public void setSegment (int index, IContent content);

	public void removeSegment (int index);

	public IContent getPart (int index);
	
	public void setPart (int index, IContent content);

	public void removePart (int index);

}
