package com.googlecode.okapi.filter.zip;

import java.util.zip.ZipEntry;

class DomZipFile extends DomZipEntry{
		
		private int position;
		
		public DomZipFile(String name, DomZipDir parent, ZipEntry entry, int position) {
			super(name, parent);
			this.position = position;
			setEntry(entry);
		}
		
		public int getPosition() {
			return position;
		}
		
		@Override
		public boolean isDir() {
			return false;
		}
	}