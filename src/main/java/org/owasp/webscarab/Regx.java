package org.owasp.webscarab;

public class Regx {
	
	
	public static void main(String args[]){
		
		String s = "<script src=\"http://code.jquery.com/jquery-1.8.3.js\"></script>";
		s = s.replaceAll("src=.*jquery.*.js","src=\"");
		System.out.println(s);
		
	}
}