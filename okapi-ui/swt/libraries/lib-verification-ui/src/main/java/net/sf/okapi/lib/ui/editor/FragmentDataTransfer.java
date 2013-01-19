/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.lib.ui.editor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.sf.okapi.common.resource.Code;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;

/**
 * Helper class to handle clipboard transfer of extracted text.
 */
public class FragmentDataTransfer extends ByteArrayTransfer {

	static private final String MIME_TYPE = "application/okapi-fragment"; // $NON-NLS-1$

	static private FragmentDataTransfer theInstance;
	
	private final int MIME_TYPE_ID = registerType(MIME_TYPE);

	private FragmentDataTransfer () {
		// Nothing to do
	}
	
	public static FragmentDataTransfer getInstance () {
		if ( theInstance == null ) {
			theInstance = new FragmentDataTransfer();
		}
		return theInstance;
	}
	
	@Override
	protected int[] getTypeIds () {
		return new int[] {MIME_TYPE_ID};
	}

	@Override
	protected String[] getTypeNames () {
		return new String[] {MIME_TYPE};
	}

	@Override
	public void javaToNative(Object object,
		TransferData transferData)
	{
		if ( !isSupportedType(transferData) ) {
			DND.error(DND.ERROR_INVALID_DATA);
		}
		FragmentData myType = (FragmentData)object;
		byte[] bytes = convertToByteArray(myType);
		if ( bytes != null ) {
			super.javaToNative(bytes, transferData);
		}
	}

	@Override
	public Object nativeToJava (TransferData transferData) {
		if ( !isSupportedType(transferData) ) return null;
		byte[] bytes = (byte[])super.nativeToJava(transferData);
		return ((bytes == null) ? null : restoreFromByteArray(bytes));
	}

	private static byte[] convertToByteArray (FragmentData data) {
		DataOutputStream dos = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			dos = new DataOutputStream(bos);
			dos.writeUTF(data.codedText);
			dos.writeUTF(Code.codesToString(data.codes));
			return bos.toByteArray();
		}
		catch ( IOException e ) {
			return null;
		}
		finally {
			if ( dos != null ) {
				try {
					dos.close();
				}
				catch ( IOException e ) {}
			}
		}
	}
	
	private static FragmentData restoreFromByteArray (byte[] bytes) {
		DataInputStream dis = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			dis = new DataInputStream(bis);
			FragmentData result = new FragmentData();
			result.codedText = dis.readUTF();
			result.codes = Code.stringToCodes(dis.readUTF());
			return result;
		}
		catch ( IOException ex ) {
			return null;
		}
		finally {
			if ( dis != null ) {
				try {
					dis.close();
				}
				catch ( IOException e ) {} // Swallow the error
			}
		}
	}

}
