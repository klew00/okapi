package net.sf.okapi.lib.ui.verification;

import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.lib.ui.editor.FragmentEditorPanel;

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
			
//			QualityCheckEditor qce = new QualityCheckEditor();
//			FilterConfigurationMapper fcMapper = new FilterConfigurationMapper();
//			DefaultFilters.setMappings(fcMapper, true, true);
//	    	BaseHelp help = new BaseHelp("dummyRoot");
//			qce.initialize(shlMain, false, help, fcMapper, null);
//			qce.edit(false);
			
			//=== Test for fragment editor
			shlMain.setLayout(new GridLayout());
			Composite comp = new Composite(shlMain, SWT.BORDER);
			comp.setLayout(new GridLayout());
			comp.setLayoutData(new GridData(GridData.FILL_BOTH));
			FragmentEditorPanel panel = new FragmentEditorPanel(comp, -1);
			
			shlMain.setMinimumSize(400, 200);
			shlMain.pack();

			TextFragment tf = new TextFragment("Text in ");
			tf.append(TagType.OPENING, "bold", "<span style='color:red;'>");
			tf.append("bold");
			tf.append(TagType.CLOSING, "bold", "</span>");
			tf.append(" with a line-break here:");
			tf.append(TagType.PLACEHOLDER, "SomeCode", "<code attribute='dataStuff' more='data'/>");
			tf.append(" and more text after; ");
			tf.append(TagType.OPENING, "span2", "<span style='color:red;'>");
			tf.append(" and more.");
			panel.setText(tf);
			
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
