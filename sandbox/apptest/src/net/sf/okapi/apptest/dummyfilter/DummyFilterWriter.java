package net.sf.okapi.apptest.dummyfilter;

import java.io.OutputStream;
import java.util.Iterator;

import net.sf.okapi.apptest.common.INameable;
import net.sf.okapi.apptest.common.IParameters;
import net.sf.okapi.apptest.common.IResource;
import net.sf.okapi.apptest.filters.FilterEvent;
import net.sf.okapi.apptest.filters.IFilterWriter;
import net.sf.okapi.apptest.resource.DocumentPart;
import net.sf.okapi.apptest.resource.Ending;
import net.sf.okapi.apptest.resource.Property;
import net.sf.okapi.apptest.resource.StartGroup;
import net.sf.okapi.apptest.resource.TextUnit;
import net.sf.okapi.apptest.skeleton.GenericSkeleton;
import net.sf.okapi.apptest.skeleton.GenericSkeletonPart;

public class DummyFilterWriter implements IFilterWriter {

	private String outputPath;
	private String language;
	private String defaultEncoding;

	public void close () {
		System.out.println("DummyFilterWriter: close() called");
		reset();
	}

	public String getName () {
		return "DummyFilterWriter";
	}

	public IParameters getParameters () {
		return null;
	}

	public FilterEvent handleEvent (FilterEvent event) {
		StartGroup grp;
		TextUnit tu;
		DocumentPart dp;
		Ending ending;
		switch ( event.getEventType() ) {
		case START_DOCUMENT:
			System.out.println("START_DOCUMENT -> StartDocument={");
			printInfo((INameable)event.getResource());
			printSkeleton((GenericSkeleton)event.getResource().getSkeleton());
			System.out.println("}");
			break;
		case END_DOCUMENT:
			ending = (Ending)event.getResource();
			System.out.println("END_DOCUMENT -> Ending={");
			System.out.println("   id="+ending.getId());
			printSkeleton((GenericSkeleton)event.getResource().getSkeleton());
			System.out.println("}");
			close();
			break;
		case START_SUBDOCUMENT:
			System.out.println("START_SUBDOCUMENT -> StartSubDocument={");
			printInfo((INameable)event.getResource());
			printSkeleton((GenericSkeleton)event.getResource().getSkeleton());
			System.out.println("}");
			break;
		case END_SUBDOCUMENT:
			ending = (Ending)event.getResource();
			System.out.println("END_SUBDOCUMENT -> Ending={");
			System.out.println("   id="+ending.getId());
			printSkeleton((GenericSkeleton)event.getResource().getSkeleton());
			System.out.println("}");
			break;
		case START_GROUP:
			grp = (StartGroup)event.getResource();
			System.out.println("START_GROUP -> StartGroup={");
			printInfo((INameable)event.getResource());
			System.out.println("   isReference="+grp.isReferent());
			printSkeleton((GenericSkeleton)event.getResource().getSkeleton());
			System.out.println("}");
			break;
		case END_GROUP:
			ending = (Ending)event.getResource();
			System.out.println("END_GROUP -> Ending={");
			System.out.println("   id="+ending.getId());
			printSkeleton((GenericSkeleton)event.getResource().getSkeleton());
			System.out.println("}");
			break;
		case TEXT_UNIT:
			tu = (TextUnit)event.getResource();
			System.out.println("TEXT_UNIT -> TextUnit={");
			printInfo((INameable)event.getResource());
			System.out.println("   isReference="+tu.isReferent());
			printTextUnit((TextUnit)event.getResource());
			printSkeleton((GenericSkeleton)event.getResource().getSkeleton());
			System.out.println("}");
			break;
		case DOCUMENT_PART:
			dp = (DocumentPart)event.getResource();
			System.out.println("DOCUMENT_PART -> DocumentPart={");
			printInfo((INameable)event.getResource());
			System.out.println("   isReference="+dp.isReferent());
			printSkeleton((GenericSkeleton)event.getResource().getSkeleton());
			System.out.println("}");
			break;
		}
		return event;
	}

	private void printTextUnit (TextUnit tu) {
		System.out.println("   text="+out(tu.toString()));
		Iterator<String> iterLang = tu.targetLanguages();
		while ( iterLang.hasNext() ) {
			String lang = iterLang.next();
			System.out.println("   target["+lang+"]={");
				TextUnit trg = tu.getTarget(lang);
				System.out.println("      text="+out(trg.toString()));
				System.out.println("      properties={");
				Iterator<String> iterProp = trg.propertyNames();
				while ( iterProp.hasNext() ) {
					String name = iterProp.next();
					Property prop = trg.getProperty(name);
					System.out.println("         prop["+name+"]='"+prop.getValue()+"' ("
						+ (prop.isWriteable() ? "localizable" : "read-only" ) + ")");
				}
				System.out.println("      }");
			System.out.println("   }");
		}
	}
	
	private void printInfo (INameable res) {
		System.out.println("   id="+((IResource)res).getId());
		System.out.println("   properties={");
		Iterator<String> iter = res.propertyNames();
		while ( iter.hasNext() ) {
			String name = iter.next();
			Property prop = res.getProperty(name);
			System.out.println("      prop["+name+"]='"+prop.getValue()+"' ("
				+ (prop.isWriteable() ? "localizable" : "read-only" ) + ")");
		}
		System.out.println("   }");
	}
	
	private void printSkeleton(GenericSkeleton skel) {
		System.out.println("   skeleton={");
		if ( skel != null ) {
			for ( GenericSkeletonPart part : skel.getParts() ) {
				System.out.println("      part='"+part.toString().replace("\n", "\\n")+"'");
			}
		}
		System.out.println("   }");
	}
	
	private String out (String text) {
		return "["+text.replace("\n", "\\n")+"]";
	}

	public void setOptions (String language,
		String defaultEncoding)
	{
		this.language = language;
		this.defaultEncoding = defaultEncoding;
		System.out.println("DummyFilterWriter: setOptions() called");
		System.out.println(" -output language = " + this.language);
		System.out.println(" -output default encoding = " + this.defaultEncoding);
	}

	public void setOutput (String path) {
		outputPath = path;
		System.out.println("DummyFilterWriter: setOutput(path) called");
		System.out.println(" -output path = " + outputPath);
	}

	public void setOutput (OutputStream output) {
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
