package net.sf.okapi.applications.rainbow.packages.test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import net.sf.okapi.applications.rainbow.packages.BaseWriter;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.Document;
import net.sf.okapi.common.resource.SkeletonUnit;
import net.sf.okapi.common.resource.TextUnit;

public class Writer extends BaseWriter {
	
	private static final String   EXTENSION = ".xml";
	
	private StringBuilder    buffer;
	private PrintWriter      writer;     


	public String getPackageType () {
		return "test";
	}
	
	public String getReaderClass () {
		//TODO: Use dynamic name
		return "net.sf.okapi.applications.rainbow.packages.test.Reader";
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
			writer = new PrintWriter(path, "UTF-8");
			writer.println("<?xml version=\"1.0\"?>");
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public void writeEndDocument (Document resource) {
		//buffer.append(resource.endToXML());
		writer.print(buffer.toString());
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

		// toXML() is recursive already, so just call it.
		buffer.append(item.toString());
	}
	
	public void writeSkeletonPart (SkeletonUnit resource) {
		buffer.append(resource.toString());
	}
	
	public void writeStartDocument (Document resource) {
		//buffer.append(resource.startToXML());
	}
}
