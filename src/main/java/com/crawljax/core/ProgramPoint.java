package com.crawljax.core;
/*
    Automatic JavaScript Invariants is a plugin for Crawljax that can be
    used to derive JavaScript invariants automatically and use them for
    regressions testing.
    Copyright (C) 2010  crawljax.com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
//package com.crawljax.plugins.aji.executiontracer;

import java.util.ArrayList;



/**
 * Representation of a Daikon program point.
 * 
 * @author Frank Groeneveld <frankgroeneveld@gmail.com>
 */
public class ProgramPoint {

	public static final String ENTERPOSTFIX = ":::ENTER";
	public static final String EXITPOSTFIX = ":::EXIT";
	public static final String POINTPOSTFIX = ":::POINT";



	/**
	 * Construct a new Daikon program point representation.
	 * 
	 * @param name
	 *            The name of the program point.
	 */


	/**
	 * Add a point in this program point (like exit, entry etc) if it doesn't exist yet.
	 * 
	 * @param prefix
	 *            Prefix to add.
	 */


	/**
	 * Add a variable declaration to this program point if it doesn't exist yet.
	 * 
	 * @param variable
	 *            The variable to add.
	 * @return The variable.
	 */


	/**
	 * @return The name.
	 */


	/**
	 * Output program point for all prefixes with variable declarations.
	 * 
	 * @return String containing Daikon variables declarations.
	 * @throws CrawljaxException
	 *             On undefined type.
	 */

	/**
	 * Returns a Daikon trace record for this program point.
	 * 
	 * @param postfix
	 *            Prefix (such as :::ENTER).
	 * @param data
	 *            Data to put in there.
	 * @return Record as a string.
	 * @throws CrawljaxException
	 *             When an unsupported type is encountered.
	 * @throws JSONException
	 *             On error.
	 */

}
