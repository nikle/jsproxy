package edu.ubc.webscarab;

import java.io.IOException;
import java.io.StringReader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import oracle.xml.parser.v2.DOMParser;
import oracle.xml.parser.v2.XMLDocument;



import oracle.xml.parser.v2.XMLElement;






//import org.cyberneko.html.parsers.DOMParser;
import org.fireinsight.proxy.transformer.Transformer;
import org.fireinsight.util.Props;
import org.owasp.webscarab.httpclient.HTTPClient;
import org.owasp.webscarab.model.HttpUrl;
import org.owasp.webscarab.model.Request;
import org.owasp.webscarab.model.Response;
import org.owasp.webscarab.plugin.proxy.ProxyPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.xml.sax.InputSource;

//import com.crawljax.util.DomUtils;

public class RemoteDebugPlugin extends ProxyPlugin {

	@Override
	public String getPluginName() {
		// TODO Auto-generated method stub
		return "RemoteDebugPlugin";
	}

	@Override
	public HTTPClient getProxyPlugin(HTTPClient in) {
		// TODO Auto-generated method stub
		return new Plugin(in);
	}
	
	public class eventlistener implements EventListener
	{
	   public String func;
	   public eventlistener(String fun){func=fun;}
	   public void handleEvent(Event e)
	   {
		  ScriptEngineManager factory = new ScriptEngineManager();
		  ScriptEngine engine = factory.getEngineByName("JavaScript");
		  try {
			engine.eval(func);
		  } catch (ScriptException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		  }
	    }
	 }
	
	private class Plugin implements HTTPClient{
		
		private HTTPClient in;
		
		public Plugin(HTTPClient in){
			this.in = in;
		}

		@Override
		public Response fetchResponse(Request request) throws IOException {
			// TODO Auto-generated method stub
			HttpUrl url = request.getURL();
			String href = "";
			if (url != null) {
				href = url.toString().toLowerCase();
			}
			
			long startFetch = System.currentTimeMillis();
			Response response = in.fetchResponse(request);
			long stopFetch = System.currentTimeMillis();
			
			if (href.contains(".js") == true) {
				response.setHeader("Content-Type", "application/javascript");
			}
			
			// handle the event registration through element attribute
			if(response != null) {
				String cType = response.getHeader("Content-Type");
				if (cType != null) { 
					cType = cType.toLowerCase();  
					String charset = "UTF-8";
					try {
						if (cType.contains("html")) {		
							modifyResponse(href, response, charset, ((Class<Transformer>) Class.forName(Props.getProperty("TransformerRemoteHTML"))).newInstance());
						}
						
						if(cType.contains("javascript")){
							//parse javascript
							//modifyResponse(href, response, charset, ( (Class<Transformer>) Class.forName(Props.getProperty("TransformerRemoteJs"))).newInstance());
							
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
			data = data.replaceAll(",\\s+}", "}");				
			
			data = data.replaceAll("https://", "http://");
			data = data.replaceAll(",\\s+]", "]");
			
			String finalhtml= tx.transform(new StringReader(data));
			//System.out.println(finalhtml);
		
			response.setContent(finalhtml.getBytes(charset) );

			String output = tx.transform(new StringReader(data)).replaceAll("0.0 === self.FileError", "void 0 === self.FileError");
			response.setContent( output.getBytes(charset) );
			
            return;			
		}
		
	}

}
