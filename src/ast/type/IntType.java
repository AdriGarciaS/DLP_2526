package ast.type;

import semantics.Visitor;

public class IntType extends AbsType implements Type {

    public IntType() {
    }

    @Override
    public String toString() {
        return "INT";
    }

    @Override
    public <TP, TR> TR accept(Visitor<TP, TR> v, TP param) {
        return v.visit(this, param);
    }

    @Override
    public int numberOfBytes() {
        return 2;
    }

    @Override
    public boolean isPromotableTo(Type other) {
        return other instanceof IntType || other instanceof DoubleType;
    }


    @Override
    public boolean isBuiltIn() {
        return true;
    }

    @Override
    public Type arithmetic(Type other, int line, int col) {
        if (other instanceof DoubleType) {
            return new DoubleType();
        }
        if (other instanceof IntType || other instanceof CharType) {
            return new IntType();
        }
        return new ErrorType(line, col, "Arithmetic: INT not compatible with " + other);
    }

    @Override
    public Type modulus(Type other, int line, int col) {
        if (other instanceof IntType || other instanceof CharType) {
            return new IntType();
        }
        return new ErrorType(line, col, "Modulus: INT not compatible with " + other);
    }

    @Override
    public Type logical(Type other, int line, int col) {
        if (other instanceof IntType || other instanceof CharType) {
            return new IntType();
        }
        return new ErrorType(line, col, "Logical: INT not compatible with " + other);
    }

    @Override
    public Type logicalNot(int line, int col) {
        return new IntType();
    }

    @Override
    public Type comparison(Type other, int line, int col) {
        if (other instanceof IntType || other instanceof CharType || other instanceof DoubleType) {
            return new IntType();
        }
        return new ErrorType(line, col, "Comparison: INT not compatible with " + other);
    }

    @Override
    public Type unaryMinus(int line, int col) {
        return new IntType();
    }

    @Override
    public Type cast(Type targetType, int line, int col) {
        if (targetType instanceof IntType || targetType instanceof DoubleType || targetType instanceof CharType) {
            return targetType;
        }
        return new ErrorType(line, col, "Cast: cannot cast INT to " + targetType);
    }

    @Override
    public Type mustBeAssignable(Type rhs, int line, int col) {
        if (!rhs.isBuiltIn() || rhs instanceof VoidType) {
            return new ErrorType(line, col, "Assignment: " + rhs + " is not a basic type");
        }
        // char -> int -> double
        if (rhs instanceof IntType || rhs instanceof CharType) {
            return this;
        }
        return new ErrorType(line, col, "Assignment: cannot assign " + rhs + " to INT");
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
        // int is a valid condition
    }

    @Override
    public void mustBeReturnable(Type expectedReturnType, int line, int col) {
        if (!this.isPromotableTo(expectedReturnType)) {
            new ErrorType(line, col,
                    "Cannot return int when " + expectedReturnType + " is expected");
        }
    }

    @Override
    public Type supertype(Type other) {
        if (other instanceof DoubleType) return new DoubleType();
        return new IntType(); // int vs int or int vs char → int
    }

    @Override
    public String suffix() { return "i"; }

}