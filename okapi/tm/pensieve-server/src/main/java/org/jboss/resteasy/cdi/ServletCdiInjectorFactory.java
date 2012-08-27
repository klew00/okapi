package org.jboss.resteasy.cdi;

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletCdiInjectorFactory extends CdiInjectorFactory {

	private static final Logger log = LoggerFactory.getLogger(ServletCdiInjectorFactory.class);

	@Override
	protected BeanManager lookupBeanManager() {
		InitialContext ctx = null;
		try {
			log.debug("Doing a lookup of BeanManager in java:comp/BeanManager");
			ctx = new InitialContext();
			return (BeanManager) ctx.lookup("java:comp/BeanManager");
		} catch (NamingException e) {
			// Workaround for WELDINT-19
			try {
				log.debug("Lookup failed. Trying java:app/BeanManager");
				return (BeanManager) ctx.lookup("java:app/BeanManager");
			} catch (NamingException ne) {
				try {
					log
							.debug("Lookup failed. Trying java:comp/env/BeanManager");
					return (BeanManager) ctx
							.lookup("java:comp/env/BeanManager");
				} catch (NamingException nex) {
					throw new RuntimeException("Unable to obtain BeanManager.",
							nex);
				}
			}
		}
	}
}
