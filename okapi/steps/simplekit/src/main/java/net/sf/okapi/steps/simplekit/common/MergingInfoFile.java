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
	private String filterParams;
	private String inputEncoding;
	private String outputName;
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
	 * @param filterParams the parameters used to extract (can be null).
	 * @param inputEncoding the encoding used to extract.
	 * @param outputName the default output name for the merged file.
	 * @param outputEncoding the default output encoding for the merged file.
	 */
	public MergingInfoFile (String readerClass,
		String filterId,
		String filterParams,
		String inputEncoding,
		String outputName,
		String outputEncoding)
	{
		this.readerClass = readerClass;
		this.filterId = filterId;
		this.filterParams = filterParams;
		this.inputEncoding = inputEncoding;
		this.outputName = outputName;
		this.outputEncoding = outputEncoding;
	}
	
	public String getReaderClass () {
		return readerClass;
	}

	public void setReaderClass (String readerClass) {
		this.readerClass = readerClass;
	}

	public String getFilterId () {
		return filterId;
	}

	public void setFilterId (String filterId) {
		this.filterId = filterId;
	}

	public String getFilterParams () {
		return filterParams;
	}

	public void setFilterParams (String filterParams) {
		this.filterParams = filterParams;
	}

	public String getInputEncoding () {
		return inputEncoding;
	}

	public void setInputEncoding (String inputEncoding) {
		this.inputEncoding = inputEncoding;
	}

	public String getOutputName () {
		return outputName;
	}

	public void setOutputName (String outputName) {
		this.outputName = outputName;
	}

	public String getOutputEncoding () {
		return outputEncoding;
	}

	public void setOutputEncoding (String outputEncoding) {
		this.outputEncoding = outputEncoding;
	}

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
			if ( filterParams == null ) {
				dos.writeUTF("");
			}
			else {
				dos.writeUTF(filterParams.toString());
			}
			dos.writeUTF(inputEncoding);
			dos.writeUTF(outputName);
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
			if ( Util.isEmpty(tmp) ) filterParams = null;
			else filterParams = tmp;
			inputEncoding = dis.readUTF();
			outputName = dis.readUTF();
			outputEncoding = dis.readUTF();
		}
		finally {
			if ( dis != null ) dis.close();
		}
	}

}
