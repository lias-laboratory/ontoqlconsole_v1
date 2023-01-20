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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLResultSet;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLStatement;
import fr.ensma.lisi.ontoql.jobdbc.impl.OntoQLSessionImpl;
import fr.ensma.lisi.ontoql.util.JDBCHelper;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * @author Mickael BARON
 */
public class OntoQLConsole {

	private String ontoQLStatement;

	private StringBuffer editorText = new StringBuffer();

	private final static int DML = 0;

	private final static int DDL = 1;

	private OntoQLSession session;

	private OntoQLConsoleURL currentOntoQLConsoleURL;

	private Log log = LogFactory.getLog(OntoQLConsole.class);

	public OntoQLConsole(String host, String port, String user, String password, String dbName, String fileName) {

		// Check if port is a number.
		int portInt = 0;
		try {
			portInt = Integer.parseInt(port);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return;
		}

		// Check if fileName is existing.
		File myFile = new File(fileName);

		if (!myFile.exists()) {
			log.error("Cannot find " + fileName + " filename.");
			return;
		}

		// Build an OntoQLConsoleURL object.
		currentOntoQLConsoleURL = new OntoQLConsoleURL("test", host, portInt, dbName, user, password);

		// Connection to the database.
		try {
			this.setSession(currentOntoQLConsoleURL.getURL(), currentOntoQLConsoleURL.getUser(),
					currentOntoQLConsoleURL.getPassword());
			log.info("Connection is successful to " + currentOntoQLConsoleURL.getURL() + " "
					+ currentOntoQLConsoleURL.getUser() + currentOntoQLConsoleURL.getPassword());
		} catch (SQLException e) {
			log.error("Connection failed to " + currentOntoQLConsoleURL.getURL() + " "
					+ currentOntoQLConsoleURL.getUser() + currentOntoQLConsoleURL.getPassword());
			return;
		}

		// Parse the script.
		try {
			parseScript(myFile);
		} catch (IOException e) {
			log.error("Fatal error during the script parsing.");
			return;
		}
	}

	private void setSession(String url, String user, String password) throws SQLException {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		log.info(url);
		Connection c = DriverManager.getConnection(url, user, password);
		session = new OntoQLSessionImpl(c);
		session.setReferenceLanguage(OntoQLHelper.ENGLISH);
	}

	private void parseScript(File scriptFile) throws IOException {
		String scriptPath = scriptFile.getPath();
		FileReader freader = new FileReader(scriptPath);
		BufferedReader bufferedReader = new BufferedReader(freader);
		StringBuffer buffer = new StringBuffer();
		StreamTokenizer st = null;

		st = new StreamTokenizer(bufferedReader);
		st.eolIsSignificant(true); // EOL is considered in the reading
		st.slashStarComments(true); // C-like comments (/* */) are ignored
		st.slashSlashComments(true); // C-like comments (// ) are ignored
		st.ordinaryChar(' '); // white space will be considered a character to
		// be read
		st.ordinaryChar('/');
		st.ordinaryChar('\''); // the quotes will be considered a character to
		// be read (otherwise the token just after is
		// ignored)
		st.ordinaryChars(45, 57); // the characters in the range 45-57 have to
		// be read. They are the: "-", ".", "/", and
		// numbers from 0 to 9.
		st.wordChars(45, 57);

		do {
			st.nextToken();

			switch (st.ttype) {
			case StreamTokenizer.TT_NUMBER:
				buffer.append(st.nval); // append a number to the SQL buffer
				break;

			case StreamTokenizer.TT_WORD:
				buffer.append(st.sval); // append a word to the SQL buffer
				break;
			case ';': // When the ";" character is found and when in the End of
				// File,
			case StreamTokenizer.TT_EOF: // it marks the execution of the OntoQL
				// statement that is in the buffer.
				String bufferString = buffer.toString();
				bufferString = bufferString.trim();
				if (bufferString.length() != 0) {
					if (bufferString.startsWith("\n")) {
						bufferString = bufferString.replaceFirst("\n", "");
					}
					if (bufferString.startsWith("@")) {
						parseScript(new File(scriptFile.getParent() + "\\" + bufferString.substring(1) + ".ontoql"));
					} else {
						ontoQLStatement = bufferString;
						editorText.append("\nOntoQL> " + ontoQLStatement + "\n\n");
						String result = performOntoQLStatement(ontoQLStatement);
						editorText.append(result);
					}
					// reinitialize buffer for next SQL statement
					buffer.delete(0, buffer.length());
					buffer.setLength(0);
				}
				break;

			default: // a single character found in ttype: "#", '>', EOL,
				// whitespace, etc.
				buffer.append((char) st.ttype); // append a character to the SQL
				// buffer
				break;
			}
		} while (st.ttype != StreamTokenizer.TT_EOF);

		log.info("------------------------\n BEGINING OF THE SCRIPT\n------------------------\n "
				+ editorText.toString() + "\n-------------------\n END OF THE SCRIPT\n-------------------\n");

		bufferedReader.close();
	}

