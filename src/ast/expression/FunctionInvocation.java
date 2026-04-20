package ast.expression;


import ast.definition.FunctionDefinition;
import ast.statement.Statement;
import semantics.Visitor;

import java.util.List;

public class FunctionInvocation extends AbsExpression implements Expression, Statement {
    private Variable function;
    private List<Expression> parameters;

    public FunctionInvocation(int line, int column, Variable function,
                              List<Expression> parameters) {
        super(line, column);
        this.function = function;
        this.parameters = parameters;
    }

    public FunctionInvocation(int line, int column, String function, List<Expression> parameters) {
        super(line, column);
        this.function = new Variable(line, column, function);
        this.parameters = parameters;
    }

    public Variable getFunction() {
        return function;
    }

    public void setFunction(Variable function) {
        this.function = function;
    }

    public List<Expression> getParameters() {
        return parameters;
    }

    public void setParameters(List<Expression> parameters) {
        this.parameters = parameters;
    }


    @Override
    protected String localeToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FunctionInvocation{\n");
        sb.append("  function -> ").append(indent(function.toString())).append("\n");

        sb.append("  parameters -> ");
        if (parameters == null || parameters.isEmpty()) {
            sb.append("no params");
        }
        else {
            sb.append("[\n");
            for (int i = 0; i < parameters.size(); i++) {
                sb.append("    ").append(parameters.get(i).toString());
                if (i < parameters.size() - 1) {
                    sb.append("\n");
                }
            }
            sb.append("\n  ]");
        }
        sb.append("\n}");
        return sb.toString();
    }

    @Override
    public <TP, TR> TR accept(Visitor<TP, TR> v, TP param) {
        return v.visit(this, param);
    }

    public void setDefinition(FunctionDefinition def) {
        this.function.setDefinition(def);
    }
}
