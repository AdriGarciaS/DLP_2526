package semantics;

import ast.Program;
import ast.definition.FunctionDefinition;
import ast.definition.VariableDefinition;
import ast.expression.*;
import ast.statement.*;
import ast.type.*;

public interface Visitor<TP, TR> {
    //ALWAYS USE THE <>

    public TR visit(Program program, TP param);
    //one visit per nonabs class

    public TR visit(FunctionDefinition definition, TP param);

    public TR visit(VariableDefinition definition, TP param);

    public TR visit(IfStatement ifStatement, TP param);

    public TR visit(ReadStatement readStatement, TP param);

    public TR visit(WriteStatement write, TP param);

    public TR visit(ReturnStatement ret, TP param);

    public TR visit(WhileStatement whileStatement, TP param);

    public TR visit(AssigmentStatement assigmentStatement, TP param);


    public TR visit(ArrayType array, TP param);

    public TR visit(CharType type, TP param);

    public TR visit(DoubleType doubleType, TP param);

    public TR visit(ErrorType errorType, TP param);

    public TR visit(FunctionType errorType, TP param);

    public TR visit(IntType intetype, TP param);

    public TR visit(RecordField recordField, TP param);

    public TR visit(RecordType recordType, TP param);

    public TR visit(VoidType voidTYpe, TP param);


    public TR visit(Access access, TP param);

    public TR visit(ArithmeticOperation arithmetic, TP param);

    public TR visit(Cast cast, TP param);

    public TR visit(CharLiteral charLiteral, TP param);

    public TR visit(Comparation comparation, TP param);

    public TR visit(DoubleLiteral doubleLiteral, TP param);

    public TR visit(FunctionInvocation functionInvocation, TP param);

    public TR visit(IntLiteral literal, TP param);

    public TR visit(LogicOperation logic, TP param);

    public TR visit(ModuleOperation module, TP param);

    public TR visit(SquareBrackets indexing, TP param);

    public TR visit(UnaryMinus minus, TP param);

    public TR visit(UnaryNegation negation, TP param);

    public TR visit(Variable variable, TP param);

}
