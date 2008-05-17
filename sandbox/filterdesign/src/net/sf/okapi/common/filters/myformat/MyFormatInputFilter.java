package net.sf.okapi.common.filters.myformat;

import net.sf.okapi.common.filters.IInputFilter;
import net.sf.okapi.common.pipeline.IResourceBuilder;
import net.sf.okapi.common.resource.ExtractionItem;
import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.IResource;
import net.sf.okapi.common.resource.IResourceContainer;
import net.sf.okapi.common.resource.Resource;
import net.sf.okapi.common.resource.ResourceContainer;

public class MyFormatInputFilter implements IInputFilter{

    private IResourceBuilder output;
    
    public void setOutput(IResourceBuilder output) {
        this.output = output;
        
    }
    
    public void convert(){
        IResource r = new Resource();
        r.setName("sample-file");
        output.startResource(r);
        
        for(int i=0;i<10;i++){
            IResourceContainer container = new ResourceContainer();
            container.setName("container " + i);
            output.startContainer(container);
            for(int j=0;j<3;j++){
                IExtractionItem item = new ExtractionItem();
                output.startExtractionItem(item);
                item.setContent("hello world number " +j + "! here i come!");
                output.endExtractionItem(item);
            }
            output.endContainer(container);
        }
        output.endResource(r);
    }

}
