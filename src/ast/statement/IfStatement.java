package ast.statement;


import ast.expression.Expression;
import semantics.Visitor;

import java.util.ArrayList;
import java.util.List;

public class IfStatement extends AbsStatement implements Statement {
    private List<Statement> ifBlock;
    private List<Statement> elseBlock;
    private Expression conditional;

    public IfStatement(int line, int column, Expression conditional, List<Statement> ifBlock,
                       List<Statement> elseBlock) {
        super(line, column);
        this.conditional = conditional;
        this.elseBlock = elseBlock;
        this.ifBlock = ifBlock;
    }

    public IfStatement(int line, int column, Expression conditional, List<Statement> ifBlock) {
        super(line, column);
        this.conditional = conditional;
        this.elseBlock = new ArrayList<Statement>();
        this.ifBlock = ifBlock;
    }

    public Expression getConditional() {
        return conditional;
    }

    public void setConditional(Expression conditional) {
        this.conditional = conditional;
    }

    public List<Statement> getElseBlock() {
        return elseBlock;
    }

    public void setElseBlock(List<Statement> elseBlock) {
        this.elseBlock = elseBlock;
    }

    public List<Statement> getIfBlock() {
        return ifBlock;
    }

    public void setIfBlock(List<Statement> ifBlock) {
        this.ifBlock = ifBlock;
    }


    @Override
    protected String localeToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IfStatement {\n");
        sb.append("  condition -> ").append(conditional.toString()).append("\n");

        sb.append("  ifBlock -> [\n");
        if (ifBlock != null) {
            for (Statement stmt : ifBlock) {
                sb.append("    ").append(stmt.toString().replace("\n", "\n    ")).append("\n");
            }
        }
        sb.append("  ]\n");

        if (elseBlock != null && !elseBlock.isEmpty()) {
            sb.append("  elseBlock -> [\n");
            for (Statement stmt : elseBlock) {
                sb.append("    ").append(stmt.toString().replace("\n", "\n    ")).append("\n");
            }
            sb.append("  ]\n");
        }

        sb.append("}");
        return sb.toString();
    }

    @Override
    public <TP, TR> TR accept(Visitor<TP, TR> v, TP param) {
        return v.visit(this, param);
    }
}
