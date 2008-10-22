package com.googlecode.okapi.filter.odf;

abstract class DomZipEntry{
		
		private String name;
		private DomZipDir parent;
		
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
