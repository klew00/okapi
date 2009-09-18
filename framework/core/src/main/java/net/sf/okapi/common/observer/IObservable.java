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
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.common.observer;

import java.util.List; //FIXME: performance

/**
 * This interface is intended to provide more flexibility to complex object
 * models when multiple inheritance is needed.
 * 
 * <p>
 * By providing Observable as an interface, instead of the JDK'a
 * {@link java.util.Observable} class, client classes have the option to mimic
 * multiple inheritance by means of a delegate pattern which uses an inner
 * private class which implements this interface.
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
 * 
 * @author Martin Fischer (original author)
 * @author Richard Gomes
 */
public interface IObservable {

	/**
	 * Attaches a observer to the Observable. After attachment the observer gets
	 * informed about changes in the Observable.
	 * 
	 * @param observer
	 *            The observer to attach to the observable
	 */
	public void addObserver(final IObserver observer);

	/**
	 * Counts how many Observers were attached to this class.
	 * 
	 * @return the number of Observers
	 * @see IObserver
	 */
	public int countObservers();

	/**
	 * Returns list of observers registered with the Observable. List returned
	 * is unmodifiable list.
	 * 
	 * @return list of observers
	 */
	public List<IObserver> getObservers();

	/**
	 * Detaches a previously attached observer to the observable. After
	 * detachment the observer does no longer receive change notifications from
	 * the observable.
	 * 
	 * @param observer
	 *            The observer to detach from the observable
	 */
	public void deleteObserver(final IObserver observer);

	/**
	 * Detaches all previously attached observer to the observable. After
	 * detachment observers do not longer receive change notifications from the
	 * observable.
	 */
	public void deleteObservers();

	/**
	 * Notifies all attached observers about changes in the observable.
	 */
	public void notifyObservers();

	/**
	 * Notifies all attached observers about changes in the observable.
	 * 
	 * @param arg
	 *            an arbitrary Object to be passed to the Observer
	 */
	public void notifyObservers(Object arg);
}