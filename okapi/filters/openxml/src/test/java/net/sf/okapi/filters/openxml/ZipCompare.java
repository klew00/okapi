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

import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sf.okapi.common.FileCompare;

/**
 * This class compares two zip files to see if they have
 * the same contents.  The zipsExactlyTheSame method takes
 * two files specified by their file paths and indicates
 * by calling FileCompare whether all files in the zip
 * are exactly the same as each other.  This can be used
 * to compare zip file output with a gold standard zip file.  
 */


public class ZipCompare {

	Logger LOGGER = LoggerFactory.getLogger(ZipCompare.class.getName());
	private FileCompare fc=null;
	public ZipCompare()
	{
		fc = new FileCompare();
	}
	
	public boolean zipsExactlyTheSame(String out, String gold)
	{
		boolean bRslt=true,bFoundit=false;
		ZipFile oZipFile,gZipFile;
		ZipEntry oZipEntry,gZipEntry;
		String sOEntryName,sGEntryName;
		InputStream ois,gis;
		Enumeration<? extends ZipEntry> oEntries=null;
		Enumeration<? extends ZipEntry> gEntries=null;
		try
		{
			File oZip = new File(out);
			oZipFile = new ZipFile(oZip);
			oEntries = oZipFile.entries();
		}
		catch(Exception e)
		{
			LOGGER.info("ZipCompare:  Output file "+out+" not found.");
//			throw new OkapiFileNotFoundException("Output file "+out+" not found.");
			return false;
		}
		try
		{
			File gZip = new File(gold);
			gZipFile = new ZipFile(gZip);
			gEntries = gZipFile.entries();
		}
		catch(Exception e)
		{
			LOGGER.info("ZipCompare:  Gold file "+gold+" not found.");
//			throw new OkapiFileNotFoundException("Gold file "+gold+" not found.");
			return false;
		}
		while( oEntries.hasMoreElements() )
		{
			oZipEntry = oEntries.nextElement();
			sOEntryName = oZipEntry.getName();
			bFoundit = false;
			while(gEntries.hasMoreElements())
			{
				gZipEntry = gEntries.nextElement();
				sGEntryName = gZipEntry.getName();
				if (sOEntryName.equals(sGEntryName))
				{
					bFoundit = true;
					try
					{
						ois = oZipFile.getInputStream(oZipEntry);
						gis = gZipFile.getInputStream(gZipEntry);
					}
					catch(Exception e)
					{
						return false;
					}
					bRslt = fc.filesExactlyTheSame(ois,gis);
					if (!bRslt)
					{
						LOGGER.info("Output and Gold Entry "+sOEntryName+" differ"); // DWH 7-16-09
						return false;
					}
					break;
				}
			}
			if (!bFoundit)
			{
				LOGGER.info("Output entry "+sOEntryName+" not found in gold."); // DWH 7-16-09
				return false;
			}
		}
		while( gEntries.hasMoreElements() )
		{
			gZipEntry = gEntries.nextElement();
			sGEntryName = gZipEntry.getName();
			bFoundit = false;
			while(oEntries.hasMoreElements())
			{
				oZipEntry = oEntries.nextElement();
				sOEntryName = oZipEntry.getName();
				if (sGEntryName.equals(sOEntryName))
				{
					bFoundit = true;
					break;
				}
			}
			if (!bFoundit)
			{
				LOGGER.info("Gold entry "+sGEntryName+" not found in output."); // DWH 7-16-09
				return false;
			}
		}
		return bRslt;
	}
}
