package ast.expression;

import semantics.Visitor;

public class ArithmeticOperation extends AbsExpression implements Expression {
    private String operator;
    private Expression rightExpression;
    private Expression leftExpression;

    public ArithmeticOperation(int line, int column, Expression leftExpression, String operator,
                               Expression rightExpression) {
        super(line, column);
        this.leftExpression = leftExpression;
        this.operator = operator;
        this.rightExpression = rightExpression;
    }

    public Expression getLeftExpression() {
        return leftExpression;
    }

    public void setLeftExpression(Expression leftExpression) {

        this.leftExpression = leftExpression;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Expression getRightExpression() {
        return rightExpression;
    }

    public void setRightExpression(Expression rightExpression) {
        this.rightExpression = rightExpression;
    }


    @Override
    protected String localeToString() {
        return "ArithmeticOperation{\n" + "  left -> " + indent(
                leftExpression.toString()) + "'\n" + "  operator -> '" + operator + "\n" + "  " + "right -> " + indent(
                rightExpression.toString()) + "\n" + "}";
    }


    @Override
    public <TP, TR> TR accept(Visitor<TP, TR> v, TP param) {
        return v.visit(this, param);
    }
}
