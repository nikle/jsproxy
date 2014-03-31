package org.fireinsight.proxy.transformer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fireinsight.util.Props;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.ScriptTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;



public class InjectRemoteHTMLTransformer implements Transformer{
	private static String  host ="http://cheetah.cc.gt.atl.ga.us:8889/";
	
	protected static String insertedJs = generateInsertedJs(host);
	
	public String transform(Reader r) throws IOException {
		// TODO Auto-generated method stub
		char[] buffer = new char[262144]; 
		int n = r.read(buffer);
		StringBuffer buf = new StringBuffer();
		while(n != -1) {
		  buf.append(buffer, 0, n);
		  n = r.read(buffer);
		}
		
		String allHTML = buf.toString();
		String lowercase = allHTML.toLowerCase();
		//allHTML = allHTML.replaceAll("src=.*jquery.*.js\"","");
		
		String pattern = "<head>";
		int index = lowercase.indexOf(pattern);
		
		if(index<0){
			pattern ="<body>";
			index = lowercase.indexOf(pattern);
		}	

		if (index >= 0) {
			index += pattern.length();
			String front = allHTML.substring(0, index);
			String ending = allHTML.substring(index);			
			String fnCounter = "";
			//"<script type='text/javascript'>if(! __xhr) { try {var __xhr = new XMLHttpRequest(); __xhr.open('GET', window.location.origin+'/notarealwebsiteurl', false); __xhr.send();} catch(e) {} }</script>";			
			allHTML = front +"\n"+ fnCounter +"\n"+ insertedJs +"\n"+ ending;
		}
		
			
		try {
			Parser parser = Parser.createParser(allHTML,"UTF-8");
			NodeList handlerList =parser.parse(null);
			
			handlerList.visitAllNodesWith(new NodeVisitor(){
				public void visitTag(Tag tag){
					if(tag.getAttribute("onclick")!=null || tag.getAttribute("onClick")!=null){
						String fun= tag.getAttribute("onclick");
						fun = fun.trim();
						if(fun.length()!=0){
							String addFun = "Reanimator.util.event.captureDomEvents.captureDomEvents(event);"+
										    "var result = Reanimator.util.event.serialization.getTraversal(event.currentTarget);"+
										    "var el={name: event.currentTarget.nodeName,id: event.currentTarget.id,path:result,"+"fun:"+"\""+fun+"\""+"};"+
										    "var last =Reanimator.log.events.length-1;"+
										    "Reanimator.log.events[last].eventHandlerList.push(el);";
							fun = addFun+fun;
							tag.setAttribute("onclick", fun);							
							System.out.println(fun);
							
							
							/*fun = "function(){"+fun+"}";
							tag.removeAttribute("onclick");
							String id= null;
							if(tag.getAttribute("id")!=null){
								id = tag.getAttribute("id");
							}else{
								//Todo
								//generate global Id for this tag
							}
							ScriptTag jsTag = new ScriptTag();
							String js ="document.getElementById(\"" +id+"\")."+"addEventListener("+"\"click\""+","+fun+");";
							jsTag.setScriptCode(js);
							ScriptTag end =new ScriptTag();
							end.setTagName("/SCRIPT");
							jsTag.setEndTag(end);
							tag.getChildren().add(jsTag);*/
						}
					}
					/*
					if(tag instanceof ScriptTag){
						ScriptTag js =(ScriptTag)tag;
						//System.out.println("Line number is " + js.getStartingLineNumber() +"source code is " + js.getScriptCode());
						try {
							InjectRemoteJSTransformer tx =((Class<InjectRemoteJSTransformer>) Class.forName(Props.getProperty("TransformerRemoteJs"))).newInstance();
							String code = tx.transform(js.getScriptCode());
							js.setScriptCode(code);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}							
					}*/
				}
			});
			
			/*NodeList handlerList = parser.extractAllNodesThatMatch(new NodeFilter(){
				@Override
				public boolean accept(Node arg0) {
					// TODO Auto-generated method stub
					if(arg0 instanceof Tag){
						Tag tag =(Tag)arg0;
						if(tag.getAttribute("onclick")!=null || tag.getAttribute("onClick")!=null){
							String fun= tag.getAttribute("onclick");
							fun = "function(){"+fun+"}";
							tag.removeAttribute("onclick");
							String id= null;
							if(tag.getAttribute("id")!=null){
								id = tag.getAttribute("id");
							}else{
								//Todo
								//generate global Id for this tag
							}
							ScriptTag jsTag = new ScriptTag();
							String js ="document.getElementById(\"" +id+"\")."+"addEventListener("+"\"click\""+","+fun+");";
							jsTag.setScriptCode(js);
							ScriptTag end =new ScriptTag();
							end.setTagName("/SCRIPT");
							jsTag.setEndTag(end);
							tag.getChildren().add(jsTag);
						}
						if(tag instanceof ScriptTag){
							ScriptTag js =(ScriptTag)tag;
							try {
								InjectRemoteJSTransformer tx =((Class<InjectRemoteJSTransformer>) Class.forName(Props.getProperty("TransformerRemoteJs"))).newInstance();
								String code = tx.transform(js.getScriptCode());
								js.setScriptCode(code);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}							
						}
					}
					return true;
				}
				
			});*/

			allHTML = handlerList.toHtml();
			//System.out.println("html segment is \n" + allHTML);
		} catch (ParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//return allHTML;
        /*Reader htmlReader = new StringReader(allHTML);
		StringBuffer output = new StringBuffer();
		HTMLTransformer tx = new HTMLTransformer(htmlReader, output);		
		try {
			tx.run();
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}*/
		
		///String outputstr = output.toString();
		String outputstr = allHTML;
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

		  srcs.add(host+"public/js/"+ "reanimator.js");
		  srcs.add(host+"public/js/"+ "reanimator-jquery.1.8.3.js");
		  srcs.add(host+"public/js/"+ "instrumentor.js");
	  
		  return srcs.toArray(new String[srcs.size()]);  
		}
}
