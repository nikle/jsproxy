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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Scope;
import org.mozilla.javascript.ast.Symbol;


/**
 * This class is used to visit all JS nodes. When a node matches a certain condition, this class
 * will add instrumentation code near this code.
 * 
 * @author Frank Groeneveld
 * @version $Id: AstInstrumenter.java 6162 2009-12-16 13:56:21Z frank $
 */
public class AstInstrumenter extends JSASTModifier {

	public static final String JSINSTRUMENTLOGNAME = "window.jsExecutionTrace";

	/**
	 * List with regular expressions of variables that should not be instrumented.
	 */
	private List<String> excludeVariableNamesList = new ArrayList<String>();

	private boolean domModifications = false;

	public AstInstrumenter() {
		super();
		excludeVariableNamesList = new ArrayList<String>();
	}

	public AstInstrumenter(List<String> excludes) {
		excludeVariableNamesList = excludes;
	}


	@Override
	protected AstNode createNode(FunctionNode function, String postfix, int lineNo) {
		String name;
		String code=null;
		//String[] variables = getVariablesNamesInScope(function);

		name = getFunctionName(function);
		if(postfix == ProgramPoint.ENTERPOSTFIX){
			code = "console.log('function " + name + " is entering');"; 
		}
		if (postfix == ProgramPoint.EXITPOSTFIX) {
			//postfix += lineNo;
			code = "console.log('function " + name + " is exiting');";
		}
		
		return parse(code);
	}

	@Override
	public void start() {
		/* nothing to do here */
	}

	private boolean shouldInstrumentDOMModifications() {
		return domModifications;
	}

	/**
	 * Add instrumentation to dynamic DOM modifications. (Still buggy)
	 */
	public void instrumentDOMModifications() {
		domModifications = true;
	}

	@Override
	protected AstNode createPointNode(String shouldLog, int lineNo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void finish(AstRoot node) {
		// TODO Auto-generated method stub
		
	}
}
