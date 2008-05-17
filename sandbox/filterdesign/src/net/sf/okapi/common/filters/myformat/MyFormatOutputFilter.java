package net.sf.okapi.common.filters.myformat;

import java.io.PrintStream;

import net.sf.okapi.common.pipeline.IResourceBuilder;
import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.IResource;
import net.sf.okapi.common.resource.IResourceContainer;

public class MyFormatOutputFilter implements IResourceBuilder{

    private PrintStream out;
    private int indentLevel = 0;

    public MyFormatOutputFilter(PrintStream out) {
        this.out = out;
    }
    
    public void startResource(IResource resource) {
        out.println("MYFORMAT-START: " + resource.getName());
        indentLevel++;
    }

    public void endResource(IResource resource) {
        indentLevel--;
        out.println("MYFORMAT-END");
    }

    public void startContainer(IResourceContainer resourceContainer) {
        out.println(getIndent()+ "CONTAINER: "+ resourceContainer.getName());
        indentLevel++;
    }

    public void endContainer(IResourceContainer resourceCntainer) {
        indentLevel--;
        
    }

    public void startExtractionItem(IExtractionItem extractionItem) {
        out.println(getIndent()+ "ITEM-START");
        indentLevel++;
    }

    public void endExtractionItem(IExtractionItem extractionItem) {
        out.println(getIndent()+ extractionItem.getContent());
        out.println(getIndent()+ "ITEM-END");
        indentLevel--;
    }

    private String getIndent(){
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<indentLevel;i++){
            builder.append("  ");
        }
        return builder.toString();
    }
}
