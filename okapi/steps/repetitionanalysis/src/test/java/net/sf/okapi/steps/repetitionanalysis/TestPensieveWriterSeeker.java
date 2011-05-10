/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.steps.repetitionanalysis;

import static org.junit.Assert.assertEquals;

import java.util.List;

import net.sf.okapi.common.ClassUtil;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.tm.pensieve.common.MetadataType;
import net.sf.okapi.tm.pensieve.common.TmHit;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import net.sf.okapi.tm.pensieve.common.TranslationUnitVariant;
import net.sf.okapi.tm.pensieve.seeker.ITmSeeker;
import net.sf.okapi.tm.pensieve.seeker.TmSeekerFactory;
import net.sf.okapi.tm.pensieve.writer.ITmWriter;
import net.sf.okapi.tm.pensieve.writer.TmWriterFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestPensieveWriterSeeker {
	private String tmDir;
	private String pathBase;
	private ITmWriter tmWriter;
	private ITmSeeker currentTm;
	
	@Before
	public void setup() {
		pathBase = Util.ensureSeparator(ClassUtil.getTargetPath(this.getClass()), true);
		//tmDir = pathBase + "tm/";
		//tmDir = pathBase;
		tmDir = Util.ensureSeparator(Util.getTempDirectory(), true) + "tm/";
		Util.createDirectories(tmDir);
//		System.out.println((new File(tmDir)).getAbsolutePath());
		tmWriter = TmWriterFactory.createFileBasedTmWriter(tmDir, true);
		currentTm = TmSeekerFactory.createFileBasedTmSeeker(tmDir);
	}
	
	@After
	public void shutdown() {
		currentTm.close();
		tmWriter.close();
		Util.deleteDirectory(tmDir, false);
	}
	
	@Test
	public void testTmReadWrite() {
		TranslationUnit unit1 = new TranslationUnit(				
				new TranslationUnitVariant(LocaleId.ENGLISH, new TextFragment("source1")),
				new TranslationUnitVariant(LocaleId.GERMAN, new TextFragment("target1")));
		unit1.setMetadataValue(MetadataType.ID, "seg1");
		tmWriter.indexTranslationUnit(unit1);		
				
		TranslationUnit unit2 = new TranslationUnit(				
				new TranslationUnitVariant(LocaleId.ENGLISH, new TextFragment("source2")),
				new TranslationUnitVariant(LocaleId.GERMAN, new TextFragment("target2")));
		unit2.setMetadataValue(MetadataType.ID, "seg2");
		tmWriter.indexTranslationUnit(unit2);		
		
		tmWriter.commit();// Called once
		
		List<TmHit> hits = currentTm.searchExact(new TextFragment("source1"), null);
		assertEquals(1, hits.size());
		TmHit hit = hits.get(0);
		assertEquals("seg1", hit.getTu().getMetadataValue(MetadataType.ID));
		
		hits = currentTm.searchExact(new TextFragment("source2"), null); 
		assertEquals(1, hits.size());
		hit = hits.get(0);
		assertEquals("seg2", hit.getTu().getMetadataValue(MetadataType.ID));
	}
	
	@Test
	public void testTmReadWriteExact() {
		// Create and commit tu1
		TranslationUnit unit1 = new TranslationUnit(				
				new TranslationUnitVariant(LocaleId.ENGLISH, new TextFragment("source1")),
				new TranslationUnitVariant(LocaleId.GERMAN, new TextFragment("target1")));
		unit1.setMetadataValue(MetadataType.ID, "seg1");
		tmWriter.indexTranslationUnit(unit1);
		tmWriter.commit();

		// Seek tu1
		List<TmHit> hits = currentTm.searchExact(new TextFragment("source1"), null);
		assertEquals(1, hits.size());
		TmHit hit = hits.get(0);
		assertEquals("seg1", hit.getTu().getMetadataValue(MetadataType.ID));
		
		// Create and commit tu2
		TranslationUnit unit2 = new TranslationUnit(				
				new TranslationUnitVariant(LocaleId.ENGLISH, new TextFragment("source2")),
				new TranslationUnitVariant(LocaleId.GERMAN, new TextFragment("target2")));
		unit2.setMetadataValue(MetadataType.ID, "seg2");
		tmWriter.indexTranslationUnit(unit2);		
		tmWriter.commit();
				
		currentTm.close();
		currentTm = TmSeekerFactory.createFileBasedTmSeeker(tmDir);
		
		// Seek tu1
		hits = currentTm.searchExact(new TextFragment("source1"), null);
		assertEquals(1, hits.size());
		hit = hits.get(0);
		assertEquals("seg1", hit.getTu().getMetadataValue(MetadataType.ID));
		
		// Seek tu2
		hits = currentTm.searchExact(new TextFragment("source2"), null);
		assertEquals(1, hits.size());
		hit = hits.get(0);
		assertEquals("seg2", hit.getTu().getMetadataValue(MetadataType.ID));
	}
	
	@Test
	public void testTmReadWriteFuzzy() {
		TranslationUnit unit1 = new TranslationUnit(				
				new TranslationUnitVariant(LocaleId.ENGLISH, new TextFragment("source1")),
				new TranslationUnitVariant(LocaleId.GERMAN, new TextFragment("target1")));
		unit1.setMetadataValue(MetadataType.ID, "seg1");
		tmWriter.indexTranslationUnit(unit1);
		tmWriter.commit();

		List<TmHit> hits = currentTm.searchFuzzy(new TextFragment("source1"), 95, 1, null); 
		if (hits.size() > 0) {
			TmHit hit = hits.get(0);
			assertEquals("seg1", hit.getTu().getMetadataValue(MetadataType.ID));
		}
		
		TranslationUnit unit2 = new TranslationUnit(				
				new TranslationUnitVariant(LocaleId.ENGLISH, new TextFragment("source2")),
				new TranslationUnitVariant(LocaleId.GERMAN, new TextFragment("target2")));
		unit2.setMetadataValue(MetadataType.ID, "seg2");
		tmWriter.indexTranslationUnit(unit2);		
		tmWriter.commit();
		
		hits = currentTm.searchFuzzy(new TextFragment("source2"), 95, 1, null); 
		if (hits.size() > 0) {
			TmHit hit = hits.get(0);
			assertEquals("seg2", hit.getTu().getMetadataValue(MetadataType.ID));
		}
	}
	
	@Test
	public void testTmReadWriteSentenceExact() {
		TranslationUnit unit1 = new TranslationUnit(				
				new TranslationUnitVariant(LocaleId.ENGLISH, new TextFragment("Elephants cannot fly.")),
				new TranslationUnitVariant(LocaleId.GERMAN, new TextFragment("Elefanten können nicht fliegen.")));
		unit1.setMetadataValue(MetadataType.ID, "seg1");
		tmWriter.indexTranslationUnit(unit1);
		tmWriter.commit();

		List<TmHit> hits = currentTm.searchExact(new TextFragment("source1"), null); 
		if (hits.size() > 0) {
			TmHit hit = hits.get(0);
			assertEquals("seg1", hit.getTu().getMetadataValue(MetadataType.ID));
		}
		
		TranslationUnit unit2 = new TranslationUnit(				
				new TranslationUnitVariant(LocaleId.ENGLISH, new TextFragment("Elephants can fly.")),
				new TranslationUnitVariant(LocaleId.GERMAN, new TextFragment("Elefanten können fliegen.")));
		unit2.setMetadataValue(MetadataType.ID, "seg2");
		tmWriter.indexTranslationUnit(unit2);		
		tmWriter.commit();
		
		hits = currentTm.searchExact(new TextFragment("Elephants can fly."), null); 
		if (hits.size() > 0) {
			TmHit hit = hits.get(0);
			assertEquals("seg2", hit.getTu().getMetadataValue(MetadataType.ID));
		}
	}
	
	@Test
	public void testTmReadWriteSentenceTfExact() {
		TextFragment tf = new TextFragment("Elephants cannot fly.");
		TextFragment ttf = new TextFragment("Elefanten können nicht fliegen.");
		
		TranslationUnit unit1 = new TranslationUnit(				
				new TranslationUnitVariant(LocaleId.ENGLISH, tf),
				new TranslationUnitVariant(LocaleId.GERMAN, ttf));
		unit1.setMetadataValue(MetadataType.ID, "seg1");
		tmWriter.indexTranslationUnit(unit1);
		tmWriter.commit();

		List<TmHit> hits = currentTm.searchExact(tf, null); 
		if (hits.size() > 0) {
			TmHit hit = hits.get(0);
			assertEquals("seg1", hit.getTu().getMetadataValue(MetadataType.ID));
		}
	}
	
	@Test
	public void testTmReadWriteSentenceFuzzy() {
		TranslationUnit unit1 = new TranslationUnit(				
				new TranslationUnitVariant(LocaleId.ENGLISH, new TextFragment("Elephants cannot fly.")),
				new TranslationUnitVariant(LocaleId.GERMAN, new TextFragment("Elefanten können nicht fliegen.")));
		unit1.setMetadataValue(MetadataType.ID, "seg1");
		tmWriter.indexTranslationUnit(unit1);
		tmWriter.commit();

		List<TmHit> hits = currentTm.searchFuzzy(new TextFragment("Elephants cannot fly."), 95, 1, null); 
		if (hits.size() > 0) {
			TmHit hit = hits.get(0);
			assertEquals("seg1", hit.getTu().getMetadataValue(MetadataType.ID));
		}
		
		TranslationUnit unit2 = new TranslationUnit(				
				new TranslationUnitVariant(LocaleId.ENGLISH, new TextFragment("Elephants can fly.")),
				new TranslationUnitVariant(LocaleId.GERMAN, new TextFragment("Elefanten können fliegen.")));
		unit2.setMetadataValue(MetadataType.ID, "seg2");
		tmWriter.indexTranslationUnit(unit2);		
		tmWriter.commit();
		
		hits = currentTm.searchFuzzy(new TextFragment("Elephants can fly."), 95, 1, null); 
		if (hits.size() > 0) {
			TmHit hit = hits.get(0);
			assertEquals("seg2", hit.getTu().getMetadataValue(MetadataType.ID));
		}
	}			
}
