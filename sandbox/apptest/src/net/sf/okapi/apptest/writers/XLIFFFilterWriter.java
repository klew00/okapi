package net.sf.okapi.apptest.writers;

import java.io.OutputStream;
import java.util.List;

import net.sf.okapi.apptest.common.IEncoder;
import net.sf.okapi.apptest.common.IParameters;
import net.sf.okapi.apptest.filters.FilterEvent;
import net.sf.okapi.apptest.filters.IFilterWriter;
import net.sf.okapi.apptest.resource.Code;
import net.sf.okapi.apptest.resource.DocumentPart;
import net.sf.okapi.apptest.resource.Ending;
import net.sf.okapi.apptest.resource.StartDocument;
import net.sf.okapi.apptest.resource.StartGroup;
import net.sf.okapi.apptest.resource.StartSubDocument;
import net.sf.okapi.apptest.resource.TextFragment;
import net.sf.okapi.apptest.resource.TextUnit;
import net.sf.okapi.apptest.resource.TextFragment.TagType;
import net.sf.okapi.common.XMLWriter;

public class XLIFFFilterWriter implements IFilterWriter {

	private OutputStream output;
	private String outputPath;
	private String outputLang;
	private XMLWriter writer;
	private boolean inFile;
	private StartDocument startDoc;
	
	public void close () {
		if ( writer != null ) {
			writer.close();
			writer = null;
		}
	}

	public String getName () {
		return "XLIFFFilterWriter";
	}

	public IParameters getParameters () {
		return null;
	}

	public FilterEvent handleEvent (FilterEvent event) {
		switch ( event.getEventType() ) {
		case START:
			processStart();
			break;
		case FINISHED:
			processFinished();
			break;
		case START_DOCUMENT:
			processStartDocument((StartDocument)event.getResource());
			break;
		case END_DOCUMENT:
			processEndDocument((Ending)event.getResource());
			close();
			break;
		case START_SUBDOCUMENT:
			processStartSubDocument((StartSubDocument)event.getResource());
			break;
		case END_SUBDOCUMENT:
			processEndSubDocument((Ending)event.getResource());
			break;
		case START_GROUP:
			processStartGroup((StartGroup)event.getResource());
			break;
		case END_GROUP:
			processEndGroup((Ending)event.getResource());
			break;
		case TEXT_UNIT:
			processTextUnit((TextUnit)event.getResource());
			break;
		case DOCUMENT_PART:
			processDocumentPart((DocumentPart)event.getResource());
			break;
		}
		return event;
	}

	private void processStart () {
		// Create the output
		createWriter();
	}

	private void processFinished () {
		close();
	}
	
	private void processStartDocument (StartDocument resource) {
		startDoc = resource;
		writer.writeStartDocument();
		writer.writeStartElement("xliff");
		writer.writeAttributeString("version", "1.1");
		writer.writeAttributeString("xmlns", "urn:oasis:names:tc:xliff:document:1.1");
		writer.writeLineBreak();
	}

	private void processEndDocument (Ending resource) {
		if ( inFile ) writeEndFile();
		writer.writeEndElementLineBreak(); // xliff
		writer.writeEndDocument();
	}

	private void processStartSubDocument (StartSubDocument resource) {
		writeStartFile(resource.getName());
	}

	private void writeStartFile (String original) {
		writer.writeStartElement("file");
		writer.writeAttributeString("original", (original!=null) ? original : "unknown");
		writer.writeAttributeString("source-language", startDoc.getLanguage());
		writer.writeAttributeString("target-language", outputLang);
		writer.writeAttributeString("datatype", "TODO");
		writer.writeLineBreak();
		inFile = true;
		writer.writeStartElement("body");
		writer.writeLineBreak();
	}
	
	private void processEndSubDocument (Ending resource) {
		writeEndFile();
	}

	private void writeEndFile () {
		writer.writeEndElementLineBreak(); // body
		writer.writeEndElementLineBreak(); // file
		inFile = false;
	}
	
	private void processStartGroup (StartGroup resource) {
		if ( !inFile ) writeStartFile(startDoc.getName());
		writer.writeStartElement("group");
		writer.writeAttributeString("id", resource.getId());
		if ( resource.getName() != null ) {
			writer.writeAttributeString("resname", resource.getName());
		}
		writer.writeLineBreak();
	}

