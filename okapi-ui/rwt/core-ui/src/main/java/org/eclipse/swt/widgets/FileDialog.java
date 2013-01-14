package org.eclipse.swt.widgets;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.UIUtil;
import net.sf.okapi.common.ui.fileupload.OkapiUploadPanel;

import org.eclipse.rwt.lifecycle.UICallBack;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.UploadPanel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

public class FileDialog {

	private Shell shell;
	private String result;
	private Label label;
	//private Text text;
	// SWT FileDialog API fields
	private String[] filterNames = new String[ 0 ];
	private String[] filterExtensions = new String[ 0 ];
	private String[] fileNames = new String[ 0 ];
	private String filterPath = "";
	private String fileName;
	private int filterIndex;
	private boolean overwrite = false;
	private OkapiUploadPanel uploadPanel;
	private boolean uploading;
	
	public FileDialog( Shell parent ) {
	    this( parent, SWT.NONE);
	  }
	
	public FileDialog( Shell parent, int style ) {
		shell = new Shell(parent, style | SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText("File Upload");
		UIUtil.inheritIcon(shell, parent);
		shell.setLayout(new GridLayout());
			
		Composite cmpTmp = new Composite(shell, SWT.BORDER);
		cmpTmp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout layTmp = new GridLayout(1, false);
		cmpTmp.setLayout(layTmp);
		
//		Label lblSaveToFile = new Label(cmpTmp, SWT.NONE);
//		lblSaveToFile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
//		lblSaveToFile.setText(prompt);
//		
//		text = new Text(cmpTmp, SWT.BORDER);
//		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		label = new Label(cmpTmp, SWT.NONE);
		GridData gdTmp = new GridData();
		gdTmp.horizontalAlignment = SWT.FILL;
		gdTmp.grabExcessHorizontalSpace = true;
		gdTmp.horizontalSpan = 1;
		label.setLayoutData(gdTmp);
		
		uploadPanel = new OkapiUploadPanel(cmpTmp, UploadPanel.FULL);
		gdTmp = new GridData();
		gdTmp.horizontalAlignment = SWT.FILL;
		gdTmp.horizontalSpan = 1;
		gdTmp.grabExcessHorizontalSpace = true;
		uploadPanel.setLayoutData(gdTmp);
		
		//--- Dialog-level buttons

		@SuppressWarnings("serial")
		SelectionAdapter okCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = null;
				uploading = false;
				
				if ( e.widget.getData().equals("o") ) { //$NON-NLS-1$
					//if ( !saveData() ) return;
					uploading = uploadPanel.start();					
					return; // The form is closed by upload handler when finishes
				}
				shell.close();
			};
		};
		OKCancelPanel pnlActions = new OKCancelPanel(shell, SWT.NONE, okCancelActions, false);
		pnlActions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		shell.setDefaultButton(pnlActions.btOK);

		UICallBack.activate( FileDialog.class.getName() + hashCode() ); // !!!
		
