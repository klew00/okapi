/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.xliffkit.opc;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filters.AbstractFilter;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.steps.xliffkit.common.persistence.JSONPersistenceSession;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;

public class OPCPackageReader extends AbstractFilter {

	private OPCPackage pack;
	private JSONPersistenceSession session = new JSONPersistenceSession();
	private Event event;
	//private LinkedHashMap<PackagePartName, PackagePart> parts = new LinkedHashMap<PackagePartName, PackagePart>();
	private LinkedList<PackagePart> parts = new LinkedList<PackagePart>();
	private PackagePart activePart;
	
	@Override
	protected boolean isUtf8Bom() {
		return false;
	}

	@Override
	protected boolean isUtf8Encoding() {
		return false;
	}

	@Override
	public void close() {
		parts.clear();
		activePart = null;
		session.end();
		try {
			pack.close();
		} catch (IOException e) {
			throw new OkapiIOException("OPCPackageReader: cannot close package");
		}
	}

	@Override
	public IParameters getParameters() {
		return null;
	}

	@Override
	public boolean hasNext() {
		return event != null;
	}

	@Override
	public Event next() {
		Event prev = event;
		event = deserializeEvent();
		return prev;
	}

	/*
	 * Deserializes events from JSON files in OPC package
	 * @return null if no events are available 
	 */
	private Event deserializeEvent() {
		Event event = null;
		if (activePart == null) {
			activePart = parts.poll();
			if (activePart == null) 
				return null;
			else
				try {
					session.start(activePart.getInputStream());
				} catch (IOException e) {
					throw new OkapiIOException("OPCPackageReader: cannot get resources from package", e);
				}
			}
		event = session.deserialize(Event.class);
		if (event == null) {			
			session.end();
			activePart = null;
			return deserializeEvent(); // Recursion until all parts are tried
		}
		return event;
	}

	@Override
	public void open(RawDocument input) {
		open(input, false);		
	}

	@Override
	public void open(RawDocument input, boolean generateSkeleton) {
		try {
			pack = OPCPackage.open(input.getStream());
		} catch (Exception e) {
			throw new OkapiIOException("OPCPackageReader: cannot open package", e);
		}
		
		List<PackagePart> coreParts = OPCPackageUtil.getCoreParts(pack);
		
		for (PackagePart part : coreParts) {
			//PackagePart sourcePart = OPCPackageUtil.getSourcePart(part); 
			PackagePart resourcesPart = OPCPackageUtil.getResourcesPart(part);
//			if (sourcePart != null && resourcesPart != null)
//				parts.put(sourcePart.getPartName(), resourcesPart);
			if (resourcesPart != null)
				parts.add(resourcesPart);
		}
				
		event = deserializeEvent();
	}

	@Override
	public void setParameters(IParameters params) {
	}

}
