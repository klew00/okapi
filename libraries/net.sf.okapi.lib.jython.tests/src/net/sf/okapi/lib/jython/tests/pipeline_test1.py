from net.sf.okapi.common.pipeline import IPipeline, Pipeline, BasePipelineStep
from net.sf.okapi.common import EventType, Event
from net.sf.okapi.common.resource import TextUnit

class Producer(BasePipelineStep):
    def preprocess(self):
        self.eventCount = 0
        
    def postprocess(self):
        pass
       
    def handleEvent(self, event):        
        self.eventCount += 1
        if (self.eventCount >= 10):                        
            return FilterEvent(FilterEventType.FINISHED, None)                
        id = str(self.eventCount)
        return FilterEvent(FilterEventType.TEXT_UNIT, TextUnit(id, "Hello Event %s" % id))
    
class Consumer(BasePipelineStep):
    def preprocess(self): pass
    def postprocess(self): pass 
    def handleEvent(self, event): 
        print "%s<===>%s" % (event.getEventType(), event.getResource())

pipeline = Pipeline()
pipeline.addStep(Producer())
pipeline.addStep(Consumer())
pipeline.execute()





