package ast.statement;

import ast.AbsLocalable;

public abstract class AbsStatement extends AbsLocalable implements Statement {

    public AbsStatement(int line, int column) {
        super(line, column);
    }
}
