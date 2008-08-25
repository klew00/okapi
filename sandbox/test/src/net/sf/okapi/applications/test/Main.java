package net.sf.okapi.applications.test;

import java.awt.Point;
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

import net.sf.okapi.common.ConfigurationString;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.IInputFilter;
import net.sf.okapi.common.filters.IOutputFilter;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.xml.XMLReader;
import net.sf.okapi.lib.segmentation.LanguageMap;
import net.sf.okapi.lib.segmentation.Rule;
import net.sf.okapi.lib.segmentation.SRXDocument;
import net.sf.okapi.lib.segmentation.Segmenter;

public class Main {

/*	private static void testContainer () {
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
			newCodes.add(new CodeFragment(IContainer.CODE_ISOLATED, 2, "[IC2]"));
			newCodes.add(new CodeFragment(IContainer.CODE_ISOLATED, 1, "[IC1]"));
			cnt.setContent(cnt.getCodedText(), newCodes);
			System.out.println("out 3: " + cnt.toString());
			
			System.out.println("---segmentation tries:");
			cnt = new Container();
			cnt.append(new Part("textoutside-before", false));
			cnt.append(new Container("t1 "));
			cnt.append(new Container("t2 "));
			cnt.append(new Part("Text in the segment", true));
			cnt.append(new CodeFragment(IContainer.CODE_ISOLATED, 1, "[ic1]"));
			cnt.append(new TextFragment(" textoutside-after"));
			System.out.println("txt=["+cnt.toString()+"]");
			System.out.println("xml=["+cnt.toXML()+"]");
			
			cnt.joinParts();
			System.out.println("no-seg: txt=["+cnt.toString()+"]");
			System.out.println("no-seg: xml=["+cnt.toXML()+"]");
			
			System.out.println("---extract() tries:");
			cnt = new Container("0123456789");
			cnt.append(new CodeFragment(IContainer.CODE_OPENING, 1, "[OC1]"));
			cnt.append("cdef");
			cnt.append(new CodeFragment(IContainer.CODE_CLOSING, 1, "[CC1]"));
			// 0123456789--cdef--
			// 012345678901234567
			System.out.println("ori=["+cnt.toString()+"]");
			IPart res = cnt.copy(2, 5);
			System.out.println("copy(2,5)=["+res.toXML()+"]");
			res = cnt.copy(4, 14);
			System.out.println("copy(4,14)=["+res.toXML()+"]");
			res = cnt.copy(0);
			System.out.println("copy(0)=["+res.toXML()+"]");
			res = cnt.copy(7);
			System.out.println("copy(7)=["+res.toXML()+"]");
			res = cnt.copy(10);
			System.out.println("copy(10)=["+res.toXML()+"]");
			res = cnt.copy(0, 1);
			System.out.println("copy(0, 1)=["+res.toXML()+"]");
			res = cnt.copy(4, 4);
			System.out.println("copy(4, 4)=["+res.toXML()+"]");
			res = cnt.copy(0, 16);
			System.out.println("copy(0, 16)=["+res.toXML()+"]");
			res = cnt.copy(10, 16);
			System.out.println("copy(10, 16)=["+res.toXML()+"]");
			res = cnt.copy(10, 12);
			System.out.println("copy(10, 12)=["+res.toXML()+"]");
			System.out.println("ori=["+cnt.toString()+"]");
			
		}		
		catch ( Exception e ) {
			e.printStackTrace();
		}
		System.out.println("---end testContainer---");
	}*/
	
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
				TextUnit item = reader.getItem();
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
	
/*	private static void testItem () {
		try {
			System.out.println("---start testItem---");
			IExtractionItem item = new ExtractionItem();
			
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
	}*/
	
