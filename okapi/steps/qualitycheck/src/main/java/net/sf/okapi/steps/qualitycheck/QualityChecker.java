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

package net.sf.okapi.steps.qualitycheck;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;

public class QualityChecker {

	private Parameters params;
	private LocaleId trgLoc;
	private List<Issue> issues;
	private XMLWriter repWriter;

	public QualityChecker () {
		params = new Parameters();
	}
	
	public void initialize (LocaleId targetLocale, String rootDir) {
		this.trgLoc = targetLocale;
		issues = new ArrayList<Issue>();

		String finalPath = Util.fillRootDirectoryVariable(params.getOutputPath(), rootDir);
		repWriter = new XMLWriter(finalPath);
		repWriter.writeStartDocument();
		repWriter.writeStartElement("html");
		repWriter.writeRawXML("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>");
		repWriter.writeStartElement("head");
		repWriter.writeEndElementLineBreak();
		repWriter.writeStartElement("body");
	}

	public Parameters getParameters () {
		return params;
	}
	
	public void setParameters (Parameters params) {
		this.params = params;
	}

	public List<Issue> getIssues () {
		return issues;
	}
	
	public void processTextUnit (TextUnit tu) {
		// Skip non-translatable entries
		if ( !tu.isTranslatable() ) return;
		
		// Get the containers
		TextContainer srcCont = tu.getSource();
		TextContainer trgCont = tu.getTarget(trgLoc);
		
		// Check if we have a target (even if option disabled)
		if ( trgCont == null ) {
			// No translation available
			reportIssue(IssueType.MISSING_TARGETTU,
				String.format("Missing translation for id=%s", tu.getId()),
				0, 0, 0, 0, srcCont.toString(), "");
			return;
		}
		
		tu.synchronizeSourceSegmentation(trgLoc);
		// Check for missing/empty target
		if ( params.getMissingTarget() ) {
			ISegments srcSegs = srcCont.getSegments();
			ISegments trgSegs = trgCont.getSegments();
			for ( Segment srcSeg : srcSegs ) {
				Segment trgSeg = trgSegs.get(srcSeg.getId());
				if ( trgSeg == null ) {
					reportIssue(IssueType.MISSING_TARGETSEG,
						String.format("Missing translation for id=%s segment=%s", tu.getId(), srcSeg.getId()),
						0, 0, 0, 0, srcSeg.toString(), "");
					continue;
				}
				if ( trgSeg.text.isEmpty() && !srcSeg.text.isEmpty() ) {
					reportIssue(IssueType.EMPTY_TARGETSEG,
						String.format("Empty translation for id=%s segment=%s", tu.getId(), srcSeg.getId()),
						0, 0, 0, 0, srcSeg.toString(), "");
				}
			}
		}
		
		String srcOri = null;
		if ( srcCont.contentIsOneSegment() ) {
			srcOri = srcCont.toString();
		}
		else {
			srcOri = srcCont.getUnSegmentedContentCopy().toString();
		}
		
		String trgOri = null;
		if ( trgCont.contentIsOneSegment() ) {
			trgOri = trgCont.toString();
		}
		else {
			trgOri = trgCont.getUnSegmentedContentCopy().toString();
		}

		checkWhiteSpaces(srcOri, trgOri);
	}

	public void completeProcess () {
		if ( repWriter != null ) {
			repWriter.writeEndElementLineBreak(); // body
			repWriter.writeEndElementLineBreak(); // html
			repWriter.writeEndDocument();
			repWriter.close();
		}
	}
	
	private void checkWhiteSpaces (String srcOri,
		String trgOri)
	{
		// Check for leading whitespaces
		if ( params.getLeadingWS() ) {
			
			// Missing ones
			for ( int i=0; i<srcOri.length(); i++ ) {
				if ( Character.isWhitespace(srcOri.charAt(i)) ) {
					if ( srcOri.length() > i ) {
						if ( trgOri.charAt(i) != srcOri.charAt(i) ) {
							//reportIssue("QC_WSMISSINGORDIFFERENT"), i), i, 1, -1, 0);
							break;
						}
					}
					else {
						//ReportIssue(string.Format(m_RM.GetString("QC_WSMISSING"), i), i, 1, -1, 0);
					}
				}
				else break;
			}

			// Extra ones
			for ( int i=0; i<trgOri.length(); i++ ) {
				if ( Character.isWhitespace(trgOri.charAt(i)) ) {
					if ( srcOri.length() > i ) {
						if ( srcOri.charAt(i) != trgOri.charAt(i) ) {
							//ReportIssue(string.Format(m_RM.GetString("QC_WSEXTRAORDIFFERENT"), i), -1, 0, i, 1);
							break;
						}
					}
					else {
						//ReportIssue(string.Format(m_RM.GetString("QC_WSEXTRA"), i),} -1, 0, i, 1);
					}
				}
				else break;
			}
		}
		
		// Check for trailing whitespaces
		if ( params.getTrailingWS() ) {

			// Missing ones
			int j = trgOri.length()-1;
			for ( int i=srcOri.length()-1; i>=0; i-- ) {
				if ( Character.isWhitespace(srcOri.charAt(i)) ) {
					if ( j >= 0 ) {
						if ( trgOri.charAt(j) != srcOri.charAt(i) ) {
							reportIssue(IssueType.MISSINGORDIFF_TRAILINGWS,
								String.format("Missing or different white space at position %d", i),
								i, 1, -1, 0, srcOri, trgOri);
							break;
						}
					}
					else {
						reportIssue(IssueType.MISSING_TRAILINGWS,
							String.format("Missing white space at position %d.", i),
							i, 1, -1, 0, srcOri, trgOri);
					}
				}
				else break;
				j--;
			}

			// Extra ones
			j = srcOri.length()-1;
			for ( int i=trgOri.length()-1; i>=0; i-- ) {
				if ( Character.isWhitespace(trgOri.charAt(i)) ) {
					if ( j >= 0 ) {
						if ( srcOri.charAt(j) != trgOri.charAt(i) ) {
							reportIssue(IssueType.EXTRAORDIFF_TRAILINGWS,
								String.format("Extra or different white space at position %d.", i),
								-1, 0, i, 1, srcOri, trgOri);
							break;
						}
					}
					else {
						reportIssue(IssueType.EXTRA_TRAILINGWS,
							String.format("Extra white space at position %d.", i),
							-1, 0, i, 1, srcOri, trgOri);
					}
				}
				else break;
				j--;
			}
		}
		
	}

	private void reportIssue (IssueType issueType,
		String message,
		int srcStart,
		int srcLength,
		int trgStart,
		int trgLength,
		String srcOri,
		String trgOri)
	{
		Issue issue = new Issue(issueType, message, srcStart, srcLength, trgStart, trgLength);
		issues.add(issue);

		if ( repWriter != null ) {
			repWriter.writeStartElement("table");

			repWriter.writeRawXML("<tr><td colspan=\"2\">");
			repWriter.writeElementString("p", issue.message);
			repWriter.writeRawXML("</td>");
			
			repWriter.writeRawXML("<tr><td>Src:</td><td>");
			repWriter.writeElementString("pre", srcOri);
			repWriter.writeRawXML("</td></td>");
			
			repWriter.writeRawXML("<tr><td>Trg:</td><td>");
			repWriter.writeElementString("pre", trgOri);
			repWriter.writeRawXML("</td></td>");
			
			repWriter.writeEndElementLineBreak(); // table
		}
	}

}
