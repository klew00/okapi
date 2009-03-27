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
package net.sf.okapi.filters.openxml.tests;

//import org.apache.log4j.BasicConfigurator;
//import org.apache.log4j.Level;
//import org.apache.log4j.Logger;

import java.io.*;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.filters.openxml.ITranslator;

public class PigLatinTranslator implements ITranslator {
	static final String CONS="BCDFGHJKLMNPQRSTVWXYZbcdfghjklmnpqrstvwxyz";
	static final String NUM="0123456789";
	static final String PUNC=" 	`~!#$%^&*()_+[{]}\\;:\",<.>?";
	static final String PUNCNL=" 	`~!#$%^&*()_+[{]}\\;:\",<.>?��";
	static final String PUNCDNL="- 	`~!#$%^&*()_+[{]}\\;:\",<.>";
	static final String UPR="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static final String LWR="abcdefghijklmnopqrstuvwxyz";

	public PigLatinTranslator()
	{
	}

	public String translate(String s)
	{
		String rslt=s,ss="",slow;
		int i,j,k,len,val;
		char carrot;
		len = s.length();
		if (len>1)
		{
			for(i=0;i<len;i++)
			{
				if (i+2<len && s.substring(i,i+3).equals("---"))
				{
					ss += "---";
					i += 2;
				}
				else if (i+1<len && s.substring(i,i+2).equals("--"))
				{
					ss += "--";
					i += 2;				
				}
				else
				{
					j = hominyOf(s.substring(i),NUM);
					if (j>0)
					{
						ss += s.substring(i,i+j);
						i += j-1;
						continue;
					}
					j = hominyOf(s.substring(i),CONS);
					if (j>0)
					{
						k = hominyLetters(s.substring(i+j));
						slow = s.substring(i,i+j).toLowerCase();
						if (k > -1)
						{
							ss += s.substring(i+j,i+j+k);
							i += k;
						}
						ss += slow+"ay";
						i += j-1;
						continue;
					}
					else
					{
						k = hominyLetters(s.substring(i));
						if (k>0)
						{
							ss += s.substring(i,i+k)+"hay";
							i += k-1;
						}
						else
						{
							carrot = s.charAt(i);
							if (TextFragment.isMarker(carrot))
							{
								ss += s.substring(i,i+2);
								i++;
							}
							else
								ss += carrot;
						}
					}
				}			
			}
			rslt = ss;
		}
		return rslt;
	}
	private int hominyOf(String s, String of)
	{
		int i=0,len=s.length();
		char carrot;
		for(i=0;i<len;i++)
		{
			carrot = s.charAt(i);
			if (of.indexOf(carrot) < 0)
				break;
		}
		return i;
	}
	private int hominyLetters(String s)
	{
		int i=0,len=s.length();
		char carrot;
		for(i=0;i<len;i++)
		{
			carrot = s.charAt(i);
			if (!Character.isLetter(carrot) && carrot!='\'')
				break;
		}
		return i;		
	}
}
