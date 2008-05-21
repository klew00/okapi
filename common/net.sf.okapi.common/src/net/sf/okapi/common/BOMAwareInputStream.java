package net.sf.okapi.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/**
 * Helper class to detect and skip BOM in InputStream, including for UTF-8.
 */
//TODO: Maybe find a better way to deal with UTF-8 BOM: this is pretty ugly...
public class BOMAwareInputStream extends InputStream {

	private static final int BUFFER_SIZE = 4;
	
	private PushbackInputStream   initStream;
	private InputStream           input;
	public String                 defaultEncoding;
	public String                 detectedEncoding;

	public BOMAwareInputStream (InputStream input,
		String defaultEncoding) {
		this.input = input;
		this.defaultEncoding = defaultEncoding;
	}
	
	public String detectEncoding ()
		throws IOException
	{
		initStream = new PushbackInputStream(input, BUFFER_SIZE);
		byte bom[] = new byte[BUFFER_SIZE];
		int n = initStream.read(bom, 0, bom.length);
		
		int unread;
		if (( bom[0] == (byte)0xEF )
			&& ( bom[1] == (byte)0xBB )
			&& ( bom[2] == (byte)0xBF )) {
			detectedEncoding = "UTF-8";
			unread = n-3;
		}
		else if (( bom[0] == (byte)0xFE )
			&& ( bom[1] == (byte)0xFF )) {
			detectedEncoding = "UTF-16BE";
			unread = n-2;
		}
		else if (( bom[0] == (byte)0xFF )
			&& ( bom[1] == (byte)0xFE )
			&& ( bom[2] == (byte)0x00 )
			&& ( bom[3] == (byte)0x00 )) {
			detectedEncoding = "UTF-32LE";
			unread = n-4;
		}
		else if (( bom[0] == (byte)0xFF )
			&& ( bom[1] == (byte)0xFE )) {
			detectedEncoding = "UTF-16LE";
			unread = n-2;
		}
		else if (( bom[0] == (byte)0x00 )
			&& ( bom[1] == (byte)0x00 )
			&& ( bom[2] == (byte)0xFE )
			&& ( bom[3] == (byte)0xFF )) {
			detectedEncoding = "UTF-32BE";
			unread = n-4;
		}
		else { // No BOM
			detectedEncoding = defaultEncoding;
			unread = n;
		}

		// Push-back bytes as needed
		if ( unread > 0 ) {
			initStream.unread(bom, (n-unread), unread);
		}

		return detectedEncoding;
	}
	
	@Override
	public int read()
		throws IOException
	{
		return initStream.read();
	}

}
