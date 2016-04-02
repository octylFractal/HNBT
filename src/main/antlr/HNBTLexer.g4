lexer grammar HNBTLexer;

@header {
package me.kenzierocks.hnbt.grammar;
}

CompoundTagType : 'compound' ;
RootName : 'root' -> mode(NORMAL) ;
CWS : [ \r\t\n]+ ;

mode NORMAL;

STRING_ONELINE : '"' (~'"' | '\\"')* '"' ;
OpenCompound : '{' ;
CloseCompound : '}' ;
OpenList : '[' ;
CloseList : ']' ;
ItemSep : ',' ;
TagToValue : '=' ;

TagType : 'compound'
        | 'byte'
        | 'byte-array'
        | 'short'
        | 'int'
        | 'int-array'
        | 'long'
        | 'float'
        | 'double'
        | 'list'
        | 'string'
        ;

INTLIKEVAL : [1-9]? [0-9]+ ;
FLOATLIKEVAL : INTLIKEVAL
             | (INTLIKEVAL | '0')? '.' [0-9]+ // 1.1, .1, 0.1
             ;

TagName : NameStartChar NameChar* ; // all "printables" (excl. whitespace)
fragment
NameChar
   : NameStartChar
   | '0'..'9'
   | '_'
   | '\u00B7'
   | '\u0300'..'\u036F'
   | '\u203F'..'\u2040'
   ;
fragment
NameStartChar
   : 'A'..'Z' | 'a'..'z'
   | '\u00C0'..'\u00D6'
   | '\u00D8'..'\u00F6'
   | '\u00F8'..'\u02FF'
   | '\u0370'..'\u037D'
   | '\u037F'..'\u1FFF'
   | '\u200C'..'\u200D'
   | '\u2070'..'\u218F'
   | '\u2C00'..'\u2FEF'
   | '\u3001'..'\uD7FF'
   | '\uF900'..'\uFDCF'
   | '\uFDF0'..'\uFFFD'
   ;
WS : [ \r\t\n]+;
