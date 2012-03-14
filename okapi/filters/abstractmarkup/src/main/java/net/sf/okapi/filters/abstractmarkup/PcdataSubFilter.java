package net.sf.okapi.filters.abstractmarkup;

import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.BaseSubFilterAdapter;
import net.sf.okapi.common.filters.FilterState;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonPart;

public class PcdataSubFilter extends BaseSubFilterAdapter {
	private int tuChildCount;

	public PcdataSubFilter(IFilter filter, FilterState state) {
		super(filter, state);
		tuChildCount = 0;
	}

	public PcdataSubFilter(IFilter filter) {
		super(filter);
		tuChildCount = 0;
	}
	
	@Override
	public void close() {
		super.close();
		tuChildCount = 0;
	}
	
	@Override
	public Event next() {
		Event e = super.next();
		// we need to escape back to the original format if HTML or XML
		if (MimeTypeMapper.XML_MIME_TYPE.equals(getMimeType()) ||
				MimeTypeMapper.HTML_MIME_TYPE.equals(getMimeType()) ||
				MimeTypeMapper.XHTML_MIME_TYPE.equals(getMimeType())) {
			switch(e.getEventType()) {
			case DOCUMENT_PART:
				DocumentPart dp = e.getDocumentPart();
				dp.setSkeleton(new GenericSkeleton(Util.escapeToXML(dp.getSkeleton().toString(), 0, true, null)));
				break;
			case TEXT_UNIT:
				ITextUnit tu = e.getTextUnit();
				
				// subfiltered textunits can inherit name from a parent TU
				if (tu.getName() == null) {
					String parentName = getState().getParentTextUnitName();
					// we need to add a child id so each tu name is unique for this subfiltered content
					if (parentName != null) {
						parentName = parentName + "-" + Integer.toString(++tuChildCount); 
					}
					tu.setName(parentName);
				}
				
				// escape the skeleton parts
				GenericSkeleton s = (GenericSkeleton)tu.getSkeleton();
				for (GenericSkeletonPart p : s.getParts()) {
					if (p.getParent() == null) {
						p.setData(Util.escapeToXML(p.getData().toString(), 0, true, null));
					}
				}							
				tu.setSkeleton(s);
				
				// now escape all the code content
				List<Code> codes = tu.getSource().getFirstContent().getCodes();
				for (Code c : codes) {
					c.setData(Util.escapeToXML(c.getData(), 0, true, null));
					if (c.hasOuterData()) {
						c.setOuterData(Util.escapeToXML(c.getOuterData(), 0, true, null));
					}								
				}
				
				// now escape any remaining text
				TextFragment f = new TextFragment(Util.escapeToXML(tu.getSource().getFirstContent().getCodedText(), 0, true, null), codes);
				tu.setSourceContent(f);
				break;
			}
		}
		return e;
	}
}
