package ast.type;

import java.util.List;


public abstract class AbsType implements Type {

    @Override
    public boolean isPromotableTo(Type other) {
        return this.getClass().equals(other.getClass());
    }

    @Override
    public boolean isBuiltIn() {
        return false;
    }

    @Override
    public Type supertype(Type other) {
        return new ErrorType(0, 0, "No supertype between " + this + " and " + other);
    }

    @Override
    public String suffix() {
        throw new IllegalArgumentException("No MAPL suffix for type: " + this);
    }

    @Override
    public Type arithmetic(Type other, int line, int col) {
        return new ErrorType(line, col,
                "Arithmetic operation not allowed between " + this + " and " + other);
    }

    @Override
    public Type modulus(Type other, int line, int col) {
        return new ErrorType(line, col,
                "Modulus operation not allowed between " + this + " and " + other);
    }

    @Override
    public Type logical(Type other, int line, int col) {
        return new ErrorType(line, col,
                "Logical operation not allowed between " + this + " and " + other);
    }

    @Override
    public Type logicalNot(int line, int col) {
        return new ErrorType(line, col, "Logical negation not allowed on " + this);
    }

    @Override
    public Type comparison(Type other, int line, int col) {
        return new ErrorType(line, col, "Comparison not allowed between " + this + " and " + other);
    }

    @Override
    public Type unaryMinus(int line, int col) {
        return new ErrorType(line, col, "Unary minus not allowed on " + this);
    }

    @Override
    public Type squareBrackets(Type indexType, int line, int col) {
        return new ErrorType(line, col, this + " is not an array, cannot be indexed");
    }

    @Override
    public Type dot(String fieldName, int line, int col) {
        return new ErrorType(line, col,
                this + " is not a struct, does not have field '" + fieldName + "'");
    }

    @Override
    public Type cast(Type targetType, int line, int col) {
        return new ErrorType(line, col, "Cannot cast " + this + " to " + targetType);
    }

    @Override
    public Type mustBeAssignable(Type rhs, int line, int col) {
        return new ErrorType(line, col, "Cannot assign " + rhs + " to " + this);
    }

    @Override
    public Type parenthesis(List<Type> argTypes, int line, int col) {
        return new ErrorType(line, col, this + " is not a function, cannot be invoked");
    }

    @Override
    public void mustBeReadable(int line, int col) {
        new ErrorType(line, col, this + " cannot be read");
    }

    @Override
    public void mustBeWritable(int line, int col) {
        new ErrorType(line, col, this + " cannot be written");
    }

    @Override
    public void mustBeComparison(int line, int col) {
        new ErrorType(line, col, this + " cannot be used as a condition (expected integer)");
    }

    @Override
    public void mustBeReturnable(Type expectedReturnType, int line, int col) {
        new ErrorType(line, col,
                "Cannot return " + this + " when " + expectedReturnType + " is expected");
    }
}