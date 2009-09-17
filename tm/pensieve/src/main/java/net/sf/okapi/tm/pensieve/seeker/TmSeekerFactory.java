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
package net.sf.okapi.tm.pensieve.seeker;

import net.sf.okapi.common.exceptions.OkapiIOException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;

public final class TmSeekerFactory {

    private TmSeekerFactory(){}

    public static ITmSeeker createFileBasedTmSeeker(String indexDirectoryPath) {
        Directory dir;
        try{
            File f = new File(indexDirectoryPath);
            if (!f.exists()){
                throw new OkapiIOException(indexDirectoryPath + " does not exist");
            }
            dir = FSDirectory.open(f);
        }catch(IOException ioe){
            throw new OkapiIOException("Trouble creating FSDirectory with the given path: " +indexDirectoryPath, ioe);
        }catch(NullPointerException npe) {
            throw new OkapiIOException("indexDirectoryPath cannot be null");
        }
        return new PensieveSeeker(dir);
    }
    
}
