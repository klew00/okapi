package net.sf.okapi.common.filters;

import net.sf.okapi.common.pipeline.IOutputPipe;

public interface IInputFilter extends IOutputPipe{

    public void convert();
}
