package net.sf.okapi.lib.search.lucene.analysis;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Lucene" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Lucene", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.io.Reader;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;

/** A WhitespaceTokenizer is a tokenizer that divides text at whitespace.
 * Adjacent sequences of non-Whitespace characters form tokens. */

public class WhitespaceTokenizer
extends Tokenizer
implements LdsTokenizer
{
    private int offset = 0, bufferIndex = 0, dataLen = 0;
    private static final int MAX_WORD_LEN = 255;
    private static final int IO_BUFFER_SIZE = 1024;
    private final char[] buffer = new char[MAX_WORD_LEN];
    private final char[] ioBuffer = new char[IO_BUFFER_SIZE];
    
    /** Construct a new WhitespaceTokenizer. */
    public WhitespaceTokenizer(Reader in)
    {
        super(in);
        offset = 0;
        bufferIndex = 0;
        dataLen = 0;
    }
    
    /** Collects only characters which do not satisfy
     * {@link Character#isWhitespace(char)}.*/
    protected boolean isTokenChar(char c)
    {
        return !Character.isWhitespace(c);
    }
    
    public void setReader(Reader p_reader)
    {
        offset = 0;         
        bufferIndex = 0;
        dataLen = 0;
        // FIXME: Just to get to compile - uncomment and fix
        //input = p_reader;
    }
    
    /** Returns the next token in the stream, or null at EOS. */
    public Token next() throws java.io.IOException
    {
        int length = 0;
        int start = offset;
        while (true)
        {
            final char c;
            
            offset++;
            if (bufferIndex >= dataLen)
            {
                dataLen = input.read(ioBuffer);
                bufferIndex = 0;
            }
            ;
            if (dataLen == -1)
            {
                if (length > 0)
                    break;
                else
                    return null;
            } else
                c = ioBuffer[bufferIndex++];
            
            if (isTokenChar(c))
            {               // if it's a token char
                
                if (length == 0)			           // start of token
                    start = offset - 1;
                
                buffer[length++] = c; // buffer it, normalized
                
                if (length == MAX_WORD_LEN)		   // buffer overflow!
                    break;
                
            } else if (length > 0)             // at non-Letter w/ chars
                break;                           // return 'em
            
        }
        
        return
            new org.apache.lucene.analysis.Token(
                new String(buffer, 0, length), start, start + length);
    }
}
