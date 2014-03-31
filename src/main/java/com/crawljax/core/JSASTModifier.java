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


import java.util.HashMap;
import java.util.List;
import java.util.Map;



//import org.apache.log4j.Logger;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.Block;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.ReturnStatement;
import org.mozilla.javascript.ast.WhileLoop;


/**
 * Abstract class that is used to define the interface and some functionality for the NodeVisitors
 * that modify JavaScript.
 * 
 * @author Frank Groeneveld
 * @version $Id: JSASTModifier.java 6161 2009-12-16 13:47:15Z frank $
 */
public abstract class JSASTModifier implements NodeVisitor {

	private final Map<String, String> mapper = new HashMap<String, String>();

	/**
	 * This is used by the JavaScript node creation functions that follow.
	 */
	private CompilerEnvirons compilerEnvirons = new CompilerEnvirons();

	/**
	 * Contains the scopename of the AST we are visiting. Generally this will be the filename
	 */
	private String scopeName = null;

	/**
	 * @param scopeName
	 *            the scopeName to set
	 */
	public void setScopeName(String scopeName) {
		this.scopeName = scopeName;
	}

	/**
	 * @return the scopeName
	 */
	public String getScopeName() {
		return scopeName;
	}


	/**
	 * Parse some JavaScript to a simple AST.
	 * 
	 * @param code
	 *            The JavaScript source code to parse.
	 * @return The AST node.
	 */
	public AstNode parse(String code) {
		Parser p = new Parser(compilerEnvirons, null);
		return p.parse(code, null, 0);
	}

	/**
	 * Find out the function name of a certain node and return "anonymous" if it's an anonymous
	 * function.
	 * 
	 * @param f
	 *            The function node.
	 * @return The function name.
	 */
	protected String getFunctionName(FunctionNode f) {
		Name functionName = f.getFunctionName();

		if (functionName == null) {
			return "anonymous" + f.getLineno();
		} else {
			return functionName.toSource();
		}
	}

	/**
	 * Creates a node that can be inserted at a certain point in function.
	 * 
	 * @param function
	 *            The function that will enclose the node.
	 * @param postfix
	 *            The postfix function name (enter/exit).
	 * @param lineNo
	 *            Linenumber where the node will be inserted.
	 * @return The new node.
	 */
	protected abstract AstNode createNode(FunctionNode function, String postfix, int lineNo);

	/**
	 * Creates a node that can be inserted before and after a DOM modification statement (such as
	 * jQuery('#test').addClass('bla');).
	 * 
	 * @param shouldLog
	 *            The variable that should be logged (for example jQuery('#test').attr('style'))
	 * @param lineNo
	 *            The line number where this will be inserted.
	 * @return The new node.
	 */
	protected abstract AstNode createPointNode(String shouldLog, int lineNo);

	/**
	 * Actual visiting method.
	 * 
	 * @param node
	 *            The node that is currently visited.
	 * @return Whether to visit the children.
	 */
	@Override
	public boolean visit(AstNode node) {
		FunctionNode func;

		/*if (node instanceof FunctionNode) {
			func = (FunctionNode) node;

			//this is function enter 
			AstNode newNode = createNode(func, ProgramPoint.ENTERPOSTFIX, func.getLineno());

			func.getBody().addChildToFront(newNode);

			// get last line of the function 
			node = (AstNode) func.getBody().getLastChild();
			// if this is not a return statement, we need to add logging here also
			if (!(node instanceof ReturnStatement)) {
				newNode = createNode(func, ProgramPoint.EXITPOSTFIX, node.getLineno());
				//add as last statement
				func.getBody().addChildToBack(newNode);
			}

		} else if(node instanceof IfStatement){
			IfStatement ifnode = (IfStatement)node;
			InfixExpression es =(InfixExpression) ifnode.getCondition();
			String tcode = "console.log('condition " + es.toSource() + " is true');"; 
			ifnode.getThenPart().addChildrenToFront(parse(tcode));
			String fcode ="console.log('condition " + es.toSource() + " is false');";
			ifnode.getElsePart().addChildrenToFront(parse(fcode));
					
		} else */
			
		if (node instanceof Assignment){
			Assignment assNode =(Assignment)node;
			if(assNode.getLeft() instanceof PropertyGet){
				PropertyGet left = (PropertyGet)assNode.getLeft();
				if (left.getRight() instanceof Name){
					String proName= left.getRight().toSource();
					if(proName.equalsIgnoreCase("onclick")){
						FunctionNode funNode =(FunctionNode)(assNode.getRight());
						String addEventCode =left.getLeft().toSource()+".addEventListener("+"\"click\","+ funNode.toSource()+",false);";
						System.out.println(addEventCode);
						AstNode newNode = parse(addEventCode);
						//System.out.println(left.getLeft().toSource());
						assNode.getParent().getParent().replaceChild(assNode.getParent(), newNode);
						//System.out.println(assNode.getParent().getParent().toSource());					
					}
				}
			}			
		}
		
		/* have a look at the children of this node */
		return true;
	}

	private AstNode getLineNode(AstNode node) {
		while ((!(node instanceof ExpressionStatement) && !(node instanceof Assignment))
		        || node.getParent() instanceof ReturnStatement) {
			node = node.getParent();
		}
		return node;
	}

	/**
	 * This method is called when the complete AST has been traversed.
	 * 
	 * @param node
	 *            The AST root node.
	 */
	public abstract void finish(AstRoot node);

	/**
	 * This method is called before the AST is going to be traversed.
	 */
	public abstract void start();
}
