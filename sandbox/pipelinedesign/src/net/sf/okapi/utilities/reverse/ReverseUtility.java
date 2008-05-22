package net.sf.okapi.utilities.reverse;

import net.sf.okapi.common.pipeline.ThrougputPipeBase;
import net.sf.okapi.common.resource.IExtractionItem;

public class ReverseUtility extends ThrougputPipeBase{

    @Override
    public void endExtractionItem(IExtractionItem extractionItem) {
        String reverse = new StringBuffer(extractionItem.getContent()).reverse().toString(); 
        extractionItem.setContent(reverse);
        
        super.endExtractionItem(extractionItem);
    }
    
}
