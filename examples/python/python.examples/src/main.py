from java.io import File

from net.sf.okapi.common.pipeline import Pipeline
from net.sf.okapi.common.pipeline import FilterPipelineStepAdaptor
from net.sf.okapi.common.pipeline import FilterWriterPipelineStepAdaptor

from net.sf.okapi.filters.xml import XMLFilter
from net.sf.okapi.filters.html import HtmlFilter
from net.sf.okapi.filters.openoffice import OpenOfficeFilter
from net.sf.okapi.filters.properties import PropertiesFilter

from UppercaseStep import UppercaseStep
from PseudoTranslateStep import PseudoTranslateStep


def pipeline(input, filter, srcLang, trgLang, inputEncoding, outputEncoding, outputPath):
    # Create the pipeline
    pipeline = Pipeline()
        
    # Create the filter step
    inputStep = FilterPipelineStepAdaptor(filter)
    
    # Add this step to the pipeline
    pipeline.addStep(inputStep)
    
    # add processing steps
    pipeline.addStep(PseudoTranslateStep(trgLang))
    #pipeline.addStep(UppercaseStep(trgLang))
    
    # Create the writer we will use
    writer = filter.createFilterWriter()
    
    # Create the writer step (using the writer provider by our filter)
    outputStep = FilterWriterPipelineStepAdaptor(writer)
    
    # Add this step to the pipeline
    pipeline.addStep(outputStep)

    # Sets the filter options
    filter.setOptions(srcLang, inputEncoding, True)

    # Sets the writer options and output
    writer.setOptions(trgLang, outputEncoding)
    writer.setOutput(outputPath)
    
    # Launch the execution
    pipeline.process(input)
    
    # destroy the pipeline and finalize all steps
    pipeline.destroy();

if __name__ == '__main__':        
    
    # example using HTML filter
    input = File("../myFile.html").toURI()
    print input
    pipeline(input, HtmlFilter(), 'en', 'fr', 'UTF-8', 'UTF-8', '../out.html')
    
    # example using XML filter
    input = File("../myFile.xml").toURI()
    pipeline(input, XMLFilter(), 'en', 'fr', 'UTF-8', 'UTF-8', '../out.xml')
           
    # example using OpenOffice filter
    input = File("../myFile.odt").toURI()
    pipeline(input, OpenOfficeFilter(), 'en', 'fr', 'UTF-8', 'UTF-8', '../out.odt')
    
    # example using Java properties filter
    input = File("../myFile.properties").toURI()
    pipeline(input, PropertiesFilter(), 'en', 'fr', 'UTF-8', 'UTF-8', '../out.properties')
    