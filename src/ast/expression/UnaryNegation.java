package ast.expression;

import semantics.Visitor;

public class UnaryNegation extends AbsExpression implements Expression {
    private Expression expression;

    public UnaryNegation(int line, int column, Expression expression) {

        super(line, column);
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }


    @Override
    protected String localeToString() {
        return "UnaryNegation{\n" + "  expression -> " + indent(expression.toString()) + "\n" + "}";
    }

    @Override
    public <TP, TR> TR accept(Visitor<TP, TR> v, TP param) {
        return v.visit(this, param);
    }
}
