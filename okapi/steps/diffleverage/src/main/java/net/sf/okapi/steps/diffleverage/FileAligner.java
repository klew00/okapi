package net.sf.okapi.steps.diffleverage;

import java.io.File;
import java.net.URI;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Take to lists of file paths and align them so that files with the same name
 * and paths match.
 * 
 * @author HARGRAVEJE
 * @param <T>
 * 
 */
public class FileAligner<T> implements Iterable<FileAlignment<T>> {
	private List<FileLikeThing<T>> newFiles;
	private List<FileLikeThing<T>> oldFiles;	
	private URI newRootUri;
	private URI oldRootUri;
	private Map<String, FileLikeThing<T>> filesMappedToFileName;
	private List<FileAlignment<T>> alignedFiles;

	/**
	 * 
	 * @param newFiles
	 * @param oldFiles
	 */
	public FileAligner(List<FileLikeThing<T>> newFiles,
			List<FileLikeThing<T>> oldFiles, URI newRootUri, URI oldRootUri) {
		this.newFiles = newFiles;
		this.oldFiles = oldFiles;		
		this.newRootUri = newRootUri;
		this.oldRootUri = oldRootUri;

		filesMappedToFileName = new TreeMap<String, FileLikeThing<T>>();

		// put old files into our sorted map
		for (FileLikeThing<T> f : oldFiles) {
			String key = getRealtivePath(f.getPath(), oldRootUri);
			if (filesMappedToFileName.containsKey(key)) {
				// FIXME: somehow we have a duplicate, throw an exception for now
				throw new RuntimeException("Duplicate path entry");
			} else {
				filesMappedToFileName.put(key, f);
			}
		}
	}

	public void align() {
		alignedFiles = new LinkedList<FileAlignment<T>>();
		for (FileLikeThing<T> f : newFiles) {
			String key = getRealtivePath(f.getPath(), newRootUri);
			FileLikeThing<T> o = filesMappedToFileName.get(key);
			if (o != null) {
				alignedFiles.add(new FileAlignment<T>(f, o));
			}
		}
	}

	public Iterator<FileAlignment<T>> iterator() {
		return alignedFiles.iterator();
	}

	/*
	 * Return path minus the root
	 */
	private String getRealtivePath(URI path, URI root) {
		return path.relativize(root).toString();
	}
}
