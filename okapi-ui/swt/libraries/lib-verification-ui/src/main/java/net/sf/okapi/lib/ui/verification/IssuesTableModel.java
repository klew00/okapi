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

package net.sf.okapi.lib.ui.verification;

import java.util.List;

import net.sf.okapi.lib.verification.Issue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

class IssuesTableModel {
	
	Table table;
	List<Issue> list;
	Color[] colors;

	public IssuesTableModel (Display display) {
		// This array must be in the same size/order as the Issue.SEVERITY_??? flags
		colors = new Color[] {
			display.getSystemColor(SWT.COLOR_YELLOW),
			new Color(null, 255, 153, 0), // Make sure we dispose of it on close
			display.getSystemColor(SWT.COLOR_RED)
		};
	}
	
	@Override
	protected void finalize () {
		dispose();
	}
	
	public void dispose () {
		// Dispose of the non-system resources
		if ( colors != null ) {
			colors[1].dispose();
			colors[1] = null;
		}
	}

	void linkTable (Table newTable,
		Listener sortListener)
	{
		table = newTable;
		
		TableColumn col = new TableColumn(table, SWT.NONE);
		col.addListener(SWT.Selection, sortListener);
		
		col = new TableColumn(table, SWT.NONE);
		col.addListener(SWT.Selection, sortListener);

		col = new TableColumn(table, SWT.NONE);
		col.setText("Text Unit");
		col.addListener(SWT.Selection, sortListener);

		col = new TableColumn(table, SWT.NONE);
		col.setText("Seg");
		col.addListener(SWT.Selection, sortListener);
		
		col = new TableColumn(table, SWT.NONE);
		col.setText("Description");
		col.addListener(SWT.Selection, sortListener);
	}
	
	void setIssues (List<Issue> list) {
		this.list = list;
	}

	// displayType: 0=all, 1=enabled 2=disabled
	void updateTable (int selection,
		int displayType,
		int issueType)
	{
		table.removeAll();
		if ( list == null ) return;
		for ( Issue issue : list ) {
			// Select the type of items to show
			switch ( displayType ) {
			case QualityCheckEditor.ISSUETYPE_ENABLED: // Enabled
				if ( !issue.enabled ) continue;
				break;
			case QualityCheckEditor.ISSUETYPE_DISABLED: // Disabled
				if ( issue.enabled ) continue;
				break;
			}
			// Select the issue type
			if ( issueType > 0 ) {
				switch ( issue.issueType ) {
				case MISSING_TARGETTU:
					if ( issueType != 1 ) continue;
					break;
				case MISSING_TARGETSEG:
				case EXTRA_TARGETSEG:
					if ( issueType != 2 ) continue;
					break;
				case EMPTY_TARGETSEG:
				case EMPTY_SOURCESEG:
					if ( issueType != 3 ) continue;
					break;
				case TARGET_SAME_AS_SOURCE:
					if ( issueType != 4 ) continue;
					break;
				case MISSING_LEADINGWS:
				case MISSINGORDIFF_LEADINGWS:
				case MISSING_TRAILINGWS:
				case MISSINGORDIFF_TRAILINGWS:
				case EXTRA_LEADINGWS:
				case EXTRAORDIFF_LEADINGWS:
				case EXTRA_TRAILINGWS:
				case EXTRAORDIFF_TRAILINGWS:
					if ( issueType != 5 ) continue;
					break;
				case MISSING_CODE:
				case EXTRA_CODE:
				case SUSPECT_CODE:
					if ( issueType != 6 ) continue;
					break;
				case UNEXPECTED_PATTERN:
					if ( issueType != 7 ) continue;
					break;
				case SUSPECT_PATTERN:
					if ( issueType != 8 ) continue;
					break;
				case TARGET_LENGTH:
					if ( issueType != 9 ) continue;
					break;
				case ALLOWED_CHARACTERS:
					if ( issueType != 10 ) continue;
					break;
				case TERMINOLOGY:
					if ( issueType != 11 ) continue;
					break;
				case LANGUAGETOOL_ERROR:
					if ( issueType != 12 ) continue;
					break;
				default:
					continue;
				}
			}
			// Display the item
			TableItem item = new TableItem(table, SWT.NONE);
			item.setChecked(issue.enabled);
			item.setForeground(1, colors[issue.severity]);
			item.setText(1, "\u2588");
			if ( issue.tuName == null ) {
				item.setText(2, issue.tuId);
			}
			else {
				item.setText(2, issue.tuId + " (" + issue.tuName + ")");
			}
			item.setText(3, (issue.segId == null ? "" : issue.segId));
			item.setText(4, issue.message);
			item.setData(issue);
		}
		
		if (( selection < 0 ) || ( selection > table.getItemCount()-1 )) {
			selection = table.getItemCount()-1;
		}
		if ( table.getItemCount() > 0 ) {
			table.setSelection(selection);
		}
	}

}
