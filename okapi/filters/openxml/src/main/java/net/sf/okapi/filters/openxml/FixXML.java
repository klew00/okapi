/*===========================================================================*/
/* Copyright (C) 2008 Dan Higinbotham                                           */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA              */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.filters.openxml;

import java.io.*;
import java.util.LinkedList;
import net.sf.okapi.common.resource.InvalidContentException;

/**
 * <p>Filters Microsoft Office Word, Excel, and Powerpoint Documents.
 * OpenXML is the format of these documents.
 * 
 * <p>Since OpenXML files are Zip files that contain XML documents,
 * this filter handles opening and processing the zip file, and
 * instantiates <b>OpenXMLContentFilter</b> to process the XML documents.
 * 
 * <p>A call to createFilterWriter returns OpenXMLZipFilterWriter, which is
 * the associated writer for this filter.  OpenXMLZipFilterWriter instantiates
 * OpenXMLContentSkeletonWriter. 
 */

public class FixXML
{
	
	private BufferedReader in;
	private BufferedWriter out;
	private InputStream inn;
	private LinkedList<TagRange> llTigger;
	private static final int STARTTAG=1;
	private static final int STANDALONETAG=2;
	private static final int ENDTAG=3;
	private static final int CONFUSEDTAG=4; // </.../>
	private String sBadFileIn;
	private String sGoodFileOut;
	private String sCurrentBuffer="";
	
