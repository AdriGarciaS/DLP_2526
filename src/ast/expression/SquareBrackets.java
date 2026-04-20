package ast.expression;

import semantics.Visitor;

public class SquareBrackets extends AbsExpression implements Expression {
    private Expression outsideExpression;
    private Expression bracketsExpression;

    public SquareBrackets(int line, int column, Expression outsideExpression,
                          Expression bracketsExpression) {
        super(line, column);
        this.bracketsExpression = bracketsExpression;
        this.outsideExpression = outsideExpression;
    }

    public Expression getBracketsExpression() {
        return bracketsExpression;
    }

    public void setBracketsExpression(Expression bracketsExpression) {
        this.bracketsExpression = bracketsExpression;
    }

    public Expression getOutsideExpression() {
        return outsideExpression;
    }

    public void setOutsideExpression(Expression outsideExpression) {
        this.outsideExpression = outsideExpression;
    }


    @Override
    protected String localeToString() {
        return outsideExpression.toString() + "[ " + bracketsExpression.toString() + " ]";
    }

    @Override
    public <TP, TR> TR accept(Visitor<TP, TR> v, TP param) {
        return v.visit(this, param);
    }
}
