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

package net.sf.okapi.Borneo.Core;

public class DBOptions {
	
	private int         m_nType;
	private String      m_sUser;
	private String      m_sPassword;
	private String      m_sServer;
	private String      m_sDatabase;
	private int         m_nPort;

	public DBOptions ()
	{
		//TODO: remove default when user config file is implemented 
		m_nType = 0;
		m_nPort = 3306;
		m_sServer = "192.168.5.2";
		m_sDatabase = "borneo_test";
		m_sPassword = "";
		m_sUser = "borneo_tester";
	}

	public int getDBType () {
		return m_nType;
	}

	public void setDBType (int p_nType) {
		m_nType = p_nType;
	}
		
	public String getUsername () {
		return m_sUser;
	}
	
	public void setUsername (String p_sUsername) {
		m_sUser = p_sUsername;
	}

	public String getPassword () {
		return m_sPassword;
	}
	
	public void setPassword (String p_sPassword) {
		m_sPassword = p_sPassword;
	}

	public String getDatabase () {
		return m_sDatabase;
	}
	
	public void setDatabase (String p_sDatabase) {
		m_sDatabase = p_sDatabase;
	}
		
	public String getServer () {
		return m_sServer;
	}
	
	public void setServer (String p_sServer) {
		m_sServer = p_sServer;
	}

	public int getPort () {
		return m_nPort;
	}
	
	public void setPort (int p_nPort) {
		m_nPort = p_nPort;
	}

}
