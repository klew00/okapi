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
from java.lang import StringBuilder
from java.lang import String

from net.sf.okapi.common import Event
from net.sf.okapi.common import IResource
from net.sf.okapi.common.pipeline import BasePipelineStep
from net.sf.okapi.common.resource import TextFragment
from net.sf.okapi.common.resource import TextUnit

OLDCHARS = String("AaEeIiOoUuYyCcDdNn")
NEWCHARS = String("\u00c2\u00e5\u00c9\u00e8\u00cf\u00ec\u00d8\u00f5\u00db\u00fc\u00dd\u00ff\u00c7\u00e7\u00d0\u00f0\u00d1\u00f1")
   
class PseudoTranslateStep(BasePipelineStep): 
    
    def __init__(self, trgLang):
        self.trgLang = trgLang
        
    def getName(self):
        return "PseudoTranslateStep"
    
    def handleTextUnit(self, event):        
        tu = event.getResource()
        if (tu.isTranslatable()):
            tf = tu.createTarget(self.trgLang, False, IResource.COPY_CONTENT)
            text = StringBuilder(tf.getCodedText())
            for i in range(0, text.length()):            
                if (TextFragment.isMarker(text.charAt(i))): 
                    continue # Skip the pair
                else:
                    n = OLDCHARS.indexOf(text.charAt(i))
                    if (n > - 1):
                        text.setCharAt(i, NEWCHARS.charAt(n))
            tf.setCodedText(text.toString())
    