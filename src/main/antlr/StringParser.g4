/** A grand parser for Java String literals, excluding quotes. */
parser grammar StringParser;

options {tokenVocab = StringLexer;}

@header {
package me.kenzierocks.hnbt.grammar;

import me.kenzierocks.hnbt.util.StringUtil;
}

stringNoQuotes
    returns [String unescaped]
    locals [StringBuilder str]
    @init {
        $str = new StringBuilder();
    }
    @after {
        $unescaped = $str.toString();
    }
    : ( esc=EscapeSequence {$str.append(StringUtil.unescapeEscapeSequence($esc.text));}
      | uesc=UnicodeEscape {$str.append(StringUtil.unescapeUnicodeEscape($uesc.text));}
      | oesc=OctalEscape {$str.append(StringUtil.unescapeOctalEscape($oesc.text));}
      | raw=RawInputCharacter {$str.append($raw.text);}
      )*
    ;
