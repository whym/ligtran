<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%
   String str = request.getParameter("q");
   if ( str == null ) { str = ""; }
   String snippet = str.length() < 24 ? str : str.substring(0, Math.min(22, str.length())) + "...";
   boolean reverse = request.getParameter("reverse") != null && !request.getParameter("reverse").equals("false");
   String queryURL = "/ligature" + (reverse?"/reverse":"") + "?format=raw&q=" + java.net.URLEncoder.encode(str, "UTF-8");
 %>
<%
   String thisURL = request.getServletPath().toString() + "?" + request.getQueryString();
   thisURL = thisURL.replace("index.jsp", "");
   if ( request.getParameter("reverse") != null ) {
     if ( thisURL.contains("reverse=") ) {
     String url = thisURL.replaceAll("(\\?)?(&)?([a-z]+)=false(&)?", "$1$4").
                          replaceAll("(\\?)?(&)?([a-z]+)=true(&)?", "$1$2$3$4");
       response.sendRedirect(url);
     }
   }
 %>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="ja" lang="ja">
<head>
<%@page contentType="text/html;charset=UTF-8"%>
<meta http-equiv="content-style-type" content="text/css" />
<meta http-equiv="content-script-type" content="text/javascript" />
<meta name="description" content="text conversion by composing &amp; decomposing ligatures" />
<meta name="keywords" content="ligtran,ligature,text-conversion,transliteration" />
<meta name="robots" content="INDEX,FOLLOW" />
<link rev="made" href="http://twitter.com/whym" />
<link rel="INDEX" href="." />
<title>ligtran<%= str.equals("") ? "": " - " + snippet%></title>

<script type="text/javascript" src="http://www.mochikit.com/packed/MochiKit/MochiKit.js"></script>
<script type="text/javascript">
function get_time() {
  return new Date().getTime();
}
function convert(path, from, to, fromc, toc) {
  $('busy').style.visibility = 'visible';
  $('busy').style.top = '1em';
  loadJSONDoc(path, {'q': $(from).value}).addCallback(function(ret){
    $(to).value = ret['result'];
  }).setFinalizer(function(){
    count(from, fromc); count(to, toc);
    changes.updateTime = get_time();
    $('busy').style.visibility = 'hidden';
    permalink.q = $(from).value;
    permalink.rev = !$('short').checked;
    $('permalink').href = $('permalinkbox').value = permalink.get();
    $(to).className = $('short').checked ? 'short' : 'long';
  })
}
function copy_all(path){
  var text_val=$(path);
  text_val.focus();
  text_val.select();
  if (!document.all) return; // IE only
  r = text_val.createTextRange();
  r.execCommand('copy');
}
function count(path, pathc) {
  var s = $(path).value;
  $(pathc).innerHTML = s.length;
}
function get_servlet() {
  return $('short').checked ? 'ligature' : 'ligature/reverse';
}

var changes = {'enterTime': null, 'updateTime': null };
var permalink = {'link': document.location.href.split('?')[0],
                 'q':    '',
                 'rev':  false,
                 'get':  function() { return this.link + '?' + (this.rev?'reverse&':'') + 'q=' + encodeURIComponent(this.q); } };
