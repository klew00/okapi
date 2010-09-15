package net.sf.okapi.steps.diffleverage;

import java.net.URI;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Take to lists of file paths and align them so that files with the same name and paths match.
 * 
 * @author HARGRAVEJE
 * @param <T>
 * 
 */
public class FileAligner<T> implements Iterable<FileAlignment<T>> {
	private List<FileLikeThing<T>> newFiles;
	private URI newRootUri;
	private Map<String, FileLikeThing<T>> oldSrcFilesMap;
	private Map<String, FileLikeThing<T>> oldTrgFilesMap;
	private List<FileAlignment<T>> alignedFiles;

	public FileAligner(List<FileLikeThing<T>> newFiles, List<FileLikeThing<T>> oldSrcFiles, List<FileLikeThing<T>> oldTrgFiles,
			URI newRootUri, URI oldSrcRootUri, URI oldTrgRootUri) {
		this(newFiles, oldSrcFiles, newRootUri, oldSrcRootUri);
		
		oldTrgFilesMap = new TreeMap<String, FileLikeThing<T>>();

		// put old files into our sorted map
		for (FileLikeThing<T> f : oldTrgFiles) {
			String key = getRealtivePath(f.getPath(), oldTrgRootUri);
			if (oldTrgFilesMap.containsKey(key)) {
				// FIXME: somehow we have a duplicate, throw an exception for now
				throw new RuntimeException("Duplicate path entry: " + key);
			} else {
				oldTrgFilesMap.put(key, f);
			}
		}

	}
	
	/**
	 * 
	 * @param newFiles
	 * @param oldFiles
	 */
	public FileAligner(List<FileLikeThing<T>> newFiles, List<FileLikeThing<T>> oldSrcFiles,
			URI newRootUri, URI oldSrcRootUri) {
		this.newFiles = newFiles;
		this.newRootUri = newRootUri;

		oldSrcFilesMap = new TreeMap<String, FileLikeThing<T>>();

		// put old files into our sorted map
		for (FileLikeThing<T> f : oldSrcFiles) {
			String key = getRealtivePath(f.getPath(), oldSrcRootUri);
			if (oldSrcFilesMap.containsKey(key)) {
				// FIXME: somehow we have a duplicate, throw an exception for now
				throw new RuntimeException("Duplicate path entry: " + key);
			} else {
				oldSrcFilesMap.put(key, f);
			}
		}
	}

	public void align() {
		alignedFiles = new LinkedList<FileAlignment<T>>();
		for (FileLikeThing<T> f : newFiles) {
			String key = getRealtivePath(f.getPath(), newRootUri);
			FileLikeThing<T> o = oldSrcFilesMap.get(key);
			if (o != null) {
				if (oldTrgFilesMap != null) {
					FileLikeThing<T> t = oldTrgFilesMap.get(key);
					alignedFiles.add(new FileAlignment<T>(f, o, t));
				} else {
					alignedFiles.add(new FileAlignment<T>(f, o));
				}
			}
		}
	}

	public Iterator<FileAlignment<T>> iterator() {
		return alignedFiles.iterator();
	}

	public List<FileAlignment<T>> getAlignments() {
		return alignedFiles;
	}

	/*
	 * Return path minus the root
	 */
	public static String getRealtivePath(URI path, URI root) {
		String r = path.relativize(root).toString();
		return path.toString().replaceFirst(r, "");
	}
}
