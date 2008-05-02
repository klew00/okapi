package net.sf.okapi.Package.ttx;

import java.io.File;
import java.util.Stack;

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

	private String           m_sVersion;
	private IFilterItem      m_SrcFI;
	private IFilterItem      m_TrgFI;
	private IFilterItem      m_CurrentFI;
	private int              m_nStatus;
	private NodeList         m_NL = null;
	private int              m_nCurNode;
	private Node             m_CurNode;
	private int              m_nInCode;
	private Stack<Boolean>   m_stkFirstChildDone;

	//TODO: Implement case for multiple file in single doc
	public TTXReader () {
		m_SrcFI = new FilterItem();
		m_TrgFI = new FilterItem();
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
			if ( sName.equals("trans-unit") ) {
				processTransUnit();
				return true;
			}
			else if ( sName.equals("file") ) {
				//TODO: handle multiple files
				processFile();
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
	
	private void processFile () {
		//TODO: check language, etc.
	}
	
	private void processTransUnit ()
		throws Exception
	{
		Element Elem = (Element)m_CurNode;
		String sTmp = Elem.getAttribute("translate");
		if ( sTmp.length() > 0 ) m_SrcFI.setTranslatable(sTmp.equals("yes"));
		sTmp = Elem.getAttribute("id");
		if ( sTmp.length() == 0 ) throw new Exception("Missing attribute 'id'.");
		else {
			try {
				m_SrcFI.setItemID(Integer.valueOf(sTmp));
			}
			catch ( Exception E ) {
				throw new Exception("Invalid value for attribute 'id'.");
			}
		}
		
		// Process the content
		while ( nextNode() ) {
			String sName = getName();
			if ( sName.equals("trans-unit") ) {
				return; // End of the trans-unit element
			}
			if ( sName.equals("source") ) {
				m_CurrentFI = m_SrcFI;
				m_nInCode = 0;
				processContent(sName);
			}
			else if ( sName.equals("target") ) {
				processTarget();
			}
		}
	}
	
	/**
	 * Processes a segment content. Set m_CurrentFI and set m_nInCode to zero before
	 * calling this method with <source> or <target>.
	 * @param p_sContainer The name of the element content that is processed.
	 */
	private void processContent (String p_sContainer)
	{
		while ( nextNode() ) {
			switch ( m_CurNode.getNodeType() ) {
			case Node.TEXT_NODE:
			case Node.CDATA_SECTION_NODE:
				if ( m_nInCode == 0 )
					m_CurrentFI.appendText(m_CurNode.getTextContent());
				break;

			case Node.ELEMENT_NODE:
				String sName = m_CurNode.getNodeName();
				if ( sName.equals(p_sContainer) ) {
					if ( sName.equals("bpt") ) m_nInCode--;
					else if ( sName.equals("ept") ) m_nInCode--;
					else if ( sName.equals("ph") ) m_nInCode--;
					else if ( sName.equals("g") ) {
						m_CurrentFI.appendCode(InlineCode.CLOSING, null, null);
					}
					// End return in all cases
					return;
				}
				
				// Else: It's a start of element
				if ( sName.equals("g") ) {
					m_CurrentFI.appendCode(InlineCode.OPENING, null, null);
				}
				else if ( sName.equals("x") ) {
					m_CurrentFI.appendCode(InlineCode.ISOLATED, null, null);
				}
				else if ( sName.equals("bpt") ) {
					m_CurrentFI.appendCode(InlineCode.OPENING, null, null);
					m_nInCode++;
				}
				else if ( sName.equals("ept") ) {
					m_CurrentFI.appendCode(InlineCode.CLOSING, null, null);
					m_nInCode++;
				}
				else if ( sName.equals("ph") ) {
					m_CurrentFI.appendCode(InlineCode.ISOLATED, null, null);
					m_nInCode++;
				}
				else if ( sName.equals("it") ) {
					m_CurrentFI.appendCode(InlineCode.ISOLATED, null, null);
					m_nInCode++;
				}
				break;
			}
		}
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
		processContent("target");

		if ( m_TrgFI.isEmpty() && !m_SrcFI.isEmpty() ) {
			return; // No translation found
		}
		m_SrcFI.setTranslated(true);
	}
}
