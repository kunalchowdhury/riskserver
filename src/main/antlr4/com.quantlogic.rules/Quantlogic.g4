grammar Quantlogic;

parse
 : block EOF
 ;

block
 : stat*
 ;

stat
: assignment
| END
;

assignment
 : ID ASSIGN expr
 ;

expr
 : MINUS expr                           #unaryMinusExpr
 | NOT expr                             #notExpr
 | expr op=(MULT | DIV | MOD) expr      #multiplicationExpr
 | expr op=(PLUS | MINUS) expr          #additiveExpr
 | expr op=(LTEQ | GTEQ | LT | GT) expr #relationalExpr
 | expr op=(EQ | NEQ) expr              #equalityExpr
 | expr op=(AND | SAND) expr            #andSandExpr
 | expr op=(OR | SOR) expr              #orSorExpr
 | expr OTHERWISE expr                  #ternaryoperator
 | expr ALT expr                        #altoperator
 | element                              #elementExpr
 ;

element
 : OPAR expr CPAR #parExpr
 | (INT | FLOAT)  #numberAtom
 | (TRUE | FALSE) #booleanAtom
 | ID             #idAtom
 | STRING         #stringAtom
 | FUNCTION        #functionAtom
 | NIL            #nilAtom
 ;

SOR : '|||';
SAND : '&&&';
OR : '||';
AND : '&&';
EQ : '==';
NEQ : '!=';
GT : '>';
LT : '<';
GTEQ : '>=';
LTEQ : '<=';
PLUS : '+';
MINUS : '-';
MULT : '*';
DIV : '/';
MOD : '%';
POW : '^';
NOT : '!';
OTHERWISE : '?' ;
ALT : ':' ;
END : 'END' ;

SCOL : ';';
ASSIGN : '=';
OPAR : '(';
CPAR : ')';
OBRACE : '{';
CBRACE : '}';

TRUE : 'true';
FALSE : 'false';
NIL : 'nil';
IF : 'if';
ELSE : 'else';
WHILE : 'while';
LOG : 'log';

ID
 : [$]*[a-zA-Z_] [a-zA-Z_0-9]*
 ;


INT
 : [0-9]+
 ;

FLOAT
 : [0-9]+ '.' [0-9]*
 | '.' [0-9]+
 ;

STRING
 : '"' (~["\r\n] | '""')* '"'
 ;

FUNCTION
 : ([a-zA-Z_0-9]+('.')[a-zA-Z_0-9]+)+ ('(')+ ~[:\r\n]*  (')')
 ;

COMMENT
 : '#' ~[\r\n]* -> skip
 ;

SPACE
 : [ \t\r\n] -> skip
 ;

