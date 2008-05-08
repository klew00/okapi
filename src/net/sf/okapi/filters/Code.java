package net.sf.okapi.filters;

/**
 * Internal representation of an in-line code in a segment.
 */
class Code {
	
	String              data;
	String              label;
	int                 type;
	int                 id;

	public Code () {
	}

	public Code (int newType,
		String newLabel,
		String newData)
	{
		type = newType;
		label = newLabel;
		data = newData;
	}
}
