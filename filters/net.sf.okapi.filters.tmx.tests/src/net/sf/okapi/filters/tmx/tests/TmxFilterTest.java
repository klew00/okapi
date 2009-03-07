package net.sf.okapi.filters.tmx.tests;

import java.io.InputStream;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.INameable;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.tmx.TmxFilter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class TmxFilterTest {

	@Before
	public void setUp() {
		System.out.println("SetUp");
	}	
	
	@Test
	public void runTest () {
		TmxFilter filter = null;		
		try {
			filter = new TmxFilter();
			filter.setOptions("EN-US","FR-CA", "UTF-8",true);
			InputStream input = TmxFilterTest.class.getResourceAsStream("/ImportTest2A.tmx");
			filter.open(input);
			process(filter);
			filter.close();
			
		}
		catch ( Throwable e ) {
			e.printStackTrace();
			Assert.fail("Exception occured");
		}
		finally {
			if ( filter != null ) filter.close();
		}
	}	
	
	private void process (IFilter filter) {
		
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
	}
}
