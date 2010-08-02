package net.sf.okapi.lib.ui.editor;

import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.lib.ui.editor.PairEditorPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ManualTry {

	static public void main (String[] args ) {
		Display dispMain = null;
		try {
			// Start the application
			dispMain = new Display();
			Shell shlMain = new Shell(dispMain);
			
			//=== Test for fragment editor
			shlMain.setLayout(new GridLayout());
			Composite comp = new Composite(shlMain, SWT.BORDER);
			comp.setLayout(new GridLayout());
			comp.setLayoutData(new GridData(GridData.FILL_BOTH));

			PairEditorPanel panel = new PairEditorPanel(comp, SWT.VERTICAL);
			
			shlMain.setMinimumSize(600, 400);
			shlMain.pack();

			TextFragment srcFrag = new TextFragment("Text in ");
			srcFrag.append(TagType.OPENING, "style1", "<span1>");
			srcFrag.append("bold");
			srcFrag.append(TagType.PLACEHOLDER, "z", "z");
			srcFrag.append(" and more bold");
			srcFrag.append(TagType.CLOSING, "style1", "</span1>");
			srcFrag.append(" with a line-break here:");
			srcFrag.append(TagType.PLACEHOLDER, "SomeCode", "<code3/>");
			srcFrag.append(" and more text after; ");
			srcFrag.append(TagType.OPENING, "span2", "<span4>");
			srcFrag.append(" and more.");
			
			TextFragment trgFrag = new TextFragment("Texte en ");
			trgFrag.append(TagType.OPENING, "style1", "<SPAN1>");
			trgFrag.append("gras");
			trgFrag.append(TagType.PLACEHOLDER, "Z", "Z");
			trgFrag.append(" et plus de gras");
			trgFrag.append(TagType.CLOSING, "style1", "</SPAN1>");
			trgFrag.append(" avec un saut-de-ligne ici\u00a0:");
			trgFrag.append(TagType.PLACEHOLDER, "SomeCode", "<CODE3/>");
			trgFrag.append(" et d'autre texte apr\u00e8s; ");
			trgFrag.append(TagType.OPENING, "span2", "<SPAN4>");
			trgFrag.append(" et encore d'autre.");
			
			panel.setText(srcFrag, trgFrag);
			
		    shlMain.open();
		    
		    // Set up the event loop.
		    while (!shlMain.isDisposed()) {
		    	if (!dispMain.readAndDispatch()) {
		    		// If no more entries in event queue
		    		dispMain.sleep();
		    	}
		    }

		}
		catch ( Throwable e ) {
			e.printStackTrace();
		}
		finally {
			if ( dispMain != null ) dispMain.dispose();
		}
	}

}
