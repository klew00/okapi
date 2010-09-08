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

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;

public class OPCPackageUtil {

	public static List<PackagePart> getCoreParts(OPCPackage pack) {
		List<PackagePart> res = new ArrayList<PackagePart>();
		
		for (PackageRelationship rel : pack.getRelationshipsByType(TKitRelationshipTypes.CORE_DOCUMENT))
			try {
				res.add(pack.getPart(PackagingURIHelper.createPartName(rel.getTargetURI())));
			} catch (InvalidFormatException e) {
				// TODO Handle exception
				e.printStackTrace();
			}			
		return res;
	}
	
	public static PackagePart getCorePart(OPCPackage pack) {
		List<PackagePart> res = new ArrayList<PackagePart>();
		
		for (PackageRelationship rel : pack.getRelationshipsByType(TKitRelationshipTypes.CORE_DOCUMENT))
			try {
				res.add(pack.getPart(PackagingURIHelper.createPartName(rel.getTargetURI())));
			} catch (InvalidFormatException e) {
				// TODO Handle exception
				e.printStackTrace();
			}			
		return res.size() > 0 ? res.get(0) : null;
	}

	private static PackagePart getPartByRelationshipType(PackagePart part, String relationshipType) {
		try {
			PackageRelationshipCollection rels = part.getRelationshipsByType(relationshipType);
			if (rels.size() == 0) return null;
			
			OPCPackage pack = part.getPackage();
			return pack.getPart(PackagingURIHelper.createPartName(rels.getRelationship(0).getTargetURI()));
		} catch (InvalidFormatException e) {
			// TODO Handle exception
			e.printStackTrace();
		}		
		return null;
	}
	
	public static PackagePart getSourcePart(PackagePart part) {		
		return getPartByRelationshipType(part, TKitRelationshipTypes.SOURCE);
	}	

	public static PackagePart getResourcesPart(PackagePart part) {		
		return getPartByRelationshipType(part, TKitRelationshipTypes.RESOURCES);
	}
}
