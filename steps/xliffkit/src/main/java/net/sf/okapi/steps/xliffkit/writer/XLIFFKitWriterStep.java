/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.xliffkit.writer;

import java.io.OutputStream;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.filterwriter.XLIFFContent;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;

@UsingParameters()
public class XLIFFKitWriterStep extends BasePipelineStep implements IFilterWriter {

	private static final String RESTYPEVALUES = 
		";auto3state;autocheckbox;autoradiobutton;bedit;bitmap;button;caption;cell;"
		+ "checkbox;checkboxmenuitem;checkedlistbox;colorchooser;combobox;comboboxexitem;"
		+ "comboboxitem;component;contextmenu;ctext;cursor;datetimepicker;defpushbutton;"
		+ "dialog;dlginit;edit;file;filechooser;fn;font;footer;frame;grid;groupbox;"
		+ "header;heading;hedit;hscrollbar;icon;iedit;keywords;label;linklabel;list;"
		+ "listbox;listitem;ltext;menu;menubar;menuitem;menuseparator;message;monthcalendar;"
		+ "numericupdown;panel;popupmenu;pushbox;pushbutton;radio;radiobuttonmenuitem;rcdata;"
		+ "row;rtext;scrollpane;separator;shortcut;spinner;splitter;state3;statusbar;string;"
		+ "tabcontrol;table;textbox;togglebutton;toolbar;tooltip;trackbar;tree;uri;userbutton;"
		+ "usercontrol;var;versioninfo;vscrollbar;window;";

	private XMLWriter writer;
	private XLIFFContent xliffCont;
	private LocaleId srcLang;
	private LocaleId trgLang;
	private boolean inFile;
	private String docMimeType;
	private String docName;
	private String outputPath;
	private String inputEncoding;
	private String configId;

	public XLIFFKitWriterStep() {
		super();
		xliffCont = new XLIFFContent();
	}
	
	public String getDescription () {
		return "Generate an XLIFF translation kit. Expects: filter events. Sends back: filter events.";
	}

	public String getName () {
		return "XLIFF Kit Writer";
	}

	public void close() {
		if ( writer != null ) {
			writer.close();
		}
	}

	public EncoderManager getEncoderManager() {
		return null;
	}

	public void setOptions(LocaleId locale, String defaultEncoding) {
		trgLang = locale;
		// ignore encoding: always use UTF-8		
	}

	public void setOutput(String path) {
		outputPath = path;		
	}

	public void setOutput(OutputStream output) {
		// TODO: implement stream		
	}

	@Override
	protected Event handleStartDocument(Event event) {
		processStartDocument((StartDocument)event.getResource());
		return event;
	}
	
	@Override
	protected Event handleEndDocument(Event event) {
		processEndDocument();
		close();
		return event;
	}
	
	@Override
	protected Event handleStartGroup(Event event) {
		processStartGroup((StartGroup)event.getResource());
		return event;
	}
	
	@Override
	protected Event handleEndGroup(Event event) {
		processEndGroup((Ending)event.getResource());
		return event;
	}
	
	@Override
	protected Event handleStartSubDocument(Event event) {
		processStartSubDocument((StartSubDocument)event.getResource());
		return event;
	}
	
	@Override
	protected Event handleEndSubDocument(Event event) {
		processEndSubDocument((Ending)event.getResource());
		return event;
	}

	@Override
	protected Event handleTextUnit(Event event) {
		processTextUnit((TextUnit)event.getResource());
		return event;
	}
	
	private void processStartDocument (StartDocument resource) {
		if ( writer != null ) writer.close();
		writer = new XMLWriter(outputPath);

		srcLang = resource.getLocale();
		writer.writeStartDocument();
		writer.writeStartElement("xliff");
		writer.writeAttributeString("version", "1.2");
		writer.writeAttributeString("xmlns", "urn:oasis:names:tc:xliff:document:1.2");
		docMimeType = resource.getMimeType();
		docName = resource.getName();
		inputEncoding = resource.getEncoding();
		IParameters params = resource.getFilterParameters();
		if ( params == null ) configId = null;
		else configId = params.getPath();
	}
	
	private void processEndDocument () {
		if ( inFile ) writeEndFile();
		writer.writeEndElementLineBreak(); // xliff
		writer.writeEndDocument();
		close();
	}

	private void processStartSubDocument (StartSubDocument resource) {
		writeStartFile(resource.getName(), resource.getMimeType(),
			configId, inputEncoding);		
	}
	
