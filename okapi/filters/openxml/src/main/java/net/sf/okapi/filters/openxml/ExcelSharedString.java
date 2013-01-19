package net.sf.okapi.filters.openxml;

/**
 * Used in FixXML to get position of tags in a document.
 * 
 */
public class ExcelSharedString
{
	boolean bEncountered; // whether or not this shared string has been seen in sheets so far
	boolean bTranslatable; // whether or not this shared string will be translated
	int nIndex=-1; // index of the shared string that has the opposite translation status
	               // default -1 means there is no case where this string has the opposite bTranslatable status
	String s; // the shared string

	public ExcelSharedString(boolean bEncountered, boolean bTranslatable, int nIndex, String s)
	{
		this.bEncountered = bEncountered;
		this.bTranslatable = bTranslatable;
		this.nIndex = nIndex;
		this.s = s;
	}
	public boolean getBEncountered()
	{
		return(bEncountered);
	}
	public void setBEncountered(boolean bEncountered)
	{
		this.bEncountered = bEncountered;
	}
	public boolean getBTranslatable()
	{
		return(bTranslatable);
	}
	public void setBTranslatable(boolean bTranslatable)
	{
		this.bTranslatable = bTranslatable;
	}
	public int getNIndex()
	{
		return(nIndex);
	}
	public void setNIndex(int nIndex)
	{
		this.nIndex = nIndex;
	}
	public String getS()
	{
		return(s);
	}
	public void setS(String s)
	{
		this.s = s;
	}
}