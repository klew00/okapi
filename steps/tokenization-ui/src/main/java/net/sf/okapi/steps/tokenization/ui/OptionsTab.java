/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

import net.sf.okapi.common.ListUtil;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.abstracteditor.IDialogPage;
import net.sf.okapi.common.ui.abstracteditor.SWTUtil;
import net.sf.okapi.steps.tokenization.Parameters;
import net.sf.okapi.steps.tokenization.common.LanguageAndTokenParameters;
import net.sf.okapi.steps.tokenization.ui.locale.LanguageSelector;
import net.sf.okapi.steps.tokenization.ui.locale.LanguageSelectorPePage;
import net.sf.okapi.steps.tokenization.ui.tokens.TokenSelector;
import net.sf.okapi.steps.tokenization.ui.tokens.TokenSelectorPePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

public class OptionsTab extends Composite implements IDialogPage {
	private Group grpTokenizeInThe;
	private Button source;
	private Button targets;
	private Button langWC;
	private Button tokensC;
//	private LanguageAndTokenParameters filterParams = new LanguageAndTokenParameters();
	private Group grpLanguagesToTokenize;
	private Button langAll;
	private Button tokensAll;
	private Button langW;
	private Button tokens;
	private Button langB;
	private Button langBC;
	private Text langWE;
	private Text langBE;
	private Text tokensE;
	private Label label_2;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public OptionsTab(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		grpTokenizeInThe = new Group(this, SWT.NONE);
		grpTokenizeInThe.setText("General");
		grpTokenizeInThe.setToolTipText("");
		grpTokenizeInThe.setLayout(new GridLayout(3, false));
		grpTokenizeInThe.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpTokenizeInThe.setData("name", "grpTokenizeInThe");
		
		source = new Button(grpTokenizeInThe, SWT.CHECK);
		source.setData("name", "source");
		source.setText("Tokenize source");
		
		label_2 = new Label(grpTokenizeInThe, SWT.NONE);
		GridData gridData_4 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gridData_4.widthHint = 100;
		label_2.setLayoutData(gridData_4);
		label_2.setData("name", "label_2");
		
		targets = new Button(grpTokenizeInThe, SWT.CHECK);
		targets.setData("name", "targets");
		targets.setText("Tokenize targets");
		
		grpLanguagesToTokenize = new Group(this, SWT.NONE);
		grpLanguagesToTokenize.setText("Languages");
		grpLanguagesToTokenize.setLayout(new GridLayout(3, false));
		grpLanguagesToTokenize.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		langAll = new Button(grpLanguagesToTokenize, SWT.RADIO);
		langAll.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		langAll.setText("Tokenize all languages");
		langAll.setData("name", "langAll");
		new Label(grpLanguagesToTokenize, SWT.NONE);
				
				langW = new Button(grpLanguagesToTokenize, SWT.RADIO);
				GridData gridData_2 = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
				gridData_2.widthHint = 200;
				langW.setLayoutData(gridData_2);
				langW.setText("Tokenize only these languages:");
				langW.setData("name", "langW");
				new Label(grpLanguagesToTokenize, SWT.NONE);
				Label label = new Label(grpLanguagesToTokenize, SWT.NONE);
				label.setText("    ");
				label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
				
				langWE = new Text(grpLanguagesToTokenize, SWT.BORDER);
				langWE.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
				langWE.setData("name", "langWE");
				langWE.addVerifyListener(new VerifyListener() {
					public void verifyText(VerifyEvent e) {
						
						e.text = e.text.toUpperCase();
					}
				});
				
				langWC = new Button(grpLanguagesToTokenize, SWT.NONE);
				{
					GridData gridData_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
					gridData_1.widthHint = 70;
					langWC.setLayoutData(gridData_1);
				}
				langWC.setData("name", "langWC");
				langWC.setText("Select...");
				
				langB = new Button(grpLanguagesToTokenize, SWT.RADIO);
				langB.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 2, 1));
				langB.setData("name", "langB");
				langB.setText("Tokenize all languages except these:");
				new Label(grpLanguagesToTokenize, SWT.NONE);
				Label label_1 = new Label(grpLanguagesToTokenize, SWT.NONE);
				label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
				
				langBE = new Text(grpLanguagesToTokenize, SWT.BORDER);
				langBE.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
				langBE.setData("name", "langBE");
				langBE.addVerifyListener(new VerifyListener() {
					public void verifyText(VerifyEvent e) {
						
						e.text = e.text.toUpperCase();
					}
				});
				
				langBC = new Button(grpLanguagesToTokenize, SWT.NONE);
				GridData gridData_6 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
				gridData_6.widthHint = 70;
				langBC.setLayoutData(gridData_6);
				langBC.setText("Select...");
				langBC.setData("name", "langBC");
		{
			Group grpTokensToCapture = new Group(this, SWT.NONE);
			grpTokensToCapture.setText("Tokens");
			grpTokensToCapture.setLayout(new GridLayout(3, false));
			GridData gridData_3 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
			gridData_3.widthHint = 600;
			grpTokensToCapture.setLayoutData(gridData_3);
			
			tokensAll = new Button(grpTokensToCapture, SWT.RADIO);
			tokensAll.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
			tokensAll.setText("Capture all tokens");
			tokensAll.setData("name", "tokensAll");
			new Label(grpTokensToCapture, SWT.NONE);
					
			tokens = new Button(grpTokensToCapture, SWT.RADIO);
			GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
			gridData.widthHint = 200;
			tokens.setLayoutData(gridData);
			tokens.setText("Capture only these tokens:");
			tokens.setData("name", "tokens");
			new Label(grpTokensToCapture, SWT.NONE);
			new Label(grpTokensToCapture, SWT.NONE);
			
			tokensE = new Text(grpTokensToCapture, SWT.BORDER);
			tokensE.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			tokensE.setData("name", "tokensE");
			tokensE.addVerifyListener(new VerifyListener() {
				public void verifyText(VerifyEvent e) {
					
					e.text = e.text.toUpperCase();
				}
			});
			
			tokensC = new Button(grpTokensToCapture, SWT.NONE);
			{
				GridData gridData_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
				gridData_1.widthHint = 70;
				tokensC.setLayoutData(gridData_1);
			}
			tokensC.setData("name", "tokensC");
			tokensC.setText("Select...");
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public boolean canClose(boolean isOK) {

		return true;
	}

	public void interop(Widget speaker) {
		
		if (speaker == source && (SWTUtil.getNotSelected(targets) || SWTUtil.getDisabled(targets)) && SWTUtil.getNotSelected(source)) {
			Dialogs.showWarning(getShell(),
				"You cannot unselect this check-box, otherwise there's noting to tokenize.", null);
			
			SWTUtil.setSelected(source, true);
		}
		
		if (speaker == targets && (SWTUtil.getNotSelected(source) || SWTUtil.getDisabled(source)) && SWTUtil.getNotSelected(targets)) {
			Dialogs.showWarning(getShell(),
				"You cannot unselect this check-box, otherwise there's noting to tokenize.", null);
			
			SWTUtil.setSelected(targets, true);
		}

		SWTUtil.setEnabled(langWC, SWTUtil.getSelected(langW));
		SWTUtil.setEnabled(langBC, SWTUtil.getSelected(langB));
		SWTUtil.setEnabled(tokensC, SWTUtil.getSelected(tokens));
		
		SWTUtil.setEnabled(langWE, SWTUtil.getSelected(langW));
		SWTUtil.setEnabled(langBE, SWTUtil.getSelected(langB));
		SWTUtil.setEnabled(tokensE, SWTUtil.getSelected(tokens));
		
		if (speaker == langWC) {

//			filterParams.languageMode = LanguageAndTokenParameters.LANGUAGES_ONLY_WHITE_LIST;
//			filterParams.languageWhiteList = langWE.getText();
			
			// No result analysis needed, the LanguagesPage will change filterParams only if its dialog was OK-ed
			//SWTUtil.inputQuery(LanguagesPage.class, getShell(), "Languages to tokenize", filterParams, null);
			String[] res = LanguageSelector.select(getShell(), LanguageSelectorPePage.class, langWE.getText());
			//SWTUtil.setText(langWE, filterParams.languageWhiteList);
			SWTUtil.setText(langWE, ListUtil.arrayAsString(res));
		}
		
		if (speaker == langBC) {

//			filterParams.languageMode = LanguageAndTokenParameters.LANGUAGES_ALL_EXCEPT_BLACK_LIST;
//			filterParams.languageBlackList = langBE.getText();
			
			// No result analysis needed, the LanguagesPage will change filterParams only if its dialog was OK-ed
			//SWTUtil.inputQuery(LanguagesPage.class, getShell(), "Languages NOT to tokenize (exceptions)", filterParams, null);
			String[] res = LanguageSelector.select(getShell(), LanguageSelectorPePage.class, langBE.getText());
			//SWTUtil.setText(langBE, filterParams.languageBlackList);
			SWTUtil.setText(langBE, ListUtil.arrayAsString(res));
		}
		
		if (speaker == tokensC) {
			
//			filterParams.tokenMode = LanguageAndTokenParameters.TOKENS_SELECTED;
//			filterParams.tokenNames = tokensE.getText();
			
			// No result analysis needed, the TokenNamesPage will change filterParams only if its dialog was OK-ed
			//SWTUtil.inputQuery(TokenNamesPage.class, getShell(), "Tokens to capture", filterParams, null);
			String[] res = TokenSelector.select(getShell(), TokenSelectorPePage.class, tokensE.getText());
			//SWTUtil.setText(tokensE, filterParams.tokenNames);
			SWTUtil.setText(tokensE, ListUtil.arrayAsString(res));
		}
		
		if (speaker == langW)
			langWE.setFocus();
		
		if (speaker == langB)
			langBE.setFocus();
			
		if (speaker == tokens)
			tokensE.setFocus();
	}

	public boolean load(Object data) {

		if (data instanceof Parameters) {
			
			Parameters params = (Parameters) data;
			
			SWTUtil.setSelected(source, params.tokenizeSource);
			SWTUtil.setSelected(targets, params.tokenizeTargets);
			
			SWTUtil.setSelected(langAll, params.languageMode == LanguageAndTokenParameters.LANGUAGES_ALL);
			SWTUtil.setSelected(langW, params.languageMode == LanguageAndTokenParameters.LANGUAGES_ONLY_WHITE_LIST);
			SWTUtil.setSelected(langB, params.languageMode == LanguageAndTokenParameters.LANGUAGES_ALL_EXCEPT_BLACK_LIST);
			
			SWTUtil.setSelected(tokensAll, params.tokenMode == LanguageAndTokenParameters.TOKENS_ALL);								
			SWTUtil.setSelected(tokens, params.tokenMode == LanguageAndTokenParameters.TOKENS_SELECTED);
			
			SWTUtil.setText(langWE, params.languageWhiteList);
			SWTUtil.setText(langBE, params.languageBlackList);
			SWTUtil.setText(tokensE, params.tokenNames);
			
//			if (filterParams != null) {
//				
//				filterParams.languageMode = params.languageMode;
//				filterParams.languageBlackList = params.languageBlackList;
//				filterParams.languageWhiteList = params.languageWhiteList;
//				
//				filterParams.tokenMode = params.tokenMode;
//				filterParams.tokenNames = params.tokenNames;
//				
//				SWTUtil.setText(langWE, filterParams.languageWhiteList);
//				SWTUtil.setText(langBE, filterParams.languageBlackList);
//				SWTUtil.setText(tokensE, filterParams.tokenNames);
//			}
		}
		
		SWTUtil.addSpeakers(this, source, targets, langWC, langBC, tokensC, langAll, tokensAll, langW, langB, tokens); 
		
		return true;
	}

	public boolean save(Object data) {

		if (data instanceof Parameters) {
			
			Parameters params = (Parameters) data;
			
			params.tokenizeSource = SWTUtil.getSelected(source);
			params.tokenizeTargets = SWTUtil.getSelected(targets);
			
			if (SWTUtil.getSelected(langAll))
				params.languageMode = LanguageAndTokenParameters.LANGUAGES_ALL;
			
			if (SWTUtil.getSelected(langW))
				params.languageMode = LanguageAndTokenParameters.LANGUAGES_ONLY_WHITE_LIST;
			
			if (SWTUtil.getSelected(langB))
				params.languageMode = LanguageAndTokenParameters.LANGUAGES_ALL_EXCEPT_BLACK_LIST;
			
			if (SWTUtil.getSelected(tokensAll))
				params.tokenMode = LanguageAndTokenParameters.TOKENS_ALL;
			
			if (SWTUtil.getSelected(tokens))
				params.tokenMode = LanguageAndTokenParameters.TOKENS_SELECTED;
									
			params.languageWhiteList = langWE.getText();
			params.languageBlackList = langBE.getText();
			params.tokenNames = tokensE.getText();;
		}

		return true;
	}
}
