package com.googlecode.okapi.filter.zip;

import java.util.zip.ZipEntry;

abstract class DomZipEntry{
		
		private String name;
		private DomZipDir parent;
		private ZipEntry entry;
		
		public DomZipEntry(String name, DomZipDir parent) {
			this.name = name;
			this.parent = parent;
			if(parent != null)
				parent.getChildren().add(this);
		}
		
		public String getName() {
			return name;
		}
		
		public DomZipDir getParent() {
			return parent;
		}
		
		public ZipEntry getEntry() {
			return entry;
		}

		public void setEntry(ZipEntry entry) {
			this.entry = entry;
		}
		
		
		public abstract boolean isDir();
		
		@Override
		public boolean equals(Object obj) {
			DomZipEntry other = (DomZipEntry) obj;
			if(other == null) return false;
			
			return this.getName().equals(other.getName()) &&
				this.isDir() == other.isDir();
		}
		
		@Override
		public int hashCode() {
		    int result = 17;
		    result = 37*result + (isDir() ? 0 : 1);
		    result = 37*result + getName().hashCode();
		    return result;
		}
	}
