package ast.type;

import ast.AbsLocalable;
import ast.errorhandler.ErrorHandler;
import semantics.Visitor;

import java.util.List;

public class ErrorType extends  AbsLocalable implements Type {

    private String errorMessage;

    public ErrorType(int line, int column, String message) {
        super(line, column);
        this.errorMessage = message;

        ErrorHandler.getInstance().addError(this);
    }

    @Override
    protected String localeToString() {
        return "ERROR : " + errorMessage + " \n ";
    }

    @Override
    public <TP, TR> TR accept(Visitor<TP, TR> v, TP param) {
        return v.visit(this, param);
    }

    @Override
    public int numberOfBytes() {
        return 0; // No tiene sentido, pero evita null
    }

    @Override
    public boolean isPromotableTo(Type other) {
        return false;
    }

    @Override
    public boolean isBuiltIn() {
        return false;
    }

    @Override
    public Type supertype(Type other) {
        return this;
    }

    @Override
    public Type arithmetic(Type other, int line, int col) {
        return this;
    }

    @Override
    public Type modulus(Type other, int line, int col) {
        return this;
    }

    @Override
    public Type logical(Type other, int line, int col) {
        return this;
    }

    @Override
    public Type logicalNot(int line, int col) {
        return this;
    }

    @Override
    public Type comparison(Type other, int line, int col) {
        return this;
    }

    @Override
    public Type unaryMinus(int line, int col) {
        return this;
    }

    @Override
    public Type squareBrackets(Type indexType, int line, int col) {
        return this;
    }

    @Override
    public Type dot(String fieldName, int line, int col) {
        return this;
    }

    @Override
    public Type cast(Type targetType, int line, int col) {
        return this;
    }

    @Override
    public Type mustBeAssignable(Type rhs, int line, int col) {
        return this;
    }

    @Override
    public Type parenthesis(List<Type> argTypes, int line, int col) {
        return this;
    }

    @Override
    public void mustBeReadable(int line, int col) {
        // No generar nuevo error
    }

    @Override
    public void mustBeWritable(int line, int col) {
        // No generar nuevo error
    }

    @Override
    public void mustBeComparison(int line, int col) {
        // No generar nuevo error
    }

    @Override
    public void mustBeReturnable(Type expectedReturnType, int line, int col) {
        // No generar nuevo error
    }
}
