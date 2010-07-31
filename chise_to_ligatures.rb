#! /usr/bin/env ruby
# -*- coding: utf-8 -*-
require 'open-uri'
urls = ['http://cvs.m17n.org/viewcvs/chise/ids/IDS-UCS-Basic.txt?content-type=text%2Fplain&revision=HEAD']
ligatures = {}
tx = {
  "\u2E85" => 'イ',
  "\u2E8A" => 'ト',
  "\u2E89" => 'リ',
  "\u2E95" => 'ヨ',
  "\u2E98" => '才',
  "\u2E9D" => '月',
  "\u2E9F" => '母',
  "\u2EA0" => '民',
  "\u2EA1" => 'シ',
  "\u2EA9" => '王',
  "\u2EAC" => '示',
  "\u2EAD" => 'ネ',
  "\u2EBC" => '月',
  "\u2ED8" => '青',
  "\u2EDD" => '食',
  "\u2EE4" => '鬼',
  "\u2EE9" => '黄',
  "\u7CF9" => '糸',

  '夕' => 'タ',
  '力' => 'カ',
  '工' => 'エ',
  '口' => 'ロ',
  '又' => 'ヌ',
  '二' => 'ニ',
  '八' => 'ハ',
  '匕' => 'ヒ',
}
txpat = Regexp.new(tx.keys.join('|'))
urls.each do |url|
  open(url) do |io|
    io.each_line do |line|
      line = line.force_encoding('utf-8')
      line.strip!
      _,ch,lig = line.split(/\t/)
      next if !ch or !lig
      if lig !~ /&/ and lig =~ /^\u2FF0/ and lig !~ /[\u2FF1-\u2FFB]/ then
        lig.gsub!(/^\u2FF0/, '')
        if lig =~ txpat then
          lig.gsub!(txpat){|x| tx[x]}
        end
        puts "#{lig} #{ch}"
      end
    end
  end
end
