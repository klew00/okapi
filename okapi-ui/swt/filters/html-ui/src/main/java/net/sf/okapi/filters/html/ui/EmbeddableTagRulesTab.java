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

package net.sf.okapi.filters.html.ui;

import net.sf.okapi.common.ui.abstracteditor.IDialogPage;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.layout.FillLayout;

public class EmbeddableTagRulesTab extends Composite implements IDialogPage {
	private Group grpEmbeddableTags;
	private Table embeddableTagTable;
	private TableColumn embeddableTagColumn;
	private TableColumn conditionalRulesColumn;
	private AddDeleteComposite addDeleteComposite;
	private TableColumn tblclmnGenericType;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public EmbeddableTagRulesTab(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		grpEmbeddableTags = new Group(this, SWT.BORDER);
		grpEmbeddableTags.setLayout(new GridLayout(1, false));
		grpEmbeddableTags.setText("Embeddable Tags");
		grpEmbeddableTags.setData("name", "grpEmbeddableTags");
		
		embeddableTagTable = new Table(grpEmbeddableTags, SWT.BORDER | SWT.FULL_SELECTION);
		embeddableTagTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		embeddableTagTable.setData("name", "table");
		embeddableTagTable.setHeaderVisible(true);
		embeddableTagTable.setLinesVisible(true);
		
		embeddableTagColumn = new TableColumn(embeddableTagTable, SWT.CENTER);
		embeddableTagColumn.setData("name", "embeddableTagColumn");
		embeddableTagColumn.setWidth(146);
		embeddableTagColumn.setText("EmbedableTag");
		
		conditionalRulesColumn = new TableColumn(embeddableTagTable, SWT.CENTER);
		conditionalRulesColumn.setData("name", "conditionalRulesColumn");
		conditionalRulesColumn.setWidth(278);
		conditionalRulesColumn.setText("Conditional Rules");
		
		tblclmnGenericType = new TableColumn(embeddableTagTable, SWT.CENTER);
		tblclmnGenericType.setWidth(114);
		tblclmnGenericType.setText("Generic Type");
		
		addDeleteComposite = new AddDeleteComposite(grpEmbeddableTags, SWT.NONE);
		addDeleteComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public boolean canClose(boolean isOK) {	
		return true;
	}

	public void interop(Widget speaker) {
	}

	public boolean load(Object data) {
		return true;
	}

	public boolean save(Object data) {
		return true;
	}
}
