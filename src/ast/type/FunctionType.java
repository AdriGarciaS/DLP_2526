package ast.type;

import ast.definition.VariableDefinition;
import semantics.Visitor;

import java.util.List;

public class FunctionType extends AbsType implements Type {
    private List<VariableDefinition> parameters;
    private Type returnType;

    public FunctionType(List<VariableDefinition> parameters, Type returnType) {
        this.parameters = parameters;
        this.returnType = returnType;
    }

    // TODO ??
    @Override
    public int numberOfBytes() {
        return 0;
    }

    @Override
    public Type parenthesis(List<Type> argTypes, int line, int col) {
        if (parameters.size() != argTypes.size()) {
            return new ErrorType(line, col,
                    "Function expects " + parameters.size() + " arguments but received " + argTypes.size());
        }

        for (int i = 0; i < parameters.size(); i++) {
            Type expected = parameters.get(i).getVariableType();
            Type received = argTypes.get(i);
            if (!received.isPromotableTo(expected)) {
                return new ErrorType(line, col,
                        "Argument " + (i + 1) + ": expected " + expected + " but got " + received);
            }
        }
        return returnType;
    }

    @Override
    public void mustBeReadable(int line, int col) {
        new ErrorType(line, col, "Cannot read a function");
    }

    @Override
    public void mustBeWritable(int line, int col) {
        new ErrorType(line, col, "Cannot write to a function");
    }

    @Override
    public void mustBeComparison(int line, int col) {
        new ErrorType(line, col, "Cannot use a function as a condition");
    }

    @Override
    public void mustBeReturnable(Type expectedReturnType, int line, int col) {
        new ErrorType(line, col, "Cannot return a function");
    }

    public List<VariableDefinition> getParameters() {
        return parameters;
    }

    public void setParameters(List<VariableDefinition> parameters) {
        this.parameters = parameters;
    }

    public Type getReturnType() {
        return returnType;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    @Override
    public String toString() {
        return "FUNCTION ( " + parameters + " ) -> " + returnType;
    }

    @Override
    public <TP, TR> TR accept(Visitor<TP, TR> v, TP param) {
        return v.visit(this, param);
    }
}