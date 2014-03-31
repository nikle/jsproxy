package org.fireinsight.proxy.transformer;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.mozilla.rhino.ast.AstNode;
import com.google.javascript.rhino.Node;

import edu.ubc.javascript.ASTFactory;
import edu.ubc.javascript.ReflectiveAstTransformer;

public class PrettyPrintingTransformer extends RhinoTransformerBase {

	@Override
	public void transform(ReflectiveAstTransformer tx, StringBuffer pw,
			Compiler compiler) throws Exception {
		Node node = compiler.getRoot().getLastChild();


		ASTFactory cg = new ASTFactory();
		AstNode ast = cg.add(node);
		String outputStr = ast.toSource();
		pw.append(outputStr);
	}
}
