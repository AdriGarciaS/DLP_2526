grammar Cmm;

@header{
    import ast.*;
    import ast.type.*;
    import ast.definition.*;
    import ast.expression.*;
    import ast.statement.*;
}

program returns [Program ast ]
    locals [List<Definition> defs = new ArrayList<Definition>()]:
    (v=varDefinition {$defs.addAll($v.ast);}
    | f=functionDefinition {$defs.add($f.ast);} )*
     m=mainFunctionDefinition {$defs.add($m.ast);} EOF
    {$ast = new Program($defs);}
    ;

variableType returns [Type ast]:
    b1=buildInType {$ast = $b1.ast;}
    | r1=record {$ast = $r1.ast; }
    | t1=variableType '[' size=INT_CONSTANT ']'
        {$ast = LexerHelper.arrayCreation( $t1.ast, $size.text);}
    ;

buildInType returns [Type ast]:
    'int' { $ast = new IntType();}
    | 'double' { $ast = new DoubleType();}
    | 'char' {$ast = new CharType();}
    ;

record returns [RecordType ast]:
    'struct' '{'
        { List<RecordField> fields = new ArrayList<>(); }
        (v1=varDefinition {
            for (VariableDefinition vd : $v1.ast) {
                fields.add(new RecordField(vd.getLine(), vd.getColumn(), vd.getVariableType(), vd.getName()));
            }
        })+
    '}'
    { $ast = new RecordType(fields); }
    ;


varDefinition returns [List<VariableDefinition> ast = new ArrayList<VariableDefinition>()]:
    t1=variableType id1=ID {$ast.add(new VariableDefinition($id1.getLine(), $id1.getCharPositionInLine()+1, $t1.ast, $id1.text));}
    (',' id2=ID {$ast.add(new VariableDefinition($id2.getLine(), $id2.getCharPositionInLine()+1, $t1.ast, $id2.text));} )* ';'
    ;

mainFunctionDefinition returns [FunctionDefinition ast]
    locals [List<VariableDefinition> localVars = new ArrayList<VariableDefinition>(),
            List<Statement> localStmts = new ArrayList<Statement>(),
            List<Locatable> fullbody = new ArrayList<Locatable>()]:
    returnT='void' name='main' '('')' '{'
        //(v=varDefinition { $localVars.addAll($v.ast); })*
        //(s=statement { $localStmts.addAll($s.ast); })*
        (v=varDefinition{ $localVars.addAll($v.ast); $fullbody.addAll($v.ast);}|s=statement{ $localStmts.addAll($s.ast);$fullbody.addAll($s.ast);})*
    '}'
    {$ast = new FunctionDefinition( $name.getLine(), $name.getCharPositionInLine()+1, new VoidType(), $name.text, new ArrayList<VariableDefinition>(), $localVars, $localStmts, $fullbody);}
    ;

functionDefinition returns [FunctionDefinition ast]
locals [List<VariableDefinition> localVars = new ArrayList<VariableDefinition>(),
            List<Statement> localStmts = new ArrayList<Statement>(),
            List<Locatable> fullbody = new ArrayList<Locatable>()]:
    returnT=functionReturnType id1=ID '(' args=functionDefinitionArgs')' '{'
        //vars=functionLocalVar
        //(s=statement { $localStmts.addAll($s.ast); })*
        (v=varDefinition{ $localVars.addAll($v.ast); $fullbody.addAll($v.ast);}|s=statement{ $localStmts.addAll($s.ast);$fullbody.addAll($s.ast);})*
    '}'
    {$ast = new FunctionDefinition( $id1.getLine(), $id1.getCharPositionInLine()+1, $returnT.ast, $id1.text, $args.ast, $localVars, $localStmts, $fullbody);}
    ;



functionLocalVar returns [List<VariableDefinition> ast = new ArrayList<VariableDefinition>()]:
    (v1=varDefinition {  $ast.addAll($v1.ast); })*
    ;

