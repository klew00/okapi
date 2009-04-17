package net.sf.okapi.filters.tmx.tests;

import java.net.URI;
import java.net.URL;

import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.filters.tests.FilterTestDriver;
import net.sf.okapi.filters.tmx.TmxFilter;
import org.junit.Assert;
import org.junit.Test;

public class TmxFilterTest {

	@Test
	public void runTest () {
		FilterTestDriver testDriver = new FilterTestDriver();
		TmxFilter filter = null;		
		try {
			filter = new TmxFilter();
			URL url = TmxFilterTest.class.getResource("/ImportTest2A.tmx");
			filter.open(new RawDocument(new URI(url.toString()), "UTF-8", "EN-US", "FR-CA"));			
			if ( !testDriver.process(filter) ) Assert.fail();
			filter.close();
			//process(filter);
			//filter.close();
			
		}
		catch ( Throwable e ) {
			e.printStackTrace();
			Assert.fail("Exception occured");
		}
		finally {
			if ( filter != null ) filter.close();
		}
	}	
	
/*	private void process (IFilter filter) {
		
		System.out.println("==================================================");
		Event event;
		while ( filter.hasNext() ) {
			event = filter.next();
			switch ( event.getEventType() ) {		
			case START_DOCUMENT:
				System.out.println("---Start Document");
				printSkeleton(event.getResource());
				break;
			case END_DOCUMENT:
				System.out.println("---End Document");
				printSkeleton(event.getResource());
				break;
			case START_GROUP:
				System.out.println("---Start Group");
				printSkeleton(event.getResource());
				break;
			case END_GROUP:
				System.out.println("---End Group");
				printSkeleton(event.getResource());
				break;
			case TEXT_UNIT:
				System.out.println("---Text Unit");
				TextUnit tu = (TextUnit)event.getResource();
				printResource(tu);
				System.out.println("S=["+tu.toString()+"]");
				for ( String lang : tu.getTargetLanguages() ) {
					System.out.println("T=["+tu.getTarget(lang).toString()+"]");
				}
				printSkeleton(tu);
				break;
			case DOCUMENT_PART:
				System.out.println("---Document Part");
				printResource((INameable)event.getResource());
				printSkeleton(event.getResource());
				break;				
			}
		}
	}
	
	private void printResource (INameable res) {
		System.out.println("  id="+res.getId());
		System.out.println("  name="+res.getName());
		System.out.println("  type="+res.getType());
		System.out.println("  mimeType="+res.getMimeType());
	}

	private void printSkeleton (IResource res) {
		ISkeleton skel = res.getSkeleton();
		if ( skel != null ) {
			System.out.println("---");
			System.out.println(skel.toString());
			System.out.println("---");
		}
	}*/
}
