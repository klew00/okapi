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

package net.sf.okapi.applications.rainbow.utilities.xsltransform;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import net.sf.okapi.common.ConfigurationString;
import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.NSContextManager;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.UIUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Editor implements IParametersEditor {
	
	private Shell shell;
	private boolean result = false;
	private OKCancelPanel pnlActions;
	private Parameters params;
	private Text edXsltPath;
	private Text edParameters;
	private IHelp help;

	public boolean edit (IParameters params,
		Object object,
		IHelp helpParam)
	{
		boolean bRes = false;
		try {
			shell = null;
			help = helpParam;
			this.params = (Parameters)params;
			shell = new Shell((Shell)object, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
			create((Shell)object);
			return showDialog();
		}
		catch ( Exception e ) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
			bRes = false;
		}
		finally {
			// Dispose of the shell, but not of the display
			if ( shell != null ) shell.dispose();
		}
		return bRes;
	}
	
	public IParameters createParameters () {
		return new Parameters();
	}
	
	private void create (Shell parent)
	{
		shell.setText(Res.getString("editor.caption")); //$NON-NLS-1$
		if ( parent != null ) UIUtil.inheritIcon(shell, parent);
		GridLayout layTmp = new GridLayout();
		layTmp.marginBottom = 0;
		layTmp.verticalSpacing = 0;
		shell.setLayout(layTmp);

		TabFolder tfTmp = new TabFolder(shell, SWT.NONE);
		tfTmp.setLayoutData(new GridData(GridData.FILL_BOTH));

		//--- Options tab

		Composite cmpTmp = new Composite(tfTmp, SWT.NONE);
		cmpTmp.setLayout(new GridLayout(4, false));
		TabItem tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText(Res.getString("editor.tabOptions")); //$NON-NLS-1$
		tiTmp.setControl(cmpTmp);

		Label label = new Label(cmpTmp, SWT.NONE);
		label.setText(Res.getString("editor.stXsltPath")); //$NON-NLS-1$
		GridData gdTmp = new GridData();
		gdTmp.horizontalSpan = 4;
		label.setLayoutData(gdTmp);

		edXsltPath = new Text(cmpTmp, SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 3;
		edXsltPath.setLayoutData(gdTmp);
		
		Button btGetPath = new Button(cmpTmp, SWT.PUSH);
		btGetPath.setText("..."); //$NON-NLS-1$
		btGetPath.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		btGetPath.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String[] paths = Dialogs.browseFilenames(shell,
					Res.getString("editor.captionSelectTemplate"), //$NON-NLS-1$
					false, null,
					Res.getString("editor.filterSelectTemplate"), //$NON-NLS-1$
					"*.xsl;*.xslt\t*.*"); //$NON-NLS-1$
				if ( paths != null ) {
					edXsltPath.setText(paths[0]);
				}
			}
		});

		label = new Label(cmpTmp, SWT.NONE);
		label.setText(Res.getString("editor.stParameters")); //$NON-NLS-1$
		
		int wideButtonWidth = Res.getInt("editor.wideButtonWidth"); //$NON-NLS-1$
		Button btGetDefaults = new Button(cmpTmp, SWT.PUSH);
		btGetDefaults.setText(Res.getString("editor.btGetDefaults")); //$NON-NLS-1$
		gdTmp = new GridData();
		gdTmp.widthHint = wideButtonWidth;
		btGetDefaults.setLayoutData(gdTmp);
		btGetDefaults.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getParametersFromTemplate();
			}
		});
		
		Button btOpenFile = new Button(cmpTmp, SWT.PUSH);
		btOpenFile.setText(Res.getString("editor.btOpenTemplate")); //$NON-NLS-1$
		gdTmp = new GridData();
		gdTmp.horizontalSpan = 2;
		gdTmp.widthHint = wideButtonWidth;
		btOpenFile.setLayoutData(gdTmp);
		btOpenFile.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Program.launch(edXsltPath.getText()); 
			}
		});
		
		edParameters = new Text(cmpTmp, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.heightHint = 70;
		gdTmp.horizontalSpan = 4;
		edParameters.setLayoutData(gdTmp);
		
		//--- Dialog-level buttons

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = false;
				if ( e.widget.getData().equals("h") ) { //$NON-NLS-1$
					if ( help != null ) help.showTopic(this, "index"); //$NON-NLS-1$
					return;
				}
				if ( e.widget.getData().equals("o") ) { //$NON-NLS-1$
					if ( !saveData() ) return;
				}
				shell.close();
			};
		};
		pnlActions = new OKCancelPanel(shell, SWT.NONE, OKCancelActions, true);
		pnlActions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		shell.setDefaultButton(pnlActions.btOK);

		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Dialogs.centerWindow(shell, parent);
		setData();
	}
	
	private boolean showDialog () {
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
		return result;
	}

	private void setData () {
		edXsltPath.setText(params.xsltPath);
		ConfigurationString tmp = new ConfigurationString(params.paramList);
		edParameters.setText(tmp.toString());
	}

	private boolean saveData () {
		if ( edXsltPath.getText().length() == 0 ) {
			edXsltPath.setFocus();
			return false;
		}
		params.xsltPath = edXsltPath.getText();
		ConfigurationString tmp = new ConfigurationString(edParameters.getText());
		params.paramList = tmp.toString();
		result = true;
		return result;
	}
	
	private void getParametersFromTemplate () {
		try {
			String path = edXsltPath.getText();
			if ( path.length() == 0 ) return;

			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setNamespaceAware(true);
		    DocumentBuilder builder = domFactory.newDocumentBuilder();
		    Document doc = builder.parse(path);

		    XPathFactory factory = XPathFactory.newInstance();
		    XPath xpath = factory.newXPath();
		    xpath.setNamespaceContext(new NSContextManager());
		    XPathExpression expr = xpath.compile("//xsl:param"); //$NON-NLS-1$

		    Object result = expr.evaluate(doc, XPathConstants.NODESET);
		    NodeList nodes = (NodeList) result;
		    ConfigurationString paramList = new ConfigurationString();
		    Element elem;
		    for (int i = 0; i < nodes.getLength(); i++) {
		    	elem = (Element)nodes.item(i);
		    	paramList.add(elem.getAttribute("name"), Util.getTextContent(elem)); //$NON-NLS-1$
		    }
		    edParameters.setText(paramList.toString());
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getMessage(), null);
		}
	}
	
}
