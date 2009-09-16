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
import net.sf.okapi.tm.pensieve.Helper;
import org.apache.lucene.store.FSDirectory;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class TmSeekerFactoryTest {

    @Test
    public void createFileBasedTmSeeker() {
        PensieveSeeker pensieveSeeker = (PensieveSeeker) TmSeekerFactory.createFileBasedTmSeeker("target/test-classes/");
        assertTrue("indexDir should be filebased", pensieveSeeker.getIndexDir() instanceof FSDirectory);
    }

    @Test(expected = OkapiIOException.class)
    public void createFileBasedTmSeekerNotDirectory() {
        TmSeekerFactory.createFileBasedTmSeeker("pom.xml");
    }

    @Test(expected = OkapiIOException.class)
    public void createFileBasedTmSeekerBadDirectory() {
        TmSeekerFactory.createFileBasedTmSeeker("prettymuch/a/non/existent/directory");
    }

    @Test(expected = OkapiIOException.class)
    public void createFileBasedTmSeekerNullDirectory() {
        TmSeekerFactory.createFileBasedTmSeeker(null);
    }

    @Test
    public void stupidCoberturaPrivateConstructorTest() throws Exception {
        Helper.genericTestConstructor(TmSeekerFactory.class);
    }
}