	public FixXML(String sBadFileIn, String sGoodFileOut)
	{
		this.sBadFileIn = sBadFileIn;
		this.sGoodFileOut = sGoodFileOut;
		this.inn = null;
	}
	public FixXML(InputStream inn, String sGoodFileOut)
	{
		this.sBadFileIn = "Unknown Filename";
		this.sGoodFileOut = sGoodFileOut;
		this.inn = inn;
	}
	public boolean DoFix()
	{
		int iBuffSize;
		if (inn==null)
			in = openAlmostXML(sBadFileIn);
		else
		{
			try
		    {
				in = new BufferedReader(new InputStreamReader(inn));
		    }
		    catch(Exception e)
		    {
		    	throw new InvalidContentException("Input file cannot be Buffered");
		    }
		}
		out = openOutputXML(sGoodFileOut);
		if (in!=null && out!=null)
		{
			fixit();
			iBuffSize = sizit();
			writit(iBuffSize);
			try {
				in.close();
				out.close();
			} 
			catch (IOException e)
			{
		  		throw new InvalidContentException("Can't read/write temporary file with error "+e.getMessage());
		  	}
		}
		return(true);
	}
	private BufferedReader openAlmostXML(String filename)
	{
	    BufferedReader in=null;
	    File theFile = new File(filename);
	    String s;
	    boolean rslt=true; // DWH 3-19-07
	    if (! theFile.exists())
	    {
	      throw new InvalidContentException(filename + " could not be Found");
	    }
	    else if (! theFile.canRead())
	    {
	      throw new InvalidContentException(filename + " cannot be Read");
	    }
	    try
	    {
	      in = new BufferedReader(new InputStreamReader(new FileInputStream(theFile),"UTF-8"));
	        // DWH 12-21-06 assume the file is encoded in UTF-8
	    }
	    catch(IOException e)
	    {
	    	throw new InvalidContentException(filename + " cannot be Buffered");
	    }
	    return(rslt ? in : null);  
	}
	private BufferedWriter openOutputXML(String filename)
    {
  	  File outFile;
  	  BufferedWriter out;
	  try
  	  {
  	    outFile = new File(filename);
  	  }
  	  catch(Throwable e)
  	  {
  		throw new InvalidContentException("Report File " + filename + " has error "+e.getMessage());
  	  }
  	  if (outFile.exists() && !outFile.canWrite())
  	  {
  		throw new InvalidContentException("Report File " + filename + " cannot be Written");
  	  }
  	  try
  	  {
  	    out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile),"UTF-8"));
  	  }
  	  catch(IOException e)
  	  {
  		throw new InvalidContentException(filename + " cannot be written because it is open");
  	  }
  	  catch(Throwable e)
  	  {
  		throw new InvalidContentException(filename + " has error "+e.getMessage());
  	  }
      return(out);
    }
	private void fixit()
	{
		String s;
		TagRange trBeg,trEnd;
		long lEnd=0L;
		int iTagType;
		int iNdex,iNdex2,iTot,iLastStartTag=-1;
		boolean bChangeToStandalone;
		llTigger = new LinkedList<TagRange>();
		LinkedList<Integer> llTagStack = new LinkedList<Integer>();
		while(true)
		{
			trBeg = gitTag(lEnd);
			if (trBeg==null)
				break;
			lEnd = trBeg.getLEnd();
			llTigger.addLast(trBeg);
		}
		iTot = llTigger.size();
		for(iNdex=0;iNdex<iTot;iNdex++)
		{
			trEnd = (TagRange)llTigger.get(iNdex);
			iTagType = trEnd.getITagType();
			switch(iTagType)
			{
				case STARTTAG:
				case STANDALONETAG:
				case CONFUSEDTAG:					
					llTagStack.add(new Integer(iNdex));
					if (iTagType==STARTTAG)
						iLastStartTag = iNdex;
					break;
				case ENDTAG:
					if (iLastStartTag>0)
					{
						for(iNdex2=iNdex-1;iNdex2>=iLastStartTag;iNdex2--)
						{
							llTagStack.remove(iNdex2);
							// remove intervening standalone tags and start tag from stack 
						}
						for(iLastStartTag--;iLastStartTag>=0;iLastStartTag--) // find previous start tag
						{
							iNdex2 = ((Integer)llTagStack.get(iLastStartTag)).intValue();
							trBeg = (TagRange)llTigger.get(iNdex2);
							iTagType = trBeg.getITagType();
							bChangeToStandalone = trBeg.getBChangeToStandalone();
							if (iTagType==STARTTAG && bChangeToStandalone==false)
								break;
						}
					}
					else
					{
						((TagRange)llTigger.get(iNdex)).setBChangeToStandalone(true);
						  // change to standalone tag if endtag doesn't match previous start tag
						llTagStack.add(new Integer(iNdex));
					}
					break;
			}
		}
		for(;iLastStartTag>-1;iLastStartTag--)
		{
			iNdex2 = ((Integer)llTagStack.get(iLastStartTag)).intValue();
			trBeg = (TagRange)llTigger.get(iNdex2);
			bChangeToStandalone = trBeg.getBChangeToStandalone();			
		}
	}
	public int sizit()
	{
		int iTot,iNdex,iSiz;
		long lMax,lCur;
		TagRange tr;
		lMax = 0;
		iTot = llTigger.size();
		for(iNdex=0;iNdex<iTot;iNdex++)
		{
			tr = (TagRange)llTigger.get(iNdex);
			lCur = tr.getLBegin() - tr.getLEnd();
			if (lCur>lMax)
				lMax = lCur;
		}
		try
		{
			iSiz = (int)lMax;
		}
		catch(Exception e)
		{
			throw new InvalidContentException("Input File is too long");
		}
		return(iSiz);
	}
	public void writit(int sbSiz)
	{
		int iTot,iNdex,iLen,iCnt;
		long lBeg,lEnd;
		char cOal,cbBuff[];
		boolean bAddFakeTagName;
		boolean bChangeToStandalone;
		boolean bAddBeginningBracket;
		boolean bAddEndBracket;
		boolean bGotEndBracket;
		cbBuff = new char[sbSiz+1];
		TagRange tr;
		StringBuffer sbBuff;
		iTot = llTigger.size();
		for(iNdex=0;iNdex<iTot;iNdex++)
		{
			tr = (TagRange)llTigger.get(iNdex);
			lBeg = tr.getLBegin();
			lEnd = tr.getLEnd();
			iLen = (int)(lBeg - lEnd);
			if (iLen>0)
			{
				try
				{
					in.read(cbBuff, (int)lBeg, iLen);
					bAddFakeTagName = tr.getBAddFakeTagName();
					bChangeToStandalone = tr.getBChangeToStandalone();
					bAddBeginningBracket = tr.getBAddBeginningBracket();
					bAddEndBracket = tr.getBAddEndBracket();
					bGotEndBracket = false;
					iCnt = 0;
					out.append('<');
					if (cbBuff[0] == '<')
						iCnt++;
					if (bAddFakeTagName)
						out.append("FAKE ");
					if (cbBuff[1]=='/' && bChangeToStandalone)
						iCnt++; // change to standalone so leave out end tag marker
					for(;iCnt<iLen;iCnt++)
					{
						cOal = cbBuff[iCnt];
						if (cOal=='/' && iCnt<iLen-1 && cbBuff[iCnt+1]=='>')
						{
							out.append("/>");
							iCnt++;
							bGotEndBracket = true;
						}
						else if (cOal=='>' && bChangeToStandalone)
						{
							out.append("/>");
							iCnt++;
							bGotEndBracket = true;
						}
						else if (bAddEndBracket && !bGotEndBracket && iCnt==iLen-1 && cOal!='>')
						{
							out.append(cOal);
							out.append('>');
						}
						else if (cOal=='>')
						{
							out.append(cOal);
							bGotEndBracket = true;
						}
						else
							out.append(cOal);
					}
				}
				catch(IOException e)
				{
					throw new InvalidContentException("Error tidying input file "+sBadFileIn);					
				}
			}
		}
	}
	public TagRange gitTag(long lOffset)
	{
		String s;
		String ss;
		String tag="";
		int len;
		int i,iTagType;
		char c,c2;
		boolean bGotEndBracket,bGotTag,bFirstChar,bInQuote,bAddBeginningBracket;
		boolean bAddFakeTagName,bAddEndBracket;
		long lRealOffset;
		lRealOffset = lOffset;
		TagRange tr=null;
		bGotEndBracket = false;
		bGotTag = false;
		bFirstChar = true;
		bInQuote = false;
		bAddBeginningBracket = true;
		bAddFakeTagName = false;
		bAddEndBracket = false;
		ss = "<";
		iTagType = STARTTAG;
		while((s=gitNextChar())!=null)
		{
			c = s.charAt(0);
			if (s.length()>1)
				c2 = s.charAt(1);
			else
				c2 = 0;
			if (bFirstChar)
			{
				bFirstChar = false;
				if (c=='/')
				{
					iTagType=ENDTAG;
					ss += c;
					continue;
				}
			}
			if (c=='"')
			{
				if (bInQuote)
					bInQuote = false;
				else
					bInQuote = true;
				ss += c;
				continue;
			}
			if (bGotEndBracket)
			{
				if (c=='<')
					break;
				else if (c=='>')
				{
					tr = new TagRange(tag,lRealOffset,lRealOffset+ss.length(),
							iTagType,bAddBeginningBracket,bAddFakeTagName,bAddEndBracket);
					llTigger.addLast(tr);
					lRealOffset = lRealOffset+ss.length();
					ss = "";
					iTagType = STANDALONETAG;
					tag = "FAKE";
					bAddFakeTagName = true;
					bAddBeginningBracket = true;
				}
				else
				{
					ss += c;
					continue;
				}
			}
			if (bGotTag)
			{
				if (c=='>' && !bInQuote)
					bGotEndBracket = true;
				else if (c=='<' && !bInQuote)
				{
					bAddEndBracket = true;
					break;
				}
				ss += c;
				continue;
			}
			if (c=='/' && c2=='>' && !bInQuote)
			{
				if (tag.length()==0)
				{
					bAddFakeTagName = true;
					tag = "FAKE";
				}
				if (iTagType==STARTTAG)
					iTagType = STANDALONETAG;
				else
					iTagType = CONFUSEDTAG;
				ss += "/>";
				continue;
			}
			if (c=='>' && !bInQuote)
			{
				if (tag.length()==0)
				{
					tag = "FAKE";
					bAddFakeTagName = true;
				}
				else
				{
					bGotEndBracket = true;
					bGotTag = true;
					continue;
				}
			}
			if (c==' ' || c==10 || c==12 || c==13)
			{
				if (tag.length()==0)
				{
					tag = "FAKE";
					bAddFakeTagName = true;
				}
				bGotTag = true;
				ss += c;
				continue;
			}
			tag += c;
		}
		tr = new TagRange(tag,lRealOffset,lRealOffset+ss.length(),
				iTagType,bAddBeginningBracket,bAddFakeTagName,bAddEndBracket);
		return(tr);
	}
	public String gitNextChar()
	{
		String s,ss=null;
		while(sCurrentBuffer.length()==0)
		{
			try
			{
				s = in.readLine();
				if (s==null)
					return(null);
				sCurrentBuffer = s;
			}
		    catch(EOFException e)
		    {
		    	return(null);
		    }
		    catch(IOException e)
		    {
		    	throw new InvalidContentException("Can't read stop word file "+sBadFileIn);
		    }
		}
		ss = sCurrentBuffer.substring(0,1);
		sCurrentBuffer = sCurrentBuffer.substring(1);
		return ss;
	}
}
