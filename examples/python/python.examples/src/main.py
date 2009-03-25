#===========================================================================
#  Copyright (C) 2009 by the Okapi Framework contributors
#-----------------------------------------------------------------------------
#  This library is free software; you can redistribute it and/or modify it 
#  under the terms of the GNU Lesser General Public License as published by 
#  the Free Software Foundation; either version 2.1 of the License, or (at 
#  your option) any later version.
#
#  This library is distributed in the hope that it will be useful, but 
#  WITHOUT ANY WARRANTY; without even the implied warranty of 
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
#  General Public License for more details.
#
#  You should have received a copy of the GNU Lesser General Public License 
#  along with this library; if not, write to the Free Software Foundation, 
#  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
#
#  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
#===========================================================================

from java.io import File

from net.sf.okapi.common.resource import FileResource
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
    pipeline.process(FileResource(input, 'UTF-8', 'en'))
    
    # destroy the pipeline and finalize all steps
    pipeline.destroy();

if __name__ == '__main__':        
   
    # example using HTML filter
    input = File("../myFile.html").toURI()
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
    