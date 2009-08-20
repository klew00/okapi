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

package net.sf.okapi.filters.table.ui;

import java.util.List;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.abstracteditor.AbstractParametersEditor;
import net.sf.okapi.common.ui.abstracteditor.IDialogPage;
import net.sf.okapi.common.ui.abstracteditor.SWTUtils;
import net.sf.okapi.filters.plaintext.common.CompoundParameters;
import net.sf.okapi.filters.plaintext.ui.OptionsTab;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Widget;

/**
 * 
 * 
 * @version 0.1, 19.06.2009
 */

public class Editor extends AbstractParametersEditor {

	@Override
	protected void createPages(TabFolder pageContainer) {

		addPage("Table", TableTab.class);
		addPage("Columns", ColumnsTab.class);
		addPage("Options", OptionsTab.class);

		addSpeaker(TableTab.class, "btnCSV");
		addSpeaker(TableTab.class, "btnTSV");
		addSpeaker(TableTab.class, "btnFWC");
		addSpeaker(TableTab.class, "header");
		addSpeaker(TableTab.class, "body");
		addSpeaker(TableTab.class, "trim");
		addSpeaker(OptionsTab.class, "allow");
	}

	@Override
	public IParameters createParameters() {

		return new net.sf.okapi.filters.table.Parameters();
	}

	@Override
	protected String getCaption() {

		return "Table Filter Parameters";
	}

	@Override
	protected void interop(Widget speaker) {
		// Interpage interop

		// Find participants

		Control btnCSV = findControl(TableTab.class, "btnCSV");
		// Control btnTSV = findControl(TableTab.class, "btnTSV");
		Control btnFWC = findControl(TableTab.class, "btnFWC");

		Control header = findControl(TableTab.class, "header");
		Control body = findControl(TableTab.class, "body");

		Control start = findControl(AddModifyColumnDefPage.class, "start");
		Control lstart = findControl(AddModifyColumnDefPage.class, "lstart");

		Control end = findControl(AddModifyColumnDefPage.class, "end");
		Control lend = findControl(AddModifyColumnDefPage.class, "lend");

		Composite columns = findPage(ColumnsTab.class);
		Control all = findControl(ColumnsTab.class, "all");
		Control defs = findControl(ColumnsTab.class, "defs");

		Control trim = findControl(TableTab.class, "trim");
		Control allow = findControl(OptionsTab.class, "allow");
		Control lead = findControl(OptionsTab.class, "lead");
		Control trail = findControl(OptionsTab.class, "trail");

		// Interaction

		// StartPos & EndPos in column definitions are enabled only when
		// fixed-width columns
		SWTUtils.enableIfSelected(start, btnFWC);
		SWTUtils.enableIfSelected(lstart, btnFWC);
		SWTUtils.enableIfSelected(end, btnFWC);
		SWTUtils.enableIfSelected(lend, btnFWC);

		SWTUtils.disableIfNotSelected(start, btnFWC);
		SWTUtils.disableIfNotSelected(lstart, btnFWC);
		SWTUtils.disableIfNotSelected(end, btnFWC);
		SWTUtils.disableIfNotSelected(lend, btnFWC);

		// CSV actions/Trim to Options/Allow trimming

		if (speaker == allow && SWTUtils.getSelected(trim) && SWTUtils.getSelected(btnCSV))
			Dialogs.showWarning(getShell(),
					"You cannot unselect this check-box while the Table/Trim values box is on.", null);

		if (speaker == body && (SWTUtils.getDisabled(header) || SWTUtils.getNotSelected(header)))
			Dialogs.showWarning(getShell(),
					"You cannot unselect this check-box, otherwise there's noting to extract from the table.", null);

		if (speaker == body && SWTUtils.getNotSelected(body) && SWTUtils.getSelected(header))
			Dialogs.showWarning(getShell(), "The Columns tab will be disabled as you're extracting the header only.",
					null);

		if (SWTUtils.getSelected(btnCSV)) {

			SWTUtils.selectIfSelected(allow, trim);
			SWTUtils.selectIfSelected(lead, trim);
			SWTUtils.selectIfSelected(trail, trim);

			SWTUtils.unselectIfNotSelected(allow, trim);
			SWTUtils.unselectIfNotSelected(lead, trim);
			SWTUtils.unselectIfNotSelected(trail, trim);

			SWTUtils.enableIfSelected(allow, trim);
			SWTUtils.enableIfSelected(lead, trim);
			SWTUtils.enableIfSelected(trail, trim);

			SWTUtils.disableIfNotSelected(allow, trim);
			SWTUtils.disableIfNotSelected(lead, trim);
			SWTUtils.disableIfNotSelected(trail, trim);

			SWTUtils.disableIfNotSelected(allow, trim);
			SWTUtils.disableIfNotSelected(lead, trim);
			SWTUtils.disableIfNotSelected(trail, trim);

			SWTUtils.enableIfSelected(allow, trim);
		} else {

			SWTUtils.setEnabled(allow, true);
		}

		// Extract table data enable state affects the Columns page
		SWTUtils.enableIfSelected(columns, body);
		// SWTUtils.selectIfSelected(all, body);

		if (SWTUtils.getSelected(body) && !SWTUtils.getSelected(defs))
			SWTUtils.setSelected(all, true);

		SWTUtils.disableIfNotSelected(columns, body);

		if (SWTUtils.getEnabled(columns))
			pageInterop(ColumnsTab.class, speaker); // to update the enabled
		// state of numColuimns and
		// panel
	}

