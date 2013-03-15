package net.sf.okapi.common;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.sf.okapi.common.BOMNewlineEncodingDetector;

import org.junit.Test;
import static org.junit.Assert.*;

/** 
 * Junt4 Test class for {@link BOMNewlineEncodingDetector}
 */
public class BOMNewlineEncodingDetectorTest {
	private static final String CR_NEWLINES = "this \ris  atest with newlines";
	private static final String LF_NEWLINES = "this \nis  atest with newlines";
	private static final String CRLF_NEWLINES = "this \r\nis  atest with newlines";
	
	private static final String DEFINITIVE = "\ufffe has a BOM";
	private static final String NON_DEFINITIVE = "does not have a BOM";
	
	private static final byte[] UTF_8_BOM = new byte[]{(byte)0xEF,(byte)0xBB,(byte)0xBF};	  
	private static final byte[] UTF_7_BOM = new byte[]{(byte)0x2B,(byte)0x2F,(byte)0x76, (byte)0x38};
	
	@Test
	public void staticGetNewlineType() {
		assertEquals(BOMNewlineEncodingDetector.NewlineType.CR, BOMNewlineEncodingDetector.getNewlineType(CR_NEWLINES));
		assertEquals(BOMNewlineEncodingDetector.NewlineType.LF, BOMNewlineEncodingDetector.getNewlineType(LF_NEWLINES));
		assertEquals(BOMNewlineEncodingDetector.NewlineType.CRLF, BOMNewlineEncodingDetector.getNewlineType(CRLF_NEWLINES));
	}
	
	@Test
	public void getNewlineType() throws IOException {
		InputStream is = new ByteArrayInputStream(CR_NEWLINES.getBytes("UTF-8"));
		BOMNewlineEncodingDetector detector = new BOMNewlineEncodingDetector(is, "UTF-8");
		assertEquals(BOMNewlineEncodingDetector.NewlineType.CR, detector.getNewlineType());
		
		is = new ByteArrayInputStream(LF_NEWLINES.getBytes("UTF-8"));
		detector = new BOMNewlineEncodingDetector(is, "UTF-8");
		assertEquals(BOMNewlineEncodingDetector.NewlineType.LF, detector.getNewlineType());
		
		is = new ByteArrayInputStream(CRLF_NEWLINES.getBytes("UTF-8"));
		detector = new BOMNewlineEncodingDetector(is, "UTF-8");		
		assertEquals(BOMNewlineEncodingDetector.NewlineType.CRLF, detector.getNewlineType());
	}
	
	@Test
	public void isDefinitve() throws IOException {
		InputStream is = new ByteArrayInputStream(DEFINITIVE.getBytes("UTF-16LE"));
		BOMNewlineEncodingDetector detector = new BOMNewlineEncodingDetector(is, "UTF-16LE");
		assertTrue(detector.isDefinitive());
		
		is = new ByteArrayInputStream(NON_DEFINITIVE.getBytes("UTF-8"));
		detector = new BOMNewlineEncodingDetector(is, "UTF-8");
		detector.detectBom();
		assertFalse(detector.isDefinitive());
	}

	@Test
	public void hasBom() throws IOException {
		InputStream is = new ByteArrayInputStream(DEFINITIVE.getBytes("UTF-16"));
		BOMNewlineEncodingDetector detector = new BOMNewlineEncodingDetector(is, "UTF-16");
		detector.detectBom();
		assertTrue(detector.hasBom());
		
		is = new ByteArrayInputStream(NON_DEFINITIVE.getBytes("UTF-8"));
		detector = new BOMNewlineEncodingDetector(is, "UTF-8");
		detector.detectBom();
		assertFalse(detector.hasBom());
	}
	
	@Test
	public void hasUtf8Bom() throws IOException {
		InputStream is = new ByteArrayInputStream(UTF_8_BOM);
		BOMNewlineEncodingDetector detector = new BOMNewlineEncodingDetector(is, "UTF-8");
		detector.detectBom();
		assertTrue(detector.hasBom());
		assertTrue(detector.hasUtf8Bom());
	}
	
	@Test
	public void hasUtf7Bom() throws IOException {
		InputStream is = new ByteArrayInputStream(UTF_7_BOM);
		BOMNewlineEncodingDetector detector = new BOMNewlineEncodingDetector(is, "UTF-7");
		detector.detectBom();
		assertTrue(detector.hasBom());
		assertTrue(detector.hasUtf7Bom());
	}
	
	@Test
	public void removeBom() throws IOException {
		InputStream is = new ByteArrayInputStream(DEFINITIVE.getBytes("UTF-16LE"));
		BOMNewlineEncodingDetector detector1 = new BOMNewlineEncodingDetector(is);
		detector1.detectAndRemoveBom();
		
		BOMNewlineEncodingDetector detector2 = new BOMNewlineEncodingDetector(is);
		detector2.detectBom();
		assertFalse(detector2.hasBom());		
	}
	
}
