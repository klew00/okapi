package com.googlecode.okapi.filter.zip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.googlecode.okapi.events.ContainerEvent;
import com.googlecode.okapi.events.DocumentEvent;
import com.googlecode.okapi.events.Event;
import com.googlecode.okapi.events.EventFactory;
import com.googlecode.okapi.events.ReferenceEvent;
import com.googlecode.okapi.pipeline.PipelineDriver;
import com.googlecode.okapi.pipeline.event.BaseDocumentParser;
import com.googlecode.okapi.pipeline.event.EventWriter;
import com.googlecode.okapi.resource.DocumentId;
import com.googlecode.okapi.resource.DocumentManager;
import com.googlecode.okapi.resource.DomEventFactory;
import com.googlecode.okapi.resource.ReferenceType;
import com.googlecode.okapi.resource.ResourceFactoryImpl;

public class ZipDocumentParser extends BaseDocumentParser {

	private ZipFile zipFile;
	private DomZipDir root;
	
	/*
	 * TODO: I think Stack's are syncronized, so overusing these has a performance-penalty.
	 */
	private Stack<Iterator<DomZipEntry>> iterators = new Stack<Iterator<DomZipEntry>>();
	private Iterator<DomZipEntry> currentIterator = null;
	
	public ZipDocumentParser(EventFactory factory, File inputFile) throws IOException {
		super(factory);
		zipFile = new ZipFile(inputFile, ZipFile.OPEN_READ);
	}
	
	public ZipDocumentParser(EventFactory factory, String inputFile) throws IOException {
		this(factory, new File(inputFile));
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

	protected ZipFile getZipFile(){
		return zipFile;
	}
	
	protected void onStartZip(){
		DocumentEvent docEvent = getEventFactory().createStartDocumentEvent();
		docEvent.setName(zipFile.getName());
		addEvent(docEvent);
	}
	
	protected void onEndZip(){
		addEndEvent(); // Document
		setEndOfDocument();
	}
	
	protected void onStartZipEntryList(){
		ContainerEvent c = getEventFactory().createStartContainerEvent();
		c.setStructuralFeature("zip-entry-list");
		addEvent(c);
	}

	protected void onEndZipEntryList(){
		addEndEvent(); //zip-entry-list
	}

	protected void onVirtualZipDirEntry(String localName){
		ContainerEvent c = getEventFactory().createStartContainerEvent();
		c.setName(localName);
		c.setStructuralFeature("zip-virtual-dir");
		addEvent(c);
	}
	
	protected void onZipDirEntry(String localName, ZipEntry entry){
		ContainerEvent c = getEventFactory().createStartContainerEvent();
		c.setName(localName);
		c.setStructuralFeature("zip-dir");
		addEvent(c);
	}
	
	protected void onZipFileEntry(String localName, ZipEntry entry){
		ReferenceEvent ref = getEventFactory().createStartReferenceEvent();
		ref.setName(entry.getName());
		ref.setType(ReferenceType.Internal);
		ref.setStructuralFeature("zip-entry");
		addEvent(ref);
	}
	
	@Override
	protected void cacheNextEvent(){
		
		if(root == null){ // lazily initialize zip model
			onStartZip();

			// could move this to another event iteration
			root = DomZipDir.createRoot();
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			int count = 0;
			while(entries.hasMoreElements()){
				count++;
				ZipEntry next = entries.nextElement();
				addEntry(next, count);
			}
			
			onStartZipEntryList();
			currentIterator = root.getChildren().iterator();
		}
		else if (currentIterator.hasNext()){
			
			DomZipEntry current = currentIterator.next();
			
			if(current.isDir()){
				DomZipDir dirEntry= (DomZipDir) current;
				if(dirEntry.isVirtual()){
					onVirtualZipDirEntry(dirEntry.getName());
				}
				else{
					onZipDirEntry(dirEntry.getName(), dirEntry.getEntry());
				}
				iterators.push(currentIterator);
				currentIterator = dirEntry.getChildren().iterator();
			}
			else{ // file
				DomZipFile file = (DomZipFile) current;
				onZipFileEntry(file.getName(), file.getEntry());
			}
		}
		else{ // end of current iterator
			if(iterators.isEmpty()){
				// end document
				onEndZipEntryList();
				onEndZip();
			}
			else{ // end dir
				addEndEvent(); //zip-dir container
				currentIterator = iterators.pop();
			}
		}
	}
	
	private void addEntry(ZipEntry entry, int pos){
		String [] path = entry.getName().split("/");
		
		DomZipDir parent = root;
		
		for (int i = 0; i < path.length-1 ; i++) {
			DomZipDir dir = (DomZipDir) parent.getChild(path[i]);
			if(dir == null){
				parent = new DomZipDir(path[i],parent);
			}
			else{
				parent = dir;
			}
		}
		
		if(entry.isDirectory()){
			DomZipDir dir = (DomZipDir) parent.getChild(path[path.length-1]);
			if(dir == null){
				dir = new DomZipDir(path[path.length-1],parent);
			}
			dir.setVirtual(false);
			dir.setEntry(entry);
		}
		else{
			new DomZipFile(path[path.length-1], parent, entry, pos);
		}
	}
	
	public static void main(String[] args) {
		String inputFile = "/home/asgeirf/jdev/netbeans-6.5beta/mobility8/sources/netbeans_databindingme-src.zip";
		EventFactory factory = new DomEventFactory(
				new ResourceFactoryImpl( new DocumentId("xyz")));
		ZipDocumentParser input;
		try{
			input = new ZipDocumentParser(factory, inputFile);
			EventWriter writer = new EventWriter(null, System.out);
			PipelineDriver<Event> driver = new PipelineDriver<Event>();
			driver.addStep(writer);
			driver.setInput(input);
			driver.run();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

}
