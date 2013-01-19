/*===========================================================================
  Copyright (C) 2010-2012 by the Okapi Framework contributors
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

package net.sf.okapi.steps.rainbowkit.common;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

/**
 * Provides a common writer to create a translation package. 
 */
public interface IPackageWriter extends IFilterWriter {

	public void setParameters (IParameters params);
	
	public IParameters getParameters ();
	
	public void setBatchInformation (String packageRoot,
		LocaleId srcLoc,
		LocaleId trgLoc,
		String inputRootDir,
		String rootDir,
		String packageId,
		String projectId,
		String creatorParams,
		String tempPackageRoot);
	
	public void setDocumentInformation (String relativeInputPath,
		String filterConfigId,
		String filterParameters,
		String inputEncoding,
		String relativeTargetPath,
		String outputEncoding,
		ISkeletonWriter skelWriter);

	//public String getMainOutputPath ();

	public void setSupporstOneOutputPerInput (boolean supporstOneOutputPerInput);
	
}
