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

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

/**
 * Class used to markup highlighted terms in text - regardless of where terms come from eg
 * if obtained from a Query or from a list of "top terms" derived from analyzing results.
 * See QueryHighlightExtractor for a class used to highlight text based on a lucene Query
 */
public class TextHighlighter
{
    private HashSet terms;
    private HashSet fields;
    private Analyzer analyzer;
    private StringBuffer newText;
    private TermHighlighter highlighter;
    
    public TextHighlighter(TermHighlighter highlighter, HashSet terms,
    HashSet fields, Analyzer analyzer)
    {
        this.terms = terms;
        this.highlighter = highlighter;
        this.analyzer = analyzer;
        this.fields = fields;
    }
    
    /**
     * Low level api to get the most relevant sections of the document
     * @param text
     * @param fragmentSize
     * @param maxNumFragments
     * @return
     * @throws IOException
     */
    public final String getBestFragment(String text, int fragmentSize)
    throws IOException
    {
        newText = new StringBuffer(text);
        TokenStream stream = null;
        
        try
        {
            org.apache.lucene.analysis.Token token;
            LinkedList allTokens = new LinkedList();
            
            // iterate through all fields and collect toekns using the
            // appropriate analyzer
            Iterator it = fields.iterator();
            while(it.hasNext())
            {
                String f = (String)it.next();
                stream = analyzer.tokenStream(f, new StringReader(text));
                while ((token = stream.next()) != null)
                {
                    allTokens.add(token);
                }
                stream.close();
            }
            
            List l = findMatches(allTokens);
            it = l.iterator();
            while(it.hasNext())
            {
                SortableToken t = (SortableToken)it.next();
                // insert match
                int s = t.getStart();
                int e = t.getEnd();
                newText.replace(s, e, highlighter.highlightTerm(text.substring(s,e)));
            }
            
            return newText.toString();
        }
        finally
        {
            if (stream != null)
            {
                try
                {
                    stream.close();
                }
                catch (Exception e)
                {
                }
            }
        }
    }
    
    private List findMatches(List p_allTokens)
    {
        LinkedList matches = new LinkedList();
        org.apache.lucene.analysis.Token token;
        Iterator it = p_allTokens.iterator();
        while(it.hasNext())
        {
            token = (org.apache.lucene.analysis.Token)it.next();
            if (terms.contains(token.termText()))
            {
                matches.add(new SortableToken(token));
            }
        }
        
        Collections.sort(matches);
        //return mergeContiguous(matches);
        return matches;
    }
    
    private List mergeContiguous(List p_list)
    {
        LinkedList matches = new LinkedList();       
        Iterator it = p_list.iterator();
        SortableToken current = null;
        while(it.hasNext())
        {
            SortableToken token = (SortableToken)it.next();
            if (current != null) // not first time
            {
                if (current.getEnd() == (token.getStart()-1))
                {                    
                    current.setEnd(token.getEnd());
                    current.setTermText(current.getTermText()+ token.getTermText());
                }
                else
                {
                    matches.add(current);
                }
            }
            current = token;                        
        }
        
        //Collections.sort(matches);
        return matches;
    }
}