package ast.definition;

import ast.Locatable;
import ast.type.Type;

public interface Definition extends Locatable {
    String getName();

    public int getScope();

    public void setScope(int i);

    Type getType();
}
