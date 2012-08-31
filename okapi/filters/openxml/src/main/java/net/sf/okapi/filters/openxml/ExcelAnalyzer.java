/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.StartTagType;
import net.htmlparser.jericho.StreamedSource;
import net.htmlparser.jericho.Tag;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.filters.abstractmarkup.ExtractionRuleState;
import net.sf.okapi.filters.yaml.TaggedFilterConfiguration;
import net.sf.okapi.filters.yaml.TaggedFilterConfiguration.RULE_TYPE;

public class ExcelAnalyzer
{
	private Logger LOGGER = Logger.getLogger(OpenXMLFilter.class.getName());

	private ZipFile zipFile;
	private ZipEntry entry;
	private Enumeration<? extends ZipEntry> entries;
	private InputStream is;
	private TreeMap<Integer,Point> tmCells;	
	private TreeSet<String> tsColors;
	private ExtractionRuleState ruleState;
	private YamlParameters parameters; // DWH 7-16-09
	private Iterator<Segment> nodeIterator;
	private StreamedSource document;
//	private RawDocument input;

	public ExcelAnalyzer(URI zipURI)
	{
		try
		{
			File fZip = new File(zipURI.getPath());
			zipFile = new ZipFile(fZip);
		}
		catch(IOException e)
		{
			LOGGER.log(Level.WARNING,"Error opening zipped Excel file.");
			throw new OkapiIOException("Error opening zipped Excel file.");
		}
	}
	public ExcelAnalyzer(ZipFile zipFile)
	{
		this.zipFile = zipFile;
	}
	
