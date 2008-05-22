package net.sf.okapi.common.filters.myformat2;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.sf.okapi.common.pipeline.PipelineException;
import net.sf.okapi.common.pipeline.PipelineStep;
import net.sf.okapi.common.pipeline.PipelineStepStatus;
import net.sf.okapi.common.resource.ExtractionItem;
import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.IResource;
import net.sf.okapi.common.resource.IResourceBuilder;

public class MyFormat2Filter implements PipelineStep {
	private static final String VERSION = "1.0.0";
	
    public PipelineStepStatus execute(IResourceBuilder resourceBuilder)
			throws PipelineException {
    	try {       		
    		// Items are key\tText lines
       		int n;
	    	while ( true ) {
	    		String line = reader.readLine();
	    		if ( line == null ) break;
	    		n = line.indexOf('\t');
	    		if ( n > -1 ) {
	    			// Put the key in the buffer
	    			res.buffer.append(line.subSequence(0, n+1));
	    			// Create and fill the item
	    			IExtractionItem item = new ExtractionItem();
	    			item.setContent(line.substring(n));
	    			// Feed it to the output
	    			// We could do in one call if needed
	    			output.startExtractionItem(item);
	    			output.endExtractionItem(item);
	    			// Reset for next item
	    			res.buffer.setLength(0);
	    			res.buffer.append("\n"); // For after the item
	    		}
	    		else {
	    			res.buffer.append(line+"\n");
	    		}
	    	}
	    	output.endResource(res);
       	}
       	catch ( Exception e ) {
       		System.err.println(e.getLocalizedMessage());
       	}
       	
       	return PipelineStepStatus.DEFAULT;
	}

	public void finish(boolean success) throws PipelineException {
		// TODO Auto-generated method stub		
	}

	public String getName() {
		return MyFormat2Filter.class.toString();
	}

	public String getRevision() {
		return VERSION;
	}

	public void prepare() throws PipelineException {
		// TODO Auto-generated method stub		
	}

	public void setName(String name) {
		// TODO Auto-generated method stub		
	}
}
