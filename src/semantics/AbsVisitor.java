package semantics;

import ast.Program;
import ast.definition.FunctionDefinition;
import ast.definition.VariableDefinition;
import ast.expression.*;
import ast.statement.*;
import ast.type.*;

public abstract class AbsVisitor<TP, TR> implements Visitor<TP, TR> {

    @Override
    public TR visit(Program program, TP param) {
        program.getDefinitions().forEach(d -> d.accept(this, param));
        return null;
    }

    @Override
    public TR visit(FunctionDefinition funcDefinition, TP param) {
        funcDefinition.getFunctionType().getParameters().forEach(p -> p.accept(this, param));
        funcDefinition.getLocalVariables().forEach(v -> v.accept(this, param));
        funcDefinition.getBodyStatements().forEach(s -> s.accept(this, param));
        return null;
    }

    @Override
    public TR visit(VariableDefinition varDefinition, TP param) {
        varDefinition.getType().accept(this, param);
        return null;
    }

    @Override
    public TR visit(IfStatement ifStatement, TP param) {
        ifStatement.getConditional().accept(this, param);
        ifStatement.getIfBlock().forEach(s -> s.accept(this, param));
        ifStatement.getElseBlock().forEach(s -> s.accept(this, param));
        return null;
    }

    @Override
    public TR visit(ReadStatement read, TP param) {
        read.getReadExpression().accept(this, param);
        return null;
    }

    @Override
    public TR visit(WriteStatement write, TP param) {
        write.getWriteExpression().accept(this, param);
        return null;
    }

    @Override
    public TR visit(ReturnStatement returnStatement, TP param) {
        returnStatement.getReturned().accept(this, param);
        return null;
    }

    @Override
    public TR visit(WhileStatement whileStatement, TP param) {
        whileStatement.getCondition().accept(this, param);
        whileStatement.getWhileBlock().forEach(s -> s.accept(this, param));
        return null;
    }

    @Override
    public TR visit(AssigmentStatement assignment, TP param) {
        assignment.getLHS().accept(this, param);
        assignment.getRHS().accept(this, param);
        return null;
    }

    @Override
    public TR visit(ArrayType arrayType, TP param) {
        arrayType.getArrayType().accept(this, param);
        return null;
    }

    @Override
    public TR visit(CharType charType, TP param) {
        return null;
    }

    @Override
    public TR visit(DoubleType doubleType, TP param) {
        return null;
    }

    @Override
    public TR visit(ErrorType errorType, TP param) {
        return null;
    }

    @Override
    public TR visit(FunctionType funType, TP param) {
        funType.getParameters().forEach(p -> p.accept(this, param));
        return null;
    }

    @Override
    public TR visit(IntType intType, TP param) {
        return null;
    }

    @Override
    public TR visit(RecordField recordField, TP param) {
        recordField.getFieldType().accept(this, param);
        return null;
    }

    @Override
    public TR visit(RecordType record, TP param) {
        record.getRecordFields().forEach(f -> f.accept(this, param));
        return null;
    }

    @Override
    public TR visit(VoidType voidType, TP param) {
        return null;
    }

    @Override
    public TR visit(Access access, TP param) {
        access.getExpression().accept(this, param);
        return null;
    }

    @Override
    public TR visit(ArithmeticOperation arithmetic, TP param) {
        arithmetic.getLeftExpression().accept(this, param);
        arithmetic.getRightExpression().accept(this, param);
        return null;
    }

    @Override
    public TR visit(Cast cast, TP param) {
        cast.getCastType().accept(this, param);
        cast.getCastExpression().accept(this, param);
        return null;
    }

    @Override
    public TR visit(CharLiteral charLiteral, TP param) {
        return null;
    }

    @Override
    public TR visit(Comparation comparator, TP param) {
        comparator.getLeftExpression().accept(this, param);
        comparator.getRightExpression().accept(this, param);
        return null;
    }

    @Override
    public TR visit(DoubleLiteral doubleLiteral, TP param) {
        return null;
    }

    @Override
    public TR visit(FunctionInvocation funcInvocation, TP param) {
        funcInvocation.getParameters().forEach(p -> p.accept(this, param));
        return null;
    }

    @Override
    public TR visit(IntLiteral intLiteral, TP param) {
        return null;
    }

    @Override
    public TR visit(LogicOperation logical, TP param) {
        logical.getLeftExpression().accept(this, param);
        logical.getRightExpression().accept(this, param);
        return null;
    }

    @Override
    public TR visit(ModuleOperation modulus, TP param) {
        modulus.getLeftExpression().accept(this, param);
        modulus.getRightExpression().accept(this, param);
        return null;
    }

    @Override
    public TR visit(SquareBrackets indexing, TP param) {
        indexing.getOutsideExpression().accept(this, param);
        indexing.getBracketsExpression().accept(this, param);
        return null;
    }

    @Override
    public TR visit(UnaryMinus unaryMinus, TP param) {
        unaryMinus.getExpression().accept(this, param);
        return null;
    }

    @Override
    public TR visit(UnaryNegation negation, TP param) {
        negation.getExpression().accept(this, param);
        return null;
    }

    @Override
    public TR visit(Variable variable, TP param) {
        return null;
    }
}