package net.sf.okapi.filters.regex;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import net.sf.okapi.common.filters.IOutputFilter;
import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.IDocumentResource;
import net.sf.okapi.common.resource.IGroupResource;
import net.sf.okapi.common.resource.ISkeletonResource;

public class OutputFilter implements IOutputFilter {
	
	private OutputStream          output;
	private OutputStreamWriter    writer;
//	private CharsetEncoder        outputEncoder;
	//private final Logger          logger = LoggerFactory.getLogger("net.sf.okapi.logging");


	public void close () {
		try {
			if ( writer != null ) {
				writer.close();
				writer = null;
			}
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public void initialize (OutputStream output,
		String encoding,
		String targetLanguage)
	{
		this.output = output;
	}

	public void endContainer (IGroupResource resourceContainer) {
	}

	private void buildContent (IExtractionItem item) {
		try {
			if ( item.hasTarget() ) {
				writer.write(item.getTarget().toString());
			}
			else {
				writer.write(item.getSource().toString());
			}
		}
		catch ( IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void endExtractionItem (IExtractionItem item) {
		if ( item.isTranslatable() ) {
			buildContent(item);
		}
	}

	public void endResource (IDocumentResource resource) {
		close();
	}

	public void startContainer (IGroupResource resource) {
	}

	public void startExtractionItem (IExtractionItem item) {
	}

	public void startResource (IDocumentResource resource) {
		try {
			// Create the output writer from the provided stream
			writer = new OutputStreamWriter(
				new BufferedOutputStream(output), resource.getTargetEncoding());
			//TODO: maybe the outputEncoder won't be needed?
			//outputEncoder = Charset.forName(resource.getTargetEncoding()).newEncoder(); 
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

    public void skeletonContainer (ISkeletonResource resource) {
    	try {
    		//TODO: Handle line-break type, we need to output the original
    		writer.write(resource.toString());
    	}
    	catch ( IOException e ) {
    		throw new RuntimeException(e);
    	}
    }
    
}