	public TreeMap<Integer,Point> analyzeExcelGetSheetSizes()
	{
		String sEntryName;
		String sDim;
		String sNum;
		String sDims[];
		int iCute,x,y;
		Integer ii;
		tmCells = new TreeMap<Integer,Point>();
		entries = zipFile.entries();
		while( entries.hasMoreElements() )
		{
			entry = entries.nextElement();
			sEntryName = entry.getName();
			iCute = sEntryName.lastIndexOf('/');
			if (iCute>-1)
				sEntryName = sEntryName.substring(iCute+1);
			if (sEntryName.substring(0, 5).equals("sheet"))
			{
				nodeIterator = createNodeIterator(entry);			
				while (nodeIterator.hasNext())
				{
					Segment segment = nodeIterator.next();
					if (segment instanceof Tag)
					{
						final Tag tag = (Tag) segment;
						if (parameters.getTaggedConfig().getElementRuleTypeCandidate(tag.getName()).equals("ATTRIBUTES_ONLY"))
						{
							if (tag.getName().equals("dimension"))
							{
								for (Attribute attribute : tag.parseAttributes())
								{
									if (attribute.getName().equals("ref"))
									{
										sDim = attribute.getValue();
										iCute = sEntryName.indexOf('.');
										if (iCute>-1)
										{
											sNum = sEntryName.substring(5,iCute);
											ii = new Integer(sNum);
											sDims = sDim.split(":");
											y = getNumberOfRows(sDims[1]); // number of rows in sheet
											x = getNumberOfColumns(sDims[1]); // number of colums in sheet
											tmCells.put(ii, new Point(x,y));
										}
									}
								}
							}
						}
					}
				}
			}
		}
		closeInput();
		return tmCells;
	}
	private int getNumberOfRows(String s) // extracts first number to end of a string
	{
		int len,ndx;
		int rslt=0;
		String goomer="0123456789";
		if (s!=null)
		{
			len = s.length();
			for(int i=0;i<len;i++)
			{
				if ((ndx=goomer.indexOf(s.charAt(i)))>-1)
				{
					try
					{
						rslt = new Integer(s.substring(ndx));
					}
					catch(Exception e) {};
					break;
				}
			}
		}
		return rslt;
	}
	private int getNumberOfColumns(String s) // extracts first number to end of a string
	{
		int len,ndx;
		int rslt=0;
		String goomer="0123456789";
		String col;
		if (s!=null)
		{
			len = s.length();
			for(int i=0;i<len;i++)
			{
				if ((ndx=goomer.indexOf(s.charAt(i)))>-1)
				{
					col = s.substring(0,i);
					len = col.length();
					if (len==1)
						rslt = Character.digit(col.charAt(0),10)-48;
					else if (len==2)
						rslt = Character.digit(col.charAt(1),10)-64 + 
							   26*(Character.digit(col.charAt(1),10)-65)
							   + 26;
					break;							   
				}
			}
		}
		return rslt;
	}
	public TreeSet analyzeExcelGetNonThemeColors()
	{
		String sEntryName;
		String sColor;
		int iCute;
		TaggedFilterConfiguration tfc;
		tsColors = new TreeSet<String>();
		entries = zipFile.entries();
		while( entries.hasMoreElements() )
		{
			entry = entries.nextElement();
			sEntryName = entry.getName();
			iCute = sEntryName.lastIndexOf('/');
			if (iCute>-1)
				sEntryName = sEntryName.substring(iCute+1);
			if (sEntryName.equals("styles.xml"))
			{
				nodeIterator = createNodeIterator(entry);
				tfc = parameters.getTaggedConfig();
				while (nodeIterator.hasNext())
				{
					Segment segment = nodeIterator.next();
					if (segment instanceof Tag)
					{
						final Tag tag = (Tag) segment;
//						if (parameters.getTaggedConfig().getMainRuleType(tag.getName()).equals("ATTRIBUTES_ONLY"))
						if (tfc.getElementRuleTypeCandidate(tag.getName()) == RULE_TYPE.ATTRIBUTES_ONLY)
						{
							if (tag.getName().equals("color"))
							{
								for (Attribute attribute : tag.parseAttributes())
								{
									if (attribute.getName().equals("rgb"))
									{
										sColor = attribute.getValue();
										tsColors.add(sColor);
									}
								}
							}
						}
					}
				}
				closeInput();
				break;
			}
		}
		return tsColors;
	}
	public TreeSet analyzeExcelGetStylesOfExcludedColors(TreeSet tsExcludedColors)
	{
		String sEntryName;
		String sColor,sCount,sFontNum,sAttName;
		int iCute,nFontTotal,nCurrentFont=0,nThisFont,nCurrentStyle=0;
		TaggedFilterConfiguration tfc;
		boolean bFonts[];
		boolean bXfing=false;
		TreeSet tsStyles;
		tsStyles = new TreeSet<String>();
		if (tsExcludedColors.size()==0)
			return tsStyles;
		tsColors = new TreeSet<String>();
		entries = zipFile.entries();
		RULE_TYPE rtRuleType;
		String sTagName;
		bFonts = new boolean[1];
		while( entries.hasMoreElements() )
		{
			entry = entries.nextElement();
			sEntryName = entry.getName();
			iCute = sEntryName.lastIndexOf('/');
			if (iCute>-1)
				sEntryName = sEntryName.substring(iCute+1);
			if (sEntryName.equals("styles.xml"))
			{
				nodeIterator = createNodeIterator(entry);
				tfc = parameters.getTaggedConfig();
				while (nodeIterator.hasNext())
				{
					Segment segment = nodeIterator.next();
					if (segment instanceof Tag)
					{
						final Tag tag = (Tag) segment;
						sTagName = tag.getName();
						rtRuleType = tfc.getElementRuleTypeCandidate(sTagName);
						if (tag.getTagType() == StartTagType.NORMAL ||
												tag.getTagType() == StartTagType.UNREGISTERED)
							// only if it's a start tag
						{
	//						if (parameters.getTaggedConfig().getMainRuleType(tag.getName()).equals("ATTRIBUTES_ONLY"))
							if (rtRuleType == RULE_TYPE.ATTRIBUTES_ONLY)
							{
								if (tag.getName().equals("color"))
								{
									for (Attribute attribute : tag.parseAttributes())
									{
										if (attribute.getName().equals("rgb"))
										{
											sColor = attribute.getValue();
											if (tsExcludedColors.contains(sColor))
												bFonts[nCurrentFont-1] = true; 
										}
									}
								}
								else if (sTagName.equals("fonts"))
								{
									for (Attribute attribute : tag.parseAttributes())
									{
										if (attribute.getName().equals("count"))
										{
											sCount = attribute.getValue();
											nFontTotal = (new Integer(sCount)).intValue();
											bFonts = new boolean[nFontTotal];
											for(int i=0;i<nFontTotal;i++)
												bFonts[i] = false;
										}
										nCurrentFont = 0;
									}
								}
								else if (sTagName.equals("xf"))
								{
									if (bXfing)
									{
										nCurrentStyle++;
										for (Attribute attribute : tag.parseAttributes())
										{
											sAttName = attribute.getName();
											if (sAttName.equals("fontId"))
											{
												sFontNum = attribute.getValue();
												nThisFont = (new Integer(sFontNum)).intValue();
												if (bFonts[nThisFont])
													tsStyles.add((String)(new Integer(nCurrentStyle-1)).toString());
											}
										}
									}
								}
							}
							else if (rtRuleType == RULE_TYPE.INLINE_ELEMENT)
							{
								if (sTagName.equals("font"))
									nCurrentFont++;
								if (sTagName.equals("cellxfs"))
									bXfing = true;
							}
						}
						else if (tag.getTagType() == EndTagType.NORMAL ||
								 tag.getTagType() == EndTagType.UNREGISTERED)
						{
							if (rtRuleType == RULE_TYPE.INLINE_ELEMENT)
							{
								if (sTagName.equals("cellxfs"))
									bXfing = false;
							}							
						}
					}
				}
				closeInput();
				break;
			}
		}
		return tsStyles;
	}
	public int analyzeExcelGetSharedStringsCount()
	{
		TaggedFilterConfiguration tfc;
		RULE_TYPE rtRuleType;
		String sEntryName;
		String sTagName;
		String sCount;
		int iCute;
		int rslt=0;
		entries = zipFile.entries();
		while( entries.hasMoreElements() )
		{
			entry = entries.nextElement();
			sEntryName = entry.getName();
			iCute = sEntryName.lastIndexOf('/');
			if (iCute>-1)
				sEntryName = sEntryName.substring(iCute+1);
			if (sEntryName.equals("sharedStrings.xml"))
			{
				setupSharedStringsReader(entry);
				tfc = parameters.getTaggedConfig();
				while (nodeIterator.hasNext())
				{
					Segment segment = nodeIterator.next();
					if (segment instanceof Tag)
					{
						final Tag tag = (Tag) segment;
						sTagName = tag.getName();
						rtRuleType = tfc.getElementRuleTypeCandidate(sTagName);
						if (rtRuleType == RULE_TYPE.INLINE_ELEMENT)
						{
							if (tag.getName().equals("sst"))
							{
								for (Attribute attribute : tag.parseAttributes())
								{
									if (attribute.getName().equals("count"))
									{
										sCount = attribute.getValue();
										if (rslt==0) // keep uniqueCount if it was there
											rslt = (new Integer(sCount)).intValue();
									}
									else if (attribute.getName().equals("uniqueCount"))
									{
										sCount = attribute.getValue();
										rslt = (new Integer(sCount)).intValue();
									}
								}
								if (rslt>0)
									break;
							}
						}
					}
				}
				closeInput();
				break;
			}
		}
		return rslt;
	}
	private void setupSharedStringsReader(ZipEntry entry)
	{
		try
		{
			is = zipFile.getInputStream(entry);
			URL urlConfig = OpenXMLContentFilter.class.getResource("/net/sf/okapi/filters/openxml/excelConfiguration.yml");
			parameters = new YamlParameters(urlConfig); // DWH 7-16-09
			ruleState = new ExtractionRuleState(true, false);
			document = new StreamedSource(new InputStreamReader(is,"UTF-8"));
			nodeIterator = document.iterator();
		}
		catch(IOException e)
		{
			LOGGER.log(Level.WARNING,"Error opening XML file within zipped Excel file.");
			throw new OkapiIOException("Error opening XML file within zipped Excel file.");			
		}
	}
	private Iterator<Segment> createNodeIterator(ZipEntry ntry)
	{
		try {			
			URL urlConfig = OpenXMLContentFilter.class.getResource("/net/sf/okapi/filters/openxml/excelStylesConfiguration.yml"); // DWH 3-9-09
			parameters = new YamlParameters(urlConfig); // DWH 7-16-09
			ruleState = new ExtractionRuleState(true, false);
			is = zipFile.getInputStream(ntry);
			document = new StreamedSource(new InputStreamReader(is));
		} catch (IOException e) {
			OkapiIOException re = new OkapiIOException(e);
			throw new OkapiIOException("Filter could not open input stream"); // dies here, so return can't be null
		}
		return document.iterator();
	}
	private void closeInput()
	{
		try
		{
			is.close();
		}
		catch(IOException e)
		{
			LOGGER.log(Level.WARNING,"Error opening zipped Excel file.");
			throw new OkapiIOException("Error opening zipped Excel file.");
		}
		finally
		{
			is = null;
		}
	}
}
