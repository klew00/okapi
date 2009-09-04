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

import org.apache.lucene.analysis.Token;

/**
 *
 * @author  HargraveJE
 */
public class SortableToken
implements Comparable
{
    private int start;
    private int end;
    private String term;
    
    /** Creates a new instance of SortableToken */
    public SortableToken(Token p_token)
    {
        start = p_token.startOffset();
        end = p_token.endOffset();
        term = p_token.termText();
    }
    
    public int getStart()
    {
        return start;
    }
    
    public void setStart(int p_start)
    {
        start = p_start;
    }
    
    public int getEnd()
    {
        return end;
    }
    
    public void setEnd(int p_end)
    {
        end = p_end;
    }    
    
    public String getTermText()
    {
        return term;
    }
    
    public void setTermText(String p_term)
    {
        term = p_term;
    }
    
    public int compareTo(Object o)
    {
        SortableToken t = (SortableToken)o;
        if (t.getStart() < getStart())
        {
            return -1;
        }
        else if (t.getStart() > getStart())
        {
            return 1;
        }
        
        // must be equal
        return 0;        
    }
}
