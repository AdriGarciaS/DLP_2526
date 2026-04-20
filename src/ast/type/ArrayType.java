package ast.type;

import semantics.Visitor;

public class ArrayType extends AbsType implements Type {
    private int size;
    private Type arrayType;

    public ArrayType(Type arrayType, int size) {
        this.arrayType = arrayType;
        this.size = size;
    }

    @Override
    public int numberOfBytes() {
        return size * arrayType.numberOfBytes();
    }

    @Override
    public Type squareBrackets(Type indexType, int line, int col) {
        if (indexType instanceof IntType || indexType instanceof CharType) {
            return this.arrayType;
        }
        return new ErrorType(line, col, "Array index must be INT or CHAR, got " + indexType);
    }

    @Override
    public void mustBeReadable(int line, int col) {
        new ErrorType(line, col, "Cannot read an array as a whole");
    }

    @Override
    public void mustBeWritable(int line, int col) {
        new ErrorType(line, col, "Cannot write to an array as a whole");
    }

    @Override
    public void mustBeComparison(int line, int col) {
        new ErrorType(line, col, "Cannot use an array as a condition");
    }

    @Override
    public void mustBeReturnable(Type expectedReturnType, int line, int col) {
        new ErrorType(line, col, "Cannot return an array directly");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Type current = this;
        while (current instanceof ArrayType) {
            ArrayType array = (ArrayType) current;
            sb.append("[").append(array.getSize()).append("]");
            current = array.getArrayType();
        }
        sb.append(current);
        return sb.toString();
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Type getArrayType() {
        return arrayType;
    }

    public void setArrayType(Type arrayType) {
        this.arrayType = arrayType;
    }

    @Override
    public <TP, TR> TR accept(Visitor<TP, TR> v, TP param) {
        return v.visit(this, param);
    }
}