		shell.addShellListener( new ShellAdapter() { // !!!
			private static final long serialVersionUID = 1L;
			@Override
		      public void shellClosed( ShellEvent e ) {
		        if (uploading) 
		        	saveData();
		        UICallBack.deactivate( FileDialog.class.getName() + hashCode() ); // !!!
		      }
		    } );
		
		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Point startSize = shell.getMinimumSize();
		if ( startSize.x < 500 ) startSize.x = 500;
		shell.setSize(startSize);
		Dialogs.centerWindow(shell, parent);
	}
	
	//public String showDialog (String fileName) {
	public String open() {
		shell.open();
//		if (fileName == null) fileName = "";
//		text.setText(Util.getFilename(fileName, true));
		uploadPanel.setFocus();		
		
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
		return result;
	}
	
	private boolean saveData () {
		try {
			result = null;
			if (Util.isEmpty(uploadPanel.getSelectedFilename())) {
				//text.selectAll();
				uploadPanel.setFocus();
				return false;
			}
			//result = Util.getFilename(uploadPanel.getSelectedFilename(), true);
			fileName = uploadPanel.getFileName();
			result = fileName;
			fileNames = new String[] {fileName};
			return true;
		}
		catch ( Exception e) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
			return false;
		}
	}
	
	  /**
	   * Returns the path of the first file that was selected in the dialog relative
	   * to the filter path, or an empty string if no such file has been selected.
	   *
	   * @return the relative path of the file
	   */
	  public String getFileName() {
	    return fileName;
	  }
	
	  /**
	   * Returns a (possibly empty) array with the paths of all files that were
	   * selected in the dialog relative to the filter path.
	   *
	   * @return the relative paths of the files
	   */
	  public String[] getFileNames() {
	    return fileNames;
	  }
	
	  /**
	   * Returns the file extensions which the dialog will use to filter the files
	   * it shows.
	   *
	   * @return the file extensions filter
	   */
	  public String[] getFilterExtensions() {
	    return filterExtensions;
	  }
	
	  /**
	   * Get the 0-based index of the file extension filter which was selected by
	   * the user, or -1 if no filter was selected.
	   * <p>
	   * This is an index into the FilterExtensions array and the FilterNames array.
	   * </p>
	   *
	   * @return index the file extension filter index
	   * @see #getFilterExtensions
	   * @see #getFilterNames
	   */
	  public int getFilterIndex() {
	    return filterIndex;
	  }
	
	  /**
	   * Returns the names that describe the filter extensions which the dialog will
	   * use to filter the files it shows.
	   *
	   * @return the list of filter names
	   */
	  public String[] getFilterNames() {
	    return filterNames;
	  }
	
	  /**
	   * Returns the directory path that the dialog will use, or an empty string if
	   * this is not set. File names in this path will appear in the dialog,
	   * filtered according to the filter extensions.
	   *
	   * @return the directory path string
	   * @see #setFilterExtensions
	   */
	  public String getFilterPath() {
	    return filterPath;
	  }
	
	  /**
	   * Returns the flag that the dialog will use to determine whether to prompt
	   * the user for file overwrite if the selected file already exists.
	   *
	   * @return true if the dialog will prompt for file overwrite, false otherwise
	   */
	  public boolean getOverwrite() {
	    return overwrite;
	  }
	
	  /**
	   * Set the initial filename which the dialog will select by default when
	   * opened to the argument, which may be null. The name will be prefixed with
	   * the filter path when one is supplied.
	   *
	   * @param string the file name
	   */
	  public void setFileName( String string ) {
	    fileName = string;
	  }
	
	  /**
	   * Set the file extensions which the dialog will use to filter the files it
	   * shows to the argument, which may be null.
	   * <p>
	   * The strings are platform specific. For example, on some platforms, an
	   * extension filter string is typically of the form "*.extension", where "*.*"
	   * matches all files. For filters with multiple extensions, use semicolon as a
	   * separator, e.g. "*.jpg;*.png".
	   * </p>
	   *
	   * @param extensions the file extension filter
	   * @see #setFilterNames to specify the user-friendly names corresponding to
	   *      the extensions
	   */
	  public void setFilterExtensions( String[] extensions ) {
	    filterExtensions = extensions;
	  }
	
	  /**
	   * Set the 0-based index of the file extension filter which the dialog will
	   * use initially to filter the files it shows to the argument.
	   * <p>
	   * This is an index into the FilterExtensions array and the FilterNames array.
	   * </p>
	   *
	   * @param index the file extension filter index
	   * @see #setFilterExtensions
	   * @see #setFilterNames
	   */
	  public void setFilterIndex( int index ) {
	    filterIndex = index;
	  }
	
	  /**
	   * Sets the names that describe the filter extensions which the dialog will
	   * use to filter the files it shows to the argument, which may be null.
	   * <p>
	   * Each name is a user-friendly short description shown for its corresponding
	   * filter. The <code>names</code> array must be the same length as the
	   * <code>extensions</code> array.
	   * </p>
	   *
	   * @param names the list of filter names, or null for no filter names
	   * @see #setFilterExtensions
	   */
	  public void setFilterNames( String[] names ) {
	    filterNames = names;
	  }
	
	  /**
	   * Sets the directory path that the dialog will use to the argument, which may
	   * be null. File names in this path will appear in the dialog, filtered
	   * according to the filter extensions. If the string is null, then the
	   * operating system's default filter path will be used.
	   * <p>
	   * Note that the path string is platform dependent. For convenience, either
	   * '/' or '\' can be used as a path separator.
	   * </p>
	   *
	   * @param string the directory path
	   * @see #setFilterExtensions
	   */
	  public void setFilterPath( String string ) {
	    filterPath = string;
	  }
	
	  /**
	   * Sets the flag that the dialog will use to determine whether to prompt the
	   * user for file overwrite if the selected file already exists.
	   *
	   * @param overwrite true if the dialog will prompt for file overwrite, false
	   *          otherwise
	   */
	  public void setOverwrite( boolean overwrite ) {
	    this.overwrite = overwrite;
	  }
	
	public void setText(String prompt) {
		label.setText(prompt + ":");
	}
	
	public String getText() {
	    return label.getText();
	}
}
