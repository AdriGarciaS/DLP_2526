package ast.type;

import ast.ASTNode;

import java.util.List;

// Interfaz: solo la declaración de métodos (contrato)
public interface Type extends ASTNode {
    int numberOfBytes();

    boolean isPromotableTo(Type other);

    boolean isBuiltIn();

    Type supertype(Type other);

    /**
     * Returns the MAPL instruction suffix for this type:
     *   char   → "b"
     *   int    → "i"
     *   double → "f"
     */
    String suffix();

    Type arithmetic(Type other, int line, int col);

    Type modulus(Type other, int line, int col);

    Type logical(Type other, int line, int col);

    Type logicalNot(int line, int col);

    Type comparison(Type other, int line, int col);

    Type unaryMinus(int line, int col);

    Type squareBrackets(Type indexType, int line, int col);

    Type dot(String fieldName, int line, int col);

    Type cast(Type targetType, int line, int col);

    Type mustBeAssignable(Type rhs, int line, int col);

    Type parenthesis(List<Type> argTypes, int line, int col);

    void mustBeReadable(int line, int col);

    void mustBeWritable(int line, int col);

    void mustBeComparison(int line, int col);

    void mustBeReturnable(Type expectedReturnType, int line, int col);
}