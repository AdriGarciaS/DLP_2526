package ast.expression;

import ast.AbsLocalable;
import ast.type.Type;

public abstract class AbsExpression extends AbsLocalable implements Expression {

    public boolean lvalue;
    private Type type;

    public AbsExpression(int line, int column) {
        super(line, column);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean getLvalue() {
        return lvalue;
    }

    public void setLvalue(boolean lvalue) {
        this.lvalue = lvalue;
    }

    String indent(String str) {
        return str.replace("\n", "\n  ");
    }
}
