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

import java.io.IOException;
import java.io.Reader;
import java.text.BreakIterator;
import java.util.LinkedList;
import java.util.Locale;

import org.apache.lucene.analysis.Tokenizer;

import com.ibm.icu.text.Normalizer;

/**
 * Ngram tokenizer for all complex text such as Thai, Laotion, Cambodian, Indic etc.
 * Break only at grapheme boundries.
 */
public class ComplexTextNgramTokenizer extends Tokenizer
implements LdsTokenizer, FuzzyTokenizer
{
    private int ngramSize = 3; // default ngram size
    private int offset = 0;
    private int index = -1;   
    private String text;
    private BreakIterator gb;
    private Object[] graphemes;
    
    /** Construct a new LetterTokenizer. */
    public ComplexTextNgramTokenizer(int p_ngramSize, Locale p_locale)
    {
        offset = 0;
        ngramSize = p_ngramSize;       
        gb = BreakIterator.getCharacterInstance(p_locale);  
        graphemes = new Object[0];
    }     
    
    private String generateText()
    throws IOException
    {
        int c;
        StringBuffer b = new StringBuffer();
        while((c = input.read()) != -1)
        {
            b.append((char)c);
        }
        
        return b.toString();
    }
    
    private void generateGraphemeBoundries()
    {
        if (text.length() <= 0) return;
        // first find our grapheme boundries
        LinkedList g = new LinkedList();
        gb.setText(text);
        int start = gb.first();
        for (int end = gb.next();end != BreakIterator.DONE; start = end, end = gb.next())
        {
            g.add(text.substring(start,end));
        }        
        graphemes = g.toArray();
    }
    
    public org.apache.lucene.analysis.Token next() 
    throws java.io.IOException
    {                                   
            index++;                        
            StringBuffer ngram = new StringBuffer();
            for (int i = 0; i < ngramSize; i++)
            {              
                if (index+i >= graphemes.length) // done
                {
                    index = -1;
                    offset = 0;
                    return null; 
                }
                ngram.append((String)graphemes[index+i]);                                
            }
            
            org.apache.lucene.analysis.Token t =
                new org.apache.lucene.analysis.Token(
                    ngram.toString(),
                    offset,
                    offset+ngram.length(),
                    "ngram");
            
            offset += ((String)graphemes[index]).length();
            return t;
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
        }
        
        public void setReader(Reader p_reader)
        {
        	// FIXME: Just to get to compile - uncomment and fix
            // input = p_reader;
            try
            {
                text = generateText();
            }
            catch(IOException e)
            {
                text = null;
            }
            generateGraphemeBoundries();            
        }         
        
        public void setText(String p_text)
        {
            text = Normalizer.compose(p_text, false);
        }      
    }
