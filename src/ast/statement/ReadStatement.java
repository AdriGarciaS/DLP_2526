package ast.statement;

import ast.expression.Expression;
import semantics.Visitor;

public class ReadStatement extends AbsStatement implements Statement {
    private Expression readExpression;

    public ReadStatement(int line, int column, Expression readExpression) {
        super(line, column);
        this.readExpression = readExpression;
    }

    public Expression getReadExpression() {
        return readExpression;
    }

    public void setReadExpression(Expression readExpression) {
        this.readExpression = readExpression;
    }

    @Override
    protected String localeToString() {
        return "ReadStatement {\n" + "  expression -> " + readExpression.toString() + "\n" + "}";
    }

    @Override
    public <TP, TR> TR accept(Visitor<TP, TR> v, TP param) {
        return v.visit(this, param);
    }
}
