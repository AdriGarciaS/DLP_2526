package ast.statement;

import ast.expression.Expression;
import semantics.Visitor;

public class WriteStatement extends AbsStatement implements Statement {
    private Expression writeExpression;

    public WriteStatement(int line, int column, Expression writeExpression) {
        super(line, column);
        this.writeExpression = writeExpression;
    }

    public Expression getWriteExpression() {
        return writeExpression;
    }

    public void setWriteExpression(Expression writeExpression) {
        this.writeExpression = writeExpression;
    }

    @Override
    protected String localeToString() {
        return "WriteStatement {\n" + "  expression -> " + writeExpression.toString() + "\n" + "}";
    }

    @Override
    public <TP, TR> TR accept(Visitor<TP, TR> v, TP param) {
        return v.visit(this, param);
    }
}
