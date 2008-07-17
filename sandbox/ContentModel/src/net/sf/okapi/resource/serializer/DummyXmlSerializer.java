package net.sf.okapi.resource.serializer;

import java.io.PrintWriter;

import net.sf.okapi.resource.ContentMarker;
import net.sf.okapi.resource.EndPairedContentMarker;
import net.sf.okapi.resource.IContent;
import net.sf.okapi.resource.IContainer;
import net.sf.okapi.resource.MarkupContainer;
import net.sf.okapi.resource.RootContainer;
import net.sf.okapi.resource.SegmentContainer;
import net.sf.okapi.resource.StandaloneContentMarker;
import net.sf.okapi.resource.StartPairedContentMarker;
import net.sf.okapi.resource.TextFragment;

public class DummyXmlSerializer implements IResouceSerializer{

	private String indentString = "  ";

	private int indentWidth = 1;
	
	private PrintWriter writer;
	
	public DummyXmlSerializer(PrintWriter writer) {
		this.writer = writer;
	}
	
	public String getIndentString() {
		return indentString;
	}

	public void setIndentString(String indentString) {
		this.indentString = indentString;
	}

	public int getIndentWidth() {
		return indentWidth;
	}

	public void setIndentWidth(int indentWidth) {
		this.indentWidth = indentWidth;
	}

	public PrintWriter getWriter() {
		return writer;
	}

	public void setWriter(PrintWriter writer) {
		this.writer = writer;
	}

	private void indent(int indent){
		for(int i=0;i<indent*indentWidth;i++){
			writer.write(indentString);
		}
	}
	
	public void serialize(IContent content) {
		serialize(content, 0);
	}

	public void serialize(TextFragment fragment) {
		serialize(fragment, 0);
	}

	public void serialize(IContainer container) {
		serialize(container, 0);
	}

	public void serialize(RootContainer container) {
		serialize(container, 0);
	}

	public void serialize(MarkupContainer container) {
		serialize(container, 0);
	}

	public void serialize(SegmentContainer container) {
		serialize(container, 0);
	}

	public void serialize(ContentMarker marker) {
		serialize(marker, 0);
	}

	public void serialize(StandaloneContentMarker marker) {
		serialize(marker, 0);
	}

	public void serialize(StartPairedContentMarker marker) {
		serialize(marker, 0);
	}

	public void serialize(EndPairedContentMarker marker) {
		serialize(marker, 0);
	}
	
	public void serialize(IContent content, int indentLevel) {
		
		if(content instanceof TextFragment){
			serialize((TextFragment)content,indentLevel);
		}
		else if(content instanceof IContainer){
			serialize((IContainer)content,indentLevel);
		}
		else if(content instanceof ContentMarker){
			serialize((ContentMarker)content, indentLevel);
		}
	}

	public void serialize(TextFragment fragment, int indentLevel) {
		// TODO should we really ignore indent level here?
		indent(indentLevel);
		writer.write("<text-run>");
		writer.write(fragment.toString());
		writer.write("</text-run>\n");
	}

	public void serialize(IContainer container, int indentLevel) {
		if(container instanceof MarkupContainer){
			serialize((MarkupContainer)container,indentLevel);
			
		}
		else if(container instanceof SegmentContainer){
			serialize((SegmentContainer)container,indentLevel);
		}
	}

	private void serializeChildren(IContainer container, int indentLevel){
		for(IContent child: container){
			serialize(child,indentLevel);
		}
	}
	
	public void serialize(RootContainer container, int indentLevel) {
		indent(indentLevel);
		writer.write("<content>\n");
		serializeChildren(container, indentLevel+1);
		indent(indentLevel);
		writer.write("</content>\n");
	}

	public void serialize(MarkupContainer container, int indentLevel) {
		indent(indentLevel);
		writer.write("<markup>\n");
		serializeChildren(container, indentLevel+1);
		indent(indentLevel);
		writer.write("</markup>\n");
	}

	public void serialize(SegmentContainer container, int indentLevel) {
		indent(indentLevel);
		writer.write("<seg>\n");
		serializeChildren(container, indentLevel+1);
		indent(indentLevel);
		writer.write("</seg>\n");
	}

	public void serialize(ContentMarker marker, int indentLevel) {
		if(marker instanceof StandaloneContentMarker){
			serialize((StandaloneContentMarker)marker, indentLevel);
		}
		else if(marker instanceof StartPairedContentMarker){
			serialize((StartPairedContentMarker)marker, indentLevel);
		}
		else if(marker instanceof EndPairedContentMarker){
			serialize((EndPairedContentMarker)marker, indentLevel);
		}
	}

	public void serialize(StandaloneContentMarker marker, int indentLevel) {
		indent(indentLevel);
		writer.write("<marker/>\n");
	}

	public void serialize(StartPairedContentMarker marker, int indentLevel) {
		indent(indentLevel);
		writer.write("<start-marker/>\n");
		
	}

	public void serialize(EndPairedContentMarker marker, int indentLevel) {
		indent(indentLevel);
		writer.write("<end-marker/>\n");
		
	}
	
	public void flush(){
		writer.flush();
	}
	
}
