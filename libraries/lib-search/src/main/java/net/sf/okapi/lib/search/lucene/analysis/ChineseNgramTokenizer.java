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

package net.sf.okapi.lib.search.lucene.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.CharStream;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;

/**
 */
public class ChineseNgramTokenizer 
extends Tokenizer
implements LdsTokenizer
// , FuzzyTokenizer  We want to force non-fuzzy behavior in this case
{    
    private int ngramSize = 2; // default ngram size     
    private int offset = -1;
    private char[] ngram; 
    
    /** Creates a new instance of NgramTokenizer */
    public ChineseNgramTokenizer()
    {
        super();
    }
    
    /** Creates a new instance of NgramTokenizer */
    public ChineseNgramTokenizer(int p_ngramSize)
    {        
        super();        
        ngramSize = p_ngramSize;
        ngram = new char[ngramSize];        
    }
    
    /** Construct a new LetterTokenizer. */
    public ChineseNgramTokenizer(Reader p_input) 
    {
        super(p_input); 
		// FIXME: Just to get to compile - fix later
        this.input = (CharStream) p_input;
        ngram = new char[ngramSize];        
    }
    
    public void setReader(Reader p_reader)
    {
		// FIXME: Just to get to compile - uncomment and fix
        //input = p_reader;
    }
    
    public Token next() throws java.io.IOException
    {                     
        int c;
        
        offset++;
        input.reset(); // reset to the "first char"-1 of the last ngram        
        for (int i = 0; i < ngramSize; i++)
        {    
            c = input.read();    
            if (Character.isWhitespace((char)c)) // skip all whitespace
            {
                i--;
                offset++;
                continue;
            }
            
            if (i == 0)
            {
                input.mark(ngramSize); // mark the second character so we can reset to start the next ngram
            }
            
            if (c == -1)
            {
                offset = -1;
                return null;
            }            
            ngram[i] = (char)c;            
        }                            
        String s = new String(ngram);   
        return new Token(new String(ngram), offset, offset+1, "ngram");        
    }
    
    /** Getter for property ngramSize.
     * @return Value of property ngramSize.
     *
     */
    public int getNgramSize()
    {
        return ngramSize;
    }
    
    /** Setter for property ngramSize.
     * @param ngramSize New value of property ngramSize.
     *
     */
    public void setNgramSize(int p_ngramSize)
    {
        this.ngramSize = p_ngramSize;
        ngram = new char[ngramSize];   
    }    
}
