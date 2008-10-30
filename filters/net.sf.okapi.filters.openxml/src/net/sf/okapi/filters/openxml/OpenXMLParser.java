/*===========================================================================*/
/* Copyright (C) 2008 Jim Hargrave                                           */
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

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap; // DWH 10-10-08

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.EndTag;
import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagType;
import net.htmlparser.jericho.Tag;
import net.sf.okapi.common.filters.GroovyFilterConfiguration;
import net.sf.okapi.common.filters.IParser;
import net.sf.okapi.common.filters.BaseParser;
import net.sf.okapi.common.filters.IParser.ParserTokenType;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.Group;
import net.sf.okapi.common.resource.IContainable;
import net.sf.okapi.common.resource.SkeletonUnit;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
//import net.sf.okapi.filters.openxml.tests.OpenXMLParserTest;
import net.sf.okapi.filters.openxml.ExtractionRuleState;
import net.sf.okapi.filters.openxml.ExtractionRule.EXTRACTION_RULE_TYPE;

public class OpenXMLParser extends BaseParser {
	public final static int MSWORD=1;
	public final static int MSEXCEL=2;
	public final static int MSPOWERPOINT=3;

	private Source htmlDocument;
	private GroovyFilterConfiguration configuration;
	private int configurationType; // DWH 10-23-08
	private int oldConfigurationType=0; // DWH 10-23-08
	private Iterator<Segment> nodeIterator;
	private ExtractionRuleState ruleState;
	private Package p=null;
	private int filetype; // DWH 10-15-08
	private String sConfigFileName; // DWH 10-15-08

	static Logger logr = Logger.getLogger("net.sf.okapi.filters.openxml");
    // see http://logging.apache.org/log4j/1.2/manual.html

	public OpenXMLParser() {
	}

