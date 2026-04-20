package ast.statement;

import ast.expression.Expression;
import semantics.Visitor;

import java.util.List;

public class WhileStatement extends AbsStatement implements Statement {
    private List<Statement> whileBlock;
    private Expression condition;

    public WhileStatement(int line, int column, Expression condition, List<Statement> whileBlock) {
        super(line, column);
        this.condition = condition;
        this.whileBlock = whileBlock;
    }

    public Expression getCondition() {
        return condition;
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    public List<Statement> getWhileBlock() {
        return whileBlock;
    }

    public void setWhileBlock(List<Statement> whileBlock) {
        this.whileBlock = whileBlock;
    }

    @Override
    protected String localeToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("WhileStatement {\n");
        sb.append("  condition -> ").append(condition.toString()).append("\n");

        sb.append("  body -> [\n");
        if (whileBlock != null) {
            for (Statement stmt : whileBlock) {
                sb.append("    ").append(stmt.toString().replace("\n", "\n    ")).append("\n");
            }
        }
        sb.append("  ]\n");

        sb.append("}");
        return sb.toString();
    }

    @Override
    public <TP, TR> TR accept(Visitor<TP, TR> v, TP param) {
        return v.visit(this, param);
    }
}
