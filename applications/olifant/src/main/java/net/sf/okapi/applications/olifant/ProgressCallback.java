/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.applications.olifant;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.observer.IObserver;
import net.sf.okapi.lib.tmdb.IProgressCallback;

public class ProgressCallback implements IProgressCallback {

	private ArrayList<IObserver> observers;
	private boolean isCanceled;
	private final LogPanel logPanel;
	
	public ProgressCallback (TmPanel tp) {
		observers = new ArrayList<IObserver>();
		this.logPanel = tp.getLog();
		addObserver(tp);
	}
	
	@Override
	public void addObserver (IObserver observer) {
		observers.add(observer);
	}

	@Override
	public int countObservers () {
		return observers.size();
	}

	@Override
	public void deleteObserver (IObserver observer) {
		observers.remove(observer);
	}

	@Override
	public void deleteObservers () {
		observers.clear();
	}

	@Override
	public List<IObserver> getObservers () {
		return observers;
	}

	@Override
	public void notifyObservers () {
		notifyObservers(null);
	}

	@Override
	public void notifyObservers (Object arg) {
		for ( IObserver observer : observers ) {
			observer.update(this, arg);
		}
	}

	private boolean updateUI (int p_kind,
		long p_count,
		String p_text)
	{
		final long count = p_count;
		final int kind = p_kind;
		final String text = p_text;
		logPanel.getDisplay().asyncExec(new Runnable() {
			public void run () {
				switch ( kind ) {
				case 0: // Start
					logPanel.startTask(text);
					break;
				case 1: // Update progress
					isCanceled = logPanel.setInfo(String.valueOf(count));
					break;
				case 3: // Normal message
					isCanceled = logPanel.log(text);
					break;
				case 4: // Warning message
					isCanceled = logPanel.log("WARNING: "+text);
					break;
				case 5: // Error message
					isCanceled = logPanel.log("ERROR: "+text);
					break;
				case 6: // Done
					logPanel.endTask(String.format("Entries processed: %d", count));
					notifyObservers();
					break;
				}
			}
		});
		// Returns true to continue, false to stop
		return !isCanceled;
	}
	
	@Override
	public void startProcess (String text) {
		updateUI(0, 0, text);
	}
	
	@Override
	public void endProcess (long count) {
		updateUI(6, count, null);
	}

	@Override
	public boolean updateProgress (long count) {
		return updateUI(1, count, null);
	}
	
	@Override
	public boolean logMessage (int type,
		String text)
	{
		switch ( type ) {
		case 1: // Warning message
			return updateUI(4, 0, text);
		case 2: // Error message
			return updateUI(5, 0, text);
		default:
		case 0: // Normal message
			return updateUI(3, 0, text);
		}
	}
	
	@Override
	public boolean isCanceled () {
		return isCanceled;
	}

}