	private static void testFilter () {
		try {
			System.out.println("---start testFilter---");
		
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
		System.out.println("---end testFilter---");
	}

	private static void testSegmentation () {
		try {
			System.out.println("---start testSegmentation---");
			SRXDocument srxDoc = new SRXDocument();
			
			ArrayList<Rule> langRule = new ArrayList<Rule>();
			langRule.add(new Rule("Mr\\.", "\\s", false));
			srxDoc.addLanguageRule("french", langRule);

			langRule = new ArrayList<Rule>();
			langRule.add(new Rule("[Ee][Tt][Cc]\\.", ".", false));
			langRule.add(new Rule("\\b\\w{2,}[\\.\\?!]+[\"\'”\\)]?", "", true));
			langRule.add(new Rule("\\.\\.\\.", "\\s", true));
			srxDoc.addLanguageRule("default", langRule);

			srxDoc.addLanguageMap(new LanguageMap("[Ff][Rr].*", "french"));
			srxDoc.addLanguageMap(new LanguageMap(".*", "default"));
			
			srxDoc.setCascade(true);
			Segmenter seg = srxDoc.applyLanguageRules("fr", null);
			
			TextContainer cont = new TextContainer(null);
			cont.append(" Mr. XYZ. (Test.)   and more test.  ");
			System.out.println(cont.toString());
			
			// All segments
			seg.computeSegments(cont);
			List<Point> list = seg.getSegmentRanges();
			cont.createSegments(list);
			System.out.println("---all segs:");
			for ( TextFragment tf : cont.getSegments() ) {
				System.out.println("s='"+tf.toString()+"'");
			}

			System.out.println("---Seg one by one:");
			cont.mergeAllSegments();
			Point range;
			while ( (range = seg.getNextSegmentRange(cont)) != null ) {
				System.out.println(cont.toString());
				System.out.println(range);
				cont.createSegment(range.x, range.y);
				for ( TextFragment tf : cont.getSegments() ) {
					System.out.println("s='"+tf.toString()+"'");
				}
			}
			System.out.println("last=["+cont.toString()+"]");
			cont.mergeAllSegments();
			System.out.println("merged=["+cont.toString()+"]");

			cont = new TextContainer(null);
			cont.append("One... Two... ");
			System.out.println(cont.toString());
			System.out.println(seg.getSegmentRanges());
			
			System.out.println("---Translation steps:");
			TextUnit tu = new TextUnit("tu1", " Mr. XYZ. Segment 2.   Segment 3.  ");
			TextContainer srcCont = tu.getSourceContent();
			
			TextContainer trgCont = new TextContainer();
			tu.setTargetContent(trgCont);

			System.out.println("src='"+srcCont.toString()+"'");
			System.out.println("trg='"+trgCont.toString()+"'");
			TextFragment srcSeg;
			TextFragment trgSeg;
			int i = 0;
			while ( (range = seg.getNextSegmentRange(srcCont)) != null ) {
				srcSeg = srcCont.createSegment(range.x, range.y);
				trgSeg = new TextFragment(srcSeg);
				translate(trgSeg);
				trgCont.append(srcCont.getFragmentBeforeSegment(i));
				trgCont.addSegment(trgSeg);
				i++;
			}
			trgCont.append(srcCont.getFragmentAfterSegment(i-1));
			System.out.println("src='"+srcCont.toString()+"'");
			for ( TextFragment tf : srcCont.getSegments() ) {
				System.out.println("s='"+tf.toString()+"'");
			}
			System.out.println("trg='"+trgCont.toString()+"'");
			for ( TextFragment tf : trgCont.getSegments() ) {
				System.out.println("t='"+tf.toString()+"'");
			}
			
		}
		catch ( Exception e ) {
			System.out.println(e.getLocalizedMessage());
		}
		System.out.println("---end testSegmentation---");
	}
	
	// fake translation
	static private void translate (TextFragment fragment) {
		fragment.append("_trans");
	}

/*	static private String printSplits (Segmenter segmenter,
		String input)
	{
		ArrayList<Integer> list = segmenter.getSplitPositions();
		StringBuilder tmp = new StringBuilder();
		int start = 0;
		for ( int pos : list ) {
			tmp.append("["+input.substring(start, pos)+"]");
			start = pos;
		}
		// Last one
		tmp.append("["+input.substring(start)+"]");
		return tmp.toString();
	}*/

	private static void testConfigString () {
		try {
			System.out.println("---start testConfigString---");
			
			String s1 = "C:\\a\\b\\c\\d";
			String s2 = "C:\\a\\B";
			System.out.println(Util.longestCommonDir(s1, s2, !Util.isOSCaseSensitive()));
			s1 = "C:\\a\\b\\c\\d";
			s2 = "C:\\Z\\B";
			System.out.println(Util.longestCommonDir(s1, s2, !Util.isOSCaseSensitive()));
			s1 = "C:\\a\\b\\c\\d";
			s2 = "A:\\a\\b";
			System.out.println(Util.longestCommonDir(s1, s2, !Util.isOSCaseSensitive()));
			
			ConfigurationString cs = new ConfigurationString(); 
			cs.add("string", "text\nline2");
			cs.add("char", 'a');
			String s = cs.toString();
			System.out.println(s);
			
			cs = new ConfigurationString(s);
			String tmp = cs.get("string", "def");
			System.out.println(tmp);
			tmp = cs.get("stringNotThere", "def");
			System.out.println(tmp);
			char x = cs.get("char", 'b');
			System.out.println(x);
			x = cs.get("charNotThere", 'b');
			System.out.println(x);

			cs.addGroup("group1", s);
			System.out.println(cs.toString());
			
		}
		catch ( Exception e ) {
			System.out.println(e.getLocalizedMessage());
		}
		System.out.println("---end testConfigString---");
	}

	public static void main (String[] args)
		throws Exception
	{
		testSegmentation();
		
		if ( args.length == 0 ) return;
//		testContainer();
		testConfigString();
//		testItem();
		testITSEngine();
		testXMLReader();
		testFilter();
	}		
		
}
