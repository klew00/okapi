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

package net.sf.okapi.tm.pensieve.writer;

import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.search.lucene.analysis.NgramAnalyzer;
import net.sf.okapi.tm.pensieve.common.Metadata;
import net.sf.okapi.tm.pensieve.common.MetadataType;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import net.sf.okapi.tm.pensieve.common.TranslationUnitField;
import net.sf.okapi.tm.pensieve.common.TranslationUnitVariant;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.Directory;

/**
 * Used to write, delete and update the index.
 */
public class PensieveWriter implements ITmWriter {
	private static final Logger LOGGER = Logger.getLogger(PensieveWriter.class.getName());

	private IndexWriter indexWriter;

	/**
	 * Creates a PensieveWriter
	 * 
	 * @param indexDirectory
	 *            - the Lucene Directory implementation of choice.
	 * @param createNewTmIndex
	 *            Set to false to append to the existing TM index file. Set to true to overwrite.
	 * @throws IOException
	 *             if the indexDirectory can not load
	 */
	public PensieveWriter(Directory indexDirectory, boolean createNewTmIndex) throws IOException {
		indexWriter = new IndexWriter(indexDirectory, new NgramAnalyzer(Locale.ENGLISH, 4),
				createNewTmIndex, IndexWriter.MaxFieldLength.UNLIMITED);
	}

	/**
	 * Commits and closes (for now) the transaction.
	 * 
	 * @throws OkapiIOException
	 *             if the commit cannot happen.
	 */
	public void close() {
		try {
			indexWriter.commit();
			indexWriter.optimize();
		} catch (IOException e) {
			throw new OkapiIOException(e); // To change body of catch statement use File | Settings | File Templates.
		} catch (AlreadyClosedException ignored) {
		} finally {
			try {
				indexWriter.close();
			} catch (IOException ignored) {
				LOGGER.log(Level.WARNING, "Exception closing Pensieve IndexWriter.", ignored); //$NON-NLS-1$
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void commit() {
		try {
			indexWriter.commit();
		} catch (IOException e) {
			throw new OkapiIOException(e);
		}
	}
	

	/**
	 * Gets a handle on the IndexWriter so that commits and rollbacks can happen outside. For now, this is a convience
	 * method. In other words, don't depend on it working for you.
	 * 
	 * @return a handle on the IndexWriter used to Create, Update or Delete the index.
	 */
	public IndexWriter getIndexWriter() {
		return indexWriter;
	}

	/**
	 * Adds a TranslationUnit to the index
	 * 
	 * @param tu
	 *            The TranslationUnit to index
	 * @throws IOException
	 *             if the TU can not be indexed.
	 * @throws IllegalArgumentException
	 *             if tu is null
	 */
	public void indexTranslationUnit(TranslationUnit tu) {
		if (tu == null) {
			throw new NullPointerException("TextUnit can not be null");
		}
		Document doc = createDocument(tu);
		if (doc != null) {
			try {
				indexWriter.addDocument(doc);
			} catch (CorruptIndexException e) {
				throw new OkapiIOException(
						"Error adding a translationUnit to the TM. Corrupted index.", e);
			} catch (IOException e) {
				throw new OkapiIOException("Error adding a translationUnit to the TM.", e);
			}
		}
	}

	/**
	 * Deletes a TranslationUnit based on the id.
	 * 
	 * @param id
	 *            The Unique ID of the TU to delete
	 * @throws OkapiIOException
	 *             if the delete can not happen
	 * @throws IllegalArgumentException
	 *             if the id is invalid
	 */
	public void delete(String id) {
		if (Util.isEmpty(id)) {
			throw new IllegalArgumentException("id is a required field for delete to happen");
		}
		try {
			indexWriter.deleteDocuments(new Term(MetadataType.ID.fieldName(), id));
		} catch (CorruptIndexException e) {
			throw new OkapiIOException("Error deleting a translationUnit from the TM. Corrupted index.", e);
		} catch (IOException e) {
			throw new OkapiIOException("Error deleting a translationUnit from the TM.", e);
		}
	}

	/**
	 * Updates a TranslationUnit.
	 * 
	 * @param tu
	 *            The TranslationUnit to update
	 * @throws IOException
	 *             if the update can not happen
	 * @throws IllegalArgumentException if the tu or MetadataType.ID is null
	 */
	public void update(TranslationUnit tu) {
		if (tu == null || tu.getMetadata().get(MetadataType.ID) == null) {
			throw new IllegalArgumentException("tu must be set and at least have its ID set");
		}
		// TODO -- make this transactional
		delete(tu.getMetadata().get(MetadataType.ID));
		indexTranslationUnit(tu);
	}

	Field createField(TranslationUnitField field, TextFragment frag, Field.Store store,
			Field.Index index) {
		return new Field(field.name(), frag.toString(), store, index);
	}

	Field createField(TranslationUnitField field, TranslationUnitVariant tuv, Field.Store store,
			Field.Index index) {
		return new Field(field.name(), tuv.getLanguage().toString(), store, index);
	}

	void addMetadataToDocument(Document doc, Metadata metadata) {
		for (MetadataType type : metadata.keySet()) {
			doc
					.add(new Field(type.fieldName(), metadata.get(type), type.store(), type
							.indexType()));
		}
	}

	/**
	 * Creates a document for a given translation unit, including inline codes.
	 * 
	 * @param tu
	 *            the translation unit used to create the document.
	 * @return a new document.
	 */
	Document createDocument(TranslationUnit tu) {
		if (tu == null) {
			throw new NullPointerException("source content not set");
		}

		// an empty source is not fatal just skip it
		if (tu.isSourceEmpty()) {
			return null;
		}

		Document doc = new Document();
		doc.add(createField(TranslationUnitField.SOURCE_LANG, tu.getSource(), Field.Store.YES,
				Field.Index.NOT_ANALYZED));
		doc.add(createRawCodedTextField(TranslationUnitField.SOURCE_EXACT, tu.getSource()
				.getContent(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		// ANALYZED_NO_NORMS: We don't need Lucene to manage this for us, we will implement our own scoring and
		// normalization.
		doc.add(createIndexedTextField(TranslationUnitField.SOURCE, tu.getSource().getContent(),
				Field.Store.NO, Field.Index.ANALYZED_NO_NORMS));
		doc.add(createCodesField(TranslationUnitField.SOURCE_CODES, tu.getSource().getContent(),
				Field.Store.YES, Field.Index.NOT_ANALYZED));
		if (!tu.isTargetEmpty()) {
			doc.add(createField(TranslationUnitField.TARGET_LANG, tu.getTarget(), Field.Store.YES,
					Field.Index.NOT_ANALYZED));
			doc.add(createRawCodedTextField(TranslationUnitField.TARGET, tu.getTarget()
					.getContent(), Field.Store.YES, Field.Index.NO));
			doc.add(createCodesField(TranslationUnitField.TARGET_CODES,
					tu.getTarget().getContent(), Field.Store.YES, Field.Index.NO));
		}
		addMetadataToDocument(doc, tu.getMetadata());
		return doc;
	}

	private Field createIndexedTextField(TranslationUnitField fieldType, TextFragment frag,
			Field.Store store, Field.Index index) {
		return new Field(fieldType.name(), frag.getText(), store, index, TermVector.YES);
	}

	private Field createRawCodedTextField(TranslationUnitField fieldType, TextFragment frag,
			Field.Store store, Field.Index index) {
		return new Field(fieldType.name(), frag.getCodedText(), store, index);
	}

	private Field createCodesField(TranslationUnitField field, TextFragment frag,
			Field.Store store, Field.Index index) {
		return new Field(field.name(), Code.codesToString(frag.getCodes()), store, index);
	}
}
