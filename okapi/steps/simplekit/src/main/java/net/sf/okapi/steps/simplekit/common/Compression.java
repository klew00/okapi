/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.steps.simplekit.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Compression {
	
	public static void zipDirectory (String dir,
		String zipPath)
		throws IOException
	{
		ZipOutputStream os = null;
		try { 
			File d = new File(dir); 
			if( !d.isDirectory() ) return; 
			os = new ZipOutputStream(new FileOutputStream(zipPath)); 
            addDirectory(d, os, null); 
        } 
		finally {
			if ( os != null ) os.close();
		}
	}
	
	private static void addDirectory (File dir,
		ZipOutputStream os,
		String subDir)
		throws FileNotFoundException, IOException
	{
        FileInputStream input = null;
        try {
        	File[] aFiles = dir.listFiles(); 
        	byte[] aBuf = new byte[1024]; 
        	for (int i=0; i<aFiles.length; i++) {
        		// Go recursively if the entry is a sub-directory
        		if( aFiles[i].isDirectory() ) {
        			addDirectory(aFiles[i], os,
        				((subDir==null)? "" : subDir + "\\") + aFiles[i].getName());
        			continue;
        		}
        		// Or add the file to the zip
        		input = new FileInputStream(aFiles[i].getPath()); 
        		os.putNextEntry(new ZipEntry(
        			((subDir==null)? "" : subDir + "\\") + aFiles[i].getName())); 
        		int nCount; 
        		while( (nCount = input.read(aBuf)) > 0 ) { 
        			os.write(aBuf, 0, nCount); 
        		}
        		os.closeEntry();
        		input.close();
        	}
        }
        finally {
        	if ( input != null ) input.close(); 
        }
	}

}

