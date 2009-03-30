package net.sf.okapi.filters.markupfilter;

import java.util.logging.Level;

import net.htmlparser.jericho.Logger;

public class OkapiJerichoLogger implements Logger {
	private java.util.logging.Logger logger;
	
	public OkapiJerichoLogger(java.util.logging.Logger logger) {
		this.logger = logger;		
	}

	public void debug(String message) {
		logger.log(Level.ALL, message);
	}

	public void error(String message) {
		logger.log(Level.SEVERE, message);
	}

	public void info(String message) {
		logger.log(Level.INFO, message);		
	}

	public boolean isDebugEnabled() {
		return logger.isLoggable(Level.ALL);
	}

	public boolean isErrorEnabled() {
		return logger.isLoggable(Level.SEVERE);
	}

	public boolean isInfoEnabled() {
		return logger.isLoggable(Level.INFO);
	}

	public boolean isWarnEnabled() {
		return logger.isLoggable(Level.WARNING);
	}

	public void warn(String message) {
		logger.log(Level.WARNING, message);		
	}
}
