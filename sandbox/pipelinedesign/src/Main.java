import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.filters.myformat2.MyFormat2Filter;
import net.sf.okapi.common.filters.myformat2.MyFormat2SimpleWriter;
import net.sf.okapi.common.filters.myformat2.MyFormat2ResourceBuilder;
import net.sf.okapi.common.pipeline.LoggingPipelineExceptionListener;
import net.sf.okapi.common.pipeline.Pipeline;
import net.sf.okapi.common.pipeline.PipelineExceptionListener;
import net.sf.okapi.common.pipeline.PipelineStep;
import net.sf.okapi.common.resource.FileRawData;
import net.sf.okapi.utilities.reverse.ReverseUtility;

public class Main {

    public static void main(String[] args) {              
        //=== Test with file and custom resources
        try {        
        	// Create the filter, writer and utility steps
        	String inputPath = "test.txt";
        	MyFormat2Filter myFilter = new MyFormat2Filter();
        	MyFormat2ResourceBuilder resourceBuilder = new MyFormat2ResourceBuilder(new FileRawData(new File(inputPath)));
        	MyFormat2SimpleWriter simpleWriter = new MyFormat2SimpleWriter();
        	ReverseUtility reverseAll = new ReverseUtility();
        	
        	// put the steps in the order we want
        	List<PipelineStep> steps = new ArrayList<PipelineStep>();
            steps.add(myFilter);
            steps.add(reverseAll);
            steps.add(simpleWriter);
        	
        	// add the steps to the pipe and add default error handler
        	List<PipelineExceptionListener> eListeners = new ArrayList<PipelineExceptionListener>();
        	Pipeline pipeline = new Pipeline();
            pipeline.setPipelineSteps(steps);
            eListeners.add(new LoggingPipelineExceptionListener());            
            
            // add steps to pipeline
            pipeline.setPipelineSteps(steps);

            // do default preprocessing on steps
            pipeline.prepare();
            
            // run all the steps
            pipeline.executeSteps(resourceBuilder);
        }
        catch ( Exception e ) {
        	System.err.println(e.getLocalizedMessage());
        }
    }
}
