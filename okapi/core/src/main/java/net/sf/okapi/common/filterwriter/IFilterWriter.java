/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.common.filterwriter;

import java.io.OutputStream;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

/**
 * Provides the common methods to generate an output from the events generated by a class
 * that implements IFilter.
 */
public interface IFilterWriter {

	/**
	 * Gets the name of this writer.
	 * @return The name of the writer.
	 */
	public String getName ();

	/**
	 * Sets the options for this writer.
	 * @param locale the output locale.
	 * @param defaultEncoding Name of the character set encoding for the output.
	 */
	public void setOptions (LocaleId locale,
		String defaultEncoding);
	
	/**
	 * Sets the output through the path of the output file.
	 * @param path Full path of the output file.
	 */
	public void setOutput (String path);

	/**
	 * Sets the output through its output stream.
	 * @param output Output stream to use for the output.
	 */
	public void setOutput (OutputStream output);
	
	/**
	 * Handles the filter events.
	 * @param event The event to process.
	 * @return The event that was processed.
	 */
	public Event handleEvent (Event event);

	/**
	 * Closes the output. Developers must make sure this method is safe to call
	 * even if there is nothing to close.
	 */
	public void close ();

	/**
	 * Gets the current parameters for this writer.
	 * @return The current parameters for this writer.
	 */
	public IParameters getParameters ();

	/**
	 * Sets new parameters for this writer.
	 * @param params The new parameters to use.
	 */
	public void setParameters (IParameters params);

	/**
	 * Cancels the current process.
	 */
	public void cancel ();

	/**
	 * Gets the current encoder manager for this writer. Some special implementation of IFilterWriter
	 * may not use an encoder manager (for example writers that do not use skeleton).
	 * @return the current encoder manager for this writer, or null if none exists for this writer.
	 */
	public EncoderManager getEncoderManager ();

	/**
	 * Gets the skeleton writer associated with this writer. Some implementation of IFilterWriter
	 * may not use a skeleton writer.
	 * @return the skeleton writer associated with this writer or null if none is associated.
	 */
	public ISkeletonWriter getSkeletonWriter ();
	
}