	private String performOntoQLStatement(String ontoqlStatement) throws IOException {
		String result = "";
		Reader bufferedReader = new StringReader(ontoqlStatement);
		StreamTokenizer st = null;

		st = new StreamTokenizer(bufferedReader);
		st.eolIsSignificant(false); // EOL is not considered in the reading
		st.slashStarComments(true); // C-like comments (/* */) are ignored
		st.slashSlashComments(true); // C-like comments (// ) are ignored

		st.nextToken();
		if (st.ttype == StreamTokenizer.TT_WORD) {
			if (st.sval.compareToIgnoreCase("SELECT") == 0) { // if the first
				// token is equal to
				// the word "SELECT"
				result = performOntoQLQuery(ontoqlStatement);
			} else if ((st.sval.compareToIgnoreCase("INSERT") == 0) || (st.sval.compareToIgnoreCase("UPDATE") == 0)
					|| (st.sval.compareToIgnoreCase("DELETE") == 0)) {
				// if it is not SELECT, it is not a query, so it needs to be
				// executed
				result = performOntoQLUpdate(ontoqlStatement, DML);
				// if the first token is not a word, then it is not to be
				// executed
				// ...
			} else {
				result = performOntoQLUpdate(ontoqlStatement, DDL);
			}
		}
		return result;
	}

	private String performOntoQLQuery(String ontoqlQuery) {
		String result;

		try {
			OntoQLStatement stmt = session.createOntoQLStatement();
			OntoQLResultSet rset = stmt.executeQuery(ontoqlQuery);
			result = JDBCHelper.resultSetToString(rset);
		} catch (JOBDBCException e) {
			result = "Error :" + e.getMessage();
		}

		return result;
	}

	private String performOntoQLUpdate(String ontoqlQuery, int typeOfUpdate) {
		String result;

		try {
			OntoQLStatement stmt = session.createOntoQLStatement();
			int nbRows = stmt.executeUpdate(ontoqlQuery);

			if (typeOfUpdate == DML)
				result = nbRows + " rows affected\n";
			else {
				if (nbRows == 0) {
					result = "statement successfully executed\n";
				} else {
					result = "an error has occured\n";
				}
			}
		} catch (JOBDBCException e) {
			result = "Error: " + e.getMessage() + "\n";
		}

		return result;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		if (args == null) {
			throw new Exception("Parameter arguments are missing.");
		}

		if (args.length != 6) {
			System.out.println("Parameter(s) is/are missing. Five parameters are attended, only " + args.length
					+ " parameter(s) is/are defined.");

			System.out.println(" - 1 : Host name (example : lisi-oracle)");
			System.out.println(" - 2 : Port value (example : 5432)");
			System.out.println(" - 3 : Database User name (example : postgres)");
			System.out.println(" - 4 : Database Password (example : postgres)");
			System.out.println(" - 5 : Database name (example : template_ontodb_continous)");
			System.out.println(" - 6 : OntoQL filename (example : d:/test/sample.ontoql)\n\n");

			throw new Exception("Parameter(s) is/are missing. Five parameters are attended, only " + args.length
					+ " parameter(s) is/are defined.");
		}

		new OntoQLConsole(args[0], args[1], args[2], args[3], args[4], args[5]);
	}
}
