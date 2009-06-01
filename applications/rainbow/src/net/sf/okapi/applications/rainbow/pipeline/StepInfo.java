/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.applications.rainbow.pipeline;

public class StepInfo {

	public String id;
	public String name;
	public String description;
	public String stepClass;
	public String editorClass;
	public String paramsData;

	public StepInfo (String id,
		String name,
		String description,
		String stepClass,
		String editorClass)
	{
		this.id = id;
		this.name = name;
		this.description = description;
		this.stepClass = stepClass;
		this.editorClass = editorClass;
	}

	@Override
	public StepInfo clone () {
		StepInfo newStep = new StepInfo(id, name, description, stepClass, editorClass);
		newStep.paramsData = paramsData;
		return newStep;
	}
}
