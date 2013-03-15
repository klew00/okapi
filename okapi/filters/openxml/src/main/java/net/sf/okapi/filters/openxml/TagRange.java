package net.sf.okapi.filters.openxml;

/**
 * Used in FixXML to get position of tags in a document.
 * 
 */
public class TagRange
{
	private static final int STARTTAG=1;
	private static final int STANDALONETAG=2;
	private static final int ENDTAG=3;
	private static final int CONFUSEDTAG=4; // </.../>
	String tag;
	long lBegin;
	long lEnd;
	int iTagType;
	boolean bAddFakeTagName;
	boolean bChangeToStandalone;
	boolean bAddBeginningBracket;
	boolean bAddEndBracket;

	public TagRange(String tag, long lBegin, Long lEnd, int iTagType, 
			boolean bAddBeginningBracket, boolean bAddFakeTagName,
			boolean bAddEndBracket)
	{
		this.tag = tag;
		this.lBegin = lBegin;
		this.lEnd = lEnd;
		this.iTagType = iTagType;
		this.bAddBeginningBracket = bAddBeginningBracket;
		this.bAddFakeTagName = bAddFakeTagName;
		this.bAddEndBracket = bAddEndBracket;
		this.bChangeToStandalone = false;
	}
	public String getTag()
	{
		return(tag);
	}
	public void setTag(String tag)
	{
		this.tag = tag;
	}
	public long getLBegin()
	{
		return(lBegin);
	}
	public void setLBegin(long lBegin)
	{
		this.lBegin = lBegin;
	}
	public long getLEnd()
	{
		return(lBegin);
	}
	public void setLEnd(long lEnd)
	{
		this.lEnd = lEnd;
	}
	public int getITagType()
	{
		return iTagType;
	}
	public void setITagType(int iTagType)
	{
		this.iTagType = iTagType;
	}
	public boolean getBAddFakeTagName()
	{
		return bAddFakeTagName;
	}
	public void setBAddFakeTagName(boolean torf)
	{
		this.bAddFakeTagName = torf;
	}
	public boolean getBChangeToStandalone()
	{
		return bChangeToStandalone;
	}
	public void setBChangeToStandalone(boolean torf)
	{
		this.bChangeToStandalone = torf;
	}
	public boolean getBAddBeginningBracket()
	{
		return bAddBeginningBracket;
	}
	public void setBAddBeginningBracket(boolean torf)
	{
		this.bAddBeginningBracket = torf;
	}
	public boolean getBAddEndBracket()
	{
		return bAddEndBracket;
	}
	public void setBAddEndBracket(boolean torf)
	{
		this.bAddEndBracket = torf;
	}
}