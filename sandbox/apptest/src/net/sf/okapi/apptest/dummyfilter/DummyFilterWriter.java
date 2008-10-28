package net.sf.okapi.apptest.dummyfilter;

import java.io.OutputStream;

import net.sf.okapi.apptest.common.IParameters;
import net.sf.okapi.apptest.filters.FilterEvent;
import net.sf.okapi.apptest.filters.IFilterWriter;
import net.sf.okapi.apptest.resource.Group;
import net.sf.okapi.apptest.resource.LocaleProperties;
import net.sf.okapi.apptest.resource.PropertiesUnit;
import net.sf.okapi.apptest.resource.SkeletonUnit;
import net.sf.okapi.apptest.resource.TextUnit;

public class DummyFilterWriter implements IFilterWriter {

	private String outputPath;
	private String language;
	private String defaultEncoding;

	public void close() {
		System.out.println("DummyFilterWriter: close() called");
		reset();
	}

	public String getName() {
		return "DummyFilterWriter";
	}

	public IParameters getParameters() {
		return null;
	}

	public void handleEvent(FilterEvent event) {
		Group grp;
		TextUnit tu;
		SkeletonUnit su;
		PropertiesUnit pu;
		switch ( event.getEventType() ) {
		case START_DOCUMENT:
			System.out.println("start-document");
			break;
		case END_DOCUMENT:
			System.out.println("end-document");
			close();
			break;
		case START_SUBDOCUMENT:
			System.out.println("start-sub-document");
			break;
		case END_SUBDOCUMENT:
			System.out.println("end-sub-document");
			break;
		case START_GROUP:
			grp = (Group)event.getResource();
			System.out.println("start-group={");
			System.out.println("   id="+grp.getID());
			if ( grp.isReference() ) {
				System.out.println("   isReference="+grp.isReference());
			}
			System.out.println("}");
			//level++;
			break;
		case END_GROUP:
			//level--;
			grp = (Group)event.getResource();
			System.out.println("end-group={");
			System.out.println("   id="+grp.getID());
			System.out.println("}");
			break;
		case SKELETON_UNIT:
			su = (SkeletonUnit)event.getResource();
			System.out.println("skeleton-unit={");
			System.out.println("   id="+su.getID());
			if ( su.isReference() ) {
				System.out.println("   isReference="+su.isReference());
			}
			System.out.println("   data="+out(su.toString()));
			System.out.println("}");
			break;
		case TEXT_UNIT:
			tu = (TextUnit)event.getResource();
			System.out.println("text-unit={");
			System.out.println("   id="+tu.getID());
			if ( tu.isReference() ) {
				System.out.println("   isReference="+tu.isReference());
			}
			System.out.println("   text="+out(tu.toString()));
			System.out.println("}");
			break;
		case PROPERTIES_UNIT:
			pu = (PropertiesUnit)event.getResource();
			System.out.println("properties-unit={");
			System.out.println("   id="+pu.getID());
			if ( pu.isReference() ) {
				System.out.println("   isReference="+pu.isReference());
			}
			LocaleProperties lp = pu.getSourceProperties();
			for ( String key : lp.getProperties().keySet() ) {
				System.out.println("   prop: key="+key+" value='"+lp.getProperty(key)+"'");
			}
			// TODO: values
			System.out.println("}");
			break;
		}
	}
	
	private String out (String text) {
		return "["+text.replace("\n", "\\n")+"]";
	}

	public void setOptions(String language,
		String defaultEncoding)
	{
		this.language = language;
		this.defaultEncoding = defaultEncoding;
		System.out.println("DummyFilterWriter: setOptions() called");
		System.out.println(" -output language = " + this.language);
		System.out.println(" -output default encoding = " + this.defaultEncoding);
	}

	public void setOutput(String path) {
		outputPath = path;
		System.out.println("DummyFilterWriter: setOutput(path) called");
		System.out.println(" -output path = " + outputPath);
	}

	public void setOutput(OutputStream output) {
		System.out.println("DummyFilterWriter: setOutput(OutputStream) called");
	}

	public void setParameters (IParameters params) {
	}

	private void reset () {
		outputPath = null;
		language = null;
		defaultEncoding = null;
	}

}
