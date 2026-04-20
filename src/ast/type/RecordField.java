package ast.type;

import ast.AbsLocalable;
import ast.Localable;
import semantics.Visitor;

public class RecordField extends AbsLocalable implements Localable {
    private String name;
    private Type fieldType;
    private int offset;

    public RecordField(int line, int column, Type fieldType, String name) {
        super(line, column);
        this.fieldType = fieldType;
        this.name = name;
    }

    public Type getFieldType() {
        return fieldType;
    }

    public void setFieldType(Type fieldType) {
        this.fieldType = fieldType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOffset() {

        return offset;
    }

    public void setOffset(int offset) {

        this.offset = offset;
    }

    @Override
    public String toString() {
        return fieldType + " " + name;
    }

    @Override
    protected String localeToString() {
        return fieldType + " " + name;
    }

    @Override
    public <TP, TR> TR accept(Visitor<TP, TR> v, TP param) {
        return v.visit(this, param);
    }


}
