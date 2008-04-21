/*===========================================================================*/
/* Copyright (C) 2008 ENLASO Corporation, Okapi Development Team             */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.Library.UI;

import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class ResourceManager {

	private Class                      m_Cls;
	private Display                    m_Disp;
	private Hashtable<String, Image>   m_htImages;
	private Hashtable<String, Color>   m_htColors1;
	private Hashtable<Integer, Color>  m_htColors2;
	private String                     m_sExt = ".png";
	private String                     m_sSubdir = "images";

	/**
	 * Creates a ResourceManager object.
	 * @param p_Class Class to use to load the resources. This class must be in the 
	 * root location of the place where the resources are.
	 * @param p_Display Display to associate with the resources.
	 */
	public ResourceManager (Class p_Class,
		Display p_Display)
	{
		System.err.println("bno- start RM");
		m_Disp = p_Display;
		m_Cls = p_Class;
		System.err.println("bno- after class assignment");
		m_htImages = new Hashtable<String, Image>();
		m_htColors1 = new Hashtable<String, Color>();
		m_htColors2 = new Hashtable<Integer, Color>();
	}
	
	/**
	 * Disposes of all the resources.
	 */
	public void dispose () {
		Enumeration<Image> E1 = m_htImages.elements();
		while ( E1.hasMoreElements() ) {
			E1.nextElement().dispose();
		}
		m_htImages.clear();
		
		Enumeration<Color> E2 = m_htColors1.elements();
		while ( E2.hasMoreElements() ) {
			E2.nextElement().dispose();
		}
		m_htColors1.clear();
		
		E2 = m_htColors2.elements();
		while ( E2.hasMoreElements() ) {
			E2.nextElement().dispose();
		}
		m_htColors2.clear();
	}
	
	/**
	 * Sets the default extension.
	 * @param p_sValue the extension, with its leading period.
	 */
	public void setDefaultExtension (String p_sValue) {
		m_sExt = p_sValue;
	}
	
	/**
	 * Sets the default sub-directory.
	 * @param p_sValue The sub-directory, without leading or trailing
	 * separators. Make sure to use '/' for internal separators.
	 */
	public void setSubDirectory (String p_sValue) {
		m_sSubdir = p_sValue;
	}

	/**
	 * Adds an image to the resource list.
	 * @param p_sName Name of the image to load. This name is also the key name
	 * to retrieve the resource later on and should be unique. If the name has 
	 * no extension or no sub-directory, the default extension and sub-directory
	 * will be added automatically for the load (but the key name stays as you 
	 * defines it).
	 * The default extension is ".png", and the default sub-directory is "images".
	 * For example:
	 * m_RM.add("myImage"); loads "images/myImage.png" and the key is "myImage".
	 * m_RM.add("myImage.gif"); loads "images/myImage.gif" and the key is "myImage.gif".
	 * m_RM.add("res/myImage.gif"); loads "res/myImage.gif" and the key is "res/myImage.gif".
	 */
	public void addImage (String p_sName) {
		System.err.println("bno- start addImage");
		String sKey = p_sName;
		if ( p_sName.lastIndexOf('.') == -1 ) {
			System.err.println("bno- adding ext");
			p_sName += m_sExt;
		}
		System.err.println("bno- image with ext: "+p_sName);
		if (( p_sName.indexOf(File.separatorChar) == -1 ) && ( m_sSubdir.length() != 0 )) {
			System.err.println("bno- adding sub-dir");
			p_sName = m_sSubdir + "/" + p_sName; // Use '/' not File.separatorChar!
		}
		System.err.println("bno- addImage: name="+p_sName);
		InputStream IS = m_Cls.getResourceAsStream(p_sName);
		System.err.println("bno- addImage: after getResourceAsStream");
		System.err.println("bno- IS="+IS.toString());
		m_htImages.put(sKey, new Image(m_Disp, m_Cls.getResourceAsStream(p_sName)));
		System.err.println("bno- addImage: after put()");
	}
	
	public void addColor (String p_sName,
		int p_nRed,
		int p_nGreen,
		int p_nBlue)
	{
		m_htColors1.put(p_sName, new Color(m_Disp, p_nRed, p_nGreen, p_nBlue));	
	}
		
	public void addColor (int p_nID,
		int p_nRed,
		int p_nGreen,
		int p_nBlue)
	{
		m_htColors2.put(p_nID, new Color(m_Disp, p_nRed, p_nGreen, p_nBlue));	
	}
		
	/**
	 * Retrieves a loaded images from the resource list.
	 * @param p_sName Key name of the resource. This name is the same you used to
	 * add the resource to the list.
	 * @return The image.
	 */
	public Image getImage (String p_sName) {
		return m_htImages.get(p_sName);
	}
	
	public Color getColor (String p_sName) {
		return m_htColors1.get(p_sName);
	}

	public Color getColor (int p_nID) {
		return m_htColors2.get(p_nID);
	}
}
