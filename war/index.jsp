<% String str = request.getParameter("q"); %>
<% if ( str == null ) { str = ""; } %>
<% boolean reverse = request.getParameter("reverse") != null; %>
<!-- -->
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="ja" lang="ja">
<head>
<%@page contentType="text/html;charset=UTF-8"%>
<meta http-equiv="content-style-type" content="text/css" />
<meta http-equiv="content-script-type" content="text/javascript" />
<title>ligtran</title>

<script type="text/javascript" src="http://www.mochikit.com/packed/MochiKit/MochiKit.js"></script>
<script type="text/javascript">
function convert(path, from, to, fromc, toc) {
  var old = $(to).value;
  loadJSONDoc(path, {'q': $(from).value}).addCallback(function(ret){
    $(to).value = ret['result'] + $(to).value.slice(old.length);
  }).addCallback(function(){
    count(from, fromc); count(to, toc);
  });
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
</script>
<style type="text/css">
h1 { text-align: center; }
body { max-width: 50%; margin: auto; background-color: #EEE; color: #444; }
textarea { width: 100%; height: 6em; font-size: 150%; padding: 0.3em; }
.counter { display: block; position: relative; top: 1em; left: 2.5em; font-size: 200%; text-align: right; vertical-align: top; }
.ligmode { text-align: center; margin: 2em 0 0 0; font-size: 150%; }
.ligmode input { margin: 0 1em; }
.ligmode label { display: inline-block; padding: 0 1.5em 0 0.5; }
.description { font: normal normal 90% serif; }
</style>
</head>
<body>
<h1><img src="ligtran_logo.png" alt="ligtran" /></h1>
<p class="description">文字と文字をくっつけたり離したりすることでテキストを短くしたり長くするウェブサービス</p>

<form action="" class="ligmode">
  <input type="radio" name="ligmode" id="short" <%=reverse?"":"checked=\"checked\""%> onclick="$('edit').onkeyup();" onkeypress="this.onclick();" />
<label for="short">短く</label>
  <input type="radio" name="ligmode" id="long"  <%=reverse?"checked=\"checked\"":""%> onclick="$('edit').onkeyup();" onkeypress="this.onclick();" />
  <label for="long">長く</label>
</form>

<div class="box">
<label class="counter" id="editc" for="edit"><%=str.length()%></label>
<textarea rows="6" cols="20" id="edit" onkeyup="convert(get_servlet(), 'edit', 'result', 'editc', 'resultc');" onmouseup="this.onkeyup();" >
<%=str%></textarea>
</div>

<div class="box">
<label class="counter" id="resultc" for="edit">0</label>
<textarea rows="6" cols="20" id="result" readonly="readonly" onfocus="copy_all('result')">
</textarea>
</div>

<h2>Twitter用ブックマークレット</h2>
<dl>
<dt><a href="javascript:location.href='http://ligtran.appspot.com/ligature?redirect_to='+encodeURIComponent(location.href.replace(/\?status=[^&\/]*&?/,'')+'?status=%s')+'&q='+encodeURIComponent(document.getElementById('status').value);void(0);">ligtran: 短くする</a></dt>
<dd>このリンク（<a href="javascript:location.href='http://ligtran.appspot.com/ligature?redirect_to='+encodeURIComponent(location.href.replace(/\?status=[^&\/]*&?/,'')+'?status=%s')+'&q='+encodeURIComponent(document.getElementById('status').value);void(0);">ligtran: 短くする</a>）をブックマークバーにドラッグして入れてください。 <a href="http://twitter.com/">Twitterの画面</a>でそれを押すと、編集欄の内容が変換されます。</dd>
<dt><a href="javascript:location.href='http://ligtran.appspot.com/ligature/reverse?redirect_to='+encodeURIComponent(location.href.replace(/\?status=[^&\/]*&?/,'')+'?status=%s')+'&q='+encodeURIComponent(document.getElementById('status').value);void(0);">ligtran: 長くする</a></dt>
<dd>同上</dd>
</dl>

<h2>説明</h2>
<p><a href="http://github.com/whym/ligtran#readme">詳しい説明とソースコードはこちら</a>です。</p>

<h2>バグ</h2>
<ul>
<li>高速に入力すると変換結果がおかしくなる（続けてエンターなどを押すと直ります）</li>
</ul>

<address>
運営 <a href="http://twitter.com/whym">@whym</a>
</address>
<script type="text/javascript">
document.getElementById('edit').focus();
window.onload(document.getElementById('edit').onkeyup());
</script>
</body>
</html>
