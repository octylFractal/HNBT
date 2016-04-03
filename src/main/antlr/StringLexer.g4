/** A grand lexer for Java String literals, excluding quotes. */
lexer grammar StringLexer;

@header {
package me.kenzierocks.hnbt.grammar;
}

// Most of the following is ripped out of the JLS.
// https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-3.3
// https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-3.10.5
UnicodeEscape : '\\' UnicodeMarker HexDigit HexDigit HexDigit HexDigit ;
fragment
UnicodeMarker: 'u'+ ;
fragment
HexDigit : [0-9a-fA-F] ;
RawInputCharacter: ~[\r\n] ; // everything except \r or \n

EscapeSequence : '\\'
                   ( 'b'
                   | 't'
                   | 'n'
                   | 'f'
                   | 'r'
                   | '"'
                   | '\''
                   | '\\'
                   )
               ;
OctalEscape : '\\'
                ( OctalDigit
                | OctalDigit OctalDigit
                | ZeroToThree OctalDigit OctalDigit
                )
            ;
fragment
OctalDigit : [0-7] ;
fragment
ZeroToThree : [0-3] ;
