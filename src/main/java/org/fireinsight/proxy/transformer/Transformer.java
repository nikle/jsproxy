package org.fireinsight.proxy.transformer;

import java.io.IOException;
import java.io.Reader;

public interface Transformer {
	public String transform(Reader r) throws IOException;
	//public String transform(String input) throws IOException;
}
