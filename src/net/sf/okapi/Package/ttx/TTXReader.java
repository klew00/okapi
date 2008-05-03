package net.sf.okapi.Package.ttx;

import java.io.File;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.okapi.Filter.FilterItem;
import net.sf.okapi.Filter.IFilterItem;
import net.sf.okapi.Filter.InlineCode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class TTXReader {

	// Same as in Borneo database TSTATUS_* values
	public static final int       STATUS_NOTRANS      = 0;
	public static final int       STATUS_UNUSED       = 1;
	public static final int       STATUS_TOTRANS      = 2;
	public static final int       STATUS_TOEDIT       = 3;
	public static final int       STATUS_TOREVIEW     = 4;
	public static final int       STATUS_OK           = 5;

	private IFilterItem      m_SrcFI;
	private IFilterItem      m_TrgFI;
	private IFilterItem      m_CurrentFI;
	private int              m_nStatus;
	private NodeList         m_NL = null;
	private int              m_nCurNode;
	private Node             m_CurNode;
	private int              m_nInCode;
	private Stack<Boolean>   m_stkFirstChildDone;
	private Pattern          idPattern;

	//TODO: Implement case for multiple file in single doc
	public TTXReader () {
		m_SrcFI = new FilterItem();
		m_TrgFI = new FilterItem();
		idPattern = Pattern.compile("(\\d+)");
	}
	
	protected void finalize ()
		throws Throwable
	{
	    try {
	    	close();
	    } finally {
	        super.finalize();
	    }
	}

	public IFilterItem getSourceItem () {
		return m_SrcFI;
	}

	public IFilterItem getTargetItem () {
		return m_TrgFI;
	}

	public void open (String p_sPath)
		throws Exception
	{
		try {
			close();
			DocumentBuilderFactory Fact = DocumentBuilderFactory.newInstance();
			Fact.setValidating(false);
			Document Doc = Fact.newDocumentBuilder().parse(new File(p_sPath));
			m_nCurNode = -1;
			m_stkFirstChildDone = new Stack<Boolean>();
			m_stkFirstChildDone.push(true); // For #document root
			m_CurNode = Doc.getDocumentElement();
			m_stkFirstChildDone.push(false);
			processDocument();
		}
		catch ( Exception E ) {
			// Reduce all exception to the same, so we can change the internal
			// implementation without changing the API
			throw E;
		}
	}

	public void close () {
		if ( m_NL != null ) m_NL = null;
	}

	public boolean readItem ()
		throws Exception
	{
		resetItem();
		while ( true ) {
			if ( !nextNode() ) {
				return false; // Document is done
			}
			String sName = getName();
			if ( sName.equals("ut") ) {
				if ( processUT() ) return true;
			}
		}
	}

	/**
	 * Gets the name of the current node or an empty string.
	 * @return The name, or an empty string if the node is null or not an element.
	 */
	private String getName () {
		if ( m_CurNode == null ) return "";
		if ( m_CurNode.getNodeType() != Node.ELEMENT_NODE ) return "";
		return m_CurNode.getNodeName();
	}
	
	private boolean nextNode () {
		if ( m_CurNode != null ) {
			if ( !m_stkFirstChildDone.peek() && m_CurNode.hasChildNodes() ) {
				// Change the flag for the current node
				m_stkFirstChildDone.push(!m_stkFirstChildDone.pop());
				// Get the new node and push its flag
				m_CurNode = m_CurNode.getFirstChild();
				m_stkFirstChildDone.push(false);
			}
			else {
				Node TmpNode = m_CurNode.getNextSibling();
				if ( TmpNode == null ) {
					m_CurNode = m_CurNode.getParentNode();
					m_stkFirstChildDone.pop();
				}
				else {
					m_CurNode = TmpNode;
					m_stkFirstChildDone.pop(); // Remove flag for previous sibling 
					m_stkFirstChildDone.push(false); // Set new flag for new sibling
				}
			}
		}
		return (m_CurNode != null);
	}
	
	private void resetItem () {
		m_SrcFI.reset();
		m_TrgFI.reset();
		m_nStatus = STATUS_TOTRANS;
	}

	private void processDocument () {
		//TODO: check version, root, etc.
	}
	
	private boolean processUT ()
		throws Exception
	{
		String sTmp = m_CurNode.getTextContent();
		if ( sTmp.indexOf("<u") == 0 ) {
			Matcher M = idPattern.matcher(sTmp);
			if ( M.find() ) {
				m_SrcFI.setTranslatable(false);
				m_SrcFI.setItemID(Integer.valueOf(
					sTmp.substring(M.start(), M.end())));
			}
			else {
				// ID not found, cannot merge
				//TODO: set log
			}
		}
		else if ( sTmp.indexOf("</u>") == 0 ) {
			// End of the TU
			return true;
		}
		return false;
	}
	
	private void processTarget () {
		Element Elem = (Element)m_CurNode;
		String sTmp = Elem.getAttribute("state");
		if ( sTmp.length() > 0 ) {
			if ( sTmp.equals("needs-translation") ) m_nStatus = STATUS_TOTRANS;
			else if ( sTmp.equals("final") ) m_nStatus = STATUS_OK;
			else if ( sTmp.equals("translated") ) m_nStatus = STATUS_TOEDIT;
			else if ( sTmp.equals("needs-review-translation") ) m_nStatus = STATUS_TOREVIEW;
		}

		m_CurrentFI = m_TrgFI;
		m_nInCode = 0;
		//TODOprocessContent("target");

		if ( m_TrgFI.isEmpty() && !m_SrcFI.isEmpty() ) {
			return; // No translation found
		}
		m_SrcFI.setTranslated(true);
	}
}
