package net.sf.okapi.lib.translation;

public interface ITMQuery extends IQuery {

	public void setMaximumHits (int max);
	
	public void setThreshold (int threshold);

}
