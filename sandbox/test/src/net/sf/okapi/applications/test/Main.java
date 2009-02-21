package net.sf.okapi.applications.test;

import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.FilterEventType;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.properties.PropertiesFilter;

public class Main {
	public static void main (String[] args) {
		try {
			IFilter filter = new PropertiesFilter();
			filter.setOptions("en", "iso-8859-1", true);
			filter.open("key1=Text1\nkey2=Text2");
			while ( filter.hasNext() ) {
				FilterEvent event = filter.next();
				if ( event.getEventType() == FilterEventType.TEXT_UNIT ) {
					TextUnit tu = (TextUnit)event.getResource();
					System.out.println("--");
					System.out.println("key=["+tu.getName()+"]");
					System.out.println("text=["+tu.getSource()+"]");
				}
			}
			filter.close();
		}
		catch ( Throwable e ) {
			e.printStackTrace();
		}
	}		
}