	private void processEndGroup (Ending resource) {
		writer.writeEndElementLineBreak(); // group
	}

	private void processTextUnit (TextUnit resource) {
		if ( !inFile ) writeStartFile(startDoc.getName());

		writer.writeStartElement("trans-unit");
		writer.writeAttributeString("id", resource.getId());
		if ( resource.getName() != null ) {
			writer.writeAttributeString("resname", resource.getName());
		}
		writer.writeLineBreak();
		
		writer.writeStartElement("source");
		writer.writeAttributeString("xml:lang", startDoc.getLanguage());
		TextFragment tf = resource.getSourceContent(); 
		writer.writeRawXML(getContent(tf, resource.getEncoder()));
		writer.writeEndElementLineBreak(); // source
		
		if ( resource.hasTarget(outputLang) ) {
			writer.writeStartElement("target");
			writer.writeAttributeString("xml:lang", outputLang);
			tf = resource.getTargetContent(outputLang); 
			writer.writeRawXML(getContent(tf, resource.getEncoder()));
			writer.writeEndElementLineBreak(); // target
		}
		
		writer.writeEndElementLineBreak(); // trans-unit
	}

	private void processDocumentPart (DocumentPart resource) {
	}

	public void setOptions (String language,
		String defaultEncoding)
	{
		outputLang = language;
		// Encoding is always UTF-8
	}

	public void setOutput (String path) {
		close(); // Make sure previous is closed
		this.outputPath = path;
	}

	public void setOutput (OutputStream output) {
		close(); // Make sure previous is closed
		//this.output = output; // then assign the new stream
		assert(false); //TODO: implement support
	}

	public void setParameters (IParameters params) {
	}

	private void createWriter () {
		// Create the output writer from the provided stream
		// or from the path if there is no stream provided
		if ( output != null ) {
			assert(false); //TODO: need to support XMLWriter with stream
		}
		writer = new XMLWriter();
		writer.create(outputPath);
		inFile = false;
	}

	private String getContent (TextFragment tf,
		IEncoder encoder)
	{
		if ( !tf.hasCode() ) { // The easy output
			if ( encoder == null ) {
				return tf.toString();
			}
			else {
				return encoder.encode(tf.toString(), 0);				
			}
		}

		List<Code> codes = tf.getCodes();
		StringBuilder tmp = new StringBuilder();
		String text = tf.getCodedText();
		Code code;
		for ( int i=0; i<text.length(); i++ ) {
			switch ( text.charAt(i) ) {
			case TextFragment.MARKER_OPENING:
				code = codes.get(TextFragment.toIndex(text.charAt(++i)));
				tmp.append(String.format("<bpt id=\"%d\">", code.getID()));
				tmp.append(encoder.encode(code.getData(), 0));
				tmp.append("</bpt>");
				break;
			case TextFragment.MARKER_CLOSING:
				code = codes.get(TextFragment.toIndex(text.charAt(++i)));
				tmp.append(String.format("<ept id=\"%d\">", code.getID()));
				tmp.append(encoder.encode(code.getData(), 0));
				tmp.append("</ept>");
				break;
			case TextFragment.MARKER_ISOLATED:
			case TextFragment.MARKER_SEGMENT:
				code = codes.get(TextFragment.toIndex(text.charAt(++i)));
				switch ( code.getTagType() ) {
				case PLACEHOLDER:
				case SEGMENTHOLDER:
					tmp.append(String.format("<ph id=\"%d\">", code.getID()));
					tmp.append(encoder.encode(code.getData(), 0));
					tmp.append("</ph>");
					break;
				case OPENING:
				case CLOSING:
					tmp.append(String.format("<it id=\"%d\" pos=\"%s\">",
						code.getID(),
						(code.getTagType()==TagType.OPENING) ? "open" : "close"));
					tmp.append(encoder.encode(code.getData(), 0));
					tmp.append("</it>");
					break;
				}
				break;
			default:
				tmp.append(encoder.encode(text.charAt(i), 0));
				break;
			}
		}
		return tmp.toString();
	}

}
