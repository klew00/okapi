/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.common.skeleton;

import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.writer.ILayerProvider;

public interface ISkeletonWriter {

	public void processStart (String language,
		String encoding,
		ILayerProvider layer,
		EncoderManager encoderManager);
	
	public void processFinished ();
	
	public String processStartDocument (StartDocument resource);
	
	public String processEndDocument (Ending resource);
	
	public String processStartSubDocument (StartSubDocument resource);
	
	public String processEndSubDocument (Ending resource);
	
	public String processStartGroup (StartGroup resource);
	
	public String processEndGroup (Ending resource);
	
	public String processTextUnit (TextUnit resource);
	
	public String processDocumentPart (DocumentPart resource);
	
}
