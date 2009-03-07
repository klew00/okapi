/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.lib.ui.segmentation;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.lib.segmentation.Segmenter;

public class FileProcessor {

	private Pattern patternOpening;
	private Pattern patternClosing;
	private Pattern patternPlaceholder;
	private GenericContent sampleOutput;
	
	public FileProcessor () {
		patternOpening = Pattern.compile("\\<(\\w+[^\\>]*)\\>"); //$NON-NLS-1$
		patternClosing = Pattern.compile("\\</(\\w+[^\\>]*)\\>"); //$NON-NLS-1$
		patternPlaceholder = Pattern.compile("\\<(\\w+[^\\>]*)/\\>"); //$NON-NLS-1$
		sampleOutput = new GenericContent();
	}
	
	/**
	 * Puts a simple text string into a TextContainer object. If the string contains
	 * XML-like tags they are converted as in-line codes. 
	 * @param text The string to put into the TextContainer object.
	 * @param textCont The TextContainer object where to put the string
	 * (it must be NOT null).
	 */
	public void populateTextContainer (String text,
		TextContainer textCont)
	{
		assert(textCont!=null);
		textCont.clear();
		textCont.setCodedText(text);
		int n;
		int start = 0;
		int diff = 0;
		
		Matcher m = patternOpening.matcher(text);
		while ( m.find(start) ) {
			n = m.start();
			diff += textCont.changeToCode(n+diff, (n+diff)+m.group().length(),
				TagType.OPENING, m.group(1));
			start = (n+m.group().length());
		}
		
		text = textCont.getCodedText();
		start = diff = 0;
		m = patternClosing.matcher(text);
		while ( m.find(start) ) {
			n = m.start();
			diff += textCont.changeToCode(n+diff, (n+diff)+m.group().length(),
				TagType.CLOSING, m.group(1));
			start = (n+m.group().length());
		}
		
		text = textCont.getCodedText();
		start = diff = 0;
		m = patternPlaceholder.matcher(text);
		while ( m.find(start) ) {
			n = m.start();
			diff += textCont.changeToCode(n+diff, (n+diff)+m.group().length(),
				TagType.PLACEHOLDER, null);
			start = (n+m.group().length());
		}
	}
	
	public void process (String inputPath,
		String outputPath,
		boolean htmlOutput,
		Segmenter segmenter)
		throws IOException
	{
		BufferedReader reader = null;
		BufferedWriter writer = null;
		try {
			reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(inputPath), "UTF-8")); //$NON-NLS-1$

			Util.createDirectories(outputPath);
			writer = new BufferedWriter(new OutputStreamWriter(
				new BufferedOutputStream(new FileOutputStream(outputPath)), "UTF-8")); //$NON-NLS-1$

			if ( htmlOutput ) {
				writer.write("<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"/>"); //$NON-NLS-1$
				writer.write("<style>p {white-space: pre; font-family: monospace; border: 1px solid; padding: 4; margin-top: 0; margin-bottom: -1;}</style></head><body>"); //$NON-NLS-1$
			}
			
			// Read the whole file into one string
			StringBuilder tmp = new StringBuilder();
			char[] buf = new char[1024];
			int count = 0;
			while (( count = reader.read(buf)) != -1 ) {
				tmp.append(buf, 0, count);
			}
			
			TextContainer textCont = new TextContainer();
			populateTextContainer(tmp.toString(), textCont);
			// Segment
			segmenter.computeSegments(textCont);
			textCont.createSegments(segmenter.getSegmentRanges());
			if ( htmlOutput ) {
				List<TextFragment> list = textCont.getSegments();
				for ( TextFragment frag : list ) {
					writer.write("<p>"); //$NON-NLS-1$
					writer.write(Util.escapeToXML(sampleOutput.setContent(frag).toString(true), 0, false, null));
					writer.write("</p>"); //$NON-NLS-1$
				}
			}
			else {
				writer.write(sampleOutput.printSegmentedContent(textCont, true, true));
			}

			if ( htmlOutput ) {
				writer.write("</body></html>"); //$NON-NLS-1$
			}
		}
		finally {
			if ( writer != null ) {
				writer.close();
			}
			if ( reader != null ) {
				reader.close();
			}
		}
	}

}
