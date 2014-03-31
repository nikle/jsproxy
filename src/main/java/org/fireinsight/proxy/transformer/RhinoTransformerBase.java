package org.fireinsight.proxy.transformer;

import java.io.IOException;
import java.io.Reader;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSSourceFile;

import edu.ubc.javascript.ReflectiveAstTransformer;
import edu.ubc.webscarab.MyProxyPlugin;

public abstract class RhinoTransformerBase implements Transformer {
	
	public abstract void transform(ReflectiveAstTransformer tx, StringBuffer pw, Compiler compiler) throws Exception;
		
	public String transform(Reader r) throws IOException {
		long start = System.currentTimeMillis();
		ReflectiveAstTransformer tx = new ReflectiveAstTransformer();
		Compiler compiler = new Compiler(System.err);
		JSSourceFile[] externs = new JSSourceFile[0];
		JSSourceFile[] inputs = {new JSSourceFile(JSSourceFile.fromReader("(?)", r))};
		CompilerOptions options = new CompilerOptions();
		compiler.compile(externs, inputs, options);
		StringBuffer pw = new StringBuffer();
		long stop = System.currentTimeMillis();
		if(MyProxyPlugin.PROFILE) {
			System.out.println("Rhino: " + (stop-start));
		}
		/*BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(Props.getProperty("Script"))));
		String line = br.readLine();
		while(line != null) {
			pw.append(line + "\n");
			line = br.readLine();
		}*/
		
		try {
			transform(tx, pw, compiler);
		} catch(Exception e) {
			
		}
		
		return pw.toString();
	}
}
