package com.googlecode.okapi.filter.zip;

import java.util.ArrayList;
import java.util.List;

class DomZipDir extends DomZipEntry{
		
		private List<DomZipEntry> children;
		private boolean isVirtual = true;
		
		private DomZipDir(){
			super("/", null);
		}

		public DomZipDir(String name, DomZipDir parent) {
			super(name, parent);
		}

		public boolean isVirtual() {
			return isVirtual;
		}
		
		public void setVirtual(boolean isVirtual) {
			this.isVirtual = isVirtual;
		}
		
		public static DomZipDir createRoot(){
			return new DomZipDir();
		}

		public boolean isEmpty(){
			return children == null ? true : children.isEmpty();
		}
		
		public DomZipEntry getChild(String name){
			for (DomZipEntry child : getChildren()) {
				if(name.equals(child.getName())){
					return child;
				}
			}
			
			return null;
		}
		
		public List<DomZipEntry> getChildren() {
			if(children == null)
				children = new ArrayList<DomZipEntry>();
			return children;
		}
		
		@Override
		public boolean isDir() {
			return true;
		}
		
	}