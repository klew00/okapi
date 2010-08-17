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

package net.sf.okapi.lib.verification;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.lib.terminology.TermHit;
import net.sf.okapi.lib.terminology.simpletb.SimpleTB;

public class TermChecker {

	private ArrayList<Issue> issues;
	private LocaleId srcLoc;
	private LocaleId trgLoc;
	private SimpleTB ta;
	
	public void initialize (SimpleTB termAccess,
		LocaleId srcLoc,
		LocaleId trgLoc)
	{
		issues = new ArrayList<Issue>();
		this.ta = termAccess;
		this.srcLoc = srcLoc;
		this.trgLoc = trgLoc;
	}
	
	public int verifyTerms (URI docId,
		TextUnit tu,
		Segment srcSeg,
		Segment trgSeg)
	{
		issues.clear();
		// Get the list of the terms in the source text
		List<TermHit> srcList = ta.getExistingTerms(srcSeg.text, srcLoc, trgLoc);
		// Get the list of the terms in the target text
		List<TermHit> trgList = ta.getExistingTerms(trgSeg.text, trgLoc, srcLoc);
		// Remove correspondences 
		removeMatches(srcList, trgList);
		//  What is left in source list is orphan source terms
		for ( TermHit th : srcList ) {
			Issue issue = new Issue(docId, IssueType.TERMINOLOGY, tu.getId(), srcSeg.getId(),
				String.format("The term \"%s\" seems not to be translated by \"%s\".", th.sourceTerm.getText(), th.targetTerm.getText()),
				-1, 0, -1, 0, Issue.SEVERITY_LOW, tu.getName());
			issues.add(issue);
		}
		return issues.size();
	}
	
	public List<Issue> getIssues () {
		return issues;
	}
	
	/**
	 * Removes from both list all the entries that are found in the source list and have their
	 * corresponding entry in the target list.
	 * <p>Assuming the source list comes from a source text and the target list from its
	 * corresponding translation: The resulting source list indicates the terms that are likely
	 * to have not been translated according the terminology, or have a different meaning as the
	 * term listed in the source list.
	 * @param srcList the source list.
	 * @param trgList the target list.
	 * @return the modified source list. Note that both lists are modified after the call.
	 */
	public static List<TermHit> removeMatches (List<TermHit> srcList,
		List<TermHit> trgList)
	{
		TermHit srcHit;
		TermHit trgHit;
		Iterator<TermHit> srcIter = srcList.iterator();
		
		outerLoop:
		while ( srcIter.hasNext() ) {
			srcHit = srcIter.next();
			Iterator<TermHit> trgIter = trgList.iterator();
			while ( trgIter.hasNext() ) {
				trgHit = trgIter.next();
				// Compare both source and target with the reverse
				if ( srcHit.targetTerm.getText().equals(trgHit.sourceTerm.getText()) &&
					srcHit.sourceTerm.getText().equals(trgHit.targetTerm.getText()) ) {
					// This is the same term: remove both items
					trgIter.remove();
					srcIter.remove();
					continue outerLoop;
				}
			}
		}
		
		return srcList;
	}
}
