/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel                                          */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.common.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CharacterInfoDialog {
	
	private Shell            shell;
	private String           help;
	private ClosePanel       pnlActions;
	private int              codePoint;
	private CLabel           stRendering;
	private Text             edCharacter;
	private Text             edCodePoint;
	private Text             edType;
	private Text             edNumValue;
	private Text             edIsJavaSpace;
	private Text             edIsUnicodeSpace;
	private boolean          settingCodePoint = false;
	private Font             sampleFont;

	@Override
	protected void finalize () {
		dispose();
	}

	public CharacterInfoDialog (Shell parent,
		String captionText,
		String helpFile)
	{
		help = helpFile;
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		if ( captionText != null ) shell.setText(captionText);
		shell.setImage(parent.getImage());
		shell.setLayout(new GridLayout());
		
		Composite cmpTmp = new Composite(shell, SWT.BORDER);
		cmpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layTmp = new GridLayout(3, false);
		cmpTmp.setLayout(layTmp);
		
		stRendering = new CLabel(cmpTmp, SWT.BORDER | SWT.CENTER);
		GridData gdTmp = new GridData();
		gdTmp.widthHint = 60;
		gdTmp.heightHint = 60;
		gdTmp.verticalSpan = 2;
		stRendering.setLayoutData(gdTmp);

		Font font = stRendering.getFont();
		FontData[] fontData = font.getFontData();
		fontData[0].setHeight(20);
		sampleFont = new Font(font.getDevice(), fontData[0]);
		stRendering.setFont(sampleFont);

		Label label = new Label(cmpTmp, SWT.NONE);
		label.setText("Code point:");
		edCodePoint = new Text(cmpTmp, SWT.BORDER);
		gdTmp = new GridData();
		gdTmp.widthHint = 60;
		edCodePoint.setLayoutData(gdTmp);
		edCodePoint.addModifyListener(new ModifyListener () {
			public void modifyText(ModifyEvent e) {
				updateCodePoint();
			}
		});
		
		label = new Label(cmpTmp, SWT.NONE);
		label.setText("Character:");
		edCharacter = new Text(cmpTmp, SWT.BORDER);
		gdTmp = new GridData();
		gdTmp.widthHint = 30;
		edCharacter.setLayoutData(gdTmp);
		edCharacter.addModifyListener(new ModifyListener () {
			public void modifyText(ModifyEvent e) {
				updateCharacter();
			}
		});
		
		label = new Label(cmpTmp, SWT.NONE);
		label.setText("Character type:");
		edType = new Text(cmpTmp, SWT.BORDER);
		edType.setEditable(false);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		edType.setLayoutData(gdTmp);
		
		label = new Label(cmpTmp, SWT.NONE);
		label.setText("Numeric value:");
		edNumValue = new Text(cmpTmp, SWT.BORDER);
		edNumValue.setEditable(false);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		edNumValue.setLayoutData(gdTmp);
		
		label = new Label(cmpTmp, SWT.NONE);
		label.setText("Java isWhitespace():");
		edIsJavaSpace = new Text(cmpTmp, SWT.BORDER);
		edIsJavaSpace.setEditable(false);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		edIsJavaSpace.setLayoutData(gdTmp);

		label = new Label(cmpTmp, SWT.NONE);
		label.setText("Is Unicode whitespace:");
		edIsUnicodeSpace = new Text(cmpTmp, SWT.BORDER);
		edIsUnicodeSpace.setEditable(false);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		edIsUnicodeSpace.setLayoutData(gdTmp);

		//--- Dialog-level buttons

		SelectionAdapter CloseActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( e.widget.getData().equals("h") ) {
					UIUtil.start(help);
					return;
				}
				shell.close();
			};
		};
		pnlActions = new ClosePanel(shell, SWT.NONE, CloseActions, (helpFile != null));
		pnlActions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		shell.setDefaultButton(pnlActions.btClose);

		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Point startSize = shell.getMinimumSize();
		if ( startSize.x < 450 ) startSize.x = 450;
		shell.setSize(startSize);
		Dialogs.centerWindow(shell, parent);
	}
	
	public void dispose () {
		if ( sampleFont != null ) {
			sampleFont.dispose();
			sampleFont = null;
		}
	}

	private void updateCodePoint () {
		try {
			if ( settingCodePoint ) return;
			String tmp = edCodePoint.getText();
			if ( tmp.length() != 4 ) return;
			int cp = Integer.valueOf(tmp, 16);
			setCodePoint(cp);
			edCodePoint.setSelection(4, 4);
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, "Invalid value: "+e.getMessage(), null);
		}
	}
	
	private void updateCharacter () {
		try {
			if ( settingCodePoint ) return;
			String tmp = edCharacter.getText();
			if ( tmp.length() < 1 ) return;
			setCodePoint(tmp.codePointAt(0));
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, "Invalid value: "+e.getMessage(), null);
		}
	}
	
	private void setCodePoint (int value) {
		settingCodePoint = true;
		codePoint = value;
		stRendering.setText(String.format("%c", codePoint));
		edCharacter.setText(stRendering.getText());
		edCodePoint.setText(String.format("%04X", codePoint));
		
		int type = Character.getType(codePoint);
		switch ( type ) {
		case Character.COMBINING_SPACING_MARK:
			edType.setText("Mc : COMBINING_SPACING_MARK");
			break;
		case Character.CONNECTOR_PUNCTUATION:
			edType.setText("Pc : CONNECTOR_PUNCTUATION");
			break;
		case Character.CONTROL:
			edType.setText("Cc : CONTROL");
			break;
		case Character.CURRENCY_SYMBOL:
			edType.setText("Sc : CURRENCY_SYMBOL");
			break;
		case Character.DASH_PUNCTUATION:
			edType.setText("Pd : DASH_PUNCTUATION");
			break;
		case Character.DECIMAL_DIGIT_NUMBER:
			edType.setText("Nd : DECIMAL_DIGIT_NUMBER");
			break;
		case Character.ENCLOSING_MARK:
			edType.setText("Me : ENCLOSING_MARK");
			break;
		case Character.END_PUNCTUATION:
			edType.setText("Pe : END_PUNCTUATION");
			break;
		case Character.FINAL_QUOTE_PUNCTUATION:
			edType.setText("Pf : FINAL_QUOTE_PUNCTUATION");
			break;
		case Character.FORMAT:
			edType.setText("Cf : FORMAT");
			break;
		case Character.INITIAL_QUOTE_PUNCTUATION:
			edType.setText("Pi : INITIAL_QUOTE_PUNCTUATION");
			break;
		case Character.LETTER_NUMBER:
			edType.setText("Nl : LETTER_NUMBER");
			break;
		case Character.UPPERCASE_LETTER:
			edType.setText("Lu : UPPERCASE_LETTER");
			break;
 		case Character.LINE_SEPARATOR:
 			edType.setText("Zl : LINE_SEPARATOR");
 			break;
 		case Character.LOWERCASE_LETTER:
	 		edType.setText("Ll : LOWERCASE_LETTER");
			break;
		case Character.MATH_SYMBOL:
			edType.setText("Sm : MATH_SYMBOL");
			break;
		case Character.MODIFIER_LETTER:
			edType.setText("Lm : MODIFIER_LETTER");
			break;
		case Character.MODIFIER_SYMBOL:
			edType.setText("Sk : MODIFIER_SYMBOL");
			break;
		case Character.NON_SPACING_MARK:
			edType.setText("Mn : NON_SPACING_MARK");
			break;
		case Character.OTHER_LETTER:
			edType.setText("Lo : OTHER_LETTER");
			break;
		case Character.OTHER_NUMBER:
			edType.setText("No : OTHER_NUMBER");
			break;
		case Character.OTHER_PUNCTUATION:
			edType.setText("Po : OTHER_PUNCTUATION");
			break;
		case Character.OTHER_SYMBOL:
			edType.setText("So : OTHER_SYMBOL");
			break;
		case Character.PARAGRAPH_SEPARATOR:
			edType.setText("Zp : PARAGRAPH_SEPARATOR");
			break;
		case Character.PRIVATE_USE:
			edType.setText("Co : PRIVATE_USE");
			break;
		case Character.SPACE_SEPARATOR:
			edType.setText("Zs : SPACE_SEPARATOR");
			break;
		case Character.START_PUNCTUATION:
			edType.setText("Ps : START_PUNCTUATION");
			break;
		case Character.SURROGATE:
			edType.setText("Cs : SURROGATE");
			break;
		case Character.TITLECASE_LETTER:
			edType.setText("Lt : TITLECASE_LETTER");
			break;
		case Character.UNASSIGNED:
			edType.setText("Cn : UNASSIGNED");
			break;
		}
		
		edIsJavaSpace.setText(Character.isWhitespace(codePoint)? "YES" : "NO");
		edIsUnicodeSpace.setText(Character.isSpaceChar(codePoint)? "YES" : "NO");
		edNumValue.setText(String.valueOf(Character.getNumericValue(codePoint)));
		
		settingCodePoint = false;
	}
		
