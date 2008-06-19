package net.sf.okapi.filters.xliff;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.common.filters.IOutputFilter;
import net.sf.okapi.common.resource.IContainer;
import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.IDocumentResource;
import net.sf.okapi.common.resource.IGroupResource;
import net.sf.okapi.common.resource.ISkeletonResource;
import net.sf.okapi.common.resource.InvalidContentException;

public class OutputFilter implements IOutputFilter {
	
	private OutputStream          output;
	private OutputStreamWriter    writer;
	private XLIFFContent          xliffCont;
	private Resource              res;
	private CharsetEncoder        outputEncoder;
	private final Logger          logger = LoggerFactory.getLogger("net.sf.okapi.logging");


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
		String targetLanguage) {
		this.output = output;
		xliffCont = new XLIFFContent();
	}

	public void endContainer (IGroupResource resourceContainer) {
	}

	private void buildContent (IContainer content)
	{
		try {
			if ( res.needTargetElement ) {
				writer.write(String.format("<target xml:lang=\"%s\">", res.getTargetLanguage()));
			}
			try {
				content.setContent(content.getCodedText(), res.inlineCodes);
			}
			catch ( InvalidContentException e ) {
				logger.error("Inline code problem:", e);
			}
			String tmp = xliffCont.setContent(content).toString(0, false, true);
			writer.write(escapeChars(tmp));
			if ( res.needTargetElement ) {
				writer.write("</target>\n");
			}
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}
	
	private String escapeChars (String text) {
		StringBuilder escaped = new StringBuilder(text.length());
		for ( int i=0; i<text.length(); i++ ) {
			if ( outputEncoder.canEncode(text.charAt(i)) )
				escaped.append(text.charAt(i));
			else
				escaped.append(String.format("&#x%04x;", text.codePointAt(i)));
		}
		return escaped.toString();
	}
	
	public void endExtractionItem (IExtractionItem item) {
		if ( item.isTranslatable() ) {
			if ( item.hasTarget() ) {
				buildContent(item.getTarget());
			}
			else {
				buildContent(item.getSource());
			}
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
			res = (Resource)resource;
			// Create the output writer from the provided stream
			writer = new OutputStreamWriter(
				new BufferedOutputStream(output), res.getTargetEncoding());
			writer.write("<?xml version=\"1.0\" encoding=\""
				+ res.getTargetEncoding() + "\"?>");
			outputEncoder = Charset.forName(res.getTargetEncoding()).newEncoder(); 
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

    public void skeletonContainer (ISkeletonResource resource) {
    	try {
    		writer.write(escapeChars(resource.toString()));
    	}
    	catch ( IOException e ) {
    		throw new RuntimeException(e);
    	}
    }
    
}