</script>
<style type="text/css">
#busy { visibility: hidden; position: fixed; right: 1em; top: -100%; }
h1 { text-align: center; }
body { background-color: #EEE; color: #444; }
#body { margin: 3em auto; }
#body { max-width: 60%; }
.short { background: #EEC; border-color: #E86; }
.long  { background: #DDE; border-color: #68E; }
label.short, label.long { border-bottom-style: solid; background-color: transparent; padding: 0 2em 0 1em; margin: 0 0.6em; }
.counter { display: inline-block; width: 6%; font-size: 200%; text-align: right; vertical-align: top; }
textarea { display: inline-block; width: 78%; margin: 0 0 0 10%; height: auto; font-size: 180%; padding: 0.3em; border: 3px solid #777; }
.box p { text-align: center; }
.ligmode { text-align: center; font-size: 150%; width: 100%; padding: 0.5em 0; background: url("arrow.png") no-repeat center; }
.ligmode label { display: inline-block; padding: 0 1.5em 0 0.8em; }
.description { font: normal normal 90% serif; width: 100%; text-align: center; }
.permalinkline { text-align: right; margin-bottom: 2em; }
#permalinkbox { font-family: monospace; }
h1 a img {border: none;}
input[type=submit] { display: block; margin: 0 auto; font-size:130%; width: 12em; }
</style>
</head>
<body>
<h1><a href="."><img src="ligtran_logo.png" alt="ligtran" /></a></h1>
<p class="description">文字と文字をくっつけたり離したりすることでテキストを短くしたり長くするウェブサービス</p>

<form action="/" method="get">
<div id="body">

<div class="box">
<p>↓ここにテキストを入力してください。</p>
<textarea rows="4" cols="20" id="edit" name="q" onmouseup="convert(get_servlet(), 'edit', 'result', 'editc', 'resultc');" onkeyup="changes.enterTime=get_time();" onfocus="this.onmouseup();">
<%=str%></textarea>
<label class="counter" id="editc" for="edit"><%=str.length()%></label>
</div>

<div class="ligmode">
<label class="short" for="short">
<input type="radio" name="reverse" value="false" id="short" <%=reverse?"":"checked=\"checked\""%> onclick="$('edit').onmouseup();" onkeypress="this.onclick();" />
短くする
</label>
<label class="long"  for="long">
<input type="radio" name="reverse" value="true" id="long"  <%=reverse?"checked=\"checked\"":""%> onclick="$('edit').onmouseup();" onkeypress="this.onclick();" />
長くする</label>
</div>

<div class="box">
<textarea rows="4" cols="20" id="result" readonly="readonly" onfocus="copy_all('result');"><jsp:include page="<%=queryURL%>" /></textarea>
<label class="counter" id="resultc" for="edit">0</label>
</div>

<noscript>
<input type="submit" />
</noscript>
<script type="text/javascript">
if(typeof (MochiKit)=="undefined"){
  document.writeln('<input type="submit" />');
}
</script>
</div>
</form>

<p class="permalinkline">
<a id="permalink" name="permalink" href="<%=thisURL%>">この結果へのリンク</a>:
<input id="permalinkbox" type="text" size="50" readonly="readonly" onfocus="copy_all('permalinkbox');" value="" />
</p>

<p id="busy"><img src="bigrotation2.gif" alt="busy" /></p>

<!-- 
<input type="text" id="enter"/>
<input type="text" id="update"/>
<input type="text" id="busyd"/>
 -->
<h2>使い方</h2>
<ol>
<li> 一つ目の入力欄にテキストを入力します。</li>
<li>「短くする」か「長くする」を選びます。</li>
<li> 変換された結果が二つ目の欄に表示されます。</li>
</ol>

<h2>Twitter用ブックマークレット</h2>
<dl>
<dt><a href="javascript:location.href='http://ligtran.appspot.com/ligature?redirect_to='+encodeURIComponent(location.href.replace(/\?status=[^&amp;\/]*&amp;?/,'')+'?status=%s')+'&amp;q='+encodeURIComponent(document.getElementById('status').value);void(0);">ligtran - 短くする</a></dt>
<dd>このリンク（<a href="javascript:location.href='http://ligtran.appspot.com/ligature?redirect_to='+encodeURIComponent(location.href.replace(/\?status=[^&amp;\/]*&amp;?/,'')+'?status=%s')+'&amp;q='+encodeURIComponent(document.getElementById('status').value);void(0);">ligtran - 短くする</a>）をブックマークバーにドラッグして入れてください。 <a href="http://twitter.com/">Twitterの画面</a>でそれを押すと、編集欄の内容が変換されます。</dd>
<dt><a href="javascript:location.href='http://ligtran.appspot.com/ligature/reverse?redirect_to='+encodeURIComponent(location.href.replace(/\?status=[^&amp;\/]*&amp;?/,'')+'?status=%s')+'&amp;q='+encodeURIComponent(document.getElementById('status').value);void(0);">ligtran - 長くする</a></dt>
<dd>同上</dd>
</dl>

<h2>説明</h2>
<p><a href="http://github.com/whym/ligtran#readme">詳しい説明とソースコードはこちら</a>です。</p>

<h2>バグ</h2>
<ul>
<li>高速に入力すると変換結果がおかしくなる（入力欄をクリックすると直ります）</li>
</ul>

<h2>クレジット</h2>
<ul>
<li><a href="http://www.mochikit.com/">MochiKit</a></li>
<li><a href="http://www.twitlogo.com/ligtran/90/33CCFF/FFFFFF/">The Twitter Logo Generator</a></li>
<li><a href="http://www.sanbaldo.com/wordpress/1/ajax_gif/">Ajax loading animated gif by Sanbaldo</a></li>
</ul>


<address>
運営 <a href="http://twitter.com/whym">@whym</a>
</address>
<script type="text/javascript">
window.onload = function(){
  if ( /q=.+/.exec(location.href) ) {
    $('edit').onmouseup();
  }
  changes.enterTime = changes.updateTime = get_time();
  $('edit').focus();
  $('busy').style.visibility = 'hidden';
}
window.setInterval(function(){
  //$('enter').value = changes.enterTime + ', ' + get_time();
  //$('update').value = changes.updateTime + ', ' + get_time();
  //$('busyd').value = $('busy').style.visibility;
  if ( !changes.enterTime || !changes.updateTime ) {
     window.onload();
  }
  if ( $('busy').style.visibility == 'hidden' && changes.updateTime <= changes.enterTime && get_time() - changes.updateTime >= 150 ) {
     document.getElementById('edit').onmouseup();
  }
}, 50);
</script>
</body>
</html>
