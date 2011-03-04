package net.sf.okapi.lib.longhornapi;

import java.io.InputStream;

/**
 * A file that's part of an {@link LonghornProject}.
 */
public interface LonghornFile {
	
	/**
	 * @return The relative path of the file that is used to store it in the project
	 */
	String getRelativePath();
	
	/**
	 * @return The content of the file
	 */
	InputStream getContent();
}
