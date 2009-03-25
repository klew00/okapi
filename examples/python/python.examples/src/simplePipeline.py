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

from net.sf.okapi.common.pipeline import Pipeline, BasePipelineStep, FilterPipelineStepAdaptor
from net.sf.okapi.common import EventType, Event
from net.sf.okapi.filters.html import HtmlFilter    
    
class Consumer(BasePipelineStep):
    def handleEvent(self, event): 
        print "%s<===>%s\nSKEL:%s\n=========" % (event.getEventType(), event.getResource(), event.getResource().getSkeleton())

pipeline = Pipeline()
pipeline.addStep(FilterPipelineStepAdaptor(HtmlFilter()))
pipeline.addStep(Consumer())


pipeline.process(FileResource(u'This is a <img src="here">simple pipeline</img> test', "en"))
pipeline.destroy()





