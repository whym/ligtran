package com.github.whym.ligature.servlet;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.text.*;
import javax.servlet.http.*;
import javax.servlet.*;
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

  public LigatureServlet() {
    this.maps = new HashMap<String,String>();
  }
  @Override public void init(ServletConfig config) throws ServletException {
    super.init(config);
    boolean reversed = false;
    if ( config.getInitParameter("reverse") != null ) {
      reversed = true;
    }
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("ligatures.txt"), "UTF-8"));
      StringBuffer buff = new StringBuffer("(");
      String line;
      while ( (line = reader.readLine()) != null ) {
        String[] s = line.split("\\s+");
        String k,v;
        if ( s.length >= 2 ) {
          if ( reversed ) {
            k = s[1];
            v = s[0];
          } else {
            k = s[0];
            v = s[1];
          }
          if ( this.maps.get(k) == null ) {
            this.maps.put(k, v);
            if ( buff.length() > 1 ) {
              buff.append("|");
            }
            buff.append(unicode_escape(k));
          }
        }
      }
      buff.append(")");
      //System.err.println(buff);//!
      this.patt = Pattern.compile(buff.toString(), Pattern.MULTILINE);
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
  }

  public CharSequence convert(CharSequence str) {
    StringBuffer buff = new StringBuffer();
    Matcher m = this.patt.matcher(str);
    boolean replaced = false;
    while ( m.find() ) {
      String rep = this.maps.get(m.group());
      if ( rep != null ) {
        m.appendReplacement(buff, rep);
        replaced = true;
      }
    }
    m.appendTail(buff);
    if ( replaced ) {
      return convert(buff);
    } else {
      return buff;
    }
  }

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    PrintWriter writer = resp.getWriter();
    String str = Normalizer.normalize(req.getParameter("str"), Normalizer.Form.NFC);
    CharSequence buff = convert(str);
    String redirect = req.getParameter("redirect_to");
    if ( redirect != null ) {
      System.err.println(redirect);//!
      redirect = String.format(redirect, java.net.URLEncoder.encode(buff.toString(), "UTF-8"));
      System.err.println(redirect);//!
      resp.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
      resp.setHeader("Location", redirect);
    } else {
      resp.setContentType("text/plain");
      resp.setCharacterEncoding("UTF-8");
      writer.printf("{\"result\": \"%s\"}", StringEscapeUtils.escapeJavaScript(buff.toString()));
    }
  }
}
