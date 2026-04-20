package ast.definition;

import ast.Localable;
import ast.type.Type;

public interface Definition extends Localable {
    String getName();

    public int getScope();

    public void setScope(int i);

    Type getType();
}
