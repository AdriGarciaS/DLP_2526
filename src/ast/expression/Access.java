package ast.expression;

import semantics.Visitor;

public class Access extends AbsExpression implements Expression {
    private String fieldname;
    private Expression expression;

    public Access(int line, int column, Expression exp, String fieldname) {
        super(line, column);
        this.fieldname = fieldname;
        this.expression = exp;

    }

    public String getFieldname() {
        return fieldname;
    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }


    @Override
    protected String localeToString() {
        return "UnaryAccess{\n" + "  field -> '" + fieldname + "'\n" + "  expression -> " + indent(
                expression.toString()) + "\n" + "}";
    }


    @Override
    public <TP, TR> TR accept(Visitor<TP, TR> v, TP param) {
        return v.visit(this, param);
    }
}