// includes empity args
functionDefinitionArgs returns [List<VariableDefinition> ast = new ArrayList<VariableDefinition>()]:
    bt1=buildInType id1=ID { $ast.add( new VariableDefinition( $id1.getLine(), $id1.getCharPositionInLine()+1, $bt1.ast, $id1.text ) ); }
    ( ',' bt2=buildInType id2=ID { $ast.add( new VariableDefinition( $id2.getLine(), $id2.getCharPositionInLine()+1, $bt2.ast, $id2.text ) ); } )*
    |
    ;

functionReturnType returns [Type ast]:
    bt=buildInType {$ast = $bt.ast; }
    | 'void'    {$ast = new VoidType();}
    ;

statement returns [List<Statement> ast = new ArrayList<Statement>()]:
    'if' '(' e1=expression')' b1=block // we should give higher preference to the if without else block, else for the last
        {$ast.add( new IfStatement($e1.ast.getLine(), $e1.ast.getColumn(), $e1.ast, $b1.ast ) );}
    | 'if' '('e1=expression')' b1=block 'else' b2=block
        {$ast.add( new IfStatement($e1.ast.getLine(), $e1.ast.getColumn(), $e1.ast, $b1.ast , $b2.ast ) );}
    | 'while' '('e1=expression')' b1=block
        {$ast.add( new WhileStatement( $e1.ast.getLine(), $e1.ast.getColumn(), $e1.ast, $b1.ast ) );}
    | <assoc=right> e1=expression '=' e2=expression ';' //assoc rigth for multi assigment
        {$ast.add( new AssigmentStatement ($e1.ast.getLine(), $e1.ast.getColumn(), $e1.ast , $e2.ast ) );}
    | 'return' e1=expression ';'
        {$ast.add ( new ReturnStatement( $e1.ast.getLine(), $e1.ast.getColumn(), $e1.ast  ) );}
    | 'read' args=ioArguments ';'
         {
            for (Expression expr : $args.ast) {
                $ast.add(new ReadStatement(expr.getLine(), expr.getColumn(), expr));
            }
         }
    | 'write' args=ioArguments ';'
        {
            for (Expression expr : $args.ast) {
                $ast.add(new WriteStatement(expr.getLine(), expr.getColumn(), expr));
            }
        }
    | id = ID '(' fArg=functionCallArg ')' ';'
        {$ast.add(new FunctionInvocation( $id.getLine(), $id.getCharPositionInLine()+1, $id.text , $fArg.ast )); }
    ;

ioArguments returns [List<Expression> ast = new ArrayList<Expression>()]:
    e1 = expression {$ast.add($e1.ast);} (',' e2=expression {$ast.add($e2.ast);} )*
    ;


block returns [List<Statement> ast = new ArrayList<Statement>()]:
    '{' (s1=statement {$ast.addAll( $s1.ast );})* '}'
    | s2=statement {$ast.addAll( $s2.ast );}
    ;

