package net.sf.okapi.applications.rainbow.packages.rtf;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import net.sf.okapi.applications.rainbow.packages.BaseWriter;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.Document;
import net.sf.okapi.common.resource.SkeletonUnit;
import net.sf.okapi.common.resource.TextUnit;

public class Writer extends BaseWriter {
	
	private static final String   EXTENSION = ".rtf";

	private StringBuilder    buffer;
	private PrintWriter      writer;     
	private CharsetEncoder   outputEncoder;


	public String getPackageType () {
		return "rtf";
	}
	
	public String getReaderClass () {
		//TODO: Use dynamic name
		return "net.sf.okapi.applications.rainbow.packages.rtf.Reader";
	}
	
	@Override
	public void writeStartPackage () {
		manifest.setSourceLocation("work");
		manifest.setTargetLocation("work");
		manifest.setOriginalLocation("original");
		manifest.setDoneLocation("done");
		super.writeStartPackage();
	}

	@Override
	public void createDocument (int docID,
		String relativeSourcePath,
		String relativeTargetPath,
		String sourceEncoding,
		String targetEncoding,
		String filtersettings,
		IParameters filterParams)
	{
		relativeWorkPath = relativeSourcePath;
		relativeWorkPath += EXTENSION;
		
		outputEncoder = Charset.forName(targetEncoding).newEncoder();

		super.createDocument(docID, relativeSourcePath, relativeTargetPath,
			sourceEncoding, targetEncoding, filtersettings, filterParams);

		try {
			if ( writer != null ) {
				writer.close();
			}
			buffer = new StringBuilder();
			String path = manifest.getRoot() + File.separator
				+ ((manifest.getSourceLocation().length() == 0 ) ? "" : (manifest.getSourceLocation() + File.separator)) 
				+ relativeWorkPath;
			Util.createDirectories(path);
			writer = new PrintWriter(path, targetEncoding);
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public void writeEndDocument (Document resource) {
		writer.write(Util.RTF_ENDCODE+"}\n");
		writer.close();
		manifest.addDocument(docID, relativeWorkPath, relativeSourcePath,
			relativeTargetPath, sourceEncoding, targetEncoding, filterID);
	}

	public void writeItem (TextUnit item,
		int status)
	{
		// Write the items in the TM if needed
		if ( item.hasTarget() ) {
			tmxWriter.writeItem(item);
		}
		if ( item.hasChild() ) {
			for ( TextUnit tu : item.childTextUnitIterator() ) {
				if ( tu.hasTarget() ) {
					tmxWriter.writeItem(tu);
				}
			}
		}

		buffer.append(item.toString());
	}
	
	public void writeSkeletonPart (SkeletonUnit resource) {
		writer.write(Util.escapeToRTF(resource.toString(), true, 0, outputEncoder));
	}
	
	public void writeStartDocument (Document resource) {
		//TODO: change codepage
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
			"{\\cs2 \\cf4\\fs40\\f1 tw4winError;}\n"+
			"{\\cs3 \\f1\\cf11 tw4winPopup;}\n"+
			"{\\cs4 \\f1\\cf10 tw4winJump;}\n"+
			"{\\cs5 \\additive \\cf15\\f1\\lang1024 tw4winExternal;}\n"+
			"{\\cs6 \\additive \\cf6\\f1\\lang1024 tw4winInternal;}\n"+
			"{\\cs7 \\cf2 tw4winTerm;}\n"+
			"{\\cs8 \\additive \\cf13\\f1\\lang1024 DO_NOT_TRANSLATE;}\n"+
			"{\\cs9 \\additive Default Paragraph Font;}"+
			"{\\cs15 \\additive \\v\\f1\\cf12\\sub tw4winMark;}"+
			"}\n"+
			"\\paperw11907\\paperh16840\\viewkind4\\viewscale100\\pard\\plain\\s0\\sb80\\slmult1\\widctlpar\\fs20\\f1 \n"+
			Util.RTF_STARTCODE);
		
	}
}
