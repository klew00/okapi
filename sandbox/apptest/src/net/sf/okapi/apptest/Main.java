package net.sf.okapi.apptest;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Main {

	public static void main (String args[])
	{
		Display dispMain = null;
		try {
			dispMain = new Display();
			Shell shlMain = new Shell(dispMain);
			MainForm mf = new MainForm(shlMain);
			shlMain.open();
			mf.run();
		}
		catch ( Throwable e ) {
			e.printStackTrace();
		}
		finally {
			if ( dispMain != null ) dispMain.dispose();
		}
    }

}
