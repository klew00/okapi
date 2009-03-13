from net.sf.okapi.common.pipeline import IPipeline, Pipeline, BasePipelineStep, IInitialStep
from net.sf.okapi.common import EventType, Event
from net.sf.okapi.common.resource import TextUnit

class Producer(BasePipelineStep, IInitialStep):
    def preprocess(self):
        self.eventCount = 0
        
    def handleEvent(self, event):        
        self.eventCount += 1                        
        id = str(self.eventCount)
        return Event(EventType.TEXT_UNIT, TextUnit(id, "Hello Event %s" % id))
    
    def hasNext(self):
        if (self.eventCount >= 10):                        
            return False;
        return True;
    
class Consumer(BasePipelineStep):  
    def handleEvent(self, event): 
        print "%s<===>%s" % (event.getEventType(), event.getResource())

pipeline = Pipeline()
pipeline.addStep(Producer())
pipeline.addStep(Consumer())
pipeline.process()





