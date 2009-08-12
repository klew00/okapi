package net.sf.okapi.common.filters.tests;

import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IContext;

/**
 * User: Christian Hargraves
 * Date: Jun 26, 2009
 * Time: 1:24:51 PM
 */
public class StubEditor implements IParametersEditor {
    public boolean edit(IParameters paramsObject, boolean readOnly, IContext context) {
        return false;
    }

    public IParameters createParameters() {
        return null;  
    }
}
