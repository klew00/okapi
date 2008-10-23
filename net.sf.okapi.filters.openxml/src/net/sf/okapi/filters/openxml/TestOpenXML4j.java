package net.sf.okapi.filters.openxml;

import org.openxml4j.opc.Package;
import org.openxml4j.opc.*;
import java.io.*;
import java.util.Iterator;
import java.util.List;

import org.openxml4j.document.wordprocessing.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.dom4j.QName;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.io.SAXReader;

import com.sun.org.apache.xml.internal.utils.NameSpace;

public class TestOpenXML4j
{

	public final static Namespace namespaceWord = new Namespace("w",
			WordprocessingML.NS_WORD12);

	static Logger logr = Logger.getLogger("net.sf.okapi.filters.openxml");
    // see http://logging.apache.org/log4j/1.2/manual.html
    
	public static void main(String[] args) throws Exception
	{
		// Open the package
		Package p;
		
		BasicConfigurator.configure();
		// PropertyConfigurator.configure("filename");
		logr.setLevel(Level.WARN);
		
		p = Package.open("M:/Java/OpenXML4j/Sample.docx", PackageAccess.READ);
		lookadat(p);
		p.close();
		
		p = Package.open("M:/Java/OpenXML4j/Sample.pptx", PackageAccess.READ);
		pudout(p);
		p.close();
		
		p = Package.open("M:/Java/OpenXML4j/Sample.xlsx", PackageAccess.READ);
		pudout(p);
		p.close();
		
	}
	
	private static void pudout(Package p) throws Exception
	{
		int n;
		Paragraph paragraph;
		WordDocument wd;
		Element e;
		for (PackagePart part : p.getParts())
		   System.out.println(part.getPartName().getURI() + " -> " + part.getContentType());
		System.out.println("");
		// Get documents core properties part relationship
		PackageRelationship coreDocumentRelationship = p.getRelationshipsByType( PackageRelationshipTypes.CORE_DOCUMENT) .getRelationship(0);

		// Get core properties part from the relationship.
		PackagePart coreDocumentPart = p.getPart(coreDocumentRelationship);

		InputStream inStream = coreDocumentPart.getInputStream();
		SAXReader docReader = new SAXReader();
		Document doc = docReader.read(inStream);
		
		wd = new WordDocument(p);

		Namespace namespaceWordProcessingML = new Namespace("w",
			"http://schemas.openxmlformats.org/wordprocessingml/2006/main");
	    Element bodyElement = doc.getRootElement().element(
	        new QName("body", namespaceWordProcessingML));

	    // Retrieves paragraph childs from body element
	    List<Paragraph> paragraphs = bodyElement.content();	    
	    for(Iterator it = paragraphs.iterator();it.hasNext();)
	    {
	    	e=(Element)it.next();
	    	if (isNodeParagraph(e))
	    	
	    	n =1;
	    }
		
		System.out.println("=====");
	}

	private static void lookadat(Package p) throws Exception
	{
/*
		WordDocument wd;
		Document content;
		List<Paragraph> paragraphs;
		List<Run> runs;
		Paragraph paragraph;
		Element boody,e;
		Run r;
		
		wd = new WordDocument(p);
		content = wd.getCoreDocument();
		boody = content.getRootElement().element(
				new QName(WordprocessingML.WORD_DOC_BODY_TAG_NAME,
						namespaceWord));
	    paragraphs = boody.content();	    
	    for(Iterator it = paragraphs.iterator();it.hasNext();)
	    {
	    	e=(Element)it.next();
	    	if (isNodeParagraph(e))
	    	{
	    		runs = e.getRootElement().element(
	    				new QName(WordprocessingML.PARAGRAPH_RUN_TAG_NAME,
	    						namespaceWord));
	    	}
	    	n =1;
	    }
*/
	}
	
/*
    public void treeWalk(Document document) {
        treeWalk( document.getRootElement() );
      }

      public void treeWalk(Element element) {
        for ( int i = 0, size = element.nodeCount(); i < size; i++ ) {
          Node node = element.node(i);
          if ( node instanceof Element ) {
            treeWalk( (Element) node );
          }
          else {
            // do something....
          }
        }
      }


	    
*/	    
	private static boolean isNodeParagraph(Element curNode) {

		if (curNode.getName().equals(WordprocessingML.PARAGRAPH_BODY_TAG_NAME)) {
			return true;
		} else {
			return false;
		}
	}

}
