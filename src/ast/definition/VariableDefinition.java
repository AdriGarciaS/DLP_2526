package ast.definition;

import ast.type.Type;
import semantics.Visitor;

public class VariableDefinition extends AbsDefinition implements Definition {

    private Type variableType;
    private int offset;

    public VariableDefinition(int line, int column, Type variableType, String name) {
        super(line, column, name);
        this.variableType = variableType;
    }


    public Type getVariableType() {

        return variableType;
    }

    public void setVariableType(Type variableType) {

        this.variableType = variableType;
    }

    public void setOffset(int offset) {

        this.offset = offset;
    }

    public int getOffset() {

        return offset;
    }

    @Override
    public Type getType() {
        return variableType;
    }


    @Override
    protected String localeToString() {
        return variableType + " " + name + " \n ";
    }

    @Override
    public <TP, TR> TR accept(Visitor<TP, TR> v, TP param) {
        return v.visit(this, param);
    }


}
