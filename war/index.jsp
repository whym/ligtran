<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<% String str = request.getParameter("q"); %>
<% if ( str == null ) { str = ""; } %>
<% String snippet = str.length() < 24 ? str : str.substring(0, Math.min(22, str.length())) + "..."; %>
<% boolean reverse = request.getParameter("reverse") != null; %>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="ja" lang="ja">
<head>
<%@page contentType="text/html;charset=UTF-8"%>
<meta http-equiv="content-style-type" content="text/css" />
<meta http-equiv="content-script-type" content="text/javascript" />
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
#body { max-width: 60%; margin: 4em auto; }
textarea { width: 100%; height: 4em; font-size: 180%; padding: 0.3em; }
.counter { display: block; position: relative; top: 1em; left: 2.5em; font-size: 200%; text-align: right; vertical-align: top; }
.ligmode { text-align: center; margin: 2em 0 0 0; font-size: 150%; }
.ligmode input[type=text] { margin: 0 1em; }
.ligmode label { display: inline-block; padding: 0 1.5em 0 0.5; }
.description { font: normal normal 90% serif; width: 100%; text-align: center; }
#permalinkbox { font-family: monospace; }
</style>
</head>
<body>
<h1><img src="ligtran_logo.png" alt="ligtran" /></h1>
<p class="description">文字と文字をくっつけたり離したりすることでテキストを短くしたり長くするウェブサービス</p>

<div id="body">

<form action="/" class="ligmode">
<input type="radio" name="ligmode" id="short" <%=reverse?"":"checked=\"checked\""%> onclick="$('edit').onmouseup();" onkeypress="this.onclick();" />
<label for="short">短く</label>
<input type="radio" name="ligmode" id="long"  <%=reverse?"checked=\"checked\"":""%> onclick="$('edit').onmouseup();" onkeypress="this.onclick();" />
<label for="long">長く</label>
</form>

<div class="box">
<label class="counter" id="editc" for="edit"><%=str.length()%></label>
<textarea rows="4" cols="20" id="edit" onmouseup="convert(get_servlet(), 'edit', 'result', 'editc', 'resultc');" onkeyup="changes.enterTime=get_time();" onfocus="this.onmouseup();" name="q">
<%=str%></textarea>
</div>

<div class="box">
<label class="counter" id="resultc" for="edit">0</label>
<textarea rows="4" cols="20" id="result" readonly="readonly" onfocus="copy_all('result');">
</textarea>
</div>

<p><a id="permalink" href="">この結果へのリンク</a>: <input id="permalinkbox" type="text" size="60" readonly="readonly" onfocus="copy_all('permalinkbox');" value="" /></p>

</div>

<p id="busy"><img src="bigrotation2.gif" alt="busy" /></p>

<!-- 
<input type="text" id="enter"/>
<input type="text" id="update"/>
<input type="text" id="busyd"/>
 -->
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
