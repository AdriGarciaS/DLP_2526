package ast.type;

import semantics.Visitor;

public class DoubleType extends AbsType implements Type {

    public DoubleType() {
    }

    @Override
    public String toString() {
        return "DOUBLE";
    }

    @Override
    public <TP, TR> TR accept(Visitor<TP, TR> v, TP param) {
        return v.visit(this, param);
    }

    @Override
    public int numberOfBytes() {
        return 4;
    }

    @Override
    public boolean isPromotableTo(Type other) {
        return other instanceof DoubleType;
    }


    @Override
    public boolean isBuiltIn() {
        return true;
    }

    @Override
    public Type arithmetic(Type other, int line, int col) {
        if (other instanceof IntType || other instanceof CharType || other instanceof DoubleType) {
            return new DoubleType();
        }
        return new ErrorType(line, col, "Arithmetic: DOUBLE not compatible with " + other);
    }

    @Override
    public Type comparison(Type other, int line, int col) {
        if (other instanceof IntType || other instanceof CharType || other instanceof DoubleType) {
            return new IntType();
        }
        return new ErrorType(line, col, "Comparison: DOUBLE not compatible with " + other);
    }

    @Override
    public Type unaryMinus(int line, int col) {
        return new DoubleType();
    }

    @Override
    public Type cast(Type targetType, int line, int col) {
        if (targetType instanceof IntType || targetType instanceof DoubleType || targetType instanceof CharType) {
            return targetType;
        }
        return new ErrorType(line, col, "Cast: cannot cast DOUBLE to " + targetType);
    }

    @Override
    public Type mustBeAssignable(Type rhs, int line, int col) {
        if (!rhs.isBuiltIn() || rhs instanceof VoidType) {
            return new ErrorType(line, col, "Assignment: " + rhs + " is not a basic type");
        }
        // char -> int -> double
        if (rhs instanceof IntType || rhs instanceof CharType || rhs instanceof DoubleType) {
            return this;
        }
        return new ErrorType(line, col, "Assignment: cannot assign " + rhs + " to DOUBLE");
    }

    @Override
    public void mustBeReadable(int line, int col) {
        // valid
    }

    @Override
    public void mustBeWritable(int line, int col) {
        // valid
    }

    @Override
    public void mustBeComparison(int line, int col) {
        // double can be used as condition (returns int)
    }

    @Override
    public void mustBeReturnable(Type expectedReturnType, int line, int col) {
        if (!this.isPromotableTo(expectedReturnType)) {
            new ErrorType(line, col,
                    "Cannot return double when " + expectedReturnType + " is expected");
        }
    }

    @Override
    public Type supertype(Type other) {
        return new DoubleType(); // double always wins
    }

    @Override
    public String suffix() { return "f"; }

}