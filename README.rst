==========================
ligtran
==========================
---------------------------------------------------------------------------------------------
文字と文字をくっつけたり离隹したりすることでテキストを短くしたり長くするウェブサービス
---------------------------------------------------------------------------------------------

 :Homepage: http://ligtran.appspot.com
 :Contact:  http://twitter.com/whym

このウェブサイトについて
============================
これは文字と文字をくっつけたり離したりすることで、テキストを短くしたり長くするウェブサービスです。
たとえば `「バイドゥ株式会社」を長く`_ すると「バイドゥ木朱式会ネ土」などとなります。

.. _「バイドゥ株式会社」を長く:
   http://ligtran.appspot.com/?reverse&q=%E3%83%90%E3%82%A4%E3%83%89%E3%82%A5%E6%A0%AA%E5%BC%8F%E4%BC%9A%E7%A4%BE

使い方は下記のとおりです。

 #. 「短く」か「長く」を選びます。
 #. 一つ目の入力欄にテキストを入力します。
 #. 二つ目の欄に変換された結果が表示されます。

変換の方法としては、標準的な合字の組([#]_, [#]_)と、その他の見た目に類似性のある文字列の組([#]_)とを用いています。

.. [#] `Unicode Normalization Forms`_
.. [#] `漢字構造情報データベース`_, `fonts.jp 漢字分解器`_
.. [#] 見た目の類似性による組は `不自然言語処理コンテスト`_ 応募時点では未実装

.. _Unicode Normalization Forms:
   http://unicode.org/reports/tr15/

.. _漢字構造情報データベース:
   http://www.kanji.zinbun.kyoto-u.ac.jp/projects/chise/ids/index.html.ja.iso-2022-jp

.. _fonts.jp 漢字分解器:
   http://www.fonts.jp/archives/search/

.. _不自然言語処理コンテスト:
   http://www.baidu.jp/unlp/

API
===============================
JSON形式でのAPIもあります。

例
  http://ligtran.appspot.com/ligature?q=fi を要求すると、 ::
  
     {"result": "\uFB01"}

  (`ﬁ (U+FB01)`__)がかえってきます。
  
  __ http://www.fileformat.info/info/unicode/char/fb01/index.htm
例
  http://ligtran.appspot.com/ligature/reverse?q=%E8%A8%88 （計）を要求すると、 ::
  
     {"result": "\u8A00\u5341"}
 
  （言十）がかえってきます。


How to build
================================
※ 主に自分用の備忘録目的なので、やや省略して書いています。

Maven を使うやり方と Ant+Ivy を使うやり方とがあります。

Maven
---------

1. mvn package
2. target/ligtran-*/ と target/ligtran-*.war ができ、これを servelet が動く場所に置けば動きます。

Ant+Ivy
---------

1. Apache Ivy をインストールし、CLASSPATH を通します。
2. ant test && ant war
3. war ディレクトリができ、これを servelet が動く場所に置けば動きます。

Credits
================================
Logo:
 The Twitter Logo Generator
 http://www.twitlogo.com/ligtran/90/33CCFF/FFFFFF/
Icons:
 Ajax loading animated gif by Sanbaldo
 http://www.sanbaldo.com/wordpress/1/ajax_gif/

..  Local Variables: ***
..  mode: rst ***
..  tab-width: 5 ***
..  End: ***
