package codegeneration;

import ast.definition.VariableDefinition;
import ast.expression.Variable;

/**
 * ADDRESS visitor: genera código que deja la DIRECCIÓN de memoria
 * de una expresión lvalue en la cima de la pila.
 *
 * Solo se implementan nodos con lvalue = true:
 *
 * VARIABLE (local o parámetro, scope != 0)
 * address[[Variable : expression → ID]] =
 *      if(Variable.def.scope == 0){
 *          <pusha> expression.definition.offset
 *      }
 *      else {
 *        <push bp>
 *  *     <pushi> expression.definition.offset
 *  *     <addi>
 *      }
 *
 */
public class AddressCGVisitor extends AbsCGVisitor {

    public AddressCGVisitor(CodeGenerator cg) {
        super(cg);
    }

    @Override
    public Void visit(Variable variable, Void param) {
        VariableDefinition def = (VariableDefinition) variable.getDefinition();
        cg.line(variable.getLine());

        if (def.getScope() == 0) {
            // Variable global
            cg.pusha(def.getOffset());
        } else {
            // Variable local o parámetro: dirección relativa a BP
            cg.pushBP();
            cg.pushi(def.getOffset());
            cg.addi();
        }
        return null;
    }
}