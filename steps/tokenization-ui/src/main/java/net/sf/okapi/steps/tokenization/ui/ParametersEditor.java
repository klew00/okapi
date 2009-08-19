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

package net.sf.okapi.steps.tokenization.ui;

import java.util.List;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.abstracteditor.AbstractParametersEditor;
import net.sf.okapi.common.ui.abstracteditor.IDialogPage;
import net.sf.okapi.filters.plaintext.common.CompoundParameters;

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Widget;

public class ParametersEditor extends AbstractParametersEditor {

	@Override
	protected void createPages(TabFolder pageContainer) {
		// TODO Auto-generated method stub

	}

	@Override
	public IParameters createParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getCaption() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void interop(Widget speaker) {
		// TODO Auto-generated method stub

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
