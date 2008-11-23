package net.sf.okapi.apptest.skeleton;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Stack;

import net.sf.okapi.common.Util;

public class RTFFilterWriter extends GenericSkeletonWriter {

	protected void createWriter () {
		try {
			// Assume the encoding is ok, call the super class
			super.createWriter();
			// Write the RTF header 
			writer.write("{\\rtf1\\ansi\\ansicpg" + "1252" + "\\uc1\\deff1 \n"+
				"{\\fonttbl \n"+
				"{\\f1 \\fmodern\\fcharset0\\fprq1 Courier New;}\n"+
				"{\\f2 \\fswiss\\fcharset0\\fprq2 Arial;}\n"+
				"{\\f3 \\froman\\fcharset0\\fprq2 Times New Roman;}}\n"+
				"{\\colortbl \\red0\\green0\\blue0;\\red0\\green0\\blue0;\\red0\\green0\\blue255;"+
				"\\red0\\green255\\blue255;\\red0\\green255\\blue0;\\red255\\green0\\blue255;"+
				"\\red255\\green0\\blue0;\\red255\\green255\\blue0;\\red255\\green255\\blue255;"+
				"\\red0\\green0\\blue128;\\red0\\green128\\blue128;\\red0\\green128\\blue0;"+
				"\\red128\\green0\\blue128;\\red128\\green0\\blue0;\\red128\\green128\\blue0;"+
				"\\red128\\green128\\blue128;\\red192\\green192\\blue192;}\n"+
				"{\\stylesheet \n"+
				"{\\s0 \\sb80\\slmult1\\widctlpar\\fs20\\f1 \\snext0 Normal;}\n"+
				"{\\cs1 \\additive \\v\\cf12\\sub\\f1 tw4winMark;}\n"+
				"{\\cs2 \\additive \\cf4\\fs40\\f1 tw4winError;}\n"+
				"{\\cs3 \\additive \\f1\\cf11 tw4winPopup;}\n"+
				"{\\cs4 \\additive \\f1\\cf10 tw4winJump;}\n"+
				"{\\cs5 \\additive \\cf15\\f1\\lang1024\\noproof tw4winExternal;}\n"+
				"{\\cs6 \\additive \\cf6\\f1\\lang1024\\noproof tw4winInternal;}\n"+
				"{\\cs7 \\additive \\cf2 tw4winTerm;}\n"+
				"{\\cs8 \\additive \\cf13\\f1\\lang1024\\noproof DO_NOT_TRANSLATE;}\n"+
				"{\\cs9 \\additive Default Paragraph Font;}"+
				"{\\cs15 \\additive \\v\\f1\\cf12\\sub tw4winMark;}"+
				"}\n"+
				"\\paperw11907\\paperh16840\\viewkind4\\viewscale100\\pard\\plain\\s0\\sb80\\slmult1\\widctlpar\\fs20\\f1 \n"+
				Util.RTF_STARTCODE);
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}
	
	
}
