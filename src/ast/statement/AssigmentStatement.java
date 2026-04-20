package ast.statement;

import ast.expression.Expression;
import semantics.Visitor;

public class AssigmentStatement extends AbsStatement implements Statement {
    private Expression variable;
    private Expression value;

    public AssigmentStatement(int line, int column, Expression variable, Expression value) {
        super(line, column);
        this.value = value;
        this.variable = variable;
    }

    public Expression getValue() {
        return value;
    }

    public void setValue(Expression value) {
        this.value = value;
    }

    public Expression getVariable() {
        return variable;
    }

    public void setVariable(Expression variable) {
        this.variable = variable;
    }


    @Override
    protected String localeToString() {
        return "AssignmentStatement {\n" + "  variable -> " + variable.toString() + "\n" + "  " + "value -> " + value.toString() + "\n" + "}";
    }


    public Expression getLHS() {
        return this.variable;
    }

    public Expression getRHS() {
        return this.value;
    }

    @Override
    public <TP, TR> TR accept(Visitor<TP, TR> v, TP param) {
        return v.visit(this, param);
    }
}
