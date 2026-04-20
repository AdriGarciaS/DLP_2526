package ast;

import semantics.Visitor;

public interface ASTNode {
    public <TP, TR>  TR accept(Visitor<TP, TR> v, TP param);
}
