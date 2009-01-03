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

package net.sf.okapi.common.ui;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MenuItem;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ResourceManager {

	@SuppressWarnings("unchecked")
	private Class cls;
	private Display display;
	private Hashtable<String, Image> images;
	private Hashtable<String, Color> colors1;
	private Hashtable<Integer, Color> colors2;
	private String ext = ".png";
	private String subdir = "images";
	private Hashtable<String, CommandItem> commands;

	/**
	 * Gets the text content of the first child of an element node.
	 * This is to use instead of node.getTextContent() which does not work with some
	 * Macintosh Java VMs.
	 * @param node The container element.
	 * @return The text of the first child node.
	 */
	private static String getTextContent (Node node) {
		Node n = node.getFirstChild();
		if ( n == null ) return null;
		return n.getNodeValue();
	}
	
	/**
	 * Creates a ResourceManager object.
	 * @param p_Class Class to use to load the resources. This class must be in the 
	 * root location of the place where the resources are.
	 * @param p_Display Display to associate with the resources.
	 */
	@SuppressWarnings("unchecked")
	public ResourceManager (Class p_Class,
		Display p_Display)
	{
		display = p_Display;
		cls = p_Class;
		images = new Hashtable<String, Image>();
		colors1 = new Hashtable<String, Color>();
		colors2 = new Hashtable<Integer, Color>();
		commands = new Hashtable<String, CommandItem>();
	}
	
	/**
	 * Disposes of all the resources.
	 */
	public void dispose () {
		Enumeration<Image> E1 = images.elements();
		while ( E1.hasMoreElements() ) {
			E1.nextElement().dispose();
		}
		images.clear();
		
		Enumeration<Color> E2 = colors1.elements();
		while ( E2.hasMoreElements() ) {
			E2.nextElement().dispose();
		}
		colors1.clear();
		
		E2 = colors2.elements();
		while ( E2.hasMoreElements() ) {
			E2.nextElement().dispose();
		}
		colors2.clear();

		if ( commands != null )
			commands.clear();
	}
	
	/**
	 * Sets the default extension.
	 * @param p_sValue the extension, with its leading period.
	 */
	public void setDefaultExtension (String p_sValue) {
		ext = p_sValue;
	}
	
	/**
	 * Sets the default sub-directory.
	 * @param p_sValue The sub-directory, without leading or trailing
	 * separators. Make sure to use '/' for internal separators.
	 */
	public void setSubDirectory (String p_sValue) {
		subdir = p_sValue;
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
		String sKey = p_sName;
		if ( p_sName.lastIndexOf('.') == -1 ) {
			p_sName += ext;
		}
		if (( p_sName.indexOf(File.separatorChar) == -1 ) && ( subdir.length() != 0 )) {
			p_sName = subdir + "/" + p_sName; // Use '/' not File.separatorChar!
		}
		images.put(sKey, new Image(display, cls.getResourceAsStream(p_sName)));
	}
	
	public void addColor (String p_sName,
		int p_nRed,
		int p_nGreen,
		int p_nBlue)
	{
		colors1.put(p_sName, new Color(display, p_nRed, p_nGreen, p_nBlue));	
	}
		
	public void addColor (int p_nID,
		int p_nRed,
		int p_nGreen,
		int p_nBlue)
	{
		colors2.put(p_nID, new Color(display, p_nRed, p_nGreen, p_nBlue));	
	}
	
	/**
	 * Retrieves a loaded images from the resource list.
	 * @param p_sName Key name of the resource. This name is the same you used to
	 * add the resource to the list.
	 * @return The image.
	 */
	public Image getImage (String p_sName) {
		return images.get(p_sName);
	}
	
	public Color getColor (String p_sName) {
		return colors1.get(p_sName);
	}

	public Color getColor (int p_nID) {
		return colors2.get(p_nID);
	}

	public void setCommand (MenuItem menuItem,
		String resName) {
		CommandItem cmd = commands.get(resName);
		menuItem.setText(cmd.label);
		if ( cmd.accelerator != 0 )
			menuItem.setAccelerator(cmd.accelerator);
	}
	
	public String getCommandLabel (String resName) {
		CommandItem cmd = commands.get(resName);
		if ( cmd == null ) return "!"+resName+"!";
		return cmd.label;
	}
	
	public void loadCommands (String path) {
		try {
			DocumentBuilderFactory Fact = DocumentBuilderFactory.newInstance();
			Fact.setValidating(false);
			Document Doc = Fact.newDocumentBuilder().parse(cls.getResourceAsStream(path));
			NodeList NL = Doc.getElementsByTagName("command");
			commands.clear();
			CommandItem item;
			String name;
			for ( int i=0; i<NL.getLength(); i++ ) {
				item = new CommandItem();
				item.label = getTextContent(NL.item(i));
				Node N = NL.item(i).getAttributes().getNamedItem("name");
				if ( N == null ) throw new Exception("The attribute 'name' is missing.");
				else name = getTextContent(N);
				if ( (N = NL.item(i).getAttributes().getNamedItem("alt")) != null )
					if ( getTextContent(N).equals("1") ) item.accelerator |= SWT.ALT;
				if ( (N = NL.item(i).getAttributes().getNamedItem("shift")) != null )
					if ( getTextContent(N).equals("1") ) item.accelerator |= SWT.SHIFT;
				if ( (N = NL.item(i).getAttributes().getNamedItem("ctrl")) != null )
					if ( getTextContent(N).equals("1") ) item.accelerator |= SWT.CONTROL;
				if ( (N = NL.item(i).getAttributes().getNamedItem("cmd")) != null )
					if ( getTextContent(N).equals("1") ) item.accelerator |= SWT.COMMAND;
				if ( (N = NL.item(i).getAttributes().getNamedItem("key")) != null ) {
					String key = getTextContent(N);
					if ( key.equals("F1") ) item.accelerator |= SWT.F1;
					else if ( key.equals("F2") ) item.accelerator |= SWT.F2;
					else if ( key.equals("F3") ) item.accelerator |= SWT.F3;
					else if ( key.equals("F4") ) item.accelerator |= SWT.F4;
					else if ( key.equals("F5") ) item.accelerator |= SWT.F5;
					else if ( key.equals("F6") ) item.accelerator |= SWT.F6;
					else if ( key.equals("F7") ) item.accelerator |= SWT.F7;
					else if ( key.equals("F8") ) item.accelerator |= SWT.F8;
					else if ( key.equals("F9") ) item.accelerator |= SWT.F9;
					else if ( key.equals("F10") ) item.accelerator |= SWT.F10;
					else if ( key.equals("F11") ) item.accelerator |= SWT.F11;
					else if ( key.equals("F12") ) item.accelerator |= SWT.F12;
					else if ( key.equals("F13") ) item.accelerator |= SWT.F13;
					else if ( key.equals("F14") ) item.accelerator |= SWT.F14;
					else if ( key.equals("F15") ) item.accelerator |= SWT.F15;
					else if ( key.equals("Up") ) item.accelerator |= SWT.ARROW_UP;
					else if ( key.equals("Down") ) item.accelerator |= SWT.ARROW_DOWN;
					else if ( key.equals("Left") ) item.accelerator |= SWT.ARROW_LEFT;
					else if ( key.equals("Right") ) item.accelerator |= SWT.ARROW_RIGHT;
					else if ( key.equals("PageUp") ) item.accelerator |= SWT.PAGE_UP;
					else if ( key.equals("PageDown") ) item.accelerator |= SWT.PAGE_DOWN;
					else if ( key.equals("Home") ) item.accelerator |= SWT.HOME;
					else if ( key.equals("End") ) item.accelerator |= SWT.END;
					else if ( key.equals("Insert") ) item.accelerator |= SWT.INSERT;
					else if ( key.equals("Enter") ) item.accelerator |= SWT.CR;
					else if ( key.equals("Delete") ) item.accelerator |= SWT.DEL;
					else item.accelerator |= key.codePointAt(0);
				}
				commands.put(name, item);
			}
        }
		catch ( Exception e ) {
			throw new RuntimeException(e);
		}
	}
}