	private void writeStartFile (String original,
		String contentType,
		String configId,
		String inputEncoding)
	{
		writer.writeStartElement("file");
		writer.writeAttributeString("original",
			(original!=null) ? original : "unknown");
		writer.writeAttributeString("source-language", srcLang.toBCP47());
		if ( trgLang != null ) {
			writer.writeAttributeString("target-language", trgLang.toBCP47());
		}
		
		if ( contentType == null ) contentType = "x-undefined";
		else if ( contentType.equals("text/html") ) contentType = "html";
		else if ( contentType.equals("text/xml") ) contentType = "xml";
		// TODO: other standard XLIFF content types
		else contentType = "x-"+contentType;
		writer.writeAttributeString("datatype",contentType);
		
		if (( configId != null ) || ( inputEncoding != null )) {
			writer.writeAttributeString("xmlns:x", "http://net.sf.okapi/ns/xliff-extensions");
			writer.writeAttributeString("x:inputEncoding", inputEncoding);
			writer.writeAttributeString("x:configId", configId);
		}
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
		if ( !inFile ) writeStartFile(docName, docMimeType, configId, inputEncoding);
		writer.writeStartElement("group");
		writer.writeAttributeString("id", resource.getId());
		String tmp = resource.getName();
		if (( tmp != null ) && ( tmp.length() != 0 )) {
			writer.writeAttributeString("resname", tmp);
		}
		tmp = resource.getType();
		if ( !Util.isEmpty(tmp) ) {
			if ( tmp.startsWith("x-") || ( RESTYPEVALUES.contains(";"+tmp+";")) ) {
				writer.writeAttributeString("restype", tmp);
			}
			else { // Make sure the value is valid
				writer.writeAttributeString("restype", "x-"+tmp);
			}
		}
		writer.writeLineBreak();
	}
	
	private void processEndGroup (Ending resource) {
		writer.writeEndElementLineBreak(); // group
	}
	
	private void processTextUnit (TextUnit tu) {
		// Check if we need to set the entry as non-translatable
//		if ( options.setApprovedAsNoTranslate ) {
//			Property prop = tu.getTargetProperty(trgLang, Property.APPROVED);
//			if (( prop != null ) && prop.getValue().equals("yes") ) {
//				tu.setIsTranslatable(false);
//			}
//		}
		// Check if we need to skip non-translatable entries
//		if ( !options.includeNoTranslate && !tu.isTranslatable() ) {
//			return;
//		}

		if ( !inFile ) writeStartFile(docName, docMimeType, configId, inputEncoding);

		writer.writeStartElement("trans-unit");
		writer.writeAttributeString("id", String.valueOf(tu.getId()));
		String tmp = tu.getName();
		if (( tmp != null ) && ( tmp.length() != 0 )) {
			writer.writeAttributeString("resname", tmp);
		}
		tmp = tu.getType();
		if ( !Util.isEmpty(tmp) ) {
			if ( tmp.startsWith("x-") || ( RESTYPEVALUES.contains(";"+tmp+";")) ) {
				writer.writeAttributeString("restype", tmp);
			}
			else { // Make sure the value is valid
				writer.writeAttributeString("restype", "x-"+tmp);
			}
		}
		if ( !tu.isTranslatable() ) {
			writer.writeAttributeString("translate", "no");
		}

		if ( trgLang != null ) {
			if ( tu.hasTargetProperty(trgLang, Property.APPROVED) ) {
				if ( tu.getTargetProperty(trgLang, Property.APPROVED).getValue().equals("yes") ) {
					writer.writeAttributeString(Property.APPROVED, "yes");
				}
				// "no" is the default
			}
		}
		
		if ( tu.preserveWhitespaces() )
			writer.writeAttributeString("xml:space", "preserve");
		writer.writeLineBreak();

		// Get the source container
		TextContainer tc = tu.getSource();
		boolean srcHasText = tc.hasText(false);

		// Write the source
		writer.writeStartElement("source");
		writer.writeAttributeString("xml:lang", srcLang.toBCP47());
		// Write full source content (always without segments markers
		writer.writeRawXML(xliffCont.toSegmentedString(tc, 0, false, false, true));
		writer.writeEndElementLineBreak(); // source
		// Write segmented source (with markers) if needed
		if ( tc.isSegmented() ) {
			writer.writeStartElement("seg-source");
			writer.writeRawXML(xliffCont.toSegmentedString(tc, 0, false, true, true));
			writer.writeEndElementLineBreak(); // seg-source
		}

		// Write the target
		if ( trgLang != null ) {
			writer.writeStartElement("target");
			writer.writeAttributeString("xml:lang", trgLang.toBCP47());
		
			// At this point tc contains the source
			// Do we have an available target to use instead?
			tc = tu.getTarget(trgLang);
			if (( tc == null ) || ( tc.isEmpty() ) || ( srcHasText && !tc.hasText(false) )) {
				tc = tu.getSource(); // Go back to the source
			}

			// Now tc hold the content to write. Write it with or without marks
		   writer.writeRawXML(xliffCont.toSegmentedString(tc, 0, false, tc.isSegmented(), true));
		   writer.writeEndElementLineBreak(); // target
		}
		
		// Note
		if ( tu.hasProperty(Property.NOTE) ) {
			writer.writeStartElement("note");
			writer.writeString(tu.getProperty(Property.NOTE).getValue());
			writer.writeEndElementLineBreak(); // note
		}

		writer.writeEndElementLineBreak(); // trans-unit
	}
}
