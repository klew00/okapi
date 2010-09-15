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

	public FileLikeThing<T> getNewFile() {
		return newFile;
	}

	public FileLikeThing<T> getOldSrcFile() {
		return oldSrcFile;
	}

	public FileLikeThing<T> getOldTrgFile() {
		return oldTrgFile;
	}
}