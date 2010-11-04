package com.github.whym.ligtran.servlet;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.text.*;
import javax.servlet.http.*;
import javax.servlet.*;
import org.apache.commons.lang.StringEscapeUtils;

public class LigatureServlet extends HttpServlet {
  private static final LigatureServlet reversedInstance = new LigatureServlet(true);
  private boolean reversed;
  private Map<String,String> maps;
  private Pattern patt;

  private static String unicode_escape(CharSequence s) {
    StringBuffer buff = new StringBuffer();
    for ( int i = 0; i < s.length(); ++i ) {
      buff.append(String.format("\\u%04x", (int)s.charAt(i)));
    }
    return buff.toString();
  }

  private static CharSequence reverse(CharSequence s) {
    return new StringBuffer(s).reverse();
  }

  public LigatureServlet() {
    this(false);
  }

  private LigatureServlet(boolean reverse) {
    _init(reverse);
  }

  @Override public void init(ServletConfig config) throws ServletException {
    super.init(config);
    boolean reversed = false;
    if ( config.getInitParameter("reverse") != null ) {
      reversed = true;
    }
    _init(reversed);
  }

  private void _init(boolean reversed) {
    this.reversed = reversed;
    this.maps = new HashMap<String,String>();
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("ligatures.txt"), "UTF-8"));
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
          if ( k.length() > 0  &&  this.maps.get(k) == null ) {
            this.maps.put(k, v);
          }
        }
      }
      List<String> ls = new ArrayList<String>(this.maps.keySet());
      Collections.sort(ls, new Comparator<String>(){
          public int compare(String a, String b) {
            if ( a.length() != b.length() ) {
              return b.length() - a.length();
            }
            return a.compareTo(b);
          }
          public boolean equals(String a, String b) {
            return compare(a, b) == 0;
          }
        });
      StringBuffer buff = new StringBuffer("(");
      for ( String k: ls) {
        if ( buff.length() > 1 ) {
          buff.append("|");
        }
        buff.append(unicode_escape(reverse(k)));
      }
      buff.append(")");
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
      String rep = reverse(this.maps.get(reverse(m.group()).toString())).toString();
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

    String str = req.getParameter("q");
    if ( str == null ) {
      str = req.getParameter("str"); // backward compatibility
    }
    if ( str == null ) {
      str = "";
    }
    String strk = Normalizer.normalize(str, Normalizer.Form.NFKC);
    str = Normalizer.normalize(str, Normalizer.Form.NFC);
    CharSequence buff = reverse(convert(reverse(str)));
    if ( !strk.equals(str) ) {
      CharSequence buff2 = reverse(convert(reverse(strk)));
      if ( (!reversed && buff2.length() < buff.length()) ||
           (reversed  && buff2.length() > buff.length()) ) {
        buff = buff2;
      }
    }
    if ( !reversed ) {
      CharSequence buff2 = reverse(convert(reversedInstance.convert(reverse(str))));
      if ( buff2.length() < buff.length()) {
        buff = buff2;
      }
    }

    // output
    String redirect = req.getParameter("redirect_to");
    if ( redirect != null ) {
      // behaivior for bookmarklet
      redirect = String.format(redirect, java.net.URLEncoder.encode(buff.toString(), "UTF-8"));
      resp.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
      resp.setHeader("Location", redirect);
    } else {
      String format = req.getParameter("format");
      if ( format == null ) {
        format = "json";
      }
      if ( format.equals("raw") ) {
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");
        writer.print(buff.toString());
      } else {
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");
        writer.printf("{\"result\": \"%s\"}", StringEscapeUtils.escapeJavaScript(buff.toString()));
      }
    }
  }
}
