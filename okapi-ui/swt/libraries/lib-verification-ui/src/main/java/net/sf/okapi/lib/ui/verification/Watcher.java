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

package net.sf.okapi.lib.ui.verification;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import org.eclipse.swt.widgets.Display;

import net.sf.okapi.common.resource.RawDocument;

public class Watcher extends TimerTask {

	private Map<File, Long> stamps;
	private QualityCheckEditor editor;
	private Display display;
	
	public Watcher (QualityCheckEditor editor,
		Display display)
	{
		this.display = display;
		this.editor = editor;
		Map<URI, RawDocument> docs = editor.getSession().getDocumentsMap();
		stamps = new HashMap<File, Long>();
		for ( URI uri : docs.keySet() ) {
			File file = new File(uri);
			stamps.put(file, file.lastModified());
		}
	}
	
	@Override
	public void run () {
		if ( stamps == null ) return;
		
		boolean needRefresh = false;
		for ( File file : stamps.keySet() ) {
			if ( stamps.get(file) != file.lastModified() ) {
				stamps.put(file, file.lastModified());
				needRefresh = true;
				// Continue to check/update all files, because the refresh will be done for all
			}
		}
		
		if ( needRefresh ) {
			display.syncExec(new Runnable() {
				public void run(){
					editor.checkAll();				    }
			});
		}
	}
	
}

/*
import java.util.*;
import java.io.*;

public abstract class FileWatcher extends TimerTask {
  private long timeStamp;
  private File file;

  public FileWatcher( File file ) {
    this.file = file;
    this.timeStamp = file.lastModified();
  }

  public final void run() {
    long timeStamp = file.lastModified();

    if( this.timeStamp != timeStamp ) {
      this.timeStamp = timeStamp;
      onChange(file);
    }
  }

  protected abstract void onChange( File file );
}
import java.util.*;
import java.io.*;

public class FileWatcherTest {
  public static void main(String args[]) {
    // monitor a single file
    TimerTask task = new FileWatcher( new File("c:/temp/text.txt") ) {
      protected void onChange( File file ) {
        // here we code the action on a change
        System.out.println( "File "+ file.getName() +" have change !" );
      }
    };

    Timer timer = new Timer();
    // repeat the check every second
    timer.schedule( task , new Date(), 1000 );
  }
}*/
