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

import java.io.InputStream;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filterwriter.GenericFilterWriter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

public class DummyFilter implements IFilter {

	private boolean canceled;
	private LinkedList<Event> queue;
	private String srcLang;
	private String trgLang;

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
		return "DummyFilter";
	}

	public String getDisplayName () {
		return "Dummy Filter";
	}

	public String getMimeType () {
		return "text/xml";
	}

	public IParameters getParameters () {
		return null;
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

	public void open (RawDocument input) {
		open(input, true);
	}
	
	public void open (RawDocument input,
		boolean generateSkeleton)
	{
		setOptions(input.getSourceLanguage(), input.getTargetLanguage(),
			input.getEncoding(), generateSkeleton);
		if ( input.getInputCharSequence() != null ) {
			open(input.getInputCharSequence());
		}
		else if ( input.getInputURI() != null ) {
			open(input.getInputURI());
		}
		else if ( input.getStream() != null ) {
			open(input.getStream());
		}
		else {
			throw new RuntimeException("RawDocument has no input defined.");
		}
	}
	
	private void open (InputStream input) {
		reset();
	}

	private void open (CharSequence inputText) {
		reset();
	}

	private void open (URI inputURI) {
		reset();
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
	}

	public ISkeletonWriter createSkeletonWriter() {
		return new GenericSkeletonWriter();
	}

	public IFilterWriter createFilterWriter () {
		return new GenericFilterWriter(createSkeletonWriter());
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
		return null;
	}

}
