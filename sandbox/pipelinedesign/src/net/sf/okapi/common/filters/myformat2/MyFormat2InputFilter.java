package net.sf.okapi.common.filters.myformat2;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.sf.okapi.common.filters.IInputFilter;
import net.sf.okapi.common.pipeline.IResourceBuilder;
import net.sf.okapi.common.resource.ExtractionItem;
import net.sf.okapi.common.resource.IExtractionItem;

public class MyFormat2InputFilter implements IInputFilter{

    private IResourceBuilder output;
    private InputStream      input;
    private BufferedReader   reader;

    
    public MyFormat2InputFilter (InputStream input) {
    	this.input = input;
    }
    
    public void setOutput (IResourceBuilder output) {
        this.output = output;
    }
    
    public void convert () {
       	try {
       		MyResource2 res = new MyResource2();
       		reader = new BufferedReader(new InputStreamReader(input));
       		output.startResource(res);

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
    }

}
