/*===========================================================================*/
/* Copyright (C) 2008 Jim Hargrave, Dan Higinbotham                          */
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

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.openxml4j.exceptions.InvalidFormatException;
import org.openxml4j.exceptions.InvalidOperationException;
import org.openxml4j.exceptions.OpenXML4JException;
import org.openxml4j.opc.Package;
import org.openxml4j.opc.*;

import java.lang.*;
import java.io.*;
//import java.net.URL;
//import java.util.HashMap;
import java.util.Iterator;
//import java.util.LinkedList;
import java.util.List;
//import java.util.Map;
import java.util.TreeMap; // DWH 10-10-08

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.EndTag;
//import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
//import net.htmlparser.jericho.StartTagType;
import net.htmlparser.jericho.Tag;

//import net.sf.okapi.common.IParameters;
//import net.sf.okapi.common.encoder.HtmlEncoder;
//import net.sf.okapi.common.filters.BaseFilter;
//import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.encoder.HtmlEncoder;
import net.sf.okapi.common.filters.PropertyTextUnitPlaceholder;
//import net.sf.okapi.common.filters.PropertyTextUnitPlaceholder.PlaceholderType;
import net.sf.okapi.filters.markupfilter.BaseMarkupFilter;
import net.sf.okapi.filters.markupfilter.ExtractionRuleState;
import net.sf.okapi.filters.markupfilter.Parameters;
import net.sf.okapi.filters.markupfilter.ExtractionRule.EXTRACTION_RULE_TYPE;
//import net.sf.okapi.common.resource.Code;
//import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeleton;
//import net.sf.okapi.filters.yaml.TaggedFilterConfiguration;
//import net.sf.okapi.filters.yaml.TaggedFilterConfiguration.RULE_TYPE;

public class OpenXMLFilter extends BaseMarkupFilter {
	public final static int MSWORD=1;
	public final static int MSEXCEL=2;
	public final static int MSPOWERPOINT=3;

//	private Source htmlDocument;
//	private Iterator<Segment> nodeIterator;
//	private ExtractionRuleState ruleState;
//	private Parameters parameters; // 1-6-09
//	private GroovyFilterConfiguration configuration; 1-6-09
	private int configurationType;
//	private int oldConfigurationType=0;
	private Package p=null;
	private int filetype; // DWH 10-15-08
	private String sConfigFileName; // DWH 10-15-08
	private int dbg; // DWH 2-16-09

	static Logger logr = Logger.getLogger("net.sf.okapi.filters.openxml");
    // see http://logging.apache.org/log4j/1.2/manual.html

	public OpenXMLFilter() {
		super(); // 1-6-09
		setMimeType("text/html");
	}

