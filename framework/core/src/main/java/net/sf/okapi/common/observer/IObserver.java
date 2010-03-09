/*
 Copyright (C) 2007 Richard Gomes

 This source code is release under the BSD License.
 
 This file is part of JQuantLib, a free-software/open-source library
 for financial quantitative analysts and developers - http://jquantlib.org/

 JQuantLib is free software: you can redistribute it and/or modify it
 under the terms of the JQuantLib license.  You should have received a
 copy of the license along with this program; if not, please email
 <jquant-devel@lists.sourceforge.net>. The license is also available online at
 <http://www.jquantlib.org/index.php/LICENSE.TXT>.

 This program is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the license for more details.
 
 JQuantLib is based on QuantLib. http://quantlib.org/
 When applicable, the original copyright notice follows this notice.
 */

/*===========================================================================
  Additional changes Copyright (C) 2008-2009 by the Okapi Framework contributors
===========================================================================*/

package net.sf.okapi.common.observer;

/**
 * This interface is intended to provide more flexibility to complex object
 * models when multiple inheritance is needed.
 * 
 * <p>
 * This class is based on the work done by Martin Fischer, with only minor
 * changes. See references below.
 * 
 * @see <a
 *      href="http://www.jroller.com/martin_fischer/entry/a_generic_java_observer_pattern">Martin
 *      Fischer: Observer and Observable interfaces</a>
 * @see <a href="http://jdj.sys-con.com/read/35878.htm">Improved
 *      Observer/Observable</a>
 * @see IObservable
 * 
 * @author Martin Fischer (original author)
 * @author Richard Gomes
 */
public interface IObserver {

	/**
	 * This method is called whenever the observed object is changed.
	 * 
	 * @param o
	 * @param arg
	 */
	public void update(IObservable o, Object arg);
}
