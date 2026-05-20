package ast.statement;

import ast.AbsLocatable;

public abstract class AbsStatement extends AbsLocatable implements Statement {

    public AbsStatement(int line, int column) {
        super(line, column);
    }
}
