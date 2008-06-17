package net.sf.okapi.applications.rainbow;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import net.sf.okapi.applications.rainbow.lib.ILog;

class LogHandler extends Handler {
	
	private ILog       log;
	

	public LogHandler (ILog log) {
		this.log = log;
	}
	
	@Override
	public void close ()
		throws SecurityException
	{
		// Do nothing
	}

	@Override
	public void flush () {
		// Do nothing
	}

	@Override
	public void publish (LogRecord record) {
		if ( record.getLevel() == Level.SEVERE ) {
			log.error(record.getMessage());
			log.error(record.getThrown().getLocalizedMessage());
			for ( StackTraceElement elem : record.getThrown().getStackTrace() ) {
				log.message(" at "+elem.toString());
			}
		}
		else if ( record.getLevel() == Level.WARNING ) {
			log.warning(record.getMessage());
		}
		else if ( record.getLevel() == Level.INFO ) {
			log.message(record.getMessage());
		}
	}

}
