package semantics;

import ast.definition.Definition;
import ast.definition.FunctionDefinition;
import ast.definition.VariableDefinition;
import ast.expression.FunctionInvocation;
import ast.expression.Variable;
import ast.type.ErrorType;
import semantics.symbolTable.SymbolTable;

public class IdentificationVisitor extends AbsVisitor<Void, Void> {

    private SymbolTable st = new SymbolTable();
    

    @Override
    public Void visit(FunctionDefinition definition, Void param) {
        // 1. Registrar la función en el ámbito actual (global)
        if (!st.insert(definition)) {
            Definition firstDef = st.find(definition.getName());
            new ErrorType(definition.getLine(), definition.getColumn(),
                    "Function '" + definition.getName() + "' already defined at line " + firstDef.getLine());
        }

        // 2. Abrir nuevo ámbito para parámetros y variables locales
        st.set();

        definition.getFunctionType().getParameters().forEach(p -> p.accept(this, param));
        definition.getLocalVariables().forEach(local -> local.accept(this, param));
        definition.getBodyStatements().forEach(stmt -> stmt.accept(this, param));

        // 3. Cerrar ámbito
        st.reset();
        return null;
    }

    @Override
    public Void visit(VariableDefinition definition, Void param) {
        if (!st.insert(definition)) {
            Definition firstDef = st.find(definition.getName());
            new ErrorType(definition.getLine(), definition.getColumn(),
                    "Variable '" + definition.getName() + "' already defined at line " + firstDef.getLine());
        }
        definition.getVariableType().accept(this, param);
        return null;
    }

    @Override
    public Void visit(FunctionInvocation funcInvocation, Void param) {
        Definition def = st.find(funcInvocation.getFunction().getName());
        if (def == null) {
            new ErrorType(funcInvocation.getLine(), funcInvocation.getColumn(),
                    "Function '" + funcInvocation.getFunction().getName() + "' is not defined");
        }
        else if (!(def instanceof FunctionDefinition)) {
            new ErrorType(funcInvocation.getLine(), funcInvocation.getColumn(),
                    "'" + funcInvocation.getFunction().getName() + "' is not a function");
        }
        else {
            funcInvocation.setDefinition((FunctionDefinition) def);
        }

        // Visitar argumentos
        funcInvocation.getParameters().forEach(p -> p.accept(this, param));
        return null;
    }

    @Override
    public Void visit(Variable variable, Void param) {
        Definition def = st.find(variable.getName());
        if (def == null) {
            new ErrorType(variable.getLine(), variable.getColumn(),
                    "Variable '" + variable.getName() + "' is not defined");
        }
        else {
            variable.setDefinition(def);
        }
        return null;
    }
}