/*	
	case Character.DIRECTIONALITY_ARABIC_NUMBER:
		edType.setText("AN : DIRECTIONALITY_ARABIC_NUMBER");
		break;
	case Character.DIRECTIONALITY_BOUNDARY_NEUTRAL:
		edType.setText("BN : DIRECTIONALITY_BOUNDARY_NEUTRAL");
		break;
	case Character.DIRECTIONALITY_COMMON_NUMBER_SEPARATOR:
		edType.setText("CS : DIRECTIONALITY_COMMON_NUMBER_SEPARATOR");
		break;
	case Character.DIRECTIONALITY_EUROPEAN_NUMBER:
		edType.setText("EN : DIRECTIONALITY_EUROPEAN_NUMBER");
		break;
	case Character.DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR:
		edType.setText("ES : DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR");
		break;
	case Character.DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR: 
		edType.setText("ET : DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR");
		break;
	case Character.DIRECTIONALITY_LEFT_TO_RIGHT:
		edType.setText("L : DIRECTIONALITY_LEFT_TO_RIGHT");
		break;
	case Character.DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING:
		edType.setText("LRE : DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING");
		break;
	case Character.DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE:
		edType.setText("LRO : DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE");
		break;
	case Character.DIRECTIONALITY_NONSPACING_MARK 
		edType.setText("Cc : CONTROL");
		break;
	          Weak bidirectional character type "NSM" in the Unicode specification. 
	case Character.DIRECTIONALITY_OTHER_NEUTRALS 
		edType.setText("Cc : CONTROL");
		break;
	          Neutral bidirectional character type "ON" in the Unicode specification. 
	case Character.DIRECTIONALITY_PARAGRAPH_SEPARATOR 
		edType.setText("Cc : CONTROL");
		break;
	          Neutral bidirectional character type "B" in the Unicode specification. 
	case Character.DIRECTIONALITY_POP_DIRECTIONAL_FORMAT 
		edType.setText("Cc : CONTROL");
		break;
	          Weak bidirectional character type "PDF" in the Unicode specification. 
	case Character.DIRECTIONALITY_RIGHT_TO_LEFT 
		edType.setText("Cc : CONTROL");
		break;
	          Strong bidirectional character type "R" in the Unicode specification. 
	case Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC 
		edType.setText("Cc : CONTROL");
		break;
	          Strong bidirectional character type "AL" in the Unicode specification. 
	case Character.DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING 
		edType.setText("Cc : CONTROL");
		break;
	          Strong bidirectional character type "RLE" in the Unicode specification. 
	case Character.DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE 
		edType.setText("Cc : CONTROL");
		break;
	          Strong bidirectional character type "RLO" in the Unicode specification. 
	case Character.DIRECTIONALITY_SEGMENT_SEPARATOR 
		edType.setText("Cc : CONTROL");
		break;
	          Neutral bidirectional character type "S" in the Unicode specification. 
	case Character.DIRECTIONALITY_UNDEFINED 
		edType.setText("Cc : CONTROL");
		break;
	          Undefined bidirectional character type. 
	case Character.DIRECTIONALITY_WHITESPACE 
		edType.setText("Cc : CONTROL");
		break;
	          Neutral bidirectional character type "WS" in the Unicode specification. 
		
	}
*/	
	public void showDialog (int codePoint) {
		setCodePoint(codePoint);
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
	}

}
