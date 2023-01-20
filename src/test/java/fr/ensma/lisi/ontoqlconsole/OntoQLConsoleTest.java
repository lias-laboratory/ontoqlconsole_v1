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

import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Stéphane JEAN
 */
public class OntoQLConsoleTest {
	@Test
	public void loadSimpleOntoQLFile() {
		URL resource = OntoQLConsoleTest.class.getResource("/sample.ontoql");

		if (resource == null) {
			Assert.fail("OntoQL sample file not found");
		}

		new OntoQLConsole("localhost", "5432", "postgres", "psql", "template_ontodb", resource.getPath());
	}
}
