package net.sf.okapi.common;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public final class FileUtil {

	/**
	 * Search all {@link File}s recursivley that pass the {@link FilenameFilter}. Adapted from
	 * http://snippets.dzone.com/posts/show/1875
	 * @param directory root directory
	 * @param filter {@link FilenameFilter} used to filter the File candidates
	 * @param recurse do we recurse or not?
	 * @return an array of {@link File}s (File[])
	 */
	public static File[] getFilteredFilesAsArray(File directory, FilenameFilter filter, boolean recurse) {
		Collection<File> files = FileUtil.getFilteredFiles(directory, filter, recurse);
		File[] arr = new File[files.size()];
		return files.toArray(arr);
	}

	/**
	 * Search all {@link File}s recursivley that pass the {@link FilenameFilter}. Adapted from
	 * http://snippets.dzone.com/posts/show/1875
	 * @param directory root directory
	 * @param filter {@link FilenameFilter} used to filter the File candidates
	 * @param recurse do we recurse or not?
	 * @return {@link Collection} of {@link File}s
	 */
	public static Collection<File> getFilteredFiles(File directory, FilenameFilter filter, boolean recurse) {
		// List of files / directories
		List<File> files = new LinkedList<File>();
	
		// Get files / directories in the directory
		File[] entries = directory.listFiles();
	
		// Go over entries
		for (File entry : entries) {
			// If there is no filter or the filter accepts the
			// file / directory, add it to the list
			if (filter == null || filter.accept(directory, entry.getName())) {
				files.add(entry);
			}
	
			// If the file is a directory and the recurse flag
			// is set, recurse into the directory
			if (recurse && entry.isDirectory()) {
				files.addAll(getFilteredFiles(entry, filter, recurse));
			}
		}
	
		// Return collection of files
		return files;
	}

}
