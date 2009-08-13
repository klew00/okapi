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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * This class does a simple byte-to-byte compare.  The
 * filesExactlyTheSame method takes two files specified
 * by URIs and returns a boolean indicating whether they
 * are the same. 
 */

public class FileCompare {

	public boolean filesExactlyTheSame(String outputFilePath, String goldFilePath)
	{
		FileInputStream ois,gis;
		File ofil,gfil;
		boolean bRslt=false;
		try
		{
			ofil = new File(outputFilePath);
			gfil = new File(goldFilePath);
			ois = new FileInputStream(ofil);
			gis = new FileInputStream(gfil);
			bRslt = filesExactlyTheSame(ois,gis);
		}
		catch(Exception e)
		{
			bRslt = false;
		}
		return bRslt;
	}
	
	public boolean filesExactlyTheSame(URI outputFileURI, URI goldFileURI)
	{
		InputStream ois,gis;
		try
		{
			ois = outputFileURI.toURL().openStream();
			gis = goldFileURI.toURL().openStream();
		}
		catch(IOException e)
		{
			return false;
		}
		return filesExactlyTheSame(ois,gis);
	}
	
	public boolean filesExactlyTheSame(InputStream ois, InputStream gis)
	{
		final int BUFSIZ=4096;
		int ored,gred;
		byte[] obuf = new byte[BUFSIZ];
		byte[] gbuf = new byte[BUFSIZ];
		try
		{
			while(ois.available()>0 && gis.available()>0)
			{
				ored = ois.read(obuf);
				gred = gis.read(gbuf);
				if (ored!=gred)
				{
					ois.close();
					gis.close();
					return false;
				}
				if (ored>0)
				{
					for(int i=0; i<ored; i++)
					{
						if (obuf[i]!=gbuf[i])
						{
							ois.close();
							gis.close();
							return false;
						}
					}
				}
				else
					return true;
			}
			return true;
		}
		catch(Exception e) {};
		return false;
	}
}
