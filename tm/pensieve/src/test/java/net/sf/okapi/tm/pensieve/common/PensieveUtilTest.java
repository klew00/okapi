package net.sf.okapi.tm.pensieve.common;

import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.tm.pensieve.Helper;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * User: Christian Hargraves
 * Date: Sep 4, 2009
 * Time: 4:12:34 PM
 */
public class PensieveUtilTest {

    @Test
    public void stupidTestOnlyForCoverage() throws Exception {
        Helper.genericTestConstructor(PensieveUtil.class);
    }

    @Test
    public void convertTranslationUnit(){
        TextUnit textUnit = new TextUnit("someId", "some great text");
        textUnit.setTargetContent("kr", new TextFragment("some great text in Korean"));
        TranslationUnit tu = PensieveUtil.convertTranslationUnit("en", "kr", textUnit);
        assertEquals("sourceLang", "en", tu.getSource().getLang());
        assertEquals("source content", "some great text", tu.getSource().getContent().toString());
        assertEquals("targetLang", "kr", tu.getTarget().getLang());
        assertEquals("target content", "some great text in Korean", tu.getTarget().getContent().toString());
    }

}
