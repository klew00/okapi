package net.sf.okapi.its;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public interface IProcessor {
	
	static int DC_LANGINFO        = 0x0001;
	static int DC_TRANSLATE       = 0x0002;
	static int DC_WITHINTEXT      = 0x0004;
	static int DC_LOCNOTE         = 0x0008;
	static int DC_TERMINOLOGY     = 0x0010;
	static int DC_DIRECTIONALITY  = 0x0020;
	static int DC_RUBY            = 0x0040;
	static int DC_ALL             = 0xFFFF;
	
	static int DIR_RTL            = 0;
	static int DIR_LTR            = 1;
	static int DIR_RLO            = 2;
	static int DIR_LRO            = 3;
	
	/**
	 * Adds a set of global rules to the document to process. The rules are added
	 * to the internal storage of the document, not to the document tree.
	 * Use this method to add one rule set or more before calling applyRules().
	 * @param docRules Document where the global rules are declared.
	 * @param docPath path of the document. This is needed because xlink:href need
	 * a initial location.
	 */
	void addExternalRules (Document rulesDoc,
		String docPath)
		throws Exception;

	void addExternalRules (String docPath)
		throws Exception;

	/**
	 * Applies the current ITS rules to the document. This method decorates
	 * the document tree with special flags that are used for getting the
	 * different ITS information later.
	 * @param dataCategories Flag indicating what data categories to apply.
	 * The value must be one of the DC_* values or several combined with 
	 * a OR operator. For example:
	 * applyRules(DC_TRANSLATE | DC_LOCNOTE);
	 */
	void applyRules (int dataCategories)
		throws Exception;
	
	/**
	 * Removes all the special attributes added when applying the ITS rules.
	 * Once you have called this method you should call applyRules() again to be able
	 * to use any ITSState again.
	 */
	void disapplyRules ();

	/**
	 * Gets the current node of the traversal.
	 * @return
	 */
	Node getNode ();

	/**
	 * Starts the traversal of the document.
	 */
	void startTraversal ();
	
	/**
	 * Moves to the next node in the traversal of the document.
	 * @return True if there is a next node, false otherwise.
	 */
	boolean nextNode ();

	/**
	 * Indicates if the current node of the traversal is translatable.
	 * @return True if the current node is translatable, false otherwise.
	 */
	boolean translate ();
	
	/**
	 * Gets the directionality for the text of the current node of the
	 * traversal.
	 * @return One of the DIR_* values.
	 */
	int getDirectionality ();
	
}
