package net.sf.okapi.applications.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.its.IProcessor;
import org.w3c.its.ITSEngine;
import org.w3c.its.ITraversal;

import net.sf.okapi.common.filters.IInputFilter;
import net.sf.okapi.common.filters.IOutputFilter;
import net.sf.okapi.common.resource.CodeFragment;
import net.sf.okapi.common.resource.Container;
import net.sf.okapi.common.resource.ExtractionItem;
import net.sf.okapi.common.resource.IContainer;
import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.IFragment;
import net.sf.okapi.filters.xml.XMLReader;

public class Main {

	private static void testContainer () {
		try {
			System.out.println("---start testContainer---");
			IContainer cnt = new Container();
			cnt.append("t1");
			cnt.append(new CodeFragment(IContainer.CODE_ISOLATED, 1, "<br/>"));
			cnt.append("t2");
			System.out.println("out 1: " + cnt.toString());
			
			String s1 = cnt.getCodedText();
			cnt.setContent(s1);
			System.out.println("out 2: " + cnt.toString());
			
			List<IFragment> codes = cnt.getCodes();
			cnt.setContent(s1, codes);
			System.out.println("out 3: " + cnt.toString());
			
			List<IFragment> list = cnt.getFragments();
			for ( IFragment frag : list ) {
				System.out.println(String.format("istext=%s, content='%s'",
					(frag.isText() ? "yes" : "no "),
					frag.toString()));
			}
			
			cnt = new Container();
			cnt.append("t1");
			cnt.append(new CodeFragment(IContainer.CODE_ISOLATED, 1, "<C1/>"));
			cnt.append("t2");
			cnt.append(new CodeFragment(IContainer.CODE_ISOLATED, 2, "<C2/>"));
			cnt.append("t3");
			System.out.println("out 1: " + cnt.toString());

			s1 = cnt.getCodedText();
			String s2 = s1.substring(2, 4);
			String s3 = s1.substring(6, 8);
			StringBuilder sb1 = new StringBuilder(s1);
			sb1.setCharAt(2, s3.charAt(0));
			sb1.setCharAt(3, s3.charAt(1));
			sb1.setCharAt(6, s2.charAt(0));
			sb1.setCharAt(7, s2.charAt(1));
			cnt.setContent(sb1.toString());
			System.out.println("out 2: " + cnt.toString());
			
			ArrayList<IFragment> newCodes = new ArrayList<IFragment>();
			newCodes.add(new CodeFragment(IContainer.CODE_ISOLATED, 2, "<Ca2/>"));
			newCodes.add(new CodeFragment(IContainer.CODE_ISOLATED, 1, "<Cb1/>"));
			cnt.setContent(cnt.getCodedText(), newCodes);
			System.out.println("out 3: " + cnt.toString());
			
			cnt.setProperty("test1", "value1");
			System.out.println(String.format("name='test1' value='%s'",
				cnt.getProperty("test1")));
			cnt.setProperty("test1", null);
			System.out.println(String.format("name='test1' value='%s'",
				cnt.getProperty("test1")));
			cnt.setProperty("test1", "value1 again");
			System.out.println(String.format("name='test1' value='%s'",
				cnt.getProperty("test1")));
			cnt.clearProperties();
			System.out.println(String.format("name='test1' value='%s'",
				cnt.getProperty("test1")));
		}		
		catch ( Exception e ) {
			e.printStackTrace();
		}
		System.out.println("---end testContainer---");
	}
	
