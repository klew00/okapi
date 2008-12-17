package net.sf.okapi.filters.tmx.tests;

import java.io.InputStream;
import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.IFilter;
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
			filter.setOptions("en-us","fr-ca", "UTF-8",true);
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
		FilterEvent event;
		while ( filter.hasNext() ) {
			event = filter.next();
			switch ( event.getEventType() ) {
			case START_DOCUMENT:
				System.out.println("--- Start Document ---\n");
				break;
			case END_DOCUMENT:
				System.out.println("--- End Document ---\n");
				break;
			//case DOCUMENT_PART:
			//	System.out.println("--- Document Part---\n");
			//	break;
			case START_GROUP:
				System.out.println("--- Start Group ---\n");
				break;
			case END_GROUP:
				System.out.println("--- End Group ---\n");
				break;
			case TEXT_UNIT:
				System.out.println("["+filter.getResource().toString()+"]");
				break;
			}
		}
	}
}
