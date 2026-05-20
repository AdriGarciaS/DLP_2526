package ast.expression;

import ast.Locatable;
import ast.type.Type;

public interface Expression extends Locatable {
    boolean getLvalue();

    void setLvalue(boolean lvalue);

    Type getType();
}
