package ast.type;

import semantics.Visitor;

public class VoidType extends AbsType implements Type {

    public VoidType() {
    }

    @Override
    public int numberOfBytes() {
        return 0;
    }

    @Override
    public boolean isBuiltIn() {
        return true;
    }

    @Override
    public void mustBeReadable(int line, int col) {
        new ErrorType(line, col, "Cannot read void");
    }

    @Override
    public void mustBeWritable(int line, int col) {
        new ErrorType(line, col, "Cannot write void");
    }

    @Override
    public void mustBeComparison(int line, int col) {
        new ErrorType(line, col, "Cannot use void as a condition");
    }

    @Override
    public void mustBeReturnable(Type expectedReturnType, int line, int col) {
        new ErrorType(line, col, "Cannot return void (only allowed in void functions)");
    }

    @Override
    public String toString() {
        return "VOID";
    }

    @Override
    public <TP, TR> TR accept(Visitor<TP, TR> v, TP param) {
        return v.visit(this, param);
    }
}