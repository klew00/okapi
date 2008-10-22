package com.googlecode.okapi.filter.odf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.googlecode.okapi.pipeline.BaseDocumentParser;
import com.googlecode.okapi.resource.Container;
import com.googlecode.okapi.resource.DocumentManager;
import com.googlecode.okapi.resource.Reference;
import com.googlecode.okapi.resource.Reference.Type;

public class OdfZipDocumentParser extends BaseDocumentParser {

	private ZipFile zipFile;
	private DomZipDir root;
	
	/*
	 * TODO: I think Stack's are syncronized, so overusing these has a performance-penalty.
	 */
	private Stack<Iterator<DomZipEntry>> iterators = new Stack<Iterator<DomZipEntry>>();
	private Iterator<DomZipEntry> currentIterator = null;
	
	public OdfZipDocumentParser(DocumentManager documentManager, File inputFile) throws IOException {
		super(documentManager);
		zipFile = new ZipFile(inputFile, ZipFile.OPEN_READ);
		documentManager.getDocument().setName(zipFile.getName());
		addStartDocumentEvent();
	}
	
	public OdfZipDocumentParser(DocumentManager documentManager, String inputFile) throws IOException {
		this(documentManager, new File(inputFile));
	}

	/**
	 * Allows a child-parser to access the input stream for a zip part
	 * 
	 * @param partId
	 * @return
	 * @throws IOException
	 */
	public InputStream getInputStreamForPart(String partId) throws IOException{
		return zipFile.getInputStream(zipFile.getEntry(partId));
	}
	
	@Override
	public void close() {
		currentIterator = null;
		root = null;
		try{
			zipFile.close();
		}
		catch(IOException e){}
		
		super.close();
	}
	
	@Override
	protected void cacheNextEvent(){
		
		if(root == null){ // lazily initialize zip model
			root = DomZipDir.createRoot();
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			int count = 0;
			while(entries.hasMoreElements()){
				count++;
				ZipEntry next = entries.nextElement();
				addEntry(next, count);
			}
			Container c = getResourceFactory().createContainer();
			c.setStructuralFeature("zip-entry-list");
			addStartContainerEvent(c);
			currentIterator = root.getChildren().iterator();
		}
		else if (currentIterator.hasNext()){
			
			DomZipEntry current = currentIterator.next();
			
			if(current.isDir()){
				DomZipDir dirEntry= (DomZipDir) current;
				
				Container c = getResourceFactory().createContainer();
				c.setName(dirEntry.getName());
				c.setStructuralFeature("zip-dir");
				addStartContainerEvent(c);
				
				iterators.push(currentIterator);
				currentIterator = dirEntry.getChildren().iterator();
				
			}
			else{ // file
				DomZipFile file = (DomZipFile) current;
				Reference ref = getResourceFactory().createReference();
				ref.setName(file.getName());
				ref.setType(Type.Internal);
				ref.setStructuralFeature("zip-entry");
				addStartReferenceEvent(ref);
				addEndReferenceEvent();
			}
		}
		else{ // end of current iterator
			if(iterators.isEmpty()){
				// end document
				addEndContainerEvent(); //zip-entry-list
				addEndDocumentEvent();
				setEndOfDocument();
			}
			else{ // end dir
				addEndContainerEvent(); //zip-dir
				currentIterator = iterators.pop();
			}
		}
	}
	
	private void addEntry(ZipEntry entry, int pos){
		String [] path = entry.getName().split("/");
		
		DomZipDir parent = root;
		
		for (int i = 0; i < path.length ; i++) {
			if(i == path.length -1 && !entry.isDirectory()){
				new DomZipFile(path[i], parent, entry, pos);
			}
			else{
				DomZipDir dir = (DomZipDir) parent.getChild(path[i]);
				if(dir == null){
					parent = new DomZipDir(path[i],parent);
				}
				else{
					parent = dir;
				}
				
			}
		}
	}

	
	void processZip(){
/*
		BufferedOutputStream dest = null;
		BufferedInputStream is = null;
		ArrayList<DocumentEntry> list = new ArrayList<DocumentEntry>();
		try {
			//TODO: generate more unique temp
			SimpleDateFormat dt = new SimpleDateFormat("_HHmmssS");
			commonPart = Util.getTempDirectory() + File.separator
				+ Util.getFilename(path, true)
				+ dt.format(new Date());
			ZipEntry entry;
			ZipFile zipfile = new ZipFile(path);
			Enumeration<? extends ZipEntry> entries = zipfile.entries();
			
			while( entries.hasMoreElements() ) {
				entry = entries.nextElement();
				DocumentEntry docEntry = new DocumentEntry();
				if ( entry.getName().equals("content.xml") ) {
					docEntry.path = commonPart + "." + entry.getName();
					docEntry.docType = entry.getName();
					list.add(docEntry);
				}
				else if ( entry.getName().equals("meta.xml") ) {
					docEntry.path = commonPart + "." + entry.getName();
					docEntry.docType = entry.getName();
					list.add(docEntry);
				}
				else if ( entry.getName().equals("styles.xml") ) {
					docEntry.path = commonPart + "." + entry.getName();
					docEntry.docType = entry.getName();
					list.add(docEntry);
				}
				else continue;
				
				Util.createDirectories(docEntry.path);
				
				// If it's a file, unzip it
				is = new BufferedInputStream(zipfile.getInputStream(entry));
				FileOutputStream fos = new FileOutputStream(docEntry.path);
				int count;
				byte data[] = new byte[BUFFER_SIZE];
				dest = new BufferedOutputStream(fos, BUFFER_SIZE);
				while ( (count = is.read(data, 0, BUFFER_SIZE)) != -1 ) {
					dest.write(data, 0, count);
				}
				dest.flush();
			}
			return list;
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
		finally {
			try {
				if ( dest != null ) dest.close();
				if ( is != null ) is.close();
			}
			catch ( IOException e ) {
				throw new RuntimeException(e);
			}
		}
		
*/	
	}
}
