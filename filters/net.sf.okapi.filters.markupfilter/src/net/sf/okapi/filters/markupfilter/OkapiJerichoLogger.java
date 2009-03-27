package net.sf.okapi.filters.markupfilter;

import net.htmlparser.jericho.Logger;

public class OkapiJerichoLogger implements Logger {
	private org.slf4j.Logger logger;
	
	public OkapiJerichoLogger(org.slf4j.Logger logger) {
		this.logger = logger;		
	}

	public void debug(String message) {
		logger.debug(message);
	}

	public void error(String message) {
		logger.error(message);
	}

	public void info(String message) {
		logger.info(message);		
	}

	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	public boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}

	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	public boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}

	public void warn(String message) {
		logger.warn(message);		
	}
}
