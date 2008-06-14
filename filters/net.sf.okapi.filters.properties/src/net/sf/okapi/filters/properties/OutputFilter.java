package net.sf.okapi.filters.properties;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import net.sf.okapi.common.filters.IOutputFilter;
import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.IDocumentResource;
import net.sf.okapi.common.resource.IGroupResource;
import net.sf.okapi.common.resource.ISkeletonResource;

public class OutputFilter implements IOutputFilter {

	private Resource              res;
	private OutputStream          output;
	private String                encoding;
	private OutputStreamWriter    writer;
	private CharsetEncoder        outputEncoder;
	
	public void initialize (OutputStream output,
		String encoding,
		String targetLanguage) {
		this.output = output;
		this.encoding = encoding;
		// Not used: targetLanguage
	}

	public void close ()
	{
		try {
			if ( writer != null ) {
				writer.close();
				writer = null;
			}
		}
		catch ( Exception e ) {
			throw new RuntimeException(e);
		}
	}

	public void endContainer (IGroupResource resource) {
	}

	public void endExtractionItem (IExtractionItem item)
	{
		try {
			// Write the buffer
			writer.write(res.sklRes.toString());
			// Then write the item content
			if ( item.hasTarget() ) {
				writer.write(escape(item.getTarget().toString()));
			}
			else {
				writer.write(escape(item.getSource().toString()));
			}
			if ( res.endingLB ) writer.write(res.lineBreak);
		}
		catch ( Exception e ) {
			System.err.println(e.getLocalizedMessage());
		}
	}

	public void endResource (IDocumentResource resource) {
		try {
			writer.write(res.sklRes.toString());
		}
		catch ( Exception e ) {
			System.err.println(e.getLocalizedMessage());
		}
		finally {
			try {
				close();
			}
			catch ( Exception e ) {
				System.err.println(e.getLocalizedMessage());
			}
		}
	}

	public void startContainer (IGroupResource resource) {
	}

	public void startExtractionItem (IExtractionItem item) {
	}

	public void startResource (IDocumentResource resource) {
		try {
			// Save the resource for later use
			res = (Resource)resource;
			
			// Create the output writer from the provided stream
			writer = new OutputStreamWriter(
				new BufferedOutputStream(output), encoding);
			outputEncoder = Charset.forName(encoding).newEncoder(); 
			
			// Write the buffer
			writer.write(res.sklRes.toString());
		}
		catch ( Exception e ) {
			throw new RuntimeException(e);
		}
	}

    public void skeletonContainer (ISkeletonResource resource) {
    	// In this writer, this is done in the other handlers
    	// (for now)
    }
    
	private String escape (String text) {
		StringBuilder escaped = new StringBuilder();
		for ( int i=0; i<text.length(); i++ ) {
			if ( text.codePointAt(i) > 127 ) {
				if ( res.params.escapeExtendedChars ) {
					escaped.append(String.format("\\u%04x", text.codePointAt(i))); 
				}
				else {
					if ( outputEncoder.canEncode(text.charAt(i)) )
						escaped.append(text.charAt(i));
					else
						escaped.append(String.format("\\u%04x", text.codePointAt(i)));
				}
			}
			else {
				switch ( text.charAt(i) ) {
				case '\n':
					escaped.append("\\n");
					break;
				case '\t':
					escaped.append("\\t");
					break;
				default:
					escaped.append(text.charAt(i));
					break;
				}
			}
		}
		return escaped.toString();
	}

}
