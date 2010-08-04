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

package net.sf.okapi.lib.ui.editor;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.ui.Dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Temporary class for testing the fragment editor with end-users, using real files.
 */
public class PairEditorUserTest {

	private Shell shell;
	private IFilterConfigurationMapper fcMapper;
	private PairEditorPanel editPanel;
	private Text edInfo;
	private Button btNext;
	private Button btPrevious;
	private Button btSave;
	private LocaleId srcLoc = LocaleId.ENGLISH;
	private LocaleId trgLoc = LocaleId.FRENCH;
	private RawDocument rawDoc;
	private ArrayList<TextUnit> textUnits = new ArrayList<TextUnit>();
	private int current = -1;
	private String outEncoding;
	private String outPath;

	public PairEditorUserTest (Object parent,
		IFilterConfigurationMapper fcMapper)
	{
		// If no parent is defined, create a new display and shell
		if ( parent == null ) {
			// Start the application
			Display dispMain = new Display();
			parent = new Shell(dispMain);
		}

		this.fcMapper = fcMapper;
		shell = new Shell((Shell)parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.MAX | SWT.MIN | SWT.APPLICATION_MODAL);
		shell.setText("Fragment Editor Testing Console");
		shell.setLayout(new GridLayout());

		createContent();
		updateButtons();
		
		Dialogs.centerWindow(shell, (Shell)parent);
	}
	
	@Override
	protected void finalize () {
		dispose();
	}

	private void dispose () {
	}

	public void showDialog () {
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
	}
	
	private void createContent () {
		Composite comp = new Composite(shell, SWT.BORDER);
		comp.setLayout(new GridLayout(4, true));
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Button btOpen = new Button(comp, SWT.PUSH);
		btOpen.setText("&Open Document...");
		btOpen.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btOpen.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				openDocument(null);
			};
		});

		btPrevious = new Button(comp, SWT.PUSH);
		btPrevious.setText("&Previous");
		btPrevious.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btPrevious.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				displayPrevious();
			};
		});
		
		btNext = new Button(comp, SWT.PUSH);
		btNext.setText("&Next");
		btNext.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btNext.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				displayNext();
			};
		});

		btSave = new Button(comp, SWT.PUSH);
		btSave.setText("&Save Output");
		btSave.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btSave.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				saveOutput();
			};
		});
		
		edInfo = new Text(comp, SWT.BORDER);
		edInfo.setEditable(false);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 4;
		edInfo.setLayoutData(gdTmp);
		
		editPanel = new PairEditorPanel(comp, SWT.VERTICAL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 4;
		editPanel.setLayoutData(gdTmp);
		editPanel.clear();
		
		// Set minimum and start sizes
		shell.pack();
		shell.setMinimumSize(shell.getSize());
		shell.setSize(680, 400);
	}

	private void openDocument (String path) {
		IFilter filter = null;
		try {
			InputDocumentDialog dlg = new InputDocumentDialog(shell, "Open Document", fcMapper);
			dlg.setData(path, null, "UTF-8", srcLoc, trgLoc);

			// Edit
			Object[] data = dlg.showDialog();
			if ( data == null ) return;
			
			// Create the raw document to add to the session
			URI uri = (new File((String)data[0])).toURI();
			srcLoc = (LocaleId)data[3];
			trgLoc = (LocaleId)data[4];
			rawDoc = new RawDocument(uri, (String)data[2], srcLoc, trgLoc);
			rawDoc.setFilterConfigId((String)data[1]);
			
			filter = fcMapper.createFilter(rawDoc.getFilterConfigId());

			// Reset
			outEncoding = null;
			outPath = null;
			current = -1;
			
			// Load the document
			textUnits = new ArrayList<TextUnit>();
			filter.open(rawDoc);
			while ( filter.hasNext() ) {
				Event event = filter.next();
				if ( event.getEventType() == EventType.TEXT_UNIT ) {
					textUnits.add(event.getTextUnit());
				}
			}
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, "Error when opening document.\n"+e.getMessage(), null);
		}
		finally {
			if ( filter != null ) {
				filter.close();
			}
			displayFirst();
		}
	}
	
	private void updateButtons () {
		btSave.setEnabled(( rawDoc != null ) && ( textUnits.size() > 0 ));
		btPrevious.setEnabled(( textUnits.size() > 0 ) && ( current > 0 ));
		btNext.setEnabled(( textUnits.size() > 0 ) && ( current < (textUnits.size()-1) ));
	}
	
	private void displayTU (int index) {
		try {
			if ( index > -1 ) {
				TextUnit tu = textUnits.get(index);
				TextFragment srcFrag = tu.getSource().getFirstContent();
				TextFragment trgFrag = tu.createTarget(trgLoc, false, IResource.COPY_ALL).getFirstContent();
				//TODO: deal with segmented content
				editPanel.setText(srcFrag, trgFrag);
				edInfo.setText(String.format("TU ID=%s  (%d of %d)", tu.getId(), current+1, textUnits.size()));
			}
			else {
				editPanel.clear();
				edInfo.setText("");
			}
			updateButtons();
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, "Error when displaying text unit.\n"+e.getMessage(), null);
		}
	}

	private void displayPrevious () {
		if ( current <= 0 ) return;
		saveCurrent();
		displayTU(--current);
	}
	
	private void displayNext () {
		if (( current < 0 ) || ( current >= textUnits.size()-1 )) return;
		saveCurrent();
		displayTU(++current);
	}
	
	private void displayFirst () {
		if ( textUnits.size() > 0 ) current = 0;
		else current = -1;
		displayTU(current);
	}
	
	private void saveCurrent () {
		if ( current == -1 ) return;
		editPanel.applyChanges();
	}

	private void saveOutput () {
		saveCurrent();
		IFilter filter = null;
		IFilterWriter writer = null;
		try {
			filter = fcMapper.createFilter(rawDoc.getFilterConfigId());
			filter.open(rawDoc);
			int tuIndex = 0;
			while ( filter.hasNext() ) {
				Event event = filter.next();
				switch ( event.getEventType() ) {
				case START_DOCUMENT:
					StartDocument sd = (StartDocument)event.getResource();
					outEncoding = rawDoc.getEncoding();
					outPath = new File(rawDoc.getInputURI()).getPath();
					outPath = Util.getDirectoryName(outPath) + File.separator + Util.getFilename(outPath, false) + ".out" + Util.getExtension(outPath);
					writer = sd.getFilterWriter();
					writer.setOptions(trgLoc, outEncoding);
					writer.setOutput(outPath);
					break;
				case TEXT_UNIT:
					TextUnit oriTU = event.getTextUnit();
					TextUnit updTU = textUnits.get(tuIndex);
					// make sure they are in sync (just to be sure)
					if ( !oriTU.getId().equals(updTU.getId()) ) {
						throw new RuntimeException("Text units de-synchronized: the underlying file has changed.");
					}
					TextContainer tc = updTU.getTarget(trgLoc);
					if ( tc != null ) oriTU.setTarget(trgLoc, tc);
					tuIndex++;
					break;
				}
				writer.handleEvent(event);
			}
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, "Error when saving document.\n"+e.getMessage(), null);
		}
	}
	
