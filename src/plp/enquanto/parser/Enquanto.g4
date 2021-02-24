grammar Enquanto;

programa : seqComando;     // sequência de comandos

seqComando: (comando ';')*;                                                 

comando: ID (',' ID)* ':=' expressao (',' expressao)*                                               # atribuicao
       | 'skip'                                                                                     # skip
       | 'se' booleano 'entao' comando ('senaose' booleano 'então' comando)* 'senao' comando        # se //(alterado)
       | 'enquanto' booleano 'faca' comando                                                         # enquanto
       | 'para' ID 'de' expressao 'ate' expressao ('passo' expressao)? 'faca' comando               # para //(inserido)
       | 'escolha' expressao ('caso' expressao ':' comando)*                                        # escolha //(inserido)
       | 'exiba' TEXTO                                                                              # exiba
       | 'escreva' expressao                                                                        # escreva
       | '{' seqComando '}'                                                                         # bloco
       ;

expressao: INT                                           # inteiro
         | 'leia'                                        # leia
         | ID                                            # id
         | expressao ('*' | '/') expressao               # opBin
         | expressao ('+' | '-') expressao               # opBin
         | '(' expressao ')'                             # expPar
         ;

booleano: BOOLEANO                                       # bool
        | expressao '=' expressao                        # opRel
        | expressao '<=' expressao                       # opRel
        | expressao '=>' expressao                       # opRel //(maior ou igual)
        | expressao '<>' expressao                       # opRel //(diferente)
        | expressao '<' expressao                        # opRel //(menor)
        | expressao '>' expressao                        # opRel //(maior)
        | 'nao' booleano                                 # naoLogico
        | booleano 'e' booleano                          # eLogico
        | booleano 'ou' booleano                         # eLogico //(ou)
        | booleano 'xou' booleano                        # eLogico //(xor)
        | '(' booleano ')'                               # boolPar
        ;

parametros: ID (',' ID)*;

funcao: ID '=' '(' parametros ')' '=>' expressao;

BOOLEANO: 'verdadeiro' | 'falso';
INT: ('0'..'9')+ ;
ID: ('a'..'z')+;
TEXTO: '"' .*? '"';

Comentario: '#' .*? '\n' -> skip;
Espaco: [ \t\n\r] -> skip;
