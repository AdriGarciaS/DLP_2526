package ast.expression;

import semantics.Visitor;

public class ModuleOperation extends AbsExpression implements Expression {

    private String operator;
    private Expression rightExpression;
    private Expression leftExpression;


    public ModuleOperation(int line, int column, Expression leftExpression, String operator,
                           Expression rightExpression) {
        super(line, column);
        this.leftExpression = leftExpression;
        this.operator = operator;
        this.rightExpression = rightExpression;
    }


    @Override
    protected String localeToString() {
        return "ModuleOperation{\n" + "  left -> " + indent(
                leftExpression.toString()) + "'\n" + "  operator -> '" + operator + "\n" + "  " + "right -> " + indent(
                rightExpression.toString()) + "\n" + "}";
    }

    @Override
    public <TP, TR> TR accept(Visitor<TP, TR> v, TP param) {
        return v.visit(this, param);
    }

    public Expression getRightExpression() {
        return rightExpression;
    }

    public Expression getLeftExpression() {
        return leftExpression;
    }
}
