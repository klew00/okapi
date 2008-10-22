package com.googlecode.okapi.filter.odf;

import java.util.zip.ZipEntry;

class DomZipFile extends DomZipEntry{
		
		private ZipEntry entry;
		private int position;
		
		public DomZipFile(String name, DomZipDir parent, ZipEntry entry, int position) {
			super(name, parent);
			this.entry = entry;
			this.position = position;
		}

		public ZipEntry getEntry() {
			return entry;
		}
		
		public int getPosition() {
			return position;
		}
		
		@Override
		public boolean isDir() {
			return false;
		}
	}