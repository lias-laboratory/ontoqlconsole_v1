/*********************************************************************************
* This file is part of OntoQLConsole Project.
* Copyright (C) 2006  LISI - ENSMA
*   Teleport 2 - 1 avenue Clement Ader
*   BP 40109 - 86961 Futuroscope Chasseneuil Cedex - FRANCE
* 
* OntoQLConsole is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* OntoQLConsole is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with OntoQLConsole.  If not, see <http://www.gnu.org/licenses/>.
**********************************************************************************/
package fr.ensma.lisi.ontoqlconsole;

/**
 * @author Stéphane JEAN
 */
public class OntoQLConsoleURL {

	private String name;

	private String ip;

	private int port;

	private String sid;

	private String user;

	private String password;

	public OntoQLConsoleURL(String name, String ip, int port, String sid, String user, String password) {
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.sid = sid;
		this.user = user;
		this.password = password;
	}

	public String toString() {
		return name;
	}

	public String getURL() {
		return "jdbc:postgresql://" + ip + ":" + port + "/" + sid;
	}

	public String getName() {
		return name;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public String getSid() {
		return sid;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}
}
