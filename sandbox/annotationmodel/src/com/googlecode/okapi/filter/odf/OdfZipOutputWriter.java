package com.googlecode.okapi.filter.odf;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.googlecode.okapi.pipeline.AbstractPipelineStep;
import com.googlecode.okapi.pipeline.ResourceEvent;
import com.googlecode.okapi.pipeline.ResourceEvent.ResourceEventType;

public class OdfZipOutputWriter extends AbstractPipelineStep{
	
	ZipOutputStream output;
	
	public OdfZipOutputWriter() {
	}
	
	@Override
	public void handleEvent(ResourceEvent event) {
		if(event.type == ResourceEventType.StartDocument){
			
		}
		// TODO Auto-generated method stub
	}
	
	private void startDocument() throws IOException{
		output = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream("")));
		ZipEntry entry = new ZipEntry("xyz");
		output.putNextEntry(entry);
		//output.write(blah)
		
	}

}
