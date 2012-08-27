import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;   

/*
Inspired by: http://blog.uncommons.org/2006/04/09/colour-coded-console-logging-with-log4j/
*/
 
/**
 * Colour-coded console appender for Log4J.
 */
public class ColorConsoleAppender extends ConsoleAppender
{
    private static final String END_COLOUR   = "\u001b[m";

    private static String FATAL_COLOUR = "\u001b[1;37;41m";
    private static String ERROR_COLOUR = "\u001b[1;31m";
    private static String WARN_COLOUR  = "\u001b[1;33m";
    private static String INFO_COLOUR  = "\u001b[32m";
    private static String DEBUG_COLOUR = "\u001b[36m";
    private static String TRACE_COLOUR = "\u001b[1;30m";

    @SuppressWarnings("static-method")
    public void setFatalColour ( String val ) { FATAL_COLOUR = val; }
    @SuppressWarnings("static-method")
    public void setErrorColour ( String val ) { ERROR_COLOUR = val; }
    @SuppressWarnings("static-method")
    public void setWarnColour  ( String val ) { WARN_COLOUR  = val; }
    @SuppressWarnings("static-method")
    public void setInfoColour  ( String val ) { INFO_COLOUR  = val; }
    @SuppressWarnings("static-method")
    public void setDebugColour ( String val ) { DEBUG_COLOUR = val; }
    @SuppressWarnings("static-method")
    public void setTraceColour ( String val ) { TRACE_COLOUR = val; }

    /**
     * Wraps the ANSI control characters around the
     * output from the super-class Appender.
     */
    @Override
    protected void subAppend(LoggingEvent event)
    {
        this.qw.write(getColour(event.getLevel()));
        super.subAppend(event);
        this.qw.write(END_COLOUR);   
 
        if(this.immediateFlush)
        {
            this.qw.flush();
        }
    }    

    /**
     * Get the appropriate control characters to change
     * the colour for the specified logging level.
     */
    private static String getColour(Level level)
    {
        switch (level.toInt())
        {
            case Priority.FATAL_INT: return FATAL_COLOUR;
            case Priority.ERROR_INT: return ERROR_COLOUR;
            case Priority.WARN_INT: return WARN_COLOUR;
            case Priority.INFO_INT: return INFO_COLOUR;
            case Priority.DEBUG_INT:return DEBUG_COLOUR;
            default: return TRACE_COLOUR;
        }
    }

}