	@Override
	protected boolean loadParameters() {

		// Iterate through parameters of sub-filters and pages to load default
		// values into the pages

		if (getParams() instanceof CompoundParameters) {

			List<IParameters> list = ((CompoundParameters) getParams()).getParameters();

			for (IParameters parameters : list)
				for (IDialogPage page : getPages())
					page.load(parameters);
		}

		// Iterate through pages, load parameters

		for (IDialogPage page : getPages()) {

			if (page == null)
				return false;

			if (!page.load(getParams())) {

				Dialogs.showError(getShell(), String.format("Error loading parameters to the %s page.",
						getCaption(page)), null);
				return false; // The page unable to load params is invalid
			}
		}

		if (getParams() instanceof CompoundParameters) {

			IParameters activeParams = ((CompoundParameters) getParams()).getActiveParameters();

			for (IDialogPage page : getPages()) {

				if (page == null)
					return false;

				if (!page.load(activeParams)) {

					Dialogs.showError(getShell(), String
							.format("Error loading parameters to the %s page.", getCaption(page)), null);
					return false; // The page unable to load params is invalid
				}
			}
		}

		for (IDialogPage page : getPages()) {

			if (page == null)
				return false;

			page.interop(null);
		}

		interop(null);

		return true;
	}

	@Override
	protected boolean saveParameters() {
		// Iterate through pages, store parameters

		if (isReadOnly()) {

			Dialogs.showWarning(getShell(), "Editor in read-only mode, parameters are not saved.", null);
			return false;
		}

		for (IDialogPage page : getPages()) {

			if (page == null)
				return false;

			page.interop(null);
		}

		interop(null);

		for (IDialogPage page : getPages()) {

			if (page == null)
				return false;
			if (!page.save(getParams())) { // Fills in parametersClass

				Dialogs.showError(getShell(), String.format("Error saving parameters from the %s page.",
						getCaption(page)),
						null);
				return false;
			}
		}

		if (getParams() instanceof CompoundParameters) {

			IParameters activeParams = ((CompoundParameters) getParams()).getActiveParameters();

			for (IDialogPage page : getPages()) {

				if (page == null)
					return false;

				if (!page.save(activeParams)) {

					Dialogs.showError(getShell(), String.format("Error saving parameters from the %s page.",
							getCaption(page)), null);
					return false;
				}
			}
		}

		return true;
	}

}
