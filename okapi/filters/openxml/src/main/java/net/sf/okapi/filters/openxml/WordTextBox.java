package net.sf.okapi.filters.openxml;

import java.util.ArrayList;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.RawDocument;

/**
 * This class acts as a placeholder for both {@link Property}s and
 * {@link TextUnit}s that are found within tags. HTML and XML attributes are the
 * canonical case. Along with the attribute name, value and type this class
 * stores offset information for the name and value that can be used by the
 * {@link AbstractBaseFilter} to automatically generate proper attribute-based
 * {@link IResource}s
 */
public class WordTextBox {
	private int textUnitId;
	private int documentPartId;

	private OpenXMLContentFilter textBoxOpenXMLContentFilter;

	/**
	 * Constructor for {@link Property} only. All offsets are the same, useful
	 * for creating placeholders for read-only {@link Property}s
	 * 
	 * @param type
	 *            - a {@link PlaceholderAccessType}
	 * @param name
	 *            - attribute name
	 * @param value
	 *            - attribute value
	 */
	public WordTextBox() {
		textBoxOpenXMLContentFilter = new OpenXMLContentFilter();	
	}
	public void open(String txbx, LocaleId sourceLanguage)
	{
		textBoxOpenXMLContentFilter.open(
				new RawDocument((CharSequence)txbx,sourceLanguage));
	}
	public ArrayList<Event> doEvents()
	{
		Event event;
		EventType etyp;
		ArrayList<Event> textBoxEventList = new ArrayList<Event>();
		while(textBoxOpenXMLContentFilter.hasNext())
		{
			event = textBoxOpenXMLContentFilter.next();
			etyp = event.getEventType();
			if (!(etyp==EventType.START_DOCUMENT || etyp==EventType.END_DOCUMENT))
				textBoxEventList.add(event);
		}
		return textBoxEventList;
	}	
	OpenXMLContentFilter getTextBoxOpenXMLContentFilter()
	{
		return textBoxOpenXMLContentFilter;
	}
	
	public void setTextUnitId(int i)
	{
		this.textUnitId = i;
	}
	public int getTextUnitId()
	{
		return textUnitId;
	}
	public void setDocumentPartId(int i)
	{
		this.documentPartId = i;
	}
	public int getDocumentPartId()
	{
		return documentPartId;
	}
}
