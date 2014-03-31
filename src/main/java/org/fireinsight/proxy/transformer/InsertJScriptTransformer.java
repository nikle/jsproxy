package org.fireinsight.proxy.transformer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

// ec2-23-20-34-25.compute-1.amazonaws.com
public class InsertJScriptTransformer implements Transformer {
	
	// private static String host = "http://184.73.167.93/zzv2/client-js/";	 
	private static String host = "http://www.cs.ubc.ca/~wohlstad/WebContent/";
	// 6c03c3724338e041428613752dd5d40a		

	// insert our own scripts
	protected static String insertedJs = generateInsertedJs(host);
	
	//@Override // r is response-body
	public String transform(Reader r) throws IOException {		
		// read the html file
		char[] buffer = new char[262144]; 
		int n = r.read(buffer);
		StringBuffer buf = new StringBuffer();
		while(n != -1) {
		  buf.append(buffer, 0, n);
		  n = r.read(buffer);
		}
		
		// allHTML is html_source of the webpage
		// parse & modify html file, insert scripts after <head>, 
		// may have to append-after <body> as back-up
		String allHTML = buf.toString();					
		//allHTML = allHTML.replaceAll("<html manifest=\"offline.appcache\">", "<html>");
		allHTML = allHTML.replace("http://72182.hittail.com/mlt.js", "http://www.ugrad.cs.ubc.ca/~k5r4/domtris/mlt.js");

		String lowerCase = allHTML.toLowerCase();
		/*replace jQuery
		String jquery = "https://ajax.googleapis.com/ajax/libs/jquery/1.4.4/jquery.min.js";
		int indexj = lowerCase.indexOf(jquery);
        if (indexj >= 0) {
        	String front	= allHTML.substring(0, indexj);
        	String ending	= allHTML.substring(indexj+jquery.length());
        	//allHTML = front +"http://ajax.googleapis.com/ajax/libs/jquery/1.4.4/jquery.js"+ ending;
        	allHTML = front +"http://ajax.googleapis.com/ajax/libs/jquery/1.4.4/jquery.min.js"+ ending;        	
        }*/

		// at first, try appending scripts just before </head>
		String pattern = "<head>";		
		int index0 = lowerCase.indexOf(pattern);

		// try appending after <head> first, then <body> if <head> does not work
		// a webpage that has frames has no body		
		if (index0 < 0) {
			pattern = "<body>";
			index0 = lowerCase.indexOf(pattern);		
		} 

		// web_page has neither <head> or <body>
		if (index0 < 0) {
		  //index0 = allHTML.toLowerCase().indexOf("</html>");		  
		}

		if (index0 >= 0) {
			index0 += pattern.length();
			String front = allHTML.substring(0, index0);
			String ending = allHTML.substring(index0);			
			String fnCounter = "";//"<script type='text/javascript'>if(! __xhr) { try {var __xhr = new XMLHttpRequest(); __xhr.open('GET', window.location.origin+'/notarealwebsiteurl', false); __xhr.send();} catch(e) {} }</script>";			
			allHTML = front +"\n"+ fnCounter +"\n"+ insertedJs +"\n"+ ending;
		}		
		
		//return allHTML;
        Reader htmlReader = new StringReader(allHTML);
		StringBuffer output = new StringBuffer();
		HTMLTransformer tx = new HTMLTransformer(htmlReader, output);		
		try {
			tx.run();
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		// temporary removes the mysterious ? that appears at the end of page
		// needs more in-depth investigation into HTMLTransformer.java
		String outputstr = output.toString();
		while ( outputstr.length() > 0 && ((int) outputstr.charAt(outputstr.length()-1)) == 65535) {
		  outputstr = outputstr.substring(0, outputstr.length()-1);		  
		}
		return outputstr;		
	}

	private static String generateInsertedJs(String host) {		
		String sources[] = getSources(host);		
		int i=0, l=sources.length;
		StringBuffer inserts = new StringBuffer();
		for (i=0; i < l; i++) {
			inserts.append(getScriptHTML(sources[i])); 	
		}
		return inserts.toString();		
	}
	
	private static String getScriptHTML(String source) {
	  StringBuffer script = new StringBuffer("<script type='text/javascript' src='");
	  script.append(source);
	  script.append("'></script>\n");
	  return script.toString();
	}	
	
	private static String[] getSources(String host) {
		
	  // Future: read srcs from a file, hard-coding for now
	  ArrayList<String> srcs = new ArrayList<String>();	  
	  /*
	   * srcs.add(host+"c/EventlistenersMirror.js"); // test-original
	   * srcs.add(host+"c/FunctionCallEntry.js");
	   * srcs.add(host+"c/EventlistenersMirror-on.js");
	   * return srcs.toArray(new String[srcs.size()]);
	   */
  
	  // firebug-lite	  
	  //srcs.add("https://getfirebug.com/firebug-lite.js#enableTrace=true,overrideConsole=false,startOpened=true");
	  //srcs.add("http://fbug.googlecode.com/svn/lite/branches/flexBox/content/firebug-lite-dev.js#enableTrace=true,overrideConsole=false,startOpened=true");
	  	  
	  // allocate object-space to the ZZ namespace	 
	  srcs.add(host+"z/"+"zzsetup.js");	  

	  // Server	  
	  srcs.add(host+"zzserver/ZZServer.js");
	  srcs.add(host+"zzserver/ZZServer-xhr.js");
	  srcs.add(host+"zzserver/ZZServer-save.js");
	  srcs.add(host+"zzserver/ZZServer-open.js");

	  // OMmeta, manage metadata inside each object
	  srcs.add(host+"ommeta/OMmeta.js");
	  
	  // ObjectsMirror, ObjectsArray is v2 of ObjectsMirror	  
	  srcs.add(host+"m/ObjectsArray.js"); 
	  srcs.add(host+"m/ObjectsArray-accessors.js");
	  srcs.add(host+"m/ObjectsArray-fse.js");
	  
	  // function closures
	  srcs.add(host+"closuremanager/ClosureManager.js");	  	  	
	  srcs.add(host+"omextensions/om-Function.js");	  	  
	  srcs.add(host+"closuremanager/ClosureManager-generate.js");
	  srcs.add(host+"closuremanager/ClosureManager-restore.js");	  

	  // DOMmirror	  
	  srcs.add(host+"v/DOMmirror.js");
	  srcs.add(host+"v/JsonML_DOM.js");
	  srcs.add(host+"v/JsonML2.js");
	  srcs.add(host+"v/DOMmirror-json.js");

	  // Event Handlers
	  srcs.add(host+"c/EventlistenersMirror.js");
	  srcs.add(host+"c/FunctionCallEntry.js");	  
	  srcs.add(host+"c/EventlistenersMirror-json.js");
	  srcs.add(host+"c/FunctionCallEntry-json.js");  
	  srcs.add(host+"c/EventlistenersMirror-on.js");
	  srcs.add(host+"c/DOMpropertiesMirror.js");
	 // srcs.add(host+"c/EventlistenersMirror-summarize.js");
	  srcs.add(host+"c/EventlistenersMirror-logs.js");		  

	  srcs.add(host+"m/ObjectsArray-json.js");
	  srcs.add(host+"m/ObjectsArray-restore.js");	  	  	  	  

	  // OMextensions, uses ZZOM_generateReference defined by ObjectsArray-json.js
	  srcs.add(host+"omextensions/om-Array.js");
	  srcs.add(host+"omextensions/om-ArrayBuffer.js");
	  srcs.add(host+"omextensions/om-AudioContext.js");
	  srcs.add(host+"omextensions/om-AudioBuffer.js");	  
	  srcs.add(host+"omextensions/om-CanvasPattern.js");
	  srcs.add(host+"omextensions/om-CanvasRenderingContext2D.js");	  
	  srcs.add(host+"omextensions/om-Date.js");  		  
	  srcs.add(host+"omextensions/om-RegExp.js");  
	  srcs.add(host+"omextensions/om-Window.js");
	  srcs.add(host+"omextensions/om-XMLHttpRequest.js");
	  
	  // Znapzhot data-structure
	  srcs.add(host+"z/Znapzhot.js");

	  // UI	 
	  srcs.add(host+"zzui/ZZuiBar.js");	  	 	  	 
	  	  
	  // main() function
	  srcs.add(host+"z/zznative.js");
	  srcs.add(host+"z/zznatree.js");
	  srcs.add(host+"z/Znapzhot.js");	  
	  srcs.add(host+"z/zzmain.js");	  	  
	  return srcs.toArray(new String[srcs.size()]);  
	}
}

