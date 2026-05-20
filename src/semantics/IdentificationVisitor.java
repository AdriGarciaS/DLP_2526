package semantics;

import ast.definition.Definition;
import ast.definition.FunctionDefinition;
import ast.definition.VariableDefinition;
import ast.expression.FunctionInvocation;
import ast.expression.Variable;
import ast.type.ErrorType;
import semantics.symbolTable.SymbolTable;

public class IdentificationVisitor extends AbsVisitor<Void, Void> {

    /*
     * DEFINITIONS
     * (P) FunctionDefinition: definition -> functionType ID vardef* statement*
     * (R) if (!st.insert(definition))
     *         new ErrorType(line, col, "Function already defined")
     *     st.set()
     *     definition.getParameters().forEach(p -> p.accept(this, param))
     *     definition.getLocalVariables().forEach(v -> v.accept(this, param))
     *     definition.getBodyStatements().forEach(s -> s.accept(this, param))
     *     st.reset()
     *
     * (P) VariableDefinition: definition -> type ID
     * (R) if (!st.insert(definition))
     *         new ErrorType(line, col, "Variable already defined")
     *     definition.getVariableType().accept(this, param)
     *
     * EXPRESSIONS
     * (P) Variable: expression -> ID
     * (R) Definition def = st.find(variable.getName())
     *     if (def == null)
     *         new ErrorType(line, col, "Variable not defined")
     *     else
     *         variable.setDefinition(def)
     *
     * (P) FunctionInvocation: expression -> ID expression*
     * (R) Definition def = st.find(funcInvocation.getFunction().getName())
     *     if (def == null)
     *         new ErrorType(line, col, "Function not defined")
     *     else if (!(def instanceof FunctionDefinition))
     *         new ErrorType(line, col, "Not a function")
     *     else
     *         funcInvocation.setDefinition((FunctionDefinition) def)
     *     funcInvocation.getParameters().forEach(p -> p.accept(this, param))
     */


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
        definition.getFullBody().forEach( sentence -> sentence.accept(this, param));
        //definition.getLocalVariables().forEach(local -> local.accept(this, param));
        //definition.getBodyStatements().forEach(stmt -> stmt.accept(this, param));

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