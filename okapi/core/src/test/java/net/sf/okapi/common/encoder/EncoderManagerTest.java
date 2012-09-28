/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.common.encoder;

import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.Util;

import org.junit.Test;
import static org.junit.Assert.*;

public class EncoderManagerTest {

	final private String XMLENCODER = "net.sf.okapi.common.encoder.XMLEncoder";
	final private String HTMLENCODER = "net.sf.okapi.common.encoder.HtmlEncoder";
	
	@Test
	public void testSimpleMapping () {
		EncoderManager em = new EncoderManager();
		em.setMapping("mimetype1", XMLENCODER);
		em.setDefaultOptions(null, "UTF-8", Util.LINEBREAK_UNIX);

		assertNull(em.getEncoder());
		em.updateEncoder("mimetype1");
		IEncoder enc = em.getEncoder();
		assertNotNull(enc);
		assertEquals(XMLENCODER, enc.getClass().getName());
	}
	
	@Test
	public void testMergeMappings () {
		EncoderManager em1 = new EncoderManager();
		em1.setMapping("mimetype1", XMLENCODER);
		em1.setDefaultOptions(null, "UTF-8", Util.LINEBREAK_UNIX);
		EncoderManager em2 = new EncoderManager();
		em2.setMapping("mimetype2", HTMLENCODER);
		em2.setDefaultOptions(null, "UTF-8", Util.LINEBREAK_UNIX);

		em1.mergeMappings(em2);
		em1.updateEncoder("mimetype2");
		IEncoder enc = em1.getEncoder();
		assertNotNull(enc);
		assertEquals(HTMLENCODER, enc.getClass().getName());
	}
	
	@Test
	public void testSetMapping() {
		EncoderManager em = new EncoderManager();
		em.setDefaultOptions(null, "UTF-16BE", "\r\n");
		
		IEncoder e1 = new XMLEncoder("UTF-8", "\n", true, true, false, 1);
		IEncoder e2 = new XMLEncoder("UTF-8", "\n", true, false, true, 2);
		
		em.setMapping(MimeTypeMapper.XML_MIME_TYPE, e1);
		em.updateEncoder(MimeTypeMapper.XML_MIME_TYPE);
		assertEquals(e1, em.getEncoder());
		
		em.setMapping(MimeTypeMapper.XML_MIME_TYPE, e2);
		em.updateEncoder(MimeTypeMapper.XML_MIME_TYPE);
		assertEquals(e2, em.getEncoder());
	}
	
}
