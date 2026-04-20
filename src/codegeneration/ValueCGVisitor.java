package codegeneration;

import ast.expression.*;
import ast.type.*;

/**
 * VALUE visitor: genera código que deja el VALOR de una expresión
 * en la cima de la pila.
 *
 * CHAR LITERAL
 * value[[CharLiteral → CHAR_CONSTANT]] =
 *     <pushb> (int) CHAR_CONSTANT
 *
 * INT LITERAL
 * value[[IntLiteral → INT_CONSTANT]] =
 *     <pushi> INT_CONSTANT
 *
 * DOUBLE LITERAL
 * value[[DoubleLiteral → DOUBLE_CONSTANT]] =s
 *     <pushf> DOUBLE_CONSTANT
 *
 * VARIABLE (lvalue → rvalue: cargar de memoria)
 * value[[Variable → ID]] =
 *     address[[Variable]]
 *     <load> variable.type.suffix()
 *
 * ARITHMETIC OPERATION
 * value[[ArithmeticOp : e1 → e2 (+ | - | * | /) e3]] =
 *     value[[e2]] ; e2.type.convertTo(e1.type)
 *     value[[e3]] ; e3.type.convertTo(e1.type)
 *     <add|sub|mul|div> e1.type.suffix()
 *
 * MODULUS
 * value[[ModuleOperation : e1 → e2 % e3]] =
 *     value[[e2]] ; e2.type.convertTo(e1.type)
 *     value[[e3]] ; e3.type.convertTo(e1.type)
 *     <modi>
 *
 * UNARY MINUS
 * value[[UnaryMinus : e1 → e2]] =
 *     <push 0> (tipo e1)
 *     value[[e2]] ; e2.type.convertTo(e1.type)
 *     <sub> e1.type.suffix()
 *
 * UNARY NEGATION (!)
 * value[[UnaryNegation : e1 → e2]] =
 *     value[[e2]]
 *     <not>
 *
 * LOGICAL OPERATION
 * value[[LogicOperation : e1 → e2 (&& | ||) e3]] =
 *     value[[e2]] ; e2.type.convertTo(e1.type)
 *     value[[e3]] ; e3.type.convertTo(e1.type)
 *     <and|or>
 *
 * COMPARISON
 * value[[Comparation : e1 → e2 op e3]] =
 *     supertype = e2.type.supertype(e3.type)
 *     value[[e2]] ; e2.type.convertTo(supertype)
 *     value[[e3]] ; e3.type.convertTo(supertype)
 *     <lt|gt|le|ge|eq|ne> supertype.suffix()
 *
 * CAST
 * value[[Cast : e1 → (type) e2]] =
 *     value[[e2]]
 *     e2.type.convertTo(e1.type)

 */
public class ValueCGVisitor extends AbsCGVisitor {

    private final AddressCGVisitor addressCGVisitor;

    public ValueCGVisitor(CodeGenerator cg, AddressCGVisitor addressCGVisitor) {
        super(cg);
        this.addressCGVisitor = addressCGVisitor;
    }

    // Constants

    @Override
    public Void visit(ArithmeticOperation arith, Void param) {
        cg.line(arith.getLine());
        arith.getLeftExpression().accept(this, param);
        cg.convert(arith.getLeftExpression().getType(), arith.getType());
        arith.getRightExpression().accept(this, param);
        cg.convert(arith.getRightExpression().getType(), arith.getType());
        cg.arithmetic(arith.getOperator(), arith.getType());
        return null;
    }

    @Override
    public Void visit(Cast cast, Void param) {
        cg.line(cast.getLine());
        cast.getCastExpression().accept(this, param);
        cg.convert(cast.getCastExpression().getType(), cast.getType());
        return null;
    }

    @Override
    public Void visit(CharLiteral charLiteral, Void param) {
        cg.line(charLiteral.getLine());
        cg.pushb((int) charLiteral.getValue());
        return null;
    }

    // Variable (lvalue → rvalue)

    @Override
    public Void visit(Comparation comparation, Void param) {
        cg.line(comparation.getLine());

        // supertype: tipo común de los dos operandos usando el método del tipo
        Type supertype = comparation.getLeftExpression().getType()
                .supertype(comparation.getRightExpression().getType());

        comparation.getLeftExpression().accept(this, param);
        cg.convert(comparation.getLeftExpression().getType(), supertype);
        comparation.getRightExpression().accept(this, param);
        cg.convert(comparation.getRightExpression().getType(), supertype);
        cg.comparison(comparation.getOperator(), supertype);
        return null;
    }

    // Arithmetic

    @Override
    public Void visit(DoubleLiteral doubleLiteral, Void param) {
        cg.line(doubleLiteral.getLine());
        cg.pushf(doubleLiteral.getValue());
        return null;
    }

    // Modulus

    @Override
    public Void visit(IntLiteral intLiteral, Void param) {
        cg.line(intLiteral.getLine());
        cg.pushi(intLiteral.getValue());
        return null;
    }

    // Unary minus

    @Override
    public Void visit(LogicOperation logic, Void param) {
        cg.line(logic.getLine());
        logic.getLeftExpression().accept(this, param);
        cg.convert(logic.getLeftExpression().getType(), logic.getType());
        logic.getRightExpression().accept(this, param);
        cg.convert(logic.getRightExpression().getType(), logic.getType());
        cg.logical(logic.getOperator());
        return null;
    }

    // Unary negation (!)

    @Override
    public Void visit(ModuleOperation module, Void param) {
        cg.line(module.getLine());
        module.getLeftExpression().accept(this, param);
        cg.convert(module.getLeftExpression().getType(), module.getType());
        module.getRightExpression().accept(this, param);
        cg.convert(module.getRightExpression().getType(), module.getType());
        cg.modulus(module.getType());
        return null;
    }

    // Logical operation

    @Override
    public Void visit(UnaryMinus unaryMinus, Void param) {
        cg.line(unaryMinus.getLine());
        Type t = unaryMinus.getType();
        if (t instanceof DoubleType) cg.pushf(0.0);
        else if (t instanceof IntType) cg.pushi(0);
        else cg.pushb(0);
        unaryMinus.getExpression().accept(this, param);
        cg.convert(unaryMinus.getExpression().getType(), t);
        cg.arithmetic("-", t);
        return null;
    }

    //  Comparison

    @Override
    public Void visit(UnaryNegation unaryNegation, Void param) {
        cg.line(unaryNegation.getLine());
        unaryNegation.getExpression().accept(this, param);
        cg.not();
        return null;
    }

    // Cast

    @Override
    public Void visit(Variable variable, Void param) {
        cg.line(variable.getLine());
        variable.accept(addressCGVisitor, param);
        cg.load(variable.getType());
        return null;
    }
}