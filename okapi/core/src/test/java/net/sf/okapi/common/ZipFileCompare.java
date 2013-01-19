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

package net.sf.okapi.common;

import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.HashMap;

/**
 * This class compares two zip files to see if they have
 * the same contents.  The zipsExactlyTheSame method takes
 * two files specified by their file paths and indicates
 * by calling FileCompare whether all files in the zip
 * are exactly the same as each other.  This can be used
 * to compare zip file output with a gold standard zip file.  
 */


public class ZipFileCompare {

	//static enum DocumentLocation {TS, CONTEXT, MESSAGE};
	
	private FileCompare fc=null;
	public ZipFileCompare()
	{
		fc = new FileCompare();
	}
	
	public boolean compareFiles (String type,
		String out, 
		String gold, 
		String encoding,
		boolean ignoreEmtpyLines)
	{
		ZipFile outZipFile,goldZipFile;
		Enumeration<? extends ZipEntry> outEntries=null;
		Enumeration<? extends ZipEntry> goldEntries=null;
		
		HashMap<String, ZipEntry> outZipMap = new HashMap<String, ZipEntry>();
		HashMap<String, ZipEntry> goldZipMap = new HashMap<String, ZipEntry>();
		
		try {
			File outZip = new File(out);
			outZipFile = new ZipFile(outZip);
			outEntries = outZipFile.entries();
		}catch(Exception e) {
			System.err.println("ZipCompare:  Output file "+out+" not found.\n");
			return false;
		}

		try {
			File goldZip = new File(gold);
			goldZipFile = new ZipFile(goldZip);
			goldEntries = goldZipFile.entries();
		} catch(Exception e) {
			System.err.println("ZipCompare:  Gold file "+gold+" not found.\n");
			return false;
		}
		
		while( outEntries.hasMoreElements() ){
			ZipEntry ze = outEntries.nextElement();
			outZipMap.put(ze.getName(), ze);
		}
		
		while( goldEntries.hasMoreElements() ){
			ZipEntry ze = goldEntries.nextElement();
			goldZipMap.put(ze.getName(), ze);
		}
		
		if( outZipMap.keySet().size() != goldZipMap.keySet().size() ){
			System.err.println("Difference in number of files:");
			System.err.println(" out: "+outZipMap.keySet().size());
			System.err.println("gold: "+goldZipMap.keySet().size()+"\n");
			return false;
		}

		if( !outZipMap.keySet().equals(goldZipMap.keySet()) ){
			System.err.println("Filenames do not match between the zipfiles\n");
			return false;
		}

		boolean failure = false;
		int identicals = 0;
		
		try {
			for (String filename: outZipMap.keySet()) {

				ZipEntry oze= outZipMap.get(filename);
				ZipEntry gze= goldZipMap.get(filename);

				System.out.println("Comparing file: "+filename);
				
				InputStream ois = outZipFile.getInputStream(oze);
				InputStream gis = goldZipFile.getInputStream(gze);

				boolean same;
				if(type.equals("PerLine")){
					same = fc.compareFilesPerLines (ois, gis, "UTF-8");
				}else if(type.equals("PerLineIgnoreEmpty")){
					same = fc.compareFilesPerLines (ois, gis, "UTF-8", ignoreEmtpyLines );
				}else{ 
					same = fc.filesExactlyTheSame(ois,gis);
				}
					
				if (same){
					identicals++;
				}else{
					System.err.println("Output and Gold Entry "+filename+" differ\n");
					if(! failure){
						failure = true;
					}
				}
			}
		}catch(Exception e) {
			System.err.println("Error opening/reading file\n");
			return false;
		}

		if( !failure ){
			System.out.println("\nAll "+identicals+" pass comparison.\n");
			return true;
		}else{
			return false;
		}
	}

	public boolean compareFilesPerLines(String out, String gold, String encoding){
		return compareFiles("PerLine", out, gold, encoding, false);
	}
	
	public boolean compareFilesPerLines(String out, String gold, String encoding, boolean ignoreEmtpyLines){
		return compareFiles("PerLineIgnoreEmpty", out, gold, encoding, ignoreEmtpyLines);
	}
	
	public boolean filesExactlyTheSame (String out, String gold){
		return compareFiles("ExactlyTheSame", out, gold, null, false);
	}
}
