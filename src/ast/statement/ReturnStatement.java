package ast.statement;

import ast.expression.Expression;
import semantics.Visitor;

public class ReturnStatement extends AbsStatement implements Statement {
    private Expression returned;

    public ReturnStatement(int line, int column, Expression returned) {
        super(line, column);
        this.returned = returned;
    }

    public Expression getReturned() {
        return returned;
    }

    public void setReturned(Expression returned) {
        this.returned = returned;
    }

    @Override
    protected String localeToString() {
        return "ReturnStatement {\n" + "  returned -> " + returned.toString() + "\n" + "}";
    }

    @Override
    public <TP, TR> TR accept(Visitor<TP, TR> v, TP param) {
        return v.visit(this, param);
    }
}
