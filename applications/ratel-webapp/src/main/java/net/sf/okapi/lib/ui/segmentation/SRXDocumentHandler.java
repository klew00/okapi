package net.sf.okapi.lib.ui.segmentation;

import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.lib.segmentation.SRXDocument;

import org.eclipse.swt.widgets.Shell;

public class SRXDocumentHandler {

	public void save(Shell shell, SRXDocument srxDoc) {
		try {
//			if ( !srxDoc.getVersion().equals("2.0") ) { //$NON-NLS-1$
//				MessageBox dlg = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
//				dlg.setText(shell.getText());
//				dlg.setMessage(Res.getString("edit.saveDocVersionWarning")); //$NON-NLS-1$
//				if ( dlg.open() != SWT.YES ) return;
//			}
//			if ( path == null ) {
//				path = Dialogs.browseFilenamesForSave(shell, Res.getString("edit.saveDocCaption"), null, null, //$NON-NLS-1$
//					Res.getString("edit.saveDocFileTypes"), //$NON-NLS-1$
//					Res.getString("edit.saveDocFilters")); //$NON-NLS-1$
//				if ( path == null ) return;
//			}
//			getSurfaceData();
//			// Save, but not the rules extra info: active/non-active (not standard) 
//			srxDoc.saveRules(path, true, false);
//			srxPath = path;
//			updateCaption();
		}
		catch ( Exception e ) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
		}
	}
}
