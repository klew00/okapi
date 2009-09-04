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

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;

/**
 */
public class ChineseSpaceTokenizer extends Tokenizer implements LdsTokenizer
{
    private int offset = 0;

    /** Creates a new instance of NgramTokenizer */
    public ChineseSpaceTokenizer()
    {
        super();
    }

    /** Construct a new LetterTokenizer. */
    public ChineseSpaceTokenizer(Reader p_input)
    {
        super(p_input);
		// FIXME: Just to get to compile - uncomment and fix
        //this.input = p_input;
    }

    public void setReader(Reader p_reader)
    {
		// FIXME: Just to get to compile - uncomment and fix
        //input = p_reader;
    }

    public Token next() throws java.io.IOException
    {
        int c;
        int start = offset;
        StringBuffer token = new StringBuffer(5);
        while (true)
        {           
            c = input.read();
            offset++;
            if (Character.isSpaceChar((char)c)
                || !isAlphanumeric((char)c)
                || c == -1)
            {
                break;
            }            
            token.append((char)c);
        }
        
        // end of file with no token
        if (c == -1 && token.length() <= 0)
        {
            offset = 0;
            return null;
        }

        return new Token(
            token.toString(),
            start,
            offset-1,
            "word");
    }

    private boolean isAlphanumeric(char c)
    {
        boolean result = true;
        int charType = Character.getType(c);
        boolean isPrivateUse = (charType == Character.PRIVATE_USE);
        if (!(Character.isUnicodeIdentifierStart(c)
            || Character.isUnicodeIdentifierPart(c)
            || isPrivateUse))
            result = false;
        return (result);
    }

}
