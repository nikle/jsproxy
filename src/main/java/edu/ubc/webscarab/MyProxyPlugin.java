package edu.ubc.webscarab;

/*  
 *  package explorer /src/edu.ubc.webscarab/
 *  folder directory /src/edu/ubc/webscarab/
 */

import java.io.IOException;
import java.io.StringReader;

import org.fireinsight.proxy.transformer.Transformer;
import org.fireinsight.util.Props;
import org.owasp.webscarab.httpclient.HTTPClient;
import org.owasp.webscarab.model.HttpUrl;
import org.owasp.webscarab.model.Request;
import org.owasp.webscarab.model.Response;
import org.owasp.webscarab.plugin.proxy.ProxyPlugin;

public class MyProxyPlugin extends ProxyPlugin {

	public static boolean PROFILE = true;
	
	@Override
	public String getPluginName() {
		return "MyProxyPlugin";
	}

	@Override
	public HTTPClient getProxyPlugin(HTTPClient in) {
		return new Plugin(in);
	}
    
	private class Plugin implements HTTPClient {

		private HTTPClient in;
		
		public Plugin(HTTPClient in) {
			this.in = in;
		}
		
		//@Override 
		@SuppressWarnings("unchecked")
		public Response fetchResponse(Request request) throws IOException {
			HttpUrl url = request.getURL();
			String href = "";
			if (url != null) {
				href = url.toString().toLowerCase()	;
			}
			
			if (href.contains("notarealwebsiteurl")) {		
			    //ReadClosuresVisitor.reset();
			    return new Response();
			}			
			long startFetch = System.currentTimeMillis();
			Response response = in.fetchResponse(request);
			long stopFetch = System.currentTimeMillis();
			
			
			if (href.contains("soundfont-ogg.js")) {
			  return response;				
			}
			
			//request is to retrieve the javascript files
			if (href.contains(".js") == true) {
				response.setHeader("Content-Type", "application/javascript");
			}
						
			if(response != null) {
				String cType = response.getHeader("Content-Type");
				if (cType != null) { 
					cType = cType.toLowerCase();  
					String charset = "UTF-8";//(cType.toLowerCase().contains("utf-8")) ? "UTF-8" : "ISO-8859-1";
					try {
						if (cType.contains("javascript") || cType.contains("text/x-js")) {
							// return response; // test-original
							if (href.contains("wohlstad") || href.contains("firebug-lite") || href.contains("fbug.googlecode.com")) {
								return response;
							}

							modifyResponse(href, response, charset, ( (Class<Transformer>) Class.forName(Props.getProperty("TransformerClassJs")) ).newInstance());

							
						} 
						else if (cType.contains("html")) {							
							if (href.contains("wohlstad") && href.contains("openZZ")) {
							    return response;
							}							
							modifyResponse(href, response, charset, ( (Class<Transformer>) Class.forName(Props.getProperty("TransformerClassHTML")) ).newInstance());
						}
					} 
					catch(Exception e) {
						throw new IOException(e);
					}
				}
			}
			
			return response;
		}
		
		private void modifyResponse(String href, Response response, String charset, Transformer tx) throws IOException {
			
			//response.setContent( tx.transform(new InputStreamReader(new ByteArrayInputStream(response.getContent()), charset)).getBytes(charset) );
			String data = new String(response.getContent(), charset);
			//if (href.indexOf("genoverse")>=0) {
			  data = data.replaceAll("\\.new", ".new2");
			  data = data.replaceAll("new\\s+:", "new2 :");
			
			  data = data.replaceAll("\\.default;"	, ".default2;");
			  data = data.replaceAll("\\.default\\."	, ".default2.");
			  data = data.replaceAll("\\.default\\s+"	, ".default2");
			
			  data = data.replaceAll("\\s+default:"	, "default2 :");
			  data = data.replaceAll("\\s+default\\s+:", "default2 :");
			//}
			
			data = data.replaceAll(",\\s+}", "}");				
			
			data = data.replaceAll("https://", "http://");
			data = data.replaceAll(",\\s+]", "]");			
		
			response.setContent( tx.transform(new StringReader(data)).getBytes(charset) );

			String output = tx.transform(new StringReader(data)).replaceAll("0.0 === self.FileError", "void 0 === self.FileError");
			response.setContent( output.getBytes(charset) );
			
            return;			
		}
	}
}
