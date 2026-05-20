package ast.expression;

import ast.AbsLocatable;
import ast.type.Type;

public abstract class AbsExpression extends AbsLocatable implements Expression {

    private boolean lvalue;
    private Type type;

    public AbsExpression(int line, int column) {
        super(line, column);
    }

    public boolean getLvalue() {
        return lvalue;
    }

    public void setLvalue(boolean lvalue) {
        this.lvalue = lvalue;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    String indent(String str) {
        return str.replace("\n", "\n  ");
    }
}
