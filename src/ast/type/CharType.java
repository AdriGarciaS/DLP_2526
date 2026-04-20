package ast.type;

import semantics.Visitor;

public class CharType extends AbsType implements Type {

    public CharType() {
    }

    @Override
    public String toString() {
        return "CHAR";
    }

    @Override
    public <TP, TR> TR accept(Visitor<TP, TR> v, TP param) {
        return v.visit(this, param);
    }

    @Override
    public int numberOfBytes() {
        return 1;
    }

    @Override
    public boolean isPromotableTo(Type other) {
        return other instanceof CharType || other instanceof IntType || other instanceof DoubleType;
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
        return new ErrorType(line, col, "Arithmetic: CHAR not compatible with " + other);
    }

    @Override
    public Type modulus(Type other, int line, int col) {
        if (other instanceof IntType || other instanceof CharType) {
            return new IntType();
        }
        return new ErrorType(line, col, "Modulus: CHAR not compatible with " + other);
    }

    @Override
    public Type logical(Type other, int line, int col) {
        if (other instanceof IntType || other instanceof CharType) {
            return new IntType();
        }
        return new ErrorType(line, col, "Logical: CHAR not compatible with " + other);
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
        return new ErrorType(line, col, "Comparison: CHAR not compatible with " + other);
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
        return new ErrorType(line, col, "Cast: cannot cast CHAR to " + targetType);
    }

    @Override
    public Type mustBeAssignable(Type rhs, int line, int col) {
        if (!rhs.isBuiltIn() || rhs instanceof VoidType) {
            return new ErrorType(line, col, "Assignment: " + rhs + " is not a built in type");
        }
        if (rhs instanceof CharType) {
            return this;
        }
        return new ErrorType(line, col, "Assignment: cannot assign " + rhs + " to CHAR");
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
        // char can be used as condition (promotes to int)
    }

    @Override
    public void mustBeReturnable(Type expectedReturnType, int line, int col) {
        if (!this.isPromotableTo(expectedReturnType)) {
            new ErrorType(line, col,
                    "Cannot return char when " + expectedReturnType + " is expected");
        }
    }


    @Override
    public Type supertype(Type other) {
        if (other instanceof DoubleType) return new DoubleType();
        if (other instanceof IntType)    return new IntType();
        return new CharType(); // char vs char → char
    }

    @Override
    public String suffix() { return "b"; }

}