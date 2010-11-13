package net.sf.okapi.steps.simplekit.common;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import net.sf.okapi.common.Util;

public class MergingInfoFile {
	
	private String readerClass;
	private String filterId;
	private String filterParameters;
	private String inputEncoding;
	private String relativeOutputPath;
	private String outputEncoding;

	/**
	 * Creates a new merging information file object with no settings.
	 */
	public MergingInfoFile () {
	}
	
	/**
	 * Creates a new merging information file object.
	 * @param readerClass the name of the reader class to use for merging.
	 * @param filterId the id of the filter used to extract.
	 * @param filterParameters the parameters used to extract (can be null).
	 * @param inputEncoding the encoding used to extract.
	 * @param relativeTargetPath the output path for the merged file relative to the root.
	 * @param outputEncoding the default output encoding for the merged file.
	 */
	public MergingInfoFile (String readerClass,
		String filterId,
		String filterParameters,
		String inputEncoding,
		String relativeTargetPath,
		String outputEncoding)
	{
		this.readerClass = readerClass;
		this.filterId = filterId;
		this.filterParameters = filterParameters;
		this.inputEncoding = inputEncoding;
		this.relativeOutputPath = relativeTargetPath;
		this.outputEncoding = outputEncoding;
	}
	
	public String getReaderClass () {
		return readerClass;
	}

//	public void setReaderClass (String readerClass) {
//		this.readerClass = readerClass;
//	}

	public String getFilterId () {
		return filterId;
	}

//	public void setFilterId (String filterId) {
//		this.filterId = filterId;
//	}

	public String getFilterParameters () {
		return filterParameters;
	}

//	public void setFilterParameters (String filterParameters) {
//		this.filterParameters = filterParameters;
//	}

	public String getInputEncoding () {
		return inputEncoding;
	}

//	public void setInputEncoding (String inputEncoding) {
//		this.inputEncoding = inputEncoding;
//	}

	public String getRelativeOutputPath () {
		return relativeOutputPath;
	}

//	public void setRelativeOutputPath (String relativeOutputPath) {
//		this.relativeOutputPath = relativeOutputPath;
//	}

	public String getOutputEncoding () {
		return outputEncoding;
	}

//	public void setOutputEncoding (String outputEncoding) {
//		this.outputEncoding = outputEncoding;
//	}

	public ByteArrayOutputStream save ()
		throws IOException
	{
		DataOutputStream dos = null;
		ByteArrayOutputStream bos = null;
		try {
			bos = new ByteArrayOutputStream();
			dos = new DataOutputStream(bos);
			dos.writeUTF(readerClass);
			dos.writeUTF(filterId);
			if ( filterParameters == null ) {
				dos.writeUTF("");
			}
			else {
				dos.writeUTF(filterParameters.toString());
			}
			dos.writeUTF(inputEncoding);
			dos.writeUTF(relativeOutputPath);
			dos.writeUTF(outputEncoding);
		}
		finally {
			if ( dos != null ) dos.close();
		}
		return bos;
	}
	
	public void read (String inputPath)
		throws IOException
	{
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(new FileInputStream(inputPath));
			readerClass = dis.readUTF();
			filterId = dis.readUTF();
			String tmp = dis.readUTF();
			if ( Util.isEmpty(tmp) ) filterParameters = null;
			else filterParameters = tmp;
			inputEncoding = dis.readUTF();
			relativeOutputPath = dis.readUTF();
			outputEncoding = dis.readUTF();
		}
		finally {
			if ( dis != null ) dis.close();
		}
	}

}
