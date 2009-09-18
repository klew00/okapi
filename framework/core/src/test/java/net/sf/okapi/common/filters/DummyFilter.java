/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.common.filters;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filterwriter.GenericFilterWriter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

public class DummyFilter implements IFilter {

	private boolean canceled;
	private LinkedList<Event> queue;
	private String srcLang;
	private String trgLang;
	private DummyParameters params;

	public DummyFilter () {
		params = new DummyParameters();
	}
	
	public void cancel () {
		canceled = true;
	}

	public void close () {
		if ( queue != null ) {
			queue.clear();
			queue = null;
		}
	}

	public String getName () {
		return "okf_dummy";
	}

	public String getDisplayName () {
		return "Dummy Filter";
	}

	public String getMimeType () {
		return "text/xml";
	}

	public IParameters getParameters () {
		return params;
	}

	public boolean hasNext () {
		return (( queue != null ) && ( !queue.isEmpty() )); 
	}

	public Event next () {
		if ( canceled ) {
			queue.clear();
			return new Event(EventType.CANCELED);
		}
		return queue.poll();
	}

	/**
	 * Use this filter with a string input where the string has all your text unit,
	 * each text separated by a '\n'.
	 * For dummy inline codes use "@#$N" where N is a number between 0 and 9.
	 */
	public void open (RawDocument input) {
		open(input, true);
	}
	
	public void open (RawDocument input,
		boolean generateSkeleton)
	{
		setOptions(input.getSourceLanguage(), input.getTargetLanguage(),
			input.getEncoding(), generateSkeleton);
		if ( input.getInputCharSequence() != null ) {
			reset(input.getInputCharSequence().toString());
		}
		else {
			// In all other case: Use the default events
			reset();
		}
	}

	private void setOptions (String sourceLanguage,
		String targetLanguage,
		String defaultEncoding,
		boolean generateSkeleton)
	{
		srcLang = sourceLanguage;
		trgLang = targetLanguage;
	}

	public void setParameters (IParameters params) {
		this.params = (DummyParameters)params;
	}

	public ISkeletonWriter createSkeletonWriter() {
		return new GenericSkeletonWriter();
	}

	public IFilterWriter createFilterWriter () {
		return new GenericFilterWriter(createSkeletonWriter());
	}

	private void reset (String data) {
		close();
		queue = new LinkedList<Event>();
		String[] parts = data.split("\n", 0);

		StartDocument sd = new StartDocument("sd1");
		sd.setLanguage(srcLang);
		sd.setMultilingual(parts.length>1);
		sd.setMimeType("text");
		queue.add(new Event(EventType.START_DOCUMENT, sd));
		
		TextUnit tu = new TextUnit("id1", parts[0]);
		String text = tu.getSourceContent().getCodedText();
		int n = text.indexOf("@#$");
		while ( n > -1 ) {
			tu.getSourceContent().changeToCode(n, n+4, TagType.PLACEHOLDER, "z");
			text = tu.getSourceContent().getCodedText();
			n = text.indexOf("@#$");
		}
		
		if ( parts.length > 1 ) {
			TextContainer tc = new TextContainer(parts[1]);
			text = tc.getCodedText();
			n = text.indexOf("@#$");
			while ( n > -1 ) {
				tc.changeToCode(n, n+4, TagType.PLACEHOLDER, "z");
				text = tc.getCodedText();
				n = text.indexOf("@#$");
			}
			tu.setTarget(trgLang, tc);
		}
		
		queue.add(new Event(EventType.TEXT_UNIT, tu));

		Ending ending = new Ending("ed1");
		queue.add(new Event(EventType.END_DOCUMENT, ending));
	}
	
	private void reset () {
		close();
		queue = new LinkedList<Event>();

		StartDocument sd = new StartDocument("sd1");
		sd.setLanguage(srcLang);
		sd.setMultilingual(true);
		sd.setMimeType("text/xml");
		GenericSkeleton skel = new GenericSkeleton("<doc>\n");
		sd.setSkeleton(skel);
		queue.add(new Event(EventType.START_DOCUMENT, sd));
		
		TextUnit tu = new TextUnit("tu1");
		TextContainer tc = tu.getSource();
		tc.append("Source text");
		tc = tu.setTarget(trgLang, new TextContainer());
		tc.append("Target text");
		skel = new GenericSkeleton("<text>\n<s>Source text</s>\n<t>");
		skel.addContentPlaceholder(tu, trgLang);
		skel.append("<t>\n</text>\n");
		tu.setSkeleton(skel);
		queue.add(new Event(EventType.TEXT_UNIT, tu));
		
		tu = new TextUnit("tu2");
		tc = tu.getSource();
		tc.append("Source text 2");
		skel = new GenericSkeleton("<text>\n<s>Source text 2</s>\n<t>");
		skel.addContentPlaceholder(tu, trgLang);
		skel.append("<t>\n</text>\n");
		tu.setSkeleton(skel);
		queue.add(new Event(EventType.TEXT_UNIT, tu));
		
		Ending ending = new Ending("ed1");
		skel = new GenericSkeleton("</doc>\n");
		ending.setSkeleton(skel);
		queue.add(new Event(EventType.END_DOCUMENT, ending));
	}

	public List<FilterConfiguration> getConfigurations() {
 		List<FilterConfiguration> list = new ArrayList<FilterConfiguration>();
		list.add(new FilterConfiguration(getName(),
			MimeTypeMapper.DEFAULT_MIME_TYPE,
			getClass().getName(),
			"Dummy Filter",
			"Default for dummy."));
		return list;
	}

}