	public boolean doOneOpenXMLFile(String sOneFileName, int filetype, int dbg)
	{
		TreeMap<String,InputStream> tmSubdocs;
		Iterator<String> it;
		InputStream isInputStream;
		PipedInputStream squishedInputStream;
		PipedOutputStream pios=null;
		String sDocName;
		this.dbg = dbg; // DWH 2-16-09
		tmSubdocs = pryopen(sOneFileName,filetype);
		for(it = tmSubdocs.keySet().iterator(); it.hasNext();)
		{
			sDocName = (String)it.next();
			isInputStream = tmSubdocs.get(sDocName);
			if (isInputStream!=null)
			{				
				try
				{
					if (sDocName.equals("Document") || sDocName.endsWith("slide+xml"))
						// main document in Word or slide in Powerpoint
					{
						if (pios!=null)
						{
							try {
								pios.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						pios = new PipedOutputStream(); // DWH 2-19-09 this may need to be final
						squishedInputStream = (PipedInputStream)combineRepeatedFormat(isInputStream,pios); // DWH 2-3-09
					    open(squishedInputStream); // DWH 2-3-09 was isInputStream
					}
					else
						open(isInputStream);						
				}
				catch(RuntimeException e) // DWH 2-13-09
				{
					String err = "Problem with configuration file "+sConfigFileName+"; "+sOneFileName+" cannot be filtered.";
					System.out.println(err);
					logr.log(Level.ERROR,err);
					pryclosed();
					return false; // filter failed because of bad configuration file
				}
				if (dbg>2)
				{
					String glorp = getParameters().toString();
					System.out.println(glorp); // This lists what YAML actually read out of the configuration file
				}
				// put out beginning group with name sDocName
				if (dbg>2)
				{
					System.out.println("\n\n<<<<<<< "+sOneFileName+" : "+sDocName+" >>>>>>>");
					displayEvents(); // DWH 2-14-09
				}
			}
		}			
		pryclosed();
		return(true); // filter succeeded and events are ready
	}

	public void displayEvents()
	{
		Event event;
		while (hasNext()) {
			event = next();
			displayOneEvent(event); // DWh 2-16-09 broke this out
		}
		System.out.println("");
		System.out.println("");
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
	
	public void pryclosed()
	{
		if (p!=null)
			p.revert();
	}
	
	public TreeMap<String,InputStream> pryopen(String filename, int filetype)
	{
		// Open the package
		TreeMap<String,InputStream> tmSubdocs=null;
//		Package p;
		PackageProperties pp;
		
		this.filetype = filetype; // DWH 10-15-08 Word, Excel, or Powerpoint
		setUpConfig(filetype); // DWH 2-16-09
		BasicConfigurator.configure();

		logr.setLevel(Level.WARN);
		
		
		try
		{
			p = Package.open(filename, PackageAccess.READ);
			pp = p.getPackageProperties();
			tmSubdocs = openZip(p);
//			p.revert();
		}
		catch(InvalidOperationException e)
		{
			System.out.println(e.getMessage()+'\n'+e.getStackTrace());
		}
		catch(InvalidFormatException e)
		{
			System.out.println(e.getMessage()+'\n'+e.getStackTrace());
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage()+'\n'+e.getStackTrace());
		}
		return(tmSubdocs);
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
		setDefaultConfig(sConfigFileName); // DWH 1-23-09
		setParameters(new Parameters(sConfigFileName)); // DWH 2-17-09 it doesn't update automatically from setDefaultConfig
	}
	
	private TreeMap<String,InputStream> openZip(Package p)
	{
		TreeMap<String,InputStream> tmWordSubdocs = new TreeMap<String,InputStream>();
		PackageRelationship coreDocumentRelationship;
		PackagePart coreDocumentPart;
		InputStream inStream;
		String sDocName,sDocType,sReverse;
		int iCute;
		try
		{
			// Get documents core properties part relationship
			coreDocumentRelationship = p.getRelationshipsByType( PackageRelationshipTypes.CORE_DOCUMENT) .getRelationship(0);
	
			// Get core properties part from the relationship.
			coreDocumentPart = p.getPart(coreDocumentRelationship);
	
			inStream = coreDocumentPart.getInputStream();
/*
			if (inStream==null)
			{
				try
				{
					FixXML fx = new FixXML("SampleDocxDocumentNoFix.xml","t-m-p.xml");
					 // this needs to use inStream as the first parameter
					fx.DoFix();
					inStream = coreDocumentPart.getInputStream();
				}
//				catch(InvalidFormatException e) {}
				catch(IOException e)
				{
					
				}
			}
*/
			tmWordSubdocs.put("Document", inStream);
			for (PackagePart part : p.getParts())
			{
			   
			   if (dbg>2)
				   System.out.println(part.getPartName().getURI() + " -> " + part.getContentType());
			   sDocType = part.getContentType();
			   iCute = sDocType.lastIndexOf('.', sDocType.length()-1);
			   if (iCute>0)
				   sDocType = sDocType.substring(iCute+1);
			   if ((filetype==MSWORD && (sDocType.equals("footnotes+xml") ||
					   					sDocType.equals("endnotes+xml") ||
				                        sDocType.equals("header+xml") ||
				                        sDocType.equals("footer+xml") ||
				                        sDocType.equals("comments+xml") ||
				                        sDocType.equals("glossary+xml"))) ||
				   (filetype==MSEXCEL && (sDocType.equals("main+xml") ||
						   				  sDocType.equals("worksheet+xml") ||
						   				  sDocType.equals("sharedStrings+xml") ||
						   				  sDocType.equals("table+xml") ||
						   				  sDocType.equals("comments+xml"))) ||
				   (filetype==MSPOWERPOINT && (sDocType.equals("slide+xml") ||
						   				       sDocType.equals("notesSlide+xml"))))
			   {
				   inStream = part.getInputStream();
				   sDocName = part.getPartName().getName();
				   if (sDocName!=null && sDocName!="" && inStream!=null)
					   tmWordSubdocs.put(sDocName+":"+sDocType,inStream);
			   }
			}
			System.out.println("");
		}
		catch(IOException e)
		{
			
		}
		catch(OpenXML4JException e)
		{
			
		}
		return(tmWordSubdocs);
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
	      boolean bIntag=false,bGotname=false,bInap=false,bHavr1=false,bInr=false,bB4text=true;
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
		    		  if (!bInr)
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
		    			  {
		    				  streamTheCurrentStuff();
		    			  }
		    		  }
		    		  else
		    		  {
		    			  r1aftext = r1aftext + tug;
		    			  bHavr1 = true;
		    		  }
		    	  }
		    	  else if (bInr)
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
	      private void havtext(String curtext)
	      {
	    	  if (bInap)
	    	  {
		    	  bB4text = false;
	    		  if (bHavr1)
		    	  {
		    		  t2 = curtext;
		    	  }
		    	  else
		    		  t1 = curtext;
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
	      private void rat(String s)
	      {
	    	try
	    	{
				bw.write(s);
//				System.out.println(s); // temporary, for debugging only
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
		// if in excluded state everything is skeleton including text
		if (getRuleState().isExludedState()) {
			addToDocumentPart(startTag.toString());
			// process these tag types to update parser state
			switch (getConfig().getMainRuleType(startTag.getName())) {
			  // DWH 1-23-09
			case EXCLUDED_ELEMENT:
				getRuleState().pushExcludedRule(startTag.getName());
				break;
			case INCLUDED_ELEMENT:
				getRuleState().pushIncludedRule(startTag.getName());
				break;
			case PRESERVE_WHITESPACE:
				getRuleState().pushPreserverWhitespaceRule(startTag.getName());
				break;
			}
			return;
		}

		switch (getConfig().getMainRuleType(startTag.getName())) {
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
					startDocumentPart(startTag.toString(), startTag.getName(), propertyTextUnitPlaceholders);
				 // DWH 1-29-09
					endDocumentPart();
				} else {
				// no attributes that need processing - just treat as skeleton
					addToDocumentPart(startTag.toString());
				}
			}
			break;
		case GROUP_ELEMENT:
			getRuleState().pushGroupRule(startTag.getName());
			startGroup(new GenericSkeleton(startTag.toString()));
			break;
		case EXCLUDED_ELEMENT:
			getRuleState().pushExcludedRule(startTag.getName());
			addToDocumentPart(startTag.toString());
			break;
		case INCLUDED_ELEMENT:
			getRuleState().pushIncludedRule(startTag.getName());
			addToDocumentPart(startTag.toString());
			break;
		case TEXT_UNIT_ELEMENT:
			getRuleState().pushTextUnitRule(startTag.getName());
			startTextUnit(new GenericSkeleton(startTag.toString())); // DWH 1-29-09
			break;
		case PRESERVE_WHITESPACE:
			getRuleState().pushPreserverWhitespaceRule(startTag.getName());
			addToDocumentPart(startTag.toString());
			break;
		default:
			if (canStartNewTextUnit()) // DWH 1-14-09 then not currently in text unit; added else
				addToDocumentPart(startTag.toString()); // 1-5-09
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
// Ask Jim what that means
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
				normalizedName = HtmlEncoder.NORMALIZED_LANGUAGE;
				return normalizedName;
			}
		}
		else if (tagName.equals("a:endpararpr") || tagName.equals("a:rpr"))
		{
			StartTag st = (StartTag) tag;
			if (st.getAttributeValue("lang") != null)
			{
				normalizedName = HtmlEncoder.NORMALIZED_LANGUAGE;
				return normalizedName;
			}
		}
		return normalizedName;
	}
/*
	@Override
	public FilterEvent next() {
		// reset state flags and buffers
//		ruleState.reset(); DWH 2-2-09
		super.getRuleState().reset(); // DWH 2-2-09 kludge; hopefully this doesn't just reset a copy

		while (hasQueuedEvents()) {
			return super.next();
		}

		while (nodeIterator.hasNext() && !isCanceled()) {
			Segment segment = nodeIterator.next();

			handleSegment(segment); // DWH 2-2-09

			if (hasQueuedEvents()) {
				break;
			}
		}

		if (!nodeIterator.hasNext()) {
			finalize(); // we are done
		}

		// return one of the waiting events
		return super.next();
	}

	protected void handleSegment(Segment segment)
	{
		if (segment instanceof Tag) {
			final Tag tag = (Tag) segment;

			// We just hit a tag that could close the current TextUnit, but
			// only if it was not opened with a TextUnit tag (i.e., complex
			// TextUnits such as <p> etc.)
			boolean inlineTag = false;
			if (getConfig().getMainRuleType(tag.getName()) == RULE_TYPE.INLINE_ELEMENT)
				inlineTag = true;
			if (isCurrentTextUnit() && !isCurrentComplexTextUnit() && !inlineTag) {
				endTextUnit();
			}

			if (tag.getTagType() == StartTagType.NORMAL || tag.getTagType() == StartTagType.UNREGISTERED) {
				handleStartTag((StartTag) tag);
			} else if (tag.getTagType() == EndTagType.NORMAL || tag.getTagType() == EndTagType.UNREGISTERED) {
				handleEndTag((EndTag) tag);
			} else if (tag.getTagType() == StartTagType.DOCTYPE_DECLARATION) {
				handleDocTypeDeclaration(tag);
			} else if (tag.getTagType() == StartTagType.CDATA_SECTION) {
				handleCdataSection(tag);
			} else if (tag.getTagType() == StartTagType.COMMENT) {
				handleComment(tag);
			} else if (tag.getTagType() == StartTagType.XML_DECLARATION) {
				handleXmlDeclaration(tag);
			} else if (tag.getTagType() == StartTagType.XML_PROCESSING_INSTRUCTION) {
				handleProcessingInstruction(tag);
			} else if (tag.getTagType() == StartTagType.MARKUP_DECLARATION) {
				handleMarkupDeclaration(tag);
			} else if (tag.getTagType() == StartTagType.SERVER_COMMON) {
				handleServerCommon(tag);
			} else if (tag.getTagType() == StartTagType.SERVER_COMMON_ESCAPED) {
				handleServerCommonEscaped(tag);
			} else { // not classified explicitly by Jericho
				if (tag instanceof StartTag) {
					handleStartTag((StartTag) tag);
				} else if (tag instanceof EndTag) {
					handleEndTag((EndTag) tag);
				} else {
					handleDocumentPart(tag);
				}
			}
		} else {
			handleText(segment);
		}		
	}
*/
}

