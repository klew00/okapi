package net.sf.okapi.steps.diffleverage;

public class FileAlignment<T> {
	private FileLikeThing<T> newFile;
	private FileLikeThing<T> oldSrcFile;
	// optional target file aligned with old source
	private FileLikeThing<T> oldTrgFile;

	public FileAlignment(FileLikeThing<T> newFile, FileLikeThing<T> oldSrcFile) {
		this.newFile = newFile;
		this.oldSrcFile = oldSrcFile;
	}

	public FileAlignment(FileLikeThing<T> newFile, FileLikeThing<T> oldSrcFile,
			FileLikeThing<T> oldTrgFile) {
		this.newFile = newFile;
		this.oldSrcFile = oldSrcFile;
		this.oldTrgFile = oldTrgFile;
	}

	public FileAlignment(FileLikeThing<T> newFile) {
		this.newFile = newFile;
	}

	public FileLikeThing<T> getNew() {
		return newFile;
	}

	public FileLikeThing<T> getOldSrc() {
		return oldSrcFile;
	}

	public FileLikeThing<T> getOldTrg() {
		return oldTrgFile;
	}
}