//	private void createTestTextUnit () {
//		rawDoc = null;
//		textUnits = new ArrayList<TextUnit>();
//		
//		TextFragment srcFrag = new TextFragment("Text in ");
//		srcFrag.append(TagType.OPENING, "style1", "<span1>");
//		srcFrag.append("bold");
//		srcFrag.append(TagType.PLACEHOLDER, "z", "z");
//		srcFrag.append(" and more bold");
//		srcFrag.append(TagType.CLOSING, "style1", "</span1>");
//		srcFrag.append(" with a line-break here:");
//		srcFrag.append(TagType.PLACEHOLDER, "SomeCode", "<code3/>");
//		srcFrag.append(" and more text after; ");
//		srcFrag.append(TagType.OPENING, "span2", "<span4>");
//		srcFrag.append(" and more.");
//		
//		TextFragment trgFrag = new TextFragment("Texte en ");
//		trgFrag.append(TagType.OPENING, "style1", "<SPAN1>");
//		trgFrag.append("gras");
//		trgFrag.append(TagType.PLACEHOLDER, "Z", "Z");
//		trgFrag.append(" et plus de gras");
//		trgFrag.append(TagType.CLOSING, "style1", "</SPAN1>");
//		trgFrag.append(" avec un saut-de-ligne ici\u00a0:");
//		trgFrag.append(TagType.PLACEHOLDER, "SomeCode", "<CODE3/>");
//		trgFrag.append(" et d'autre texte apr\u00e8s; ");
//		trgFrag.append(TagType.OPENING, "span2", "<SPAN4>");
//		trgFrag.append(" et encore d'autre.");
//		
//		TextUnit tu = new TextUnit("id");
//		tu.setSource(new TextContainer(srcFrag));
//		tu.setTargetContent(trgLoc, trgFrag);
//		textUnits.add(tu);
//		
//		displayFirst();
//	}

}
