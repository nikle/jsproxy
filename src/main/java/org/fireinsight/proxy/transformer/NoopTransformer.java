package org.fireinsight.proxy.transformer;

import java.io.IOException;
import java.io.Reader;

public class NoopTransformer implements Transformer {

	//@Override
	public String transform(Reader r) throws IOException {
		char[] buffer = new char[1024];
		int n;
		StringBuffer buf = new StringBuffer();
		buf.append("//Transformed\n");
		while((n = r.read(buffer)) != -1) {
			buf.append(buffer, 0, n);
		}
		return buf.toString();
	}
}
