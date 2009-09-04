// Modifications made by the Okapi FrameWork Team under the LGPL license
 /*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.lib.search.lucene.highlight;


/**
 * Interface used for highlighting search terms.
 *
 * @version $Id: TermHighlighter.java,v 1.1 2006/08/08 16:06:00 higinbothamdw Exp $
 * @author Maik Schreiber (mailto: bZ@iq-computing.de)
 */
public interface TermHighlighter
{
  /**
   * Highlight a search term. For example, an HTML TermHighlighter could simply do:
   *
   * <p><dl><dt></dt><dd><code>return "&lt;b&gt;" + term + "&lt;/b&gt;";</code></dd></dl>
   *
   * @param term term text to highlight
   *
   * @return highlighted term text
   */
  String highlightTerm(String term);
}
