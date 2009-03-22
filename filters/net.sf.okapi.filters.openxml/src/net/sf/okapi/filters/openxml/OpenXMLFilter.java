/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/
package net.sf.okapi.filters.openxml;

//import org.apache.log4j.BasicConfigurator;
//import org.apache.log4j.Level;
//import org.apache.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap; // DWH 10-10-08

import net.htmlparser.jericho.EndTag;
import net.htmlparser.jericho.Segment;
//import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.Tag;

//import net.sf.okapi.common.BOMAwareInputStream;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.filters.PropertyTextUnitPlaceholder;
import net.sf.okapi.filters.markupfilter.BaseMarkupFilter;
import net.sf.okapi.filters.markupfilter.Parameters;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.skeleton.GenericSkeleton;

public class OpenXMLFilter extends BaseMarkupFilter {
	public final static int MSWORD=1;
	public final static int MSEXCEL=2;
	public final static int MSPOWERPOINT=3;

	private int configurationType;
	private Package p=null;
//	private int filetype; // DWH 10-15-08
	private String sConfigFileName; // DWH 10-15-08
	private URL urlConfig; // DWH 3-9-09
	private int dbg=0; // DWH 2-16-09
	private Hashtable htXMLFileType=null;

//	static Logger logr = Logger.getLogger("net.sf.okapi.filters.openxml");
    // see http://logging.apache.org/log4j/1.2/manual.html

	public OpenXMLFilter() {
		super(); // 1-6-09
		setMimeType("text/xml");
	}

	public void displayOneEvent(Event event)
	{
		String etyp=event.getEventType().toString();
		if (event.getEventType() == EventType.TEXT_UNIT) {
//			assertTrue(event.getResource() instanceof TextUnit);
		} else if (event.getEventType() == EventType.DOCUMENT_PART) {
//			assertTrue(event.getResource() instanceof DocumentPart);
		} else if (event.getEventType() == EventType.START_GROUP
				|| event.getEventType() == EventType.END_GROUP) {
//			assertTrue(event.getResource() instanceof StartGroup || event.getResource() instanceof Ending);
		}
		if (etyp.equals("START"))
			System.out.println("\n");
		System.out.println(etyp + ": ");
		if (event.getResource() != null) {
			System.out.println("(" + event.getResource().getId()+")");
			if (event.getResource() instanceof DocumentPart) {
				System.out.println(((DocumentPart) event.getResource()).getSourcePropertyNames());
			} else {
				System.out.println(event.getResource().toString());
			}
			if (event.getResource().getSkeleton() != null) {
				System.out.println("*Skeleton: \n" + event.getResource().getSkeleton().toString());
			}
		}
		
	}
	public void setUpConfig(int filetype)
	{
		if (filetype==MSWORD)
		{
			sConfigFileName = "/net/sf/okapi/filters/openxml/wordConfiguration.yml"; // DWH 1-5-09 groovy -> yml
			configurationType = MSWORD;
		}			
		else if (filetype==MSEXCEL)
		{
			sConfigFileName = "/net/sf/okapi/filters/openxml/excelConfiguration.yml"; // DWH 1-5-09 groovy -> yml
			configurationType = MSEXCEL;
		}
		else if (filetype==MSPOWERPOINT)
		{
			sConfigFileName = "/net/sf/okapi/filters/openxml/powerpointConfiguration.yml"; // DWH 1-5-09 groovy -> yml
			configurationType = MSPOWERPOINT;
		}
		urlConfig = OpenXMLFilter.class.getResource(sConfigFileName); // DWH 3-9-09
		setDefaultConfig(urlConfig); // DWH 3-9-09
		setParameters(new Parameters(urlConfig)); // DWH 3-9-09 it doesn't update automatically from setDefaultConfig
	}
	
