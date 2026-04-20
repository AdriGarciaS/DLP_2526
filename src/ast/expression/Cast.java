package ast.expression;

import ast.type.Type;
import semantics.Visitor;

public class Cast extends AbsExpression implements Expression {
    private Type castType;
    private Expression castExpression;

    public Cast(int line, int column, Type castType, Expression castExpression) {
        super(line, column);
        this.castExpression = castExpression;
        this.castType = castType;
    }


    public Expression getCastExpression() {
        return castExpression;
    }

    public void setCastExpression(Expression castExpression) {
        this.castExpression = castExpression;
    }

    public Type getCastType() {
        return castType;
    }

    public void setCastType(Type castType) {
        this.castType = castType;
    }

    @Override
    public String toString() {
        return localeToString();
    }

    @Override
    protected String localeToString() {
        return "Cast{\n" + "  type -> " + castType + "\n" + "  expression -> " + indent(
                castExpression.toString()) + "\n" + "}";
    }

    @Override
    public <TP, TR> TR accept(Visitor<TP, TR> v, TP param) {
        return v.visit(this, param);
    }
}
