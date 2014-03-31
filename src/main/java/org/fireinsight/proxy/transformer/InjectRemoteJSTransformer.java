package org.fireinsight.proxy.transformer;

import java.io.IOException;
import java.io.StringReader;

import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;

import com.crawljax.core.AstInstrumenter;
import com.crawljax.core.JSASTModifier;
 
public class InjectRemoteJSTransformer {

	public String transform(String jscode){
		AstRoot ast =null;
	    /*initialize javascript context;*/
	    Context cx= Context.enter(); 	
	    Parser rhinoParser = new Parser(new CompilerEnvirons(),cx.getErrorReporter());	    	
	    try {
			ast = rhinoParser.parse(new StringReader(jscode), null, 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    	
	    JSASTModifier modifier = new AstInstrumenter();	
	    ast.visit(modifier);
	    	
	    //ast.visitAll(new printer());
	    //System.out.println(ast.getJsDoc());
	    return ast.toSource();

	}

}
