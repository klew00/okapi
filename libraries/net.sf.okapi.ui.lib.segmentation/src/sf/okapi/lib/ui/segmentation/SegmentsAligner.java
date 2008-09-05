/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel                                          */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package sf.okapi.lib.ui.segmentation;

import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;

public class SegmentsAligner {
	
	private AlignmentDialog  alignDlg;
	private Shell            shell;
	private String           currentDocument;
	
	
	@Override
	protected void finalize () {
		if ( alignDlg != null ) {
			alignDlg.close();
			alignDlg = null;
		}
	}
	
	public SegmentsAligner (Shell shell) {
		this.shell = shell;
	}

	public void setDocumentName (String documentName) {
		currentDocument = documentName;
	}
	
	/**
	 * Verifies the alignment of the segments of a given TextUnit object.
	 * @param tu The text unit containing the segments to verify.
	 * @return True if the segments are deemed aligned correctly, false if there
	 * is no target or if the segments are not deemed correctly aligned.
	 */
	public boolean align (TextUnit tu) {
		if ( !tu.hasTarget() ) return false;
		return align(tu.getSourceContent(), tu.getTargetContent());
	}
	
	/**
	 * Verifies the alignment of the segments inside a source and a target
	 * TextContainer objects.
	 * @param source The source container.
	 * @param target The target container.
	 * @return True if the segments are deemed aligned correctly, false otherwise.
	 */
	public boolean align (TextContainer source,
		TextContainer target)
	{
		// Check if both are segmented
		if ( !source.isSegmented() || !target.isSegmented() ) return false;
		
		// Check the number of segments
		if ( source.getSegments().size() != target.getSegments().size() ) {
			// Optional visual alignment to fix the problems
			return visualAlignment(source, target, "Different number of segments.");
		}
		// Assumes the list have same number of segments now
		// Sanity check using common anchors
		List<TextFragment> srcList = source.getSegments();
		List<TextFragment> trgList = target.getSegments();
		for ( int i=0; i<srcList.size(); i++ ) {
			if ( srcList.get(i).getCodes().size() != trgList.get(i).getCodes().size() ) {
				// Optional visual check
				return visualAlignment(source, target,
					"Different number of in-line codes in at least one segment.");
			}
		}
		return true;
	}
	
	/**
	 * Aligns interactively two sets of segments in TextContainer objects.
	 * @param source The source segments.
	 * @param target The target segments.
	 * @return True if the segments are deemed aligned, false if the operation
	 * is canceled or if no alignment is possible.
	 */
	public boolean visualAlignment (TextContainer source,
		TextContainer target,
		String cause)
	{
		if ( alignDlg == null ) {
			alignDlg = new AlignmentDialog(shell);
		}
		alignDlg.showDialog(source, target, currentDocument, cause);
		//TODO: return real result later (debug now)
		return false;
	}
}
