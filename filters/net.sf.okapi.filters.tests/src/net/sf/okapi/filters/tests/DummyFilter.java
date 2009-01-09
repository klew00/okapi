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

package net.sf.okapi.filters.tests;

import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.FilterEventType;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeleton;

public class DummyFilter implements IFilter {

	private boolean canceled;
	private LinkedList<FilterEvent> queue;
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

	public IParameters getParameters () {
		return null;
	}

	public boolean hasNext () {
		return (( queue != null ) && ( !queue.isEmpty() )); 
	}

	public FilterEvent next () {
		if ( canceled ) {
			queue.clear();
			return new FilterEvent(FilterEventType.CANCELED);
		}
		return queue.poll();
	}

	public void open (InputStream input) {
		reset();
	}

	public void open (CharSequence inputText) {
		reset();
	}

	public void open (URL inputURL) {
		reset();
	}

	public void setOptions (String sourceLanguage,
		String defaultEncoding,
		boolean generateSkeleton)
	{
		setOptions(sourceLanguage, null, defaultEncoding, generateSkeleton);
	}

	public void setOptions (String sourceLanguage,
		String targetLanguage,
		String defaultEncoding,
		boolean generateSkeleton)
	{
		srcLang = sourceLanguage;
		trgLang = targetLanguage;
	}

	public void setParameters (IParameters params) {
	}

	private void reset () {
		close();
		queue = new LinkedList<FilterEvent>();

		queue.add(new FilterEvent(FilterEventType.START));
		
		StartDocument sd = new StartDocument("sd1");
		sd.setLanguage(srcLang);
		sd.setIsMultilingual(true);
		sd.setMimeType("text/xml");
		GenericSkeleton skel = new GenericSkeleton("<doc>\n");
		sd.setSkeleton(skel);
		queue.add(new FilterEvent(FilterEventType.START_DOCUMENT, sd));
		
		TextUnit tu = new TextUnit("tu1");
		TextContainer tc = tu.getSource();
		tc.append("Source text");
		tc = tu.setTarget(trgLang, new TextContainer());
		tc.append("Target text");
		skel = new GenericSkeleton("<text>\n<s>Source text</s>\n<t>");
		skel.addRef(tu, trgLang);
		skel.append("<t>\n</text>\n");
		tu.setSkeleton(skel);
		queue.add(new FilterEvent(FilterEventType.TEXT_UNIT, tu));
		
		tu = new TextUnit("tu2");
		tc = tu.getSource();
		tc.append("Source text 2");
		skel = new GenericSkeleton("<text>\n<s>Source text 2</s>\n<t>");
		skel.addRef(tu, trgLang);
		skel.append("<t>\n</text>\n");
		tu.setSkeleton(skel);
		queue.add(new FilterEvent(FilterEventType.TEXT_UNIT, tu));
		
		Ending ending = new Ending("ed1");
		skel = new GenericSkeleton("</doc>\n");
		ending.setSkeleton(skel);
		queue.add(new FilterEvent(FilterEventType.END_DOCUMENT, ending));
		
		queue.add(new FilterEvent(FilterEventType.FINISHED));
	}

}
