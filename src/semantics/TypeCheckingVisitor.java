package semantics;

import ast.Program;
import ast.definition.FunctionDefinition;
import ast.definition.VariableDefinition;
import ast.expression.*;
import ast.statement.*;
import ast.type.*;

/**
 * Visitor de comprobación de tipos.
 * Hereda de AbsVisitor para reutilizar el recorrido base del AST.
 */
public class TypeCheckingVisitor extends AbsVisitor<Void, Void> implements Visitor<Void, Void> {

    /*
     *
     * EXPRESSIONS
     * (P) Variable: expression -> ID
     * (R) expression.lvalue = true
     *
     * (P) ArithmeticOperation: expression1 -> expression2 expression3
     * (R) expression1.type = expression2.type.arithmetic(expression3.type)
     *     expression1.lvalue = false
     *
     * (P) Cast: expression1 -> type expression2
     * (R) expression1.type = type
     *     expression1.lvalue = false
     *
     * (P) CharLiteral: expression -> CHAR_CONSTANT
     * (R) expression.type = new CharType()
     *     expression.lvalue = false
     *
     * (P) Comparation: expression1 -> expression2 expression3
     * (R) expression1.type = expression2.type.comparison(expression3.type)
     *     expression1.lvalue = false
     *
     * (P) DoubleLiteral: expression -> REAL_CONSTANT
     * (R) expression.type = new DoubleType()
     *     expression.lvalue = false
     *
     * (P) Access: expression1 -> expression2 ID
     * (R) expression1.type = expression2.type.dot(ID)
     *     expression.lvalue = true
     *
     * (P) FunctionInvocation: expression1 -> expression2 expression3*
     * (R) expression1.type = expression2.type.parenthesis( expression3* )
     *     expression.lvalue = false
     *
     * (P) SquareBrackets: expression1 -> expression2 expression3
     * (R) expression1.type = expression2.type.squareBrackets(expression3.type)
     *     expression.lvalue = true
     *
     * (P) IntLiteral: expression -> INT_CONSTANT
     * (R) expression.type = new IntType()
     *     expression.lvalue = false
     *
     * (P) LogicOperation: expression1 -> expression2 expression3
     * (R) expression1.type = expression2.type.logical(expression3.type)
     *     expression.lvalue = false
     *
     * (P) UnaryNegation: expression1 -> expression2
     * (R) expression1.type = expression2.type.logicalNot()
     *     expression.lvalue = false
     *
     * (P) ModuleOperation: expression1 -> expression2 expression3
     * (R) expression1.type = expression2.type.modulus(expression3.type)
     *     expression.lvalue = false
     *
     * (P) UnaryMinus: expression1 -> expression2
     * (R) expression1.type = expression2.type.unaryMinus()
     *     expression.lvalue = false
     *
     * STATEMENTS
     * (P) ReadStatement: statement -> expression
     * (R) expression.type.mustBeReadable()
     *     expression.lvalue debe ser true
     *
     * (P) AssigmentStatement: statement -> expression1 expression2
     * (R) expression1.type.mustBeAssignable(expression2.type)
     *     expression1.lvalue debe ser true
     *
     * (P) WriteStatement: statement -> expression
     * (R) expression.type.mustBeWritable()
     *
     * (P) IfStatement: statement -> expression statement2* statement3*
     * (R) expression.type.mustBeComparison()
     *
     * (P) WhileStatement: statement -> expression statement2*
     * (R) expression.type.mustBeComparison()
     *
     * (P) ReturnStatement: statement -> expression
     * (R) expression.type.mustBeReturnable(currentReturnType)
     *
     * (P) FunctionDefinition: definition -> type ID vardef* statement*
     * (R) El tipo de retorno de la función se almacena en currentReturnType
     *     para comprobar los return del cuerpo.
     *
     */

    // Tipo de retorno de la función que se está visitando actualmente
    private Type currentReturnType;


    @Override
    public Void visit(Program program, Void param) {
        program.getDefinitions().forEach(d -> d.accept(this, param));
        return null;
    }

    @Override
    public Void visit(FunctionDefinition funcDefinition, Void param) {
        funcDefinition.getFunctionType().getParameters().forEach(p -> p.accept(this, param));
        funcDefinition.getLocalVariables().forEach(v -> v.accept(this, param));

        currentReturnType = funcDefinition.getFunctionType().getReturnType();
        funcDefinition.getBodyStatements().forEach(s -> s.accept(this, param));
        currentReturnType = null;
        return null;
    }

    @Override
    public Void visit(VariableDefinition varDefinition, Void param) {
        varDefinition.getType().accept(this, param);
        return null;
    }

    @Override
    public Void visit(IfStatement ifStatement, Void param) {
        ifStatement.getConditional().accept(this, param);
        ifStatement.getConditional()
                .getType()
                .mustBeComparison(ifStatement.getLine(), ifStatement.getColumn());
        ifStatement.getIfBlock().forEach(s -> s.accept(this, param));
        ifStatement.getElseBlock().forEach(s -> s.accept(this, param));
        return null;
    }

    @Override
    public Void visit(ReadStatement read, Void param) {
        read.getReadExpression().accept(this, param);
        if (!read.getReadExpression().getLvalue()) {
            new ErrorType(read.getLine(), read.getColumn(), "Lvalue required in read statement");
        }
        else {
            read.getReadExpression().getType().mustBeReadable(read.getLine(), read.getColumn());
        }
        return null;
    }

    @Override
    public Void visit(WriteStatement write, Void param) {
        write.getWriteExpression().accept(this, param);
        write.getWriteExpression().getType().mustBeWritable(write.getLine(), write.getColumn());
        return null;
    }

