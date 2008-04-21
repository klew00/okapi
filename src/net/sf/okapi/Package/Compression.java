/*===========================================================================*/
/* Copyright (C) 2008 ENLASO Corporation, Okapi Development Team             */
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
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.Package;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Compression {
	
	public static void zipDirectory (String p_sDir,
		String p_sZipPath)
		throws IOException
	{
		ZipOutputStream OS = null;
		try { 
			File D = new File(p_sDir); 
			if( !D.isDirectory() ) return; 
			OS = new ZipOutputStream(new FileOutputStream(p_sZipPath)); 
            addDirectory(D, OS, null); 
        } 
		finally {
			if ( OS != null ) OS.close();
		}
	}
	
	private static void addDirectory (File p_Dir,
		ZipOutputStream p_OS,
		String p_sSubdir)
		throws FileNotFoundException, IOException
	{
        FileInputStream IS = null;
        try {
        	File[] aFiles = p_Dir.listFiles(); 
        	byte[] aBuf = new byte[1024]; 
        	for (int i=0; i<aFiles.length; i++) {
        		// Go recursively if the entry is a sub-directory
        		if( aFiles[i].isDirectory() ) {
        			addDirectory(aFiles[i], p_OS,
        				((p_sSubdir==null)? "" : p_sSubdir + "\\") + aFiles[i].getName());
        			continue;
        		}
        		// Or add the file to the zip
        		IS = new FileInputStream(aFiles[i].getPath()); 
        		p_OS.putNextEntry(new ZipEntry(
        			((p_sSubdir==null)? "" : p_sSubdir + "\\") + aFiles[i].getName())); 

        		int nCount; 
        		while( (nCount = IS.read(aBuf)) > 0 ) { 
        			p_OS.write(aBuf, 0, nCount); 
        		}
        		p_OS.closeEntry();
        	}
        }
        finally {
        	if ( IS != null ) IS.close(); 
        }
	}

}