	public void close() {
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
		if (filetype==MSWORD)
		{
			sConfigFileName = "/net/sf/okapi/filters/openxml/wordConfiguration.groovy";
			configurationType = MSWORD;
		}			
		else if (filetype==MSEXCEL)
		{
			sConfigFileName = "/net/sf/okapi/filters/openxml/excelConfiguration.groovy";
			configurationType = MSEXCEL;
		}
		else if (filetype==MSPOWERPOINT)
		{
			sConfigFileName = "/net/sf/okapi/filters/openxml/powerpointConfiguration.groovy";
			configurationType = MSPOWERPOINT;
		}
		BasicConfigurator.configure();
		// PropertyConfigurator.configure("filename");
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
			   System.out.println(part.getPartName().getURI() + " -> " + part.getContentType());
			   sDocType = part.getContentType();
			   iCute = sDocType.lastIndexOf('.', sDocType.length()-1);
			   if (iCute>0)
				   sDocType = sDocType.substring(iCute+1);
			   if ((filetype==MSWORD && (sDocType.equals("footnotes+xml") ||
					   					sDocType.equals("endnotes+xml") ||
				                        sDocType.equals("header+xml") ||
				                        sDocType.equals("footer+xml") ||
				                        sDocType.equals("comments+xml"))) ||
				   (filetype==MSEXCEL && (sDocType.equals("worksheet+xml") ||
						   				  sDocType.equals("sharedStrings+xml"))) ||
				   (filetype==MSPOWERPOINT && (sDocType.equals("slide+xml") ||
						   				       sDocType.equals("notesSlide+xml"))))
			   {
				   sDocName = part.getPartName().getName();
				   inStream = part.getInputStream();
				   if (sDocName!=null && sDocName!="" && inStream!=null)
					   tmWordSubdocs.put(sDocName,inStream);
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
	
	public void resetParse() // DWH 
	{
		Iterator<Segment> nodeIterator = new TreeMap().keySet().iterator(); // empty
		setFinishedParsing(false);
	}
	
	private String getTagNameForConfig(Tag tag) // DWH
	  // DWH 10-2 converts : to _ so it will match tags in config file
	{
		String sTagName,sTagNameForConfig;
		sTagName = tag.getName();
		sTagNameForConfig = sTagName.replace(':','_');
		return(sTagNameForConfig);
	}
	
	private void initialize() { // DWH 10-15-08 use sConfigFileName	
		if (configuration == null || oldConfigurationType!=configurationType) {
			configuration = new GroovyFilterConfiguration(sConfigFileName); // DWH 10-15-08 sConfigFileName
			// DWH was defaultConfiguration under html
			oldConfigurationType = configurationType; // DWH 10-23-08
		}

		// Segment iterator
		ruleState = new ExtractionRuleState();
		htmlDocument.fullSequentialParse();
		nodeIterator = htmlDocument.getNodeIterator();
	}

	public void open(CharSequence input) {
		htmlDocument = new Source(input);
		initialize();
	}

	public void open(InputStream input) {
		try {
			htmlDocument = new Source(input);
		} catch (IOException e) {
			// TODO Wrap unchecked exception
			throw new RuntimeException(e);
		}
		initialize();
	}

	public void open(URL input) {
		try {
			htmlDocument = new Source(input);
		} catch (IOException e) {
			// TODO: Wrap unchecked exception
			throw new RuntimeException(e);
		}
		initialize();
	}

	public void setHtmlFilterConfiguration(GroovyFilterConfiguration configuration) {
		this.configuration = configuration;
	}

	public IContainable getResource() {
		return getFinalizedToken();
	}
	
	public ParserTokenType parseNext() {
		if (isFinishedParsing()) {
			return ParserTokenType.ENDINPUT;
		}
	
		// reset state flags and buffers
		ruleState.reset();
		initializeLoop();
	
		while (!isFinishedToken() && nodeIterator.hasNext() && !isCanceled()) {
			Segment segment = nodeIterator.next();
	
			if (segment instanceof Tag) {
				final Tag tag = (Tag) segment;
	
				if (tag.getTagType() == StartTagType.NORMAL || tag.getTagType() == StartTagType.UNREGISTERED) {
					handleStartTag((StartTag) tag);
				} else if (tag.getTagType() == EndTagType.NORMAL || tag.getTagType() == EndTagType.UNREGISTERED) {
					handleEndTag((EndTag) tag);
				} else if (tag.getTagType() == StartTagType.DOCTYPE_DECLARATION) {
					handleSkeleton(tag);
				} else if (tag.getTagType() == StartTagType.CDATA_SECTION) {
					handleCdataSection(tag);
				} else if (tag.getTagType() == StartTagType.COMMENT) {
					handleSkeleton(tag);
				} else if (tag.getTagType() == StartTagType.XML_DECLARATION) {
					handleSkeleton(tag);
				} else if (tag.getTagType() == StartTagType.XML_PROCESSING_INSTRUCTION) {
					handleSkeleton(tag);
				} else if (tag.getTagType() == StartTagType.MARKUP_DECLARATION) {
					handleSkeleton(tag);
				} else if (tag.getTagType() == StartTagType.SERVER_COMMON) {
					// TODO: Handle server formats
					handleSkeleton(tag);
				} else if (tag.getTagType() == StartTagType.SERVER_COMMON_ESCAPED) {
					// TODO: Handle server formats
					handleSkeleton(tag);
				} else { // not classified explicitly by Jericho
					if (tag instanceof StartTag) {
						handleStartTag((StartTag) tag);
					} else if (tag instanceof EndTag) {
						handleEndTag((EndTag) tag);
					} else {
						handleSkeleton(tag);
					}
				}
			} else {
				handleText(segment);
			}
		}
	
		if (isCanceled()) {
			return getFinalizedTokenType();
		}
	
		if (!nodeIterator.hasNext()) {
			// take care of the token from the previous run
			finalizeCurrentToken();
			setFinishedParsing(true);
		}
	
		// return our finalized token
		return getFinalizedTokenType();
	}
	
	private void handleCdataSection(Tag tag) {
		// if in excluded state everything is skeleton including text
		if (ruleState.isExludedState()) {
			appendToSkeletonUnit(tag.toString(), tag.getBegin(), tag.length());
			return;
		}
	
		// TODO: special handling for CDATA sections (may call sub-filters
		// or unescape content etc.)
		appendToSkeletonUnit(tag.toString(), tag.getBegin(), tag.length());
	}
	
	private void handleText(Segment text) {
		// if in excluded state everything is skeleton including text
		if (ruleState.isExludedState()) {
			appendToSkeletonUnit(text.toString(), text.getBegin(), text.length());
			return;
		}
	
		// check for ignorable whitespace and add it to the skeleton
		// The Jericho html parser always pulls out the largest stretch of text
		// so standalone whitespace should always be ignorable if we are not
		// already processing inline text
		if (text.isWhiteSpace() && !ruleState.isInline()) {
			appendToSkeletonUnit(text.toString(), text.getBegin(), text.length());
			return;
		}
	
		// its not pure whitespace so we are now processing inline text
		ruleState.setInline(true);
		appendToTextUnit(text.toString());
	}
	
	private void handleSkeleton(Tag tag) {
		appendToSkeletonUnit(tag.toString(), tag.getBegin(), tag.length());
	}
	
	private void handleStartTag(StartTag startTag) {
		// if in excluded state everything is skeleton including text
		if (ruleState.isExludedState()) {
			appendToSkeletonUnit(startTag.toString(), startTag.getBegin(), startTag.length());
			// process these tag types to update parser state
//			switch (configuration.getMainRuleType(startTag.getName())) { // DWH 10-15-08 commented
			switch (configuration.getMainRuleType(getTagNameForConfig(startTag))) { // DWH 10-15-08 
			case EXCLUDED_ELEMENT:
				ruleState.pushExcludedRule(startTag.getName());
				break;
			case INCLUDED_ELEMENT:
				ruleState.pushIncludedRule(startTag.getName());
				break;
			case PRESERVE_WHITESPACE:
				ruleState.pushPreserverWhitespaceRule(startTag.getName());
				break;
			}
			return;
		}
	
//		switch (configuration.getMainRuleType(startTag.getName())) {
		switch (configuration.getMainRuleType(getTagNameForConfig(startTag))) {
		case INLINE_ELEMENT:
			ruleState.setInline(true);
			addToCurrentTextUnit(startTag);
			break;
	
		case ATTRIBUTES_ONLY:
			if (configuration.hasActionableAttributes(startTag.getName())) {				
			}
			break;
		case GROUP_ELEMENT:
			startGroup(startTag.getName(), startTag.toString());
			break;
		case EXCLUDED_ELEMENT:
			ruleState.pushExcludedRule(startTag.getName());
			appendToSkeletonUnit(startTag.toString(), startTag.getBegin(), startTag.length());
			break;
		case INCLUDED_ELEMENT:
			ruleState.pushIncludedRule(startTag.getName());
			appendToSkeletonUnit(startTag.toString(), startTag.getBegin(), startTag.length());
			break;
		case TEXT_UNIT_ELEMENT:
			// TODO: I'm wondering if we really need before and after skeleton.
			// If we do need it I need to know which tags to apply it to.
			appendToSkeletonUnit(startTag.toString(), startTag.getBegin(), startTag.length());
			break;
		case PRESERVE_WHITESPACE:
			ruleState.pushPreserverWhitespaceRule(startTag.getName());
			appendToSkeletonUnit(startTag.toString(), startTag.getBegin(), startTag.length());
			break;
		default:
			ruleState.setInline(false);
			appendToSkeletonUnit(startTag.toString(), startTag.getBegin(), startTag.length());
		}
	}
	
	private void handleEndTag(EndTag endTag) {
		// if in excluded state everything is skeleton including text
		if (ruleState.isExludedState()) {
			appendToSkeletonUnit(endTag.toString(), endTag.getBegin(), endTag.length());
			// process these tag types to update parser state
//			switch (configuration.getMainRuleType(endTag.getName())) { // DWH 10-15-08 commented
			switch (configuration.getMainRuleType(getTagNameForConfig(endTag))) { // DWH 10-15-08
			case EXCLUDED_ELEMENT:
				ruleState.popExcludedIncludedRule();
				break;
			case INCLUDED_ELEMENT:
				ruleState.popExcludedIncludedRule();
				break;
			case PRESERVE_WHITESPACE:
				ruleState.popPreserverWhitespaceRule();
				break;
			}
	
			return;
		}
	
//		switch (configuration.getMainRuleType(endTag.getName())) { // DWH 10-15-08 commented
		switch (configuration.getMainRuleType(getTagNameForConfig(endTag))) { // DWH 10-15-08
		case INLINE_ELEMENT:
			ruleState.setInline(true);
			addToCurrentTextUnit(endTag);
			break;
		case GROUP_ELEMENT:
			endGroup(endTag.toString());
			break;
		case EXCLUDED_ELEMENT:
			ruleState.popExcludedIncludedRule();
			appendToSkeletonUnit(endTag.toString(), endTag.getBegin(), endTag.length());
			break;
		case INCLUDED_ELEMENT:
			ruleState.popExcludedIncludedRule();
			appendToSkeletonUnit(endTag.toString(), endTag.getBegin(), endTag.length());
			break;
		case TEXT_UNIT_ELEMENT:
			// TODO: if we really need before and after skeleton I need to know
			// which tags to apply it to.
			appendToSkeletonUnit(endTag.toString(), endTag.getBegin(), endTag.length());
			break;
		case PRESERVE_WHITESPACE:
			ruleState.popPreserverWhitespaceRule();
			appendToSkeletonUnit(endTag.toString(), endTag.getBegin(), endTag.length());
			break;
		default:
			ruleState.setInline(false);
			appendToSkeletonUnit(endTag.toString(), endTag.getBegin(), endTag.length());
			break;
		}
	}
	
	private void addAttribute(StartTag startTag) {
		// convert Jericho attributes to HashMap
		Map<String, String> attrs = new HashMap<String, String>();
		attrs = startTag.getAttributes().populateMap(attrs, true);
		for (Attribute attribute : startTag.getAttributes()) {
//			if (configuration.isTranslatableAttribute(startTag.getName(), attribute.getName(), attrs)) { // DWH 10-15-08 commented
			if (configuration.isTranslatableAttribute(getTagNameForConfig(startTag), attribute.getName(), attrs)) { // DWH 10-15-08 
	
//			} else if (configuration.isLocalizableAttribute(startTag.getName(), attribute.getName(), attrs)) { // DWH 10-15-08 commented
			} else if (configuration.isLocalizableAttribute(getTagNameForConfig(startTag), attribute.getName(), attrs)) { // DWH 10-15-08
	
			}
		}
	}
	private void addToCurrentTextUnit(Tag tag) {
		TextFragment.TagType tagType;
		if (tag.getTagType() == StartTagType.NORMAL || tag.getTagType() == StartTagType.UNREGISTERED) {
			if (((StartTag) tag).isSyntacticalEmptyElementTag())
				tagType = TextFragment.TagType.PLACEHOLDER;
			else
				tagType = TextFragment.TagType.OPENING;
		} else if (tag.getTagType() == EndTagType.NORMAL || tag.getTagType() == EndTagType.UNREGISTERED) {
			tagType = TextFragment.TagType.CLOSING;
		} else {
			tagType = TextFragment.TagType.PLACEHOLDER;
		}
		appendToTextUnit(new Code(tagType, tag.getName(), tag.toString()));
	}
}