    @Override
    public Void visit(ReturnStatement returnStatement, Void param) {
        returnStatement.getReturned().accept(this, param);
        returnStatement.getReturned()
                .getType()
                .mustBeReturnable(currentReturnType, returnStatement.getLine(),
                        returnStatement.getColumn());
        return null;
    }

    @Override
    public Void visit(WhileStatement whileStatement, Void param) {
        whileStatement.getCondition().accept(this, param);
        whileStatement.getCondition()
                .getType()
                .mustBeComparison(whileStatement.getLine(), whileStatement.getColumn());
        whileStatement.getWhileBlock().forEach(s -> s.accept(this, param));
        return null;
    }

    @Override
    public Void visit(AssigmentStatement assignment, Void param) {
        assignment.getLHS().accept(this, param);
        assignment.getRHS().accept(this, param);
        if (!assignment.getLHS().getLvalue()) {
            new ErrorType(assignment.getLine(), assignment.getColumn(),
                    "Lvalue required in assignment");
        }
        else {
            assignment.getLHS()
                    .getType()
                    .mustBeAssignable(assignment.getRHS().getType(), assignment.getLine(),
                            assignment.getColumn());
        }
        return null;
    }

    @Override
    public Void visit(Access access, Void param) {
        access.getExpression().accept(this, param);
        access.setLvalue(true);
        access.setType(access.getExpression()
                .getType()
                .dot(access.getFieldname(), access.getLine(), access.getColumn()));
        return null;
    }

    @Override
    public Void visit(ArithmeticOperation arithmetic, Void param) {
        arithmetic.getLeftExpression().accept(this, param);
        arithmetic.getRightExpression().accept(this, param);
        arithmetic.setLvalue(false);
        arithmetic.setType(arithmetic.getLeftExpression()
                .getType()
                .arithmetic(arithmetic.getRightExpression().getType(), arithmetic.getLine(),
                        arithmetic.getColumn()));
        return null;
    }

    @Override
    public Void visit(Cast cast, Void param) {
        cast.getCastType().accept(this, param);
        cast.getCastExpression().accept(this, param);
        cast.setLvalue(false);
        cast.setType(cast.getCastExpression()
                .getType()
                .cast(cast.getCastType(), cast.getLine(), cast.getColumn()));
        return null;
    }

    @Override
    public Void visit(CharLiteral charLiteral, Void param) {
        charLiteral.setLvalue(false);
        charLiteral.setType(new CharType());
        return null;
    }

    @Override
    public Void visit(Comparation comparator, Void param) {
        comparator.getLeftExpression().accept(this, param);
        comparator.getRightExpression().accept(this, param);
        comparator.setLvalue(false);
        comparator.setType(comparator.getLeftExpression()
                .getType()
                .comparison(comparator.getRightExpression().getType(), comparator.getLine(),
                        comparator.getColumn()));
        return null;
    }

    @Override
    public Void visit(DoubleLiteral doubleLiteral, Void param) {
        doubleLiteral.setLvalue(false);
        doubleLiteral.setType(new DoubleType());
        return null;
    }


    @Override
    public Void visit(FunctionInvocation functionInvocation, Void param) {
        functionInvocation.getParameters().forEach(p -> p.accept(this, param));
        functionInvocation.setLvalue(false);
        functionInvocation.setType(functionInvocation.getFunction()
                .getDefinition()
                .getType()
                .parenthesis(functionInvocation.getParameters()
                        .stream()
                        .map(Expression::getType)
                        .toList(), functionInvocation.getLine(), functionInvocation.getColumn()));
        return null;
    }

    @Override
    public Void visit(IntLiteral intLiteral, Void param) {
        intLiteral.setLvalue(false);
        intLiteral.setType(new IntType());
        return null;
    }


    @Override
    public Void visit(LogicOperation logical, Void param) {
        logical.getLeftExpression().accept(this, param);
        logical.getRightExpression().accept(this, param);
        logical.setLvalue(false);
        logical.setType(logical.getLeftExpression()
                .getType()
                .logical(logical.getRightExpression().getType(), logical.getLine(),
                        logical.getColumn()));
        return null;
    }

    @Override
    public Void visit(ModuleOperation modulus, Void param) {
        modulus.getLeftExpression().accept(this, param);
        modulus.getRightExpression().accept(this, param);
        modulus.setLvalue(false);
        modulus.setType(modulus.getLeftExpression()
                .getType()
                .modulus(modulus.getRightExpression().getType(), modulus.getLine(),
                        modulus.getColumn()));
        return null;
    }

    @Override
    public Void visit(SquareBrackets indexing, Void param) {
        indexing.getOutsideExpression().accept(this, param);
        indexing.getBracketsExpression().accept(this, param);
        indexing.setLvalue(true);
        indexing.setType(indexing.getOutsideExpression()
                .getType()
                .squareBrackets(indexing.getBracketsExpression().getType(), indexing.getLine(),
                        indexing.getColumn()));
        return null;
    }

    @Override
    public Void visit(UnaryMinus unaryMinus, Void param) {
        unaryMinus.getExpression().accept(this, param);
        unaryMinus.setLvalue(false);
        unaryMinus.setType(unaryMinus.getExpression()
                .getType()
                .unaryMinus(unaryMinus.getLine(), unaryMinus.getColumn()));
        return null;
    }

    @Override
    public Void visit(UnaryNegation negation, Void param) {
        negation.getExpression().accept(this, param);
        negation.setLvalue(false);
        negation.setType(negation.getExpression()
                .getType()
                .logicalNot(negation.getLine(), negation.getColumn()));
        return null;
    }

    @Override
    public Void visit(Variable variable, Void param) {
        variable.setLvalue(true);
        variable.setType(variable.getDefinition().getType());
        return null;
    }

}