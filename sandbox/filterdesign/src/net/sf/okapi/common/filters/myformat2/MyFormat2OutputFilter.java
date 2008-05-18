package net.sf.okapi.common.filters.myformat2;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import net.sf.okapi.common.pipeline.IResourceBuilder;
import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.IResource;
import net.sf.okapi.common.resource.IResourceContainer;

public class MyFormat2OutputFilter implements IResourceBuilder {

    private OutputStream     out;
    private MyResource2      res;
    private Writer           writer;   

    public MyFormat2OutputFilter (OutputStream out) {
        this.out = out;
    }
    
    public void startResource (IResource resource) {
    	try {
    		res = (MyResource2)resource;
    		writer = new OutputStreamWriter(out);
    		writer.write(res.buffer.toString());
    	}
    	catch ( Exception e ) {
    		System.err.println(e.getLocalizedMessage());
    	}
    }

    public void endResource (IResource resource) {
    	try {
    		writer.write(res.buffer.toString());
    	}
    	catch ( Exception e ) {
    		System.err.println(e.getLocalizedMessage());
    	}
    	finally {
    		try {
    			writer.close();
    		}
        	catch ( Exception e ) {
        		System.err.println(e.getLocalizedMessage());
        	}
    	}
    }

    public void startContainer (IResourceContainer resourceContainer) {
    }

    public void endContainer (IResourceContainer resourceCntainer) {
    }

    public void startExtractionItem (IExtractionItem extractionItem) {
    	try {
    		writer.write(res.buffer.toString());
    	}
    	catch ( Exception e ) {
    		System.err.println(e.getLocalizedMessage());
    	}
    }

    public void endExtractionItem(IExtractionItem extractionItem) {
    	try {
    		writer.write(extractionItem.getContent());
    	}
    	catch ( Exception e ) {
    		System.err.println(e.getLocalizedMessage());
    	}
    }

}
