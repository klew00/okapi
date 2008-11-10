package net.sf.okapi.filters.mif.tests;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.filters.mif.MIFFilter;

public class MIFFilterTest {
	
	@Before
	public void setUp() {		
	}

	@Test
	public void runTest () {
		MIFFilter filter = null;		
		try {
			filter = new MIFFilter();
			InputStream input = MIFFilterTest.class.getResourceAsStream("/Test01.mif");
			filter.open(input);
			FilterEvent event;
			while ( filter.hasNext() ) {
				event = filter.next();
				switch ( event.getEventType() ) {
				case SKELETON_UNIT:
					System.out.println("skl=["+filter.getResource().toString()+"]");
					System.out.println("-----");
				}
			}
		}
		catch ( Throwable e ) {
			e.printStackTrace();
		}
		finally {
			if ( filter != null ) filter.close();
		}
	}
}
