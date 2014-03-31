package org.fireinsight.proxy.transformer;

import java.util.LinkedList;
import java.util.List;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.jscomp.mozilla.rhino.ast.Assignment;
import com.google.javascript.jscomp.mozilla.rhino.ast.AstNode;
import com.google.javascript.jscomp.mozilla.rhino.ast.FunctionNode;
import com.google.javascript.rhino.Node;

import edu.ubc.javascript.ASTFactory;
import edu.ubc.javascript.BlockExpansionVisitor;
import edu.ubc.javascript.ClosureCheck;
import edu.ubc.javascript.FunctionNodeClosureInfoVisitor;
import edu.ubc.javascript.ReadClosuresVisitor;
import edu.ubc.javascript.ReflectiveAstTransformer;
import edu.ubc.javascript.ScopeVisitor;
import edu.ubc.webscarab.MyProxyPlugin;

public class ClosureTransformer extends RhinoTransformerBase {

	@Override
	public void transform(ReflectiveAstTransformer tx, StringBuffer pw, Compiler compiler) throws Exception {
		long start = System.currentTimeMillis();
		Node node = compiler.getRoot().getLastChild();
	
		ScopeVisitor sv = new ScopeVisitor();
		NodeTraversal traversal = new NodeTraversal(compiler, sv);
		traversal.traverse(compiler.getRoot().getLastChild());
		long stop = System.currentTimeMillis();
		if(MyProxyPlugin.PROFILE) {
			System.out.println("ScopeVisitor: " + (stop-start));
		}
				
		start = System.currentTimeMillis();
		ClosureCheck cc = new ClosureCheck(sv);
		traversal = new NodeTraversal(compiler, cc);
		traversal.traverse(compiler.getRoot().getLastChild());
		stop = System.currentTimeMillis();
		if(MyProxyPlugin.PROFILE) {
			System.out.println("ClosureCheck: " + (stop-start));
		}

		start = System.currentTimeMillis();
		ASTFactory cg = new ASTFactory();
		AstNode ast = cg.add(node);
		stop = System.currentTimeMillis();
		if(MyProxyPlugin.PROFILE) {
			System.out.println("ASTFactory: " + (stop-start));
		}
	
		start = System.currentTimeMillis();
		FunctionNodeClosureInfoVisitor fnciv = new FunctionNodeClosureInfoVisitor(tx);
		ast.visit(fnciv);
		stop = System.currentTimeMillis();
		if(MyProxyPlugin.PROFILE) {
			System.out.println("FunctionNodeClosureInfo: " + (stop-start));
		}
		
		start = System.currentTimeMillis();
		BlockExpansionVisitor bb = new BlockExpansionVisitor(ast, tx);
		bb.visit(ast);
		tx.commit();
		stop = System.currentTimeMillis();
		if(MyProxyPlugin.PROFILE) {
			System.out.println("BlockExpansionVisitor: " + (stop-start));
		}
		
		start = System.currentTimeMillis();
		ReadClosuresVisitor osv = new ReadClosuresVisitor(fnciv, tx);
		ast.visit(osv);
		tx.commit();
		stop = System.currentTimeMillis();
		if(MyProxyPlugin.PROFILE) {
			System.out.println("ReadClosuresVisitor: " + (stop-start));
		}
		
//		start = System.currentTimeMillis();
//		List<String> names = new LinkedList<String>();
//		for(FunctionNode fn : osv.innerFunctions) {
//			Assignment assign = ASTFactory.assign(ASTFactory.name("__zz__.funIndex['" + fn.getName() + "']"), fn);
//			tx.insertAfter(ast, ast.getLastChild(), ASTFactory.exprStmt(assign));
//			names.add(fn.getName());
//		}
//		tx.commit();
//		stop = System.currentTimeMillis();
//		if(MyProxyPlugin.PROFILE) {
//			System.out.println("GenFunIndex: " + (stop-start));
//		}
		
		start = System.currentTimeMillis();
		String outputStr = ast.toSource();
		stop = System.currentTimeMillis();
		if(MyProxyPlugin.PROFILE) {
			System.out.println("toSource: " + (stop-start));
		}
		pw.append(outputStr);
	}
}
