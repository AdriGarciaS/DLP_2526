package ast;

import ast.definition.Definition;
import semantics.Visitor;

import java.util.List;

public class Program implements ASTNode {
    private List<Definition> definitions;

    public Program(List<Definition> definitions) {
        this.definitions = definitions;
    }

    public List<Definition> getDefinitions() {
        return definitions;

    }

    public void setDefinitions(List<Definition> definitions) {
        this.definitions = definitions;
    }

    @Override
    public String toString() {
        if (definitions == null || definitions.isEmpty()) {
            return "Program { empty }";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Program {\n");

        for (int i = 0; i < definitions.size(); i++) {
            sb.append("  ").append(definitions.get(i).toString());
            if (i < definitions.size() - 1) {
                sb.append("\n"); // Línea en blanco entre definiciones
            }
        }

        sb.append("\n}");
        return sb.toString();
    }


    @Override
    public <TP, TR> TR accept(Visitor<TP, TR> v, TP param) {
        return v.visit(this, param);
    }
}
