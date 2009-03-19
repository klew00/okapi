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

from net.sf.okapi.common import Event
from net.sf.okapi.common import IResource
from net.sf.okapi.common.pipeline import BasePipelineStep
from net.sf.okapi.common.resource import TextFragment
from net.sf.okapi.common.resource import TextUnit

class UppercaseStep(BasePipelineStep):  
    
    def __init__(self, targetLang):
        self.trgLang = targetLang
               
    def getName(self): 
        return "UppercaseStep"
    
    def handleTextUnit(self, event):
        tu = event.getResource()
        if (tu.isTranslatable()):
            tf = tu.createTarget(self.trgLang, False, IResource.COPY_CONTENT)
            tf.setCodedText(tf.getCodedText().upper())
           
