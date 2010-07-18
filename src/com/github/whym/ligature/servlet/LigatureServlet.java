package com.github.whym.ligature.servlet;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import javax.servlet.http.*;

public class LigatureServlet extends HttpServlet {
  private Map<String,String> maps;
  private Pattern patt;

  public LigatureServlet() throws IOException {
    this.maps = new HashMap<String,String>();
    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("ligatures.txt"), "UTF-8"));
    StringBuffer buff = new StringBuffer("(");
    String line;
    while ( (line = reader.readLine()) != null ) {
      String[] s = line.split("\\s+");
      System.err.println(Arrays.toString(s));//!
      if ( s.length >= 2 ) {
        this.maps.put(s[0],s[1]);
        if ( buff.length() > 1 ) {
          buff.append("|");
        }
        buff.append(s[0]);
      }
    }
    buff.append(")");
    this.patt = Pattern.compile(buff.toString());
  }

  public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws IOException {
    resp.setContentType("text/plain");
    resp.setCharacterEncoding("UTF-8");
    PrintWriter writer = resp.getWriter();
    String str = req.getParameterMap().get("str")[0];
    StringBuffer buff = new StringBuffer();
    Matcher m = this.patt.matcher(str);
    while ( m.find() ) {
      String rep = this.maps.get(m.group());
      System.err.println(m.group() + rep);//!
      if ( rep != null ) {
        m.appendReplacement(buff, rep);
      }
    }
    m.appendTail(buff);
    writer.printf("{return: \"%s\"}", buff); // TODO: escape using StringEscapeUtils
  }
}
