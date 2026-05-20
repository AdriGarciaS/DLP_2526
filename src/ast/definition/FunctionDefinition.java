package ast.definition;

import ast.Locatable;
import ast.statement.Statement;
import ast.type.FunctionType;
import ast.type.Type;
import semantics.Visitor;

import java.util.List;

public class FunctionDefinition extends AbsDefinition implements Definition {
    private List<Statement> bodyStatements;
    private List<VariableDefinition> localVariables;
    private FunctionType functionType;
    private List<Locatable> fullBody;

    public FunctionDefinition(int line, int column, Type returnType, String name,
                              List<VariableDefinition> args,
                              List<VariableDefinition> localVariables,
                              List<Statement> bodyStatements, List<Locatable> fullBody) {
        super(line, column, name);
        this.bodyStatements = bodyStatements;
        this.functionType = new FunctionType(args, returnType);
        this.localVariables = localVariables;
        this.fullBody = fullBody;
    }

    public List<Statement> getBodyStatements() {
        return bodyStatements;
    }

    public void setBodyStatements(List<Statement> bodyStatements) {
        this.bodyStatements = bodyStatements;
    }

    public FunctionType getFunctionType() {
        return functionType;
    }

    public void setFunctionType(FunctionType functionType) {
        this.functionType = functionType;
    }

    public List<VariableDefinition> getLocalVariables() {
        return localVariables;
    }

    public void setLocalVariables(List<VariableDefinition> localVariables) {
        this.localVariables = localVariables;
    }

    public List<Locatable> getFullBody() {
        return fullBody;
    }

    public void setFullBody(List<Locatable> fullBody) {
        this.fullBody = fullBody;
    }

    @Override
    protected String localeToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FunctionDefinition{ name ->")
                .append(name)
                .append(" : return type ->")
                .append(functionType.getReturnType())
                .append("\n ( params -> ");

        // Parámetros
        List<VariableDefinition> params = functionType.getParameters();
        if (params == null || params.isEmpty()) {
            sb.append("no params");
        }
        else {
            for (int i = 0; i < params.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                VariableDefinition param = params.get(i);
                sb.append(param.getVariableType()).append(" ").append(param.getName());
            }
        }
        sb.append(")");

        // Variables locales
        sb.append("\n locals -> ");
        if (localVariables == null || localVariables.isEmpty()) {
            sb.append("no locals");
        }
        else {
            sb.append("[");
            for (int i = 0; i < localVariables.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                VariableDefinition local = localVariables.get(i);
                sb.append(local.getVariableType()).append(" ").append(local.getName());
            }
            sb.append("]");
        }

        // Cuerpo
        sb.append("\n body -> ");
        if (bodyStatements == null || bodyStatements.isEmpty()) {
            sb.append("no statements in body");
        }
        else {
            sb.append("[\n");
            for (int i = 0; i < bodyStatements.size(); i++) {
                sb.append("\t\t").append(bodyStatements.get(i).toString());
                if (i < bodyStatements.size() - 1) {
                    sb.append("\n");
                }
            }
            sb.append("\n\t]");
        }

        sb.append("\n}");
        return sb.toString();
    }

    @Override
    public <TP, TR> TR accept(Visitor<TP, TR> v, TP param) {
        return v.visit(this, param);
    }

    @Override
    public Type getType() {
        return this.functionType;
    }
}
