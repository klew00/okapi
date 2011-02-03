/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.applications.rainbow.batchconfig;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;

import net.sf.okapi.applications.rainbow.pipeline.PipelineStorage;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.ReferenceParameter;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.pipeline.IPipeline;
import net.sf.okapi.common.pipeline.IPipelineStep;

public class BatchConfiguration {
	
	private static final int MAXBUFFERSIZE = 1024*8; 
	private static final int MAXBLOCKLEN = 65000;
	private static final String SIGNATURE = "batchConf";
	private static final int VERSION = 1;

	public void exportConfiguration (String configPath,
		IPipeline pipeline,
		IFilterConfigurationMapper fcMapper)
	{
		DataOutputStream dos = null;
		
		try {
			// Prepare the output
			dos = new DataOutputStream(new FileOutputStream(configPath));
			dos.writeUTF(SIGNATURE);
			dos.writeInt(VERSION);

			//=== Section 1: the dereferenced files of the pipeline's parameters
			// int = id (-1 mark the end)
			// String = extension
			// String = content
			
			// Go through each step of the pipeline
			for ( IPipelineStep step : pipeline.getSteps() ) {
				// Get the parameters for that step
				IParameters params = step.getParameters();
				if ( params == null ) continue;
				// Get all methods for the parameters object
				Method[] methods = params.getClass().getMethods();
				// Look for references
				int id = 0;
				for ( Method m : methods ) {
					if ( Modifier.isPublic(m.getModifiers() ) && m.isAnnotationPresent(ReferenceParameter.class)) {
						String refPath = (String)m.invoke(params);
						harvestReferencedFile(dos, ++id, refPath);
					}
				}
			}
			
			// Last ID=-1 to mark no more references
			dos.writeInt(-1);

			
			//=== Section 2: The pipeline itself
			
			// OK to use null, because the available steps are not used for writing
			PipelineStorage store = new PipelineStorage(null);
			store.write(pipeline);
			writeLongString(dos, store.getStringOutput());
			
			
			
			//=== Section 3: The filter configurations

			// Get the number of custom configurations
			Iterator<FilterConfiguration> iter = fcMapper.getAllConfigurations();
			int count = 0;
			while ( iter.hasNext() ) {
				if ( iter.next().custom ) count++;
			}
			dos.writeInt(count);

			// Write each filter configuration
			iter = fcMapper.getAllConfigurations();
			while ( iter.hasNext() ) {
				FilterConfiguration fc = iter.next();
				if ( fc.custom ) {
					dos.writeUTF(fc.configId);
					IParameters params = fcMapper.getCustomParameters(fc);
					dos.writeUTF(params.toString());
				}
			}
			
		}
		catch ( IllegalArgumentException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch ( IllegalAccessException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch ( InvocationTargetException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch ( FileNotFoundException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch ( IOException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			// Close the output file
			if ( dos != null ) {
				try {
					dos.close();
				}
				catch ( IOException e ) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void installConfiguration (String configPath,
		String outputDir)
	{
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(configPath, "r");
			String tmp = raf.readUTF(); // signature
			if ( !SIGNATURE.equals(tmp) ) {
				throw new OkapiIOException("Invalid file format.");
			}
			int version = raf.readInt(); // Version info
			if ( version != VERSION ) {
				throw new OkapiIOException("Invalid version.");
			}
			
			Util.createDirectories(outputDir);
			
			//=== Section 1: references data

			// Build a lookup table to the references
			HashMap<Integer, Long> refMap = new HashMap<Integer, Long>();
			long pos = raf.getFilePointer();
			int id = raf.readInt(); // First ID or end of section marker
			while ( id != -1 ) {
				// Add the entry in the lookup table
				refMap.put(id, pos);
				// Skip over the data to move to the next reference
				long size = raf.readLong();
				if ( size > 0 ) {
					raf.seek(raf.getFilePointer()+size);
				}
				// Then get the information for next entry
				pos = raf.getFilePointer(); // Position
				id = raf.readInt(); // ID
			}
		
			//=== Section 2 : the pipeline itself
			
			tmp = readLongString(raf);
			
//			PipelineStorage store = new PipelineStorage(null, tmp);
//			store.read();
//			
//			store.write(pipeline);
//			writeLongString(dos, store.getStringOutput());
			
			//=== Section 3 : the filter configurations
			
			// Get the number of filter configurations
			int count = raf.readInt();
			
			// Read each one
			for ( int i=0; i<count; i++ ) {
				String configId = raf.readUTF();
				String data = raf.readUTF();
				// And create the parameters file
				String path = outputDir + File.separator + configId + ".fprm";
				PrintWriter pw = new PrintWriter(path, "UTF-8"); 
				pw.write(data);
				pw.close();
			}
			
		}
		catch ( FileNotFoundException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch ( IOException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if ( raf != null ) {
				try {
					raf.close();
				}
				catch ( IOException e ) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void harvestReferencedFile (DataOutputStream dos,
		int id,
		String refPath)
		throws IOException
	{
		FileInputStream fis = null;
		try {
			dos.writeInt(id);
			String ext = Util.getExtension(refPath);
			dos.writeUTF(ext);

			// Deal with empty references
			if ( Util.isEmpty(refPath) ) {
				dos.writeLong(0);
				return;
			}
			// Else: copy the content of the referenced file
			
			// Write the size of the file
			File file = new File(refPath);
			long size = file.length();
			dos.writeLong(size);
			
			// Write the content
			if ( size > 0 )  {
				fis = new FileInputStream(refPath);
				int bufferSize = Math.min(fis.available(), MAXBUFFERSIZE);
				byte[] buffer = new byte[bufferSize];
				int bytesRead = fis.read(buffer, 0, bufferSize);
				while ( bytesRead > 0 ) {
					dos.write(buffer, 0, bufferSize);
					bufferSize = Math.min(fis.available(), MAXBUFFERSIZE);
					bytesRead = fis.read(buffer, 0, bufferSize);
				}
			}
		}
		finally {
			if ( fis != null ) {
				fis.close();
			}
		}
	}
	
	private void writeLongString (DataOutputStream dos,
		String data)
		throws IOException
	{
		int r = (data.length() % MAXBLOCKLEN);
		int n = (data.length() / MAXBLOCKLEN);
		int count = n + ((r > 0) ? 1 : 0);
		
		dos.writeInt(count); // Number of blocks
		int pos = 0;
	
		// Write the full blocks
		for ( int i=0; i<n; i++ ) {
			dos.writeUTF(data.substring(pos, pos+MAXBLOCKLEN));
			pos += MAXBLOCKLEN;
		}
		// Write the remaining text
		if ( r > 0 ) {
			dos.writeUTF(data.substring(pos));
		}
	}

	private String readLongString (RandomAccessFile raf)
		throws IOException
	{
		StringBuilder tmp = new StringBuilder();
		int count = raf.readInt();
		for ( int i=0; i<count; i++ ) {
			tmp.append(raf.readUTF());
		}
		return tmp.toString();
	}

}
