package net.sf.okapi.applications.rainbow.utilities.bomconversion;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import net.sf.okapi.applications.rainbow.utilities.ISimpleUtility;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;

public class Utility implements ISimpleUtility {

	private final byte[]     UTF8BOM = {(byte)0xEF,(byte)0xBB,(byte)0xBF};
	
	private IParameters      params;
	private String           commonFolder;
	private String           inputPath;
	private String           outputPath;
	private boolean          removeBOM;
	private byte[]           buffer;
	

	public Utility () {
		params = new Parameters();
	}
	
	public void resetLists () {
		// Not used in this utility
	}
	
	public String getID () {
		return "oku_bomconversion";
	}
	
	/**
	 * Checks for BOM presence
	 * @param buffer The buffer to check.
	 * @param length The number of usable bytes in the buffer.
	 * @return 0 if there is no BOM.
	 */
	private int hasBOM (byte[] buffer,
		int length)
	{
		if ( length > 1 ) {
			// Check for UTF-16
			if (( buffer[0] == (byte)0xFE )
				&& ( buffer[1] == (byte)0xFF )) {
				// UTF-16BE
				return 2;
			}
			else if (( buffer[0] == (byte)0xFF )
				&& ( buffer[1] == (byte)0xFE )) {
				// UTF-16LE
				return 2;
			}
			// Check for UTF-8
			if ( length > 2 ) {
				if (( buffer[0] == (byte)0xEF )
					&& ( buffer[1] == (byte)0xBB )
					&& ( buffer[2] == (byte)0xBF )) {
					// UTF-8
					return 3;
				}
				// Check for UTF-32
				if ( length > 3) {
					if (( buffer[0] == (byte)0xFF )
						&& ( buffer[1] == (byte)0xFE )
						&& ( buffer[2] == (byte)0x00 )
						&& ( buffer[3] == (byte)0x00 )) {
						// UTF-32LE
						return 4;
					}
					else if (( buffer[0] == (byte)0x00 )
						&& ( buffer[1] == (byte)0x00 )
						&& ( buffer[2] == (byte)0xFE )
						&& ( buffer[3] == (byte)0xFF )) {
						// UTF-32BE
						return 4;
					}
				}
			}
		}
		return 0;
	}
	
	public void processInput () {
		FileInputStream input = null;
		FileOutputStream output = null;
		try {
			// Open the input
			input = new FileInputStream(inputPath);
			// Open the output
			Util.createDirectories(outputPath);
			output = new FileOutputStream(outputPath);
			
			// Reset the start of the buffer
			for ( int i=0; i<5; i++ ) buffer[i] = 0;
			// And read the 4 initial bytes
			int len = input.read(buffer, 0, 4);
			
			// Process the initial buffer
			if ( len == -1 ) {
				// Do nothing yet
			}
			else if ( len == 0 ) { // Empty file
				if ( !removeBOM ) { // Add the BOM
					// Assume the file is UTF-8
					output.write(UTF8BOM);
				}
			}
			else { // Non-empty file
				int n = hasBOM(buffer, len);
				if ( n > 0 ) { // A BOM is present
					if ( removeBOM ) {
						// Skip it, output the remaining bytes
						output.write(buffer, n, len-n);
					}
					else { // Add the BOM: It's there, just write the buffer 
						output.write(buffer, 0, len);
					}
				}
				else { // No BOM present
					if ( !removeBOM ) { // If we add, do it 
						// Assume the file is UTF-8
						output.write(UTF8BOM);
					}
					// Then write the buffer we checked
					output.write(buffer, 0, len);
				}
			}
			
			// Now copy the remaining of the file
			while ( (len = input.read(buffer)) > 0 ) {
				output.write(buffer, 0, len);
			}
		}
		catch ( FileNotFoundException e ) {
			throw new RuntimeException(e);
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
		finally {
			try { // Close the files
				if ( output != null ) {
					output.close();
					output = null;
				}
				if ( input != null ) {
					input.close();
					input = null;
				}
			}
			catch ( IOException e ) {
				throw new RuntimeException(e);
			}
		}
	}

	public void doEpilog () {
		// Release the buffer
		buffer = null;
	}

	public void doProlog (String sourceLanguage,
		String targetLanguage)
	{
		commonFolder = null; // Reset
		removeBOM = (params.getParameter("removeBOM").equals("1"));
		buffer = new byte[1024*2];
	}

	public String getInputRoot () {
		return null;
	}

	public String getOutputRoot () {
		return null;
	}

	public IParameters getParameters () {
		return params;
	}

	public boolean hasParameters () {
		return true;
	}

	public boolean isFilterDriven () {
		return false;
	}

	public boolean needsRoots () {
		return false;
	}

	public void addInputData (String path,
		String encoding,
		String filterSettings)
	{
		inputPath = path;
	}

	public void addOutputData (String path,
		String encoding)
	{
		// Compute the longest common folder
		commonFolder = Util.longestCommonDir(commonFolder,
			Util.getDirectoryName(path), !Util.isOSCaseSensitive());
		outputPath = path;
		// Encoding stays the same as the input
	}

	public void setParameters (IParameters paramsObject) {
		params = paramsObject;
	}

	public void setRoots (String inputRoot,
		String outputRoot)
	{
		// Not used in this utility.
	}

	public String getFolderAfterProcess () {
		return commonFolder;
	}

	public int getInputCount() {
		return 1;
	}
}
