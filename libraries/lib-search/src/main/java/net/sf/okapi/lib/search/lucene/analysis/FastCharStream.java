// FastCharStream.java
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

import java.io.IOException;
import java.io.Reader;

/** An efficient implementation of JavaCC's CharStream interface.  <p>Note that
 * this does not do line-number counting, but instead keeps track of the
 * character position of the token in the input, as required by Lucene's {@link
 * org.apache.lucene.analysis.Token} API. */
public final class FastCharStream implements CharStream
{
    char[] buffer = null;
    
    int bufferLength = 0;				  // end of valid chars
    int bufferPosition = 0;			  // next char to read
    
    int tokenStart = 0;				  // offset in buffer
    int bufferStart = 0;				  // position in file of buffer
    
    Reader input;					  // source of chars
    
    /** Constructs from a Reader. */
    public FastCharStream(Reader r)
    {
        input = r;
    }
    
    public final char readChar() throws IOException
    {
        if (bufferPosition >= bufferLength)
            refill();
        return buffer[bufferPosition++];
    }
    
    private final void refill() throws IOException
    {
        int newPosition = bufferLength - tokenStart;
        
        if (tokenStart == 0)
        {			  // token won't fit in buffer
            if (buffer == null)
            {			  // first time: alloc buffer
                buffer = new char[2048];
            } else if (bufferLength == buffer.length)
            { // grow buffer
                char[] newBuffer = new char[buffer.length*2];
                System.arraycopy(buffer, 0, newBuffer, 0, bufferLength);
                buffer = newBuffer;
            }
        } else
        {					  // shift token to front
            System.arraycopy(buffer, tokenStart, buffer, 0, newPosition);
        }
        
        bufferLength = newPosition;			  // update state
        bufferPosition = newPosition;
        bufferStart += tokenStart;
        tokenStart = 0;
        
        int charsRead =				  // fill space in buffer
        input.read(buffer, newPosition, buffer.length-newPosition);
        if (charsRead == -1)
            throw new IOException("read past eof");
        else
            bufferLength += charsRead;
    }
    
    public final char BeginToken() throws IOException
    {
        tokenStart = bufferPosition;
        return readChar();
    }
    
    public final void backup(int amount)
    {
        bufferPosition -= amount;
    }
    
    public final String GetImage()
    {
        return new String(buffer, tokenStart, bufferPosition - tokenStart);
    }
    
    public final char[] GetSuffix(int len)
    {
        char[] value = new char[len];
        System.arraycopy(buffer, bufferPosition - len, value, 0, len);
        return value;
    }
    
    public final void Done()
    {
        try
        {
            input.close();
        } catch (IOException e)
        {
            System.err.println("Caught: " + e + "; ignoring.");
        }
    }
    
    public final int getColumn()
    {
        return bufferStart + bufferPosition;
    }
    public final int getLine()
    {
        return 1;
    }
    public final int getEndColumn()
    {
        return bufferStart + bufferPosition;
    }
    public final int getEndLine()
    {
        return 1;
    }
    public final int getBeginColumn()
    {
        return bufferStart + tokenStart;
    }
    public final int getBeginLine()
    {
        return 1;
    }
}