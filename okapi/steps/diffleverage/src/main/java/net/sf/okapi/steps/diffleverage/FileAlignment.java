package net.sf.okapi.steps.diffleverage;

public class FileAlignment<T> {
	private FileLikeThing<T> newFile;
	private FileLikeThing<T> oldFile;
	
	public FileAlignment(FileLikeThing<T> newFile, FileLikeThing<T> oldFile) {
		this.newFile = newFile;
		this.oldFile = oldFile;
	}
	
	public FileLikeThing<T> getNewFile() {
		return newFile;
	}
	public FileLikeThing<T> getOldFile() {
		return oldFile;
	}
}