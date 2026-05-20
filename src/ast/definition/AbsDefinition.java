package ast.definition;

import ast.AbsLocatable;

public abstract class AbsDefinition extends AbsLocatable implements Definition {
    protected String name;
    protected int scope;

    public AbsDefinition(int line, int column, String name) {
        super(line, column);
        this.name = name;
    }
    

    @Override
    public String getName() {
        return this.name;
    }

    public int getScope() {
        return scope;
    }

    public void setScope(int i) {
        this.scope = i;
    }
}
