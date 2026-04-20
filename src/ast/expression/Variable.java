package ast.expression;

import ast.definition.Definition;
import semantics.Visitor;

public class Variable extends AbsExpression implements Expression {
    private String name;
    private Definition definition;

    public Variable(int line, int column, String name) {

        super(line, column);
        this.name = name;
    }

    public Definition getDefinition() {
        return definition;
    }

    public void setDefinition(Definition definition) {
        this.definition = definition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected String localeToString() {
        return "Variable -> " + name;
    }

    @Override
    public <TP, TR> TR accept(Visitor<TP, TR> v, TP param) {
        return v.visit(this, param);
    }
}
