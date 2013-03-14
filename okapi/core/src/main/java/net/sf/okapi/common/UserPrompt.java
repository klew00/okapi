/*===========================================================================
  Copyright (C) 2013 by the Okapi Framework contributors
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

package net.sf.okapi.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.sf.okapi.common.exceptions.OkapiUserCanceledException;

/**
 * A CLI implementation of {@link IUserPrompt}.
 */
public class UserPrompt implements IUserPrompt {

	@Override
	public void initialize(Object uiParent, String title) {
		// No initialization required.
	}

	@Override
	public boolean promptYesNoCancel(String message)
		throws OkapiUserCanceledException {
		
		System.out.println(message);
		System.out.print("[Y]es/[N]o/[C]ancel: ");
		InputStreamReader stream = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(stream);;
		while (true) {
			try {
				String input = reader.readLine().toLowerCase();
				if (input.equals("yes") || input.equals("y")) return true;
				if (input.equals("no") || input.equals("n")) return false;
				if (input.equals("cancel") || input.equals("c"))
					throw new OkapiUserCanceledException("Operation was canceled by user.");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public boolean promptOKCancel(String message)
			throws OkapiUserCanceledException {
		
		System.out.println(message);
		System.out.print("[O]K/[C]ancel: ");
		InputStreamReader stream = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(stream);;
		while (true) {
			try {
				String input = reader.readLine().toLowerCase();
				if (input.equals("ok") || input.equals("o")) return true;
				if (input.equals("cancel") || input.equals("c"))
					throw new OkapiUserCanceledException("Operation was canceled by user.");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
