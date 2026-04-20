package ast;

public interface Localable extends ASTNode {
    int getLine();
    int getColumn();
}