expression returns [Expression ast]:
    '(' e1=expression')'
        { $ast = $e1.ast ;}
    | e1=expression '['e2= expression ']'
        { $ast = new SquareBrackets($e1.ast.getLine(), $e1.ast.getColumn(), $e1.ast , $e2.ast);}
    | e1=expression  '.' id=ID // acces field
        {$ast = new Access( $e1.ast.getLine(), $e1.ast.getColumn(), $e1.ast , $id.text );}
    | '('t=buildInType')' e1=expression  // cast
        {$ast = new Cast($e1.ast.getLine(), $e1.ast.getColumn(), $t.ast , $e1.ast);}
    | '-' e1=expression    // unary minus
        {$ast = new UnaryMinus($e1.ast.getLine(), $e1.ast.getColumn(), $e1.ast);}
    | '!' e1=expression    // unary negation
        {$ast = new UnaryNegation($e1.ast.getLine(), $e1.ast.getColumn(), $e1.ast);}
    | e1=expression op=('/'|'*'|'%') e2=expression // alg op : high priority
        {$ast = LexerHelper.operationOrModule( $e1.ast, $op.text, $e2.ast );}
    | e1 = expression op=('+'|'-') e2=expression // alg op : low priority
         {$ast = new ArithmeticOperation($e1.ast.getLine(), $e1.ast.getColumn() , $e1.ast , $op.text, $e2.ast ); }
    | e1=expression op=('>' | '>=' | '<' | '<=' | '!=' |'==') e2=expression // comparative op
        {$ast = new Comparation($e1.ast.getLine(), $e1.ast.getColumn() , $e1.ast , $op.text, $e2.ast );}
    | e1=expression op=('&&'|'||') e2=expression // and / or
        {$ast = new LogicOperation($e1.ast.getLine(), $e1.ast.getColumn() , $e1.ast , $op.text, $e2.ast );}
    | id = ID '(' arg=functionCallArg ')'
        {$ast = new FunctionInvocation( $id.getLine(), $id.getCharPositionInLine()+1, $id.text , $arg.ast );}
    | i1 = INT_CONSTANT {$ast = new IntLiteral($i1.getLine(), $i1.getCharPositionInLine()+1 , LexerHelper.lexemeToInt($i1.text) ); }
    | r1=REAL_CONSTANT {$ast = new DoubleLiteral($r1.getLine(), $r1.getCharPositionInLine()+1 , LexerHelper.lexemeToReal($r1.text) ); }
    | c1=CHAR_CONSTANT {$ast = new CharLiteral($c1.getLine(), $c1.getCharPositionInLine()+1 , LexerHelper.lexemeToChar($c1.text) ); }
    | id = ID    {$ast = new Variable($id.getLine(), $id.getCharPositionInLine()+1 , $id.text );}
      ;


// inlcudes the epsilon (nothing)
functionCallArg returns [List<Expression> ast = new ArrayList<Expression>()]:
    e1 = expression {$ast.add($e1.ast);} (',' e2=expression {$ast.add($e2.ast);} )*
    |
    ;


fragment
NEW_LINE: '\r'
        |'\n'
    ;
fragment
LETTER: [a-zA-Z]
    ;
fragment
DIGIT:[0-9]
    ;
fragment
DECIMAL: DIGIT+
    ;
fragment
NUMBER: [1-9]DIGIT*
    ;
fragment
SIGNED_NUMBER: ('+'|'-')NUMBER
    ;
fragment
FLOATING_POINT: INT_CONSTANT'.'
    | '.'DECIMAL
    | INT_CONSTANT'.'DECIMAL
    ;
fragment
EXPONENT: (FLOATING_POINT|INT_CONSTANT) [Ee] (SIGNED_NUMBER|INT_CONSTANT)
    ;

fragment
// cualquier cosa menos backslash o comilla!!
SIMPLE_CHAR:'\'' ~['\\] '\''
    ;
fragment
ASCII_CHAR:  '\'' '\\'NUMBER '\''
    ;
fragment
SPECIAL_CHAR : '\'' '\\n' '\'' | '\'' '\\t' '\''
    ;



INT_CONSTANT: NUMBER
            | '0'
            ;
REAL_CONSTANT: FLOATING_POINT|EXPONENT
    ;
// de reglas espcificas a mas generales, las identifica por orden de aparicion !!
CHAR_CONSTANT:  SIMPLE_CHAR | ASCII_CHAR | SPECIAL_CHAR
    ;
ID: (LETTER|'_')(LETTER|DIGIT|'_')*
    ;

COMMENT: '//' .*? (NEW_LINE|EOF) ->skip
    ;

MULTILINE_COMMENT: '/*' .*? '*/' ->skip
    ;
WS: [ \t\r\n]+ -> skip
    ;


