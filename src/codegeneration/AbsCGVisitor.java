package codegeneration;

import semantics.AbsVisitor;

/**
 * Clase base para los tres visitors de generación de código.
 * <p>
 * Hereda de AbsVisitor<Void, Void> para reutilizar el comportamiento
 * por defecto (recorrido del árbol sin hacer nada).
 * <p>
 * ADDRESS : visita expresiones con lvalue (Variable, SquareBrackets, Access)
 * deja una dirección de memoria en la pila
 * VALUE   : visita todas las expresiones
 * deja el valor de la expresión en la pila
 * EXECUTE : visita Program, FunctionDefinition, Statements
 * genera código con efectos laterales (no deja valor en pila)
 */
public abstract class AbsCGVisitor extends AbsVisitor<Void, Void> {

    protected final CodeGenerator cg;

    public AbsCGVisitor(CodeGenerator cg) {
        this.cg = cg;
    }
}