	public InputStream combineRepeatedFormat(final InputStream in, final PipedOutputStream pios)
	{
		PipedInputStream piis=null;
//		final PipedOutputStream pios = new PipedOutputStream();
		try {
			piis = new PipedInputStream(pios);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		final OutputStreamWriter osw = new OutputStreamWriter(pios);
		final BufferedWriter bw = new BufferedWriter(osw);
		final InputStreamReader isr = new InputStreamReader(in);
		final BufferedReader br = new BufferedReader(isr);
	    Thread readThread = new Thread(new Runnable()
	    {
	      char cbuf[] = new char[512];
	      String curtag="",curtext="",curtagname="",onp="",offp="";
	      String r1b4text="",r1aftext="",t1="";
	      String r2b4text="",r2aftext="",t2="";
	      int i,n;
	      boolean bIntag=false,bGotname=false,bInap=false,bHavr1=false,bInr=false,bB4text=true,bInInnerR=false;
	      public void run()
	      {
	        try
	        {
	          while((n=br.read(cbuf,0,512))!=-1)
	          {
		    	for(i=0;i<n;i++)
		    	{
		    		handleOneChar(cbuf[i]);
		    	}
	          }
	          if (curtext.length()>0)
	        	  havtext(curtext);
	        }
	        catch(IOException e) {}
		    try {
		    	br.close();
		    	isr.close();
		    	bw.flush();
		    	bw.close();
//				osw.flush();
				osw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      }
	      private void handleOneChar(char c)
	      {
	    	  if (c=='>')
	    	  {
	    		  curtag = curtag + ">";
	    		  havatag(curtag,curtagname);
	    		  curtag = "";
	    		  curtagname = "";
	    		  bIntag = false;
	    	  }
	    	  else if (c=='<')
	    	  {
	    		  if (!bIntag)
	    		  {
		    		  if (curtext.length()>0)
		    		  {
		    			  havtext(curtext);
		    			  curtext = "";
		    		  }
	    			  curtag = curtag + "<";
	    			  bIntag = true;
	    			  bGotname = false;
	    		  }
	    		  else
	    		  {
	    			  curtag = curtag + "&lt;";
	    		  }
	    	  }
	    	  else
	    	  {
	    		  if (bIntag)
	    		  {
	    			  curtag = curtag + c;
	    			  if (!bGotname)
	    				  if (c==' ')
	    					  bGotname = true;
	    				  else
	    					  curtagname = curtagname + c;
	    		  }
	    		  else
	    			  curtext = curtext + c;
	    	  }
	      }
	      private void havatag(String tug,String tugname)
	      {
	    	  if (tugname.equals("w:p") || tugname.equals("a:p"))
	    	  {
	    		  onp = tug;
	    		  bInap = true;
	    		  bInr = false;
	    		  bInInnerR = false; // DWH 3-9-09
	    		  bHavr1 = false;
	    		  bB4text = false;
	    	  }
	    	  else if (tugname.equals("/w:p") || tugname.equals("/a:p"))
	    	  {
	    		  offp = tug;
	    		  bInap = false;
	    		  streamTheCurrentStuff();
	    	  }
	    	  else if (bInap)
	    	  {
		    	  if (tugname.equals("w:r") || tugname.equals("a:r"))
		    	  {
		    		  if (bInr)
		    		  {
		    			  bInInnerR = true; // DWH 3-2-09 ruby text has embedded <w:r> codes
		    			  innanar(tug);
		    		  }
		    		  else
		    		  {
		    			  if (bHavr1)
		    				  r2b4text = tug;
		    			  else
		    				  r1b4text = tug;
		    			  bInr = true;
		    			  bB4text = true;
		    		  }
		    	  }
		    	  else if (tugname.equals("/w:r") || tugname.equals("/a:r"))
		    	  {
		    		  if (bInInnerR)
		    		  {
		    			  bInInnerR = false; // DWH 3-2-09
		    			  innanar(tug);
		    		  }
		    		  else
		    		  {
			    		  bInr = false;
			    		  if (bHavr1)
			    		  {
			    			  r2aftext = r2aftext + tug;
			    			  if (r1b4text.equals(r2b4text) && r2aftext.equals(r2aftext))
			    			  {
			    				  t1 = t1 + t2;
			    				  r2b4text = "";
			    				  r2aftext = "";
			    				  t2 = "";
			    			  }
			    			  else
			    				  streamTheCurrentStuff();
			    		  }
			    		  else
			    		  {
			    			  r1aftext = r1aftext + tug;
			    			  bHavr1 = true;
			    		  }
		    		  }
		    	  }
		    	  else if (bInr)
		    		  innanar(tug);
		    	  else
		    	  {
		    		  streamTheCurrentStuff();
		    		  onp = tug; // this puts out <w:p> and any previous unoutput <w:r> blocks,
		    		  		     // then puts current tag in onp to be output next 
		    	  }
	    	  }
	    	  else
				rat(tug);
	      }
	      private void innanar(String tug)
	      {
    		  if (bHavr1)
    		  {
    			  if (bB4text)
    				  r2b4text = r2b4text + tug;
    			  else
    				  r2aftext = r2aftext + tug;
    		  }
    		  else
    		  {
    			  if (bB4text)
    				  r1b4text = r1b4text + tug;
    			  else
    				  r1aftext = r1aftext + tug;
    		  }	    	  
	      }
	      private void havtext(String curtext)
	      {
	    	  if (bInap)
	    	  {
		    	  if (bInInnerR) // DWH 3-2-09 (just the condition) ruby text has embedded <w:r> codes
		    		  innanar(curtext);
		    	  else
		    	  {
		    		  bB4text = false;
		    		  if (bHavr1)
			    	  {
			    		  t2 = curtext;
			    	  }
			    	  else
			    		  t1 = curtext;
		    	  }
	    	  }
	    	  else
				rat(curtext);
	      }
	      private void streamTheCurrentStuff()
	      {
	    	  if (bInap)
	    	  {
	    		    rat(onp+r1b4text+t1+r1aftext);
	    		    onp = "";
					r1b4text = r2b4text;
					t1 = t2;
					r1aftext = r2aftext;
					r2b4text = "";
					t2 = "";
					r2aftext = "";
					offp = "";
	    	  }
	    	  else
	    	  {
			  	    rat(onp+r1b4text+t1+r1aftext+r2b4text+t2+r2aftext+offp);
					onp = "";
					r1b4text = "";
					t1 = "";
					r1aftext = "";
					r2b4text = "";
					t2 = "";
					r2aftext = "";
					offp = "";
					bHavr1 = false;
	    	  }
	      }
	      private void rat(String s) // the Texan form of "write"
	      {
	    	try
	    	{
				bw.write(s);
				if (dbg>3)
					System.out.println(s); // for debugging only
			} catch (IOException e) {
				// do some logging here
				// e.printStackTrace();
				s = s + " ";
			}
	      }
	    });
	    readThread.start();
		return piis;
	}
	
	public void resetParse() // DWH $$$$
	{
		Iterator<Segment> nodeIterator = new TreeMap().keySet().iterator(); // empty
//		setFinishedParsing(false); 1-5-09
	}
	
	protected void handleCdataSection(Tag tag) { // 1-5-09
		addToDocumentPart(tag.toString());
		// TODO: special handling for CDATA sections (may call sub-filters or
		// unescape content etc.)
	}

	@Override
	protected void handleText(Segment text) {
		// if in excluded state everything is skeleton including text
		if (getRuleState().isExludedState()) {
			addToDocumentPart(text.toString());
			return;
		}

		// check for ignorable whitespace and add it to the skeleton
		// The Jericho html parser always pulls out the largest stretch of text
		// so standalone whitespace should always be ignorable if we are not
		// already processing inline text
		if (text.isWhiteSpace() && !isInsideTextRun()) {
			addToDocumentPart(text.toString());
			return;
		}

		if (canStartNewTextUnit()) {
			startTextUnit(text.toString());
		} else {
			addToTextUnit(text.toString());
		}
	}

	@Override
	protected void handleDocumentPart(Tag tag) {
		if (canStartNewTextUnit()) // DWH ifline and whole else: is an inline code if inside a text unit
			addToDocumentPart(tag.toString()); // 1-5-09
		else
			addCodeToCurrentTextUnit(tag);				
	}

	@Override
	protected void handleStartTag(StartTag startTag) {
		String sTagName = startTag.getName(); // DWH 2-26-09
		String sTagString; // DWH 2-26-09
		String sPartName; // DWH 2-26-09 for PartName attribute in [Content_Types].xml
		String sContentType; // DWH 2-26-09 for ContentType attribute in [Content_Types].xml
		// if in excluded state everything is skeleton including text
		if (getRuleState().isExludedState()) {
			addToDocumentPart(startTag.toString());
			// process these tag types to update parser state
			switch (getConfig().getMainRuleType(sTagName)) {
			  // DWH 1-23-09
			case EXCLUDED_ELEMENT:
				getRuleState().pushExcludedRule(sTagName);
				break;
			case INCLUDED_ELEMENT:
				getRuleState().pushIncludedRule(sTagName);
				break;
			case PRESERVE_WHITESPACE:
				getRuleState().pushPreserverWhitespaceRule(sTagName);
				break;
			}
			return;
		}
		sTagString = startTag.toString(); // DWH 2-26-09
		switch (getConfig().getMainRuleType(sTagName)) {
		  // DWH 1-23-09
		case INLINE_ELEMENT:
			if (canStartNewTextUnit()) {
				startTextUnit();
			}
			addCodeToCurrentTextUnit(startTag);
			break;

		case ATTRIBUTES_ONLY:
			// we assume we have already ended any (non-complex) TextUnit in
			// the main while loop above
			List<PropertyTextUnitPlaceholder> propertyTextUnitPlaceholders;

			if (!canStartNewTextUnit()) // DWH 2-14-09 document part just created is part of inline codes
				addCodeToCurrentTextUnit(startTag);
			else
			{
				propertyTextUnitPlaceholders = createPropertyTextUnitPlaceholders(startTag); // 1-29-09
				if (propertyTextUnitPlaceholders != null && !propertyTextUnitPlaceholders.isEmpty()) { // 1-29-09
					startDocumentPart(sTagString, sTagName, propertyTextUnitPlaceholders);
				 // DWH 1-29-09
					endDocumentPart();
				} else {
				// no attributes that need processing - just treat as skeleton
					addToDocumentPart(sTagString);
				}
			}
			break;
		case GROUP_ELEMENT:
			getRuleState().pushGroupRule(sTagName);
			startGroup(new GenericSkeleton(sTagString));
			break;
		case EXCLUDED_ELEMENT:
			getRuleState().pushExcludedRule(sTagName);
			addToDocumentPart(sTagString);
			break;
		case INCLUDED_ELEMENT:
			getRuleState().pushIncludedRule(sTagName);
			addToDocumentPart(sTagString);
			break;
		case TEXT_UNIT_ELEMENT:
			getRuleState().pushTextUnitRule(sTagName);
			startTextUnit(new GenericSkeleton(sTagString)); // DWH 1-29-09
			break;
		case PRESERVE_WHITESPACE:
			getRuleState().pushPreserverWhitespaceRule(sTagName);
			addToDocumentPart(sTagString);
			break;
		default:
			if (sTagName.equals("override")) // DWH 2-26-09 in [Content_Types].xml
			{ // it could be slow to do this test every time; I wonder if there is a better way
				sPartName = startTag.getAttributeValue("PartName");
				sContentType = startTag.getAttributeValue("ContentType");
				if (htXMLFileType!=null)
					htXMLFileType.put(sPartName, sContentType);
			}
			if (canStartNewTextUnit()) // DWH 1-14-09 then not currently in text unit; added else
				addToDocumentPart(sTagString); // 1-5-09
			else
				addCodeToCurrentTextUnit(startTag);				
		}
	}

	@Override
	protected void handleEndTag(EndTag endTag) {
		// if in excluded state everything is skeleton including text
		if (getRuleState().isExludedState()) {
			addToDocumentPart(endTag.toString());
			// process these tag types to update parser state
			switch (getConfig().getMainRuleType(endTag.getName())) {
			  // DWH 1-23-09
			case EXCLUDED_ELEMENT:
				getRuleState().popExcludedIncludedRule();
				break;
			case INCLUDED_ELEMENT:
				getRuleState().popExcludedIncludedRule();
				break;
			case PRESERVE_WHITESPACE:
				getRuleState().popPreserverWhitespaceRule();
				break;
			}

			return;
		}

		switch (getConfig().getMainRuleType(endTag.getName())) {
		  // DWH 1-23-09
		case INLINE_ELEMENT:
			if (canStartNewTextUnit()) {
				startTextUnit();
			}
			addCodeToCurrentTextUnit(endTag);
			break;
		case GROUP_ELEMENT:
			getRuleState().popGroupRule();
			endGroup(new GenericSkeleton(endTag.toString()));
			break;
		case EXCLUDED_ELEMENT:
			getRuleState().popExcludedIncludedRule();
			addToDocumentPart(endTag.toString());
			break;
		case INCLUDED_ELEMENT:
			getRuleState().popExcludedIncludedRule();
			addToDocumentPart(endTag.toString());
			break;
		case TEXT_UNIT_ELEMENT:
			getRuleState().popTextUnitRule();
			endTextUnit(new GenericSkeleton(endTag.toString()));
			break;
		case PRESERVE_WHITESPACE:
			getRuleState().popPreserverWhitespaceRule();
			addToDocumentPart(endTag.toString());
			break;
		default:
			if (canStartNewTextUnit()) // DWH 1-14-09 then not currently in text unit; added else
				addToDocumentPart(endTag.toString()); // not in text unit, so add to skeleton
			else
				addCodeToCurrentTextUnit(endTag); // in text unit, so add to inline codes
			break;
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.markupfilter.BaseMarkupFilter#handleComment(net.htmlparser.jericho.Tag)
	 */
	@Override
	protected void handleComment(Tag tag) {
		handleDocumentPart(tag);		
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.markupfilter.BaseMarkupFilter#handleDocTypeDeclaration(net.htmlparser.jericho.Tag)
	 */
	@Override
	protected void handleDocTypeDeclaration(Tag tag) {
		handleDocumentPart(tag);		
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.markupfilter.BaseMarkupFilter#handleMarkupDeclaration(net.htmlparser.jericho.Tag)
	 */
	@Override
	protected void handleMarkupDeclaration(Tag tag) {
		handleDocumentPart(tag);		
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.markupfilter.BaseMarkupFilter#handleProcessingInstruction(net.htmlparser.jericho.Tag)
	 */
	@Override
	protected void handleProcessingInstruction(Tag tag) {
		handleDocumentPart(tag);		
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.markupfilter.BaseMarkupFilter#handleServerCommon(net.htmlparser.jericho.Tag)
	 */
	@Override
	protected void handleServerCommon(Tag tag) {
		handleDocumentPart(tag);		
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.markupfilter.BaseMarkupFilter#handleServerCommonEscaped(net.htmlparser.jericho.Tag)
	 */
	@Override
	protected void handleServerCommonEscaped(Tag tag) {
		handleDocumentPart(tag);		
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.markupfilter.BaseMarkupFilter#handleXmlDeclaration(net.htmlparser.jericho.Tag)
	 */
	@Override
	protected void handleXmlDeclaration(Tag tag) {
		handleDocumentPart(tag);		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.filters.IFilter#getName()
	 */
	public String getName() {
		return "OpenXMLFilter";
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.okapi.common.markupfilter.BaseMarkupFilter#normalizeName(java.
	 * lang.String)
	 */
	@Override
	protected String normalizeAttributeName(String attrName, String attrValue, Tag tag) {
		// normalize values for HTML
		String normalizedName = attrName;
		String tagName; // DWH 2-19-09 */
// Any attribute that encodes language should be renamed here to "language"
// Any attribute that encodes locale or charset should be normalized too
/*
		// <meta http-equiv="Content-Type"
		// content="text/html; charset=ISO-2022-JP">
		if (isMetaCharset(attrName, attrValue, tag)) {
			normalizedName = HtmlEncoder.NORMALIZED_ENCODING;
			return normalizedName;
		}

		// <meta http-equiv="Content-Language" content="en"
		if (tag.getName().equals("meta") && attrName.equals(HtmlEncoder.CONTENT)) {
			StartTag st = (StartTag) tag;
			if (st.getAttributeValue("http-equiv") != null) {
				if (st.getAttributeValue("http-equiv").equals("Content-Language")) {
					normalizedName = HtmlEncoder.NORMALIZED_LANGUAGE;
					return normalizedName;
				}
			}
		}
*/
		// <w:lang w:val="en-US" ...>
		tagName = tag.getName();
		if (tagName.equals("w:lang"))
		{
			StartTag st = (StartTag) tag;
			if (st.getAttributeValue("w:val") != null)
			{
				normalizedName = Property.LANGUAGE;
				return normalizedName;
			}
		}
		else if (tagName.equals("a:endpararpr") || tagName.equals("a:rpr"))
		{
			StartTag st = (StartTag) tag;
			if (st.getAttributeValue("lang") != null)
			{
				normalizedName = Property.LANGUAGE;
				return normalizedName;
			}
		}
		return normalizedName;
	}
	protected void initFileTypes() // DWH 2-26-09
	{
		htXMLFileType = new Hashtable();
	}
	protected String getContentType(String sPartName) // DWH 2-26-09
	{
		String rslt="",tmp;
		if (sPartName!=null)
		{
			tmp = (String)htXMLFileType.get(sPartName);
			if (tmp!=null)
				rslt = tmp;
		}
		return(rslt);
	}
	public void setDbg(int dbg)
	{
		this.dbg = dbg;
	}
}
