package ast.expression;

import ast.Localable;
import ast.type.Type;

public interface Expression extends Localable {
    boolean getLvalue();

    void setLvalue(boolean lvalue);

    Type getType();
}
