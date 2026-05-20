package codegeneration;

import ast.definition.VariableDefinition;
import ast.expression.Access;
import ast.expression.SquareBrackets;
import ast.expression.Variable;
import ast.type.IntType;
import ast.type.RecordField;
import ast.type.RecordType;

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
 * SQUARE BRACKETS
 * address[[SquareBrackets : e1 → e2[e3]]] =
 *     address[[e2]]
 *     value[[e3]]
 *     e3.type.convertTo(IntType)
 *     pushi e1.type.numberOfBytes()
 *     muli
 *     addi
 *
 * ACCESS
 * address[[Access : e1 → e2.ID]] =
 *     address[[e2]]
 *     pushi e1.definition.offset
 *     addi
 *
 */
public class AddressCGVisitor extends AbsCGVisitor {

    private ValueCGVisitor valueCGVisitor;

    public AddressCGVisitor(CodeGenerator cg) {
        super(cg);
    }

    public void setValueCGVisitor(ValueCGVisitor valueCGVisitor) {
        this.valueCGVisitor = valueCGVisitor;
    }

    @Override
    public Void visit(Access access, Void param) {
        cg.line(access.getLine());
        access.getExpression().accept(this, param);
        RecordType record = (RecordType) access.getExpression().getType();
        int fieldOffset = record.getRecordFields().stream()
                .filter(f -> f.getName().equals(access.getFieldname()))
                .findFirst()
                .get()
                .getOffset();
        cg.pushi(fieldOffset);
        cg.addi();
        return null;
    }

    @Override
    public Void visit(SquareBrackets sq, Void param) {
        cg.line(sq.getLine());
        sq.getOutsideExpression().accept(this, param);
        sq.getBracketsExpression().accept(valueCGVisitor, param);
        cg.convert(sq.getBracketsExpression().getType(), new IntType());
        cg.pushi(sq.getType().numberOfBytes());
        cg.muli();
        cg.addi();
        return null;
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