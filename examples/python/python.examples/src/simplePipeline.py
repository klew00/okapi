from net.sf.okapi.common.pipeline import Pipeline, BasePipelineStep, FilterPipelineStepAdaptor
from net.sf.okapi.common import EventType, Event
from net.sf.okapi.filters.html import HtmlFilter    
    
class Consumer(BasePipelineStep):
    def handleEvent(self, event): 
        print "%s<===>%s\nSKEL:%s\n===========================" % (event.getEventType(), event.getResource(), event.getResource().getSkeleton())

pipeline = Pipeline()
filter = HtmlFilter()
pipeline.addStep(FilterPipelineStepAdaptor(filter))
pipeline.addStep(Consumer())

pipeline.process(u'This is a <img src="here">simple pipeline</img> test')





