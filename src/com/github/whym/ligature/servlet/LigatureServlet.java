package com.github.whym.ligature.servlet;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.text.*;
import javax.servlet.http.*;
import org.apache.commons.lang.StringEscapeUtils;

public class LigatureServlet extends HttpServlet {
  private Map<String,String> maps;
  private Pattern patt;

  private static String unicode_escape(String s) {
    StringBuffer buff = new StringBuffer();
    for ( char c: s.toCharArray() ) {
      buff.append(String.format("\\u%04x", (int)c));
    }
    return buff.toString();
  }

  public LigatureServlet() throws IOException {
    this.maps = new HashMap<String,String>();
    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("ligatures.txt"), "UTF-8"));
    StringBuffer buff = new StringBuffer("(");
    String line;
    while ( (line = reader.readLine()) != null ) {
      String[] s = line.split("\\s+");
      if ( s.length >= 2 ) {
        if ( this.maps.get(s[0]) == null ) {
          this.maps.put(s[0],s[1]);
          if ( buff.length() > 1 ) {
            buff.append("|");
          }
          buff.append(unicode_escape(s[0]));
        }
      }
    }
    buff.append(")");
    //System.err.println(buff);//!
    this.patt = Pattern.compile(buff.toString());
  }

  public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws IOException {
    resp.setContentType("text/plain");
    resp.setCharacterEncoding("UTF-8");
    PrintWriter writer = resp.getWriter();
    String str = Normalizer.normalize(req.getParameterMap().get("str")[0], Normalizer.Form.NFC);
    StringBuffer buff = new StringBuffer();
    Matcher m = this.patt.matcher(str);
    while ( m.find() ) {
      String rep = this.maps.get(m.group());
      if ( rep != null ) {
        m.appendReplacement(buff, rep);
      }
    }
    m.appendTail(buff);
    writer.printf("{\"return\": \"%s\"}", buff); // TODO: escape using StringEscapeUtils
  }
}
