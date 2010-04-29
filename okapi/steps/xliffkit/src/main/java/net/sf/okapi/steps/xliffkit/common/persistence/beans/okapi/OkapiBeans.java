/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi;

import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Range;
import net.sf.okapi.common.annotation.ScoresAnnotation;
import net.sf.okapi.common.filterwriter.GenericFilterWriter;
import net.sf.okapi.common.filterwriter.TMXFilterWriter;
import net.sf.okapi.common.filterwriter.ZipFilterWriter;
import net.sf.okapi.common.resource.BaseNameable;
import net.sf.okapi.common.resource.BaseReferenceable;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.Document;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.InlineAnnotation;
import net.sf.okapi.common.resource.MultiEvent;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.resource.TargetPropertiesAnnotation;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextPart;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonPart;
import net.sf.okapi.common.skeleton.ZipSkeleton;
import net.sf.okapi.filters.openxml.ConditionalParameters;
import net.sf.okapi.filters.openxml.OpenXMLZipFilterWriter;
import net.sf.okapi.filters.pensieve.PensieveFilterWriter;
import net.sf.okapi.filters.po.POFilterWriter;
import net.sf.okapi.steps.formatconversion.TableFilterWriter;
import net.sf.okapi.steps.xliffkit.common.persistence.BeanMapper;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.ListBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.TypeInfoBean;

public class OkapiBeans {

	public static void register() {
		// General purpose beans
		BeanMapper.registerBean(List.class, ListBean.class);
		BeanMapper.registerBean(IParameters.class, ParametersBean.class);
		BeanMapper.registerBean(Object.class, TypeInfoBean.class); // If no bean was found, use just this one to store class info
		
		// Specific class beans				
		BeanMapper.registerBean(Event.class, EventBean.class);		
		BeanMapper.registerBean(TextUnit.class, TextUnitBean.class);
		BeanMapper.registerBean(RawDocument.class, RawDocumentBean.class);
		BeanMapper.registerBean(Property.class, PropertyBean.class);
		BeanMapper.registerBean(ScoresAnnotation.class, ScoresAnnotationBean.class);
		BeanMapper.registerBean(ConditionalParameters.class, ConditionalParametersBean.class);
		BeanMapper.registerBean(TextFragment.class, TextFragmentBean.class);
		BeanMapper.registerBean(TextContainer.class, TextContainerBean.class);
		BeanMapper.registerBean(Code.class, CodeBean.class);
		BeanMapper.registerBean(Document.class, DocumentBean.class);
		BeanMapper.registerBean(DocumentPart.class, DocumentPartBean.class);
		BeanMapper.registerBean(Ending.class, EndingBean.class);
		BeanMapper.registerBean(MultiEvent.class, MultiEventBean.class);
		BeanMapper.registerBean(TextPart.class, TextPartBean.class);
		BeanMapper.registerBean(Segment.class, SegmentBean.class);
		BeanMapper.registerBean(Range.class, RangeBean.class);
		BeanMapper.registerBean(BaseNameable.class, BaseNameableBean.class);
		BeanMapper.registerBean(BaseReferenceable.class, BaseReferenceableBean.class);
		BeanMapper.registerBean(StartDocument.class, StartDocumentBean.class);
		BeanMapper.registerBean(StartGroup.class, StartGroupBean.class);
		BeanMapper.registerBean(StartSubDocument.class, StartSubDocumentBean.class);
		BeanMapper.registerBean(TargetPropertiesAnnotation.class, TargetPropertiesAnnotationBean.class);
		BeanMapper.registerBean(GenericSkeleton.class, GenericSkeletonBean.class);
		BeanMapper.registerBean(GenericSkeletonPart.class, GenericSkeletonPartBean.class);
		BeanMapper.registerBean(ZipSkeleton.class, ZipSkeletonBean.class);
		BeanMapper.registerBean(ZipFile.class, ZipFileBean.class);
		BeanMapper.registerBean(ZipEntry.class, ZipEntryBean.class);
		BeanMapper.registerBean(InputStream.class, InputStreamBean.class);
		BeanMapper.registerBean(InlineAnnotation.class, InlineAnnotationBean.class);
		BeanMapper.registerBean(GenericFilterWriter.class, GenericFilterWriterBean.class);
		BeanMapper.registerBean(TMXFilterWriter.class, TMXFilterWriterBean.class);
		BeanMapper.registerBean(ZipFilterWriter.class, ZipFilterWriterBean.class);
		// Registered here to require dependencies at development-time
		BeanMapper.registerBean(OpenXMLZipFilterWriter.class, TypeInfoBean.class); 		
		BeanMapper.registerBean(PensieveFilterWriter.class, TypeInfoBean.class);
		BeanMapper.registerBean(POFilterWriter.class, TypeInfoBean.class);
		BeanMapper.registerBean(TableFilterWriter.class, TypeInfoBean.class);		
		//registerBean(.class, Bean.class);
	}
}
