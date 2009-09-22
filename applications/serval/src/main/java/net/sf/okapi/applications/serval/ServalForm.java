package net.sf.okapi.applications.serval;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.InputDialog;
import net.sf.okapi.common.ui.UIUtil;
import net.sf.okapi.filters.tmx.TmxFilter;
import net.sf.okapi.lib.translation.QueryManager;
import net.sf.okapi.lib.translation.QueryResult;
import net.sf.okapi.tm.pensieve.tmx.OkapiTmxImporter;
import net.sf.okapi.tm.pensieve.writer.ITmWriter;
import net.sf.okapi.tm.pensieve.writer.TmWriterFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.File;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServalForm {

	private Shell shell;
	private Text edQuery;
	private Text edSource;
	private Text edTarget;
	private Table tblResults;
	private TableModel modResults;
	private QueryManager queryMgt;
	private Font displayFont;
	private Button chkRawText;
	private Label stElapsedTime;
	
	public ServalForm (Shell shell) {
		try {
			this.shell = shell;
			
			queryMgt = new QueryManager();
			queryMgt.setLanguages(getDefaultSourceLanguage(), this.getDefaultTargetLanguage());

			// Default
			//queryMgt.addAndInitializeResource(new GoogleMTConnector(), "GoogleMT", null);
			// For test
			//SimpleTMConnector smptm = new SimpleTMConnector();
			//queryMgt.addAndInitializeResource(smptm, "SimpleTM test", "C:\\Projects\\CaridianBCT\\AlignerTests\\RealProject\\PL-PL\\outputdb_PL");
			
			createContent();
		}
		catch ( Throwable E ) {
			Dialogs.showError(shell, E.getMessage(), null);			
		}
	}
	
	private void createContent () {
		GridLayout layTmp = new GridLayout(4, false);
		shell.setLayout(layTmp);
		
		// Menus
	    Menu menuBar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menuBar);

		// File menu
		MenuItem topItem = new MenuItem(menuBar, SWT.CASCADE);
		topItem.setText("&File");
		Menu dropMenu = new Menu(shell, SWT.DROP_DOWN);
		topItem.setMenu(dropMenu);
		
		MenuItem menuItem = new MenuItem(dropMenu, SWT.PUSH);
		menuItem.setText("&Resources Manager...");
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				editOptions();
            }
		});

		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		menuItem.setText("&Import TMX in Pensieve TM...");
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				importTMXInPensieve();
            }
		});

		new MenuItem(dropMenu, SWT.SEPARATOR);
		
		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		menuItem.setText("Exit\tAlt+F4");
		menuItem.setAccelerator(SWT.F4 | SWT.ALT);
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.close();
            }
		});
		
		Label stTmp = new Label(shell, SWT.NONE);
		stTmp.setText("Query:");
		stTmp.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
	
		edQuery = new Text(shell, SWT.BORDER | SWT.V_SCROLL);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.heightHint = 36;
		gdTmp.horizontalSpan = 3;
		edQuery.setLayoutData(gdTmp);

		Font font = edQuery.getFont();
		FontData[] fontData = font.getFontData();
		fontData[0].setHeight(10);
		displayFont = new Font(font.getDevice(), fontData[0]);
		edQuery.setFont(displayFont);
		
		stTmp = new Label(shell, SWT.NONE); // Place-holder
		
		Button btSearch = UIUtil.createGridButton(shell, SWT.PUSH, "Search", 80, 1);
		btSearch.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				query();
			}
		});
		shell.setDefaultButton(btSearch);
		
		chkRawText = new Button(shell, SWT.CHECK);
		chkRawText.setText("Raw text");
		
		stElapsedTime = new Label(shell, SWT.NONE);
		stElapsedTime.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		stTmp = new Label(shell, SWT.NONE);
		stTmp.setText("Target:");
		stTmp.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

		edTarget = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.heightHint = 36;
		gdTmp.horizontalSpan = 3;
		edTarget.setLayoutData(gdTmp);
		edTarget.setEditable(false);
		edTarget.setFont(displayFont);

		stTmp = new Label(shell, SWT.NONE);
		stTmp.setText("Source:");
		stTmp.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

		edSource = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.heightHint = 36;
		gdTmp.horizontalSpan = 3;
		edSource.setLayoutData(gdTmp);
		edSource.setEditable(false);
		edSource.setFont(displayFont);
		
		tblResults = new Table(shell, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 4;
		tblResults.setFont(displayFont);
		tblResults.setLayoutData(gdTmp);
		tblResults.setHeaderVisible(true);
		tblResults.addControlListener(new ControlAdapter() {
		    public void controlResized(ControlEvent e) {
		    	Table table = (Table)e.getSource();
		    	Rectangle rect = table.getClientArea();
				int nPart = rect.width / 100;
				int nRemain = rect.width % 100;
				table.getColumn(0).setWidth(8*nPart);
				table.getColumn(1).setWidth(12*nPart);
				table.getColumn(2).setWidth(40*nPart);
				table.getColumn(3).setWidth((40*nPart)+nRemain);
		    }
		});
		tblResults.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				updateCurrentHit(-1);
            }
		});
		modResults = new TableModel();
		modResults.linkTable(tblResults);
	}
	
	private void query () {
		try {
			long start = System.nanoTime(); 
			if ( chkRawText.getSelection() ) {
				queryMgt.query(edQuery.getText());
			}
			else {
				queryMgt.query(parseToTextFragment(edQuery.getText()));
			}
			long end = System.nanoTime(); 
			modResults.updateTable(queryMgt);
			updateCurrentHit(end-start);
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
		}
	}
	
	private void editOptions () {
		try {
			QueryManagerForm form = new QueryManagerForm(shell, "Resources Manager", queryMgt);
			form.showDialog();
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
		}
	}
	private void updateCurrentHit (long elapsedTime) {
		int n = tblResults.getSelectionIndex();
		if ( elapsedTime > 0 ) {
			stElapsedTime.setText("Time (in milliseconds): "+elapsedTime/1000000);
		}
		else {
			stElapsedTime.setText("");
		}
		if ( n < 0 ) {
			edTarget.setText("");
			edSource.setText("");
			return;
		}
		// Else: set the new current data
		QueryResult qr = queryMgt.getResults().get(n);
		edTarget.setText(qr.target.toString());
		edSource.setText(qr.source.toString());
	}
	
	public void run () {
		try {
			Display Disp = shell.getDisplay();
			while ( !shell.isDisposed() ) {
				if (!Disp.readAndDispatch())
					Disp.sleep();
			}
		}
		finally {
			// Dispose of any global resources
			if ( queryMgt != null ) {
				queryMgt.close();
				queryMgt = null;
			}
			if ( displayFont != null ) {
				displayFont.dispose();
				displayFont = null;
			}
		}
	}

	private String getDefaultSourceLanguage ()
	{
		// In most case the 'source' language is English
		// Even when we are on non-English machines
		return "en-US";
	}
	
	private String getDefaultTargetLanguage ()
	{
		// Use the local language by default
		Locale Loc = Locale.getDefault();
		String sCode = Loc.getLanguage();
		if ( Loc.getCountry().length() > 0 ) {
			sCode = sCode + "-" + Loc.getCountry();
		}
		// If it's the same as the source, use an arbitrary value.
		if ( areSameLanguages(sCode, getDefaultSourceLanguage(), true) ) return "fr-FR";
		else return sCode;
	}

	private boolean areSameLanguages (String p_sLanguage1,
		String p_sLanguage2,
		boolean p_bIgnoreSubLanguage)
	{
		String sL1;
		String sL2;
		if ( p_sLanguage1.length() == 0 ) return false;
		if ( p_sLanguage2.length() == 0 ) return false;

		p_sLanguage1 = p_sLanguage1.replace('_', '-');
		p_sLanguage2 = p_sLanguage2.replace('_', '-');

		if ( !p_bIgnoreSubLanguage ) {
			sL1 = p_sLanguage1;
			sL2 = p_sLanguage2;
		}
		else { // Do not take the sub-language is account
			if ( p_sLanguage1.length() > 2 ) {
				sL1 = p_sLanguage1.substring(0, 3);
				if ( sL1.charAt(2) == '-' ) sL1 = sL1.substring(0, 2);
			}
			else sL1 = p_sLanguage1.substring(0, 2);

			if ( p_sLanguage2.length() > 2 ) {
				sL2 = p_sLanguage2.substring(0, 3);
				if ( sL2.charAt(2) == '-' ) sL2 = sL2.substring(0, 2);
			}
			else sL2 = p_sLanguage2.substring(0, 2);
		}

		return sL1.equalsIgnoreCase(sL2);
	}

	/**
	 * Converts the search string into a TextFragment.
	 * With minor modifications this code is based on the SRXEditor -> processInlineCodes() method.
	 * @param  text  	Textstring to convert to TextFragment
	 * @return      TextFragment created from the search string text field
	 */	
	public TextFragment parseToTextFragment (String text) {
		
		//--parses any thing within <...> into opening codes
		//--parses any thing within </...> into closing codes
		//--parses any thing within <.../> into placeholder codes
		Pattern patternOpening = Pattern.compile("\\<(\\w+)[ ]*[^\\>/]*\\>");
		Pattern patternClosing = Pattern.compile("\\</(\\w+)[ ]*[^\\>]*\\>");
		Pattern patternPlaceholder = Pattern.compile("\\<(\\w+)[ ]*[^\\>]*/\\>");
		
		TextFragment tf = new TextFragment();
		
		tf.setCodedText(text);

		int n;
		int start = 0;
		int diff = 0;
		
		Matcher m = patternOpening.matcher(text);
		
		while ( m.find(start) ) {
			n = m.start();
			diff += tf.changeToCode(n+diff, (n+diff)+m.group().length(),
				TagType.OPENING, m.group(1));
			start = (n+m.group().length());
		}
		
		text = tf.getCodedText();
		start = diff = 0;
		m = patternClosing.matcher(text);
		while ( m.find(start) ) {
			n = m.start();
			diff += tf.changeToCode(n+diff, (n+diff)+m.group().length(),
				TagType.CLOSING, m.group(1));
			start = (n+m.group().length());
		}
		
		text = tf.getCodedText();
		start = diff = 0;
		m = patternPlaceholder.matcher(text);
		while ( m.find(start) ) {
			n = m.start();
			diff += tf.changeToCode(n+diff, (n+diff)+m.group().length(),
				TagType.PLACEHOLDER, null);
			start = (n+m.group().length());
		}
		return tf;
	}

	private void importTMXInPensieve () {
		try {
			// Get the directory
			DirectoryDialog dlg = new DirectoryDialog(shell);
			String dir = dlg.open();
			if (  dir == null ) return;
			
			// Get the languages
			InputDialog dlg2 = new InputDialog(shell, "Languages",
				"Enter source and target language separated by a space", "EN-US FR-FR", null, 0, -1, -1);
			String tmp = dlg2.showDialog();
			if ( Util.isEmpty(tmp) ) return;
			String[] langs = tmp.split("[ ,]", 0);
			if ( langs.length != 2 ) {
				throw new RuntimeException(String.format("Invalid languages: '%s'", tmp));
			}
			
			// Get TMX file
			String[] paths = Dialogs.browseFilenames(shell, "Select TMX Document to Import", false, null, null, null);
			if ( paths == null ) return;
			
			TmxFilter filter = new TmxFilter();
			OkapiTmxImporter imp = new OkapiTmxImporter(langs[0], filter);
			
			ITmWriter writer = TmWriterFactory.createFileBasedTmWriter(dir, false);
			
			File file = new File(paths[0]);
			imp.importTmx(file.toURI(), langs[1], writer);
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
		}
	}

}

