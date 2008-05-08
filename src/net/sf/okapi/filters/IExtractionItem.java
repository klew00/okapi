package net.sf.okapi.filters;

public interface IExtractionItem extends ISegment {

	boolean isSegmented ();

	/**
	 * Gets the number of segments in this object. The minimum value is 1.
	 * @return The number of segments in the object.
	 */
	int getSegmentCount ();
	
	ISegment[] getSegments ();
	
}