	private static void testXMLReader () {
		try {
			System.out.println("---start testXMLReader---");
			XMLReader reader = new XMLReader();
			String inputName = "testdata\\Test02.xml";
			InputStream input = new FileInputStream(inputName);
			reader.open(input, inputName);
			int n;
			do {
				n = reader.read();
				IExtractionItem item = reader.getItem();
				switch ( n ) {
				case XMLReader.RESULT_STARTTRANSUNIT:
					System.out.println("sTU:"+item.getType()+",'"+item.getSource().toString()+"'");
					break;
				case XMLReader.RESULT_ENDTRANSUNIT:
					System.out.println("eTU:"+item.getType()+",'"+item.getSource().toString()+"'");
					break;
				}
			} while ( n > XMLReader.RESULT_ENDINPUT );
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
		System.out.println("---end testXMLReader---");
	}
	
	private static void testITSEngine () {
		try {
			System.out.println("---start testITSEngine---");
			DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
			fact.setNamespaceAware(true);
			fact.setValidating(false);
			String inputPath = "testdata//test02.xml";
			InputStream input = new FileInputStream(inputPath);
			Document doc = fact.newDocumentBuilder().parse(input);
			URI inputURI = new URI("file:///"+inputPath);
			ITSEngine itsEng = new ITSEngine(doc, inputURI);
			itsEng.applyRules(IProcessor.DC_ALL);
			
			itsEng.startTraversal();
			Node node;
			while ( (node = itsEng.nextNode()) != null ) {
				switch ( node.getNodeType() ) {
				case Node.ELEMENT_NODE:
					if ( itsEng.backTracking() ) {
						System.out.println("end of "+node.getLocalName());
					}
					else {
						System.out.println("start of "+node.getLocalName());
						System.out.println(String.format("   translate=%s",
							(itsEng.translate() ? "yes" : "no")));
						switch ( itsEng.getWithinText() ) {
						case ITraversal.WITHINTEXT_YES:
							System.out.println("   withinText=yes");
							break;
						case ITraversal.WITHINTEXT_NESTED:
							System.out.println("   withinText=nested");
							break;
						default:
							System.out.println("   withinText=no");
							break;
						}
						System.out.println(String.format("   directionality=%d",
							itsEng.getDirectionality()));
						NamedNodeMap list = node.getAttributes();
						for ( int i=0; i<list.getLength(); i++ ) {
							Node attr = list.item(i);
							System.out.println("      - " + attr.getNodeName() + ", trans="
								+ (itsEng.translate(attr.getNodeName()) ? "yes" : "no"));
						}
					}
					break;
				}
			}
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
		System.out.println("---end testITSEngine---");
	}
	
	private static void testItem () {
		try {
			System.out.println("---start testItem---");
			IExtractionItem item = new ExtractionItem();
			
			item.addSegment(new Container("This is segment 1. "));
			item.addSegment(new Container("This is segment 2. "));
			List<IContainer> list = item.getSegments();
			for ( IContainer seg : list ) {
				System.out.println("seg='"+seg.toString()+"'");
			}
			System.out.println("all segs= '"+item.toString()+"'");
			item.addSegment(new Container("This is segment 3."));
			list = item.getSegments();
			for ( IContainer seg : list ) {
				System.out.println("seg='"+seg.toString()+"'");
			}
			System.out.println("all segs= '"+item.toString()+"'");
			
			item.removeSegmentation();
			System.out.println("After removing segs:");
			list = item.getSegments();
			for ( IContainer seg : list ) {
				System.out.println("seg='"+seg.toString()+"'");
			}
			System.out.println("all segs= '"+item.toString()+"'");
			
			item = new ExtractionItem();
			item.getSource().append("item1");
			ExtractionItem childItem1 = new ExtractionItem();
			childItem1.getSource().append("child1 of item1");
			item.addChild(childItem1);
			ExtractionItem childItem2 = new ExtractionItem();
			childItem2.getSource().append("child1 of child1 of item1");
			childItem1.addChild(childItem2);
			childItem2 = new ExtractionItem();
			childItem2.getSource().append("child2 of child1 of item1");
			childItem1.addChild(childItem2);
			
			IExtractionItem currentItem = item.getFirstItem();
			do {
				System.out.println("item text=" + currentItem.toString()
					+ ", parent=" + (currentItem.getParent()==null ? "none" : currentItem.getParent().toString()));
			} while ((currentItem = item.getNextItem()) != null);
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
		System.out.println("---end testItem---");
	}
	
	private static void testFilter () {
		try {
			System.out.println("---start testContainer---");
		
			String inputFile = "test.properties";
			IInputFilter inputFlt = new net.sf.okapi.filters.properties.InputFilter();
			FileInputStream input = new FileInputStream(inputFile);
			inputFlt.initialize(input, inputFile, null, "utf-16", null, null);
			
			IOutputFilter outputFlt = new net.sf.okapi.filters.properties.OutputFilter();
			FileOutputStream output = new FileOutputStream("test.out.properties");
			outputFlt.initialize(output, "us-ascii", null);
			
			inputFlt.setOutput(outputFlt);
			inputFlt.process();
		}
		catch ( Exception e ) {
			System.out.println(e.getLocalizedMessage());
		}
		System.out.println("---end testContainer---");
	}

	public static void main (String[] args)
		throws Exception
	{
		testITSEngine();
		if ( args.length == 0 ) return;
		testContainer();
		testXMLReader();
		testItem();
		testFilter();
	}		
		
}
