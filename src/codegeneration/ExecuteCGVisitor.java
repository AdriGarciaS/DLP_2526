package codegeneration;

import ast.Program;
import ast.definition.FunctionDefinition;
import ast.definition.VariableDefinition;
import ast.expression.FunctionInvocation;
import ast.statement.*;
import ast.type.FunctionType;
import ast.type.Type;
import ast.type.VoidType;

/*
 *
 * PROGRAM
 * execute[[Program → definition*]] =
 *     <#source> sourceFile
 *     ' Global vars comment
 *     <call main>
 *     <halt>
 *     for each definition: execute[[definition]]
 *
 * FUNCTION DEFINITION
 * execute[[FunctionDef → type ID varDef* statement*]] =
 *     <label> ID:
 *     ' params/locals comment
 *     <enter> localBytes
 *     for each statement: execute[[statement]]
 *     if returnType == void: <ret 0, localBytes, paramBytes>
 *
 * WRITE
 * execute[[Write → expression]] =
 *     value[[expression]]
 *     <out> expression.type.suffix()
 *
 * READ
 * execute[[Read → expression]] =
 *     address[[expression]]
 *     <in> expression.type.suffix()
 *     <store> expression.type.suffix()
 *
 * ASSIGNMENT
 * execute[[Assignment → lhs = rhs]] =
 *     address[[lhs]]
 *     value[[rhs]]
 *     rhs.type.convertTo(lhs.type)
 *     <store> lhs.type.suffix()
 */
public class ExecuteCGVisitor extends AbsCGVisitor {

    private final ValueCGVisitor valueCGVisitor;
    private final AddressCGVisitor addressCGVisitor;
    private Type currentReturnType;
    private int currentLocalBytes;
    private int currentParamBytes;

    public ExecuteCGVisitor(CodeGenerator cg, ValueCGVisitor valueCGVisitor,
                            AddressCGVisitor addressCGVisitor) {
        super(cg);
        this.valueCGVisitor = valueCGVisitor;
        this.addressCGVisitor = addressCGVisitor;
    }

    @Override
    public Void visit(Program program, Void param) {
        cg.sectionComment("Global variables:");
        for (var def : program.getDefinitions()) {
            if (def instanceof VariableDefinition vd) {
                cg.sectionComment("  " + vd.getVariableType() + " " + vd.getName()
                        + " (offset " + vd.getOffset() + ")");
            }
        }
        cg.call("main");
        cg.halt();

        for (var def : program.getDefinitions()) {
            if (def instanceof FunctionDefinition) {
                def.accept(this, param);
            }
        }
        return null;
    }

    @Override
    public Void visit(FunctionDefinition funcDef, Void param) {
        cg.line(funcDef.getLine());
        cg.label(funcDef.getName());

        FunctionType ft = funcDef.getFunctionType();

        cg.comment("Parameters");
        for (var p : ft.getParameters()) {
            cg.comment("  " + p.getVariableType() + " " + p.getName()
                    + " (offset " + p.getOffset() + ")");
        }
        cg.comment("Local variables");
        for (var v : funcDef.getLocalVariables()) {
            cg.comment("  " + v.getVariableType() + " " + v.getName()
                    + " (offset " + v.getOffset() + ")");
        }

        int localBytes = funcDef.getLocalVariables().stream()
                .mapToInt(v -> v.getVariableType().numberOfBytes()).sum();
        int paramBytes = ft.getParameters().stream()
                .mapToInt(v -> v.getVariableType().numberOfBytes()).sum();

        cg.enter(localBytes);

        currentReturnType = ft.getReturnType();
        currentLocalBytes = localBytes;
        currentParamBytes = paramBytes;

        funcDef.getBodyStatements().forEach(s -> s.accept(this, param));

        if (ft.getReturnType() instanceof VoidType) {
            cg.ret(0, localBytes, paramBytes);
        }
        return null;
    }

    @Override
    public Void visit(IfStatement ifStatement, Void param) {
        cg.line(ifStatement.getLine());
        String elseLabel = cg.nextLabel();
        String exitLabel = cg.nextLabel();
        cg.comment("If statement");
        ifStatement.getConditional().accept(valueCGVisitor, param);
        cg.jz(elseLabel);
        cg.comment("Body of the if branch");
        ifStatement.getIfBlock().forEach(s -> s.accept(this, param));
        cg.jmp(exitLabel);
        cg.labelDef(elseLabel);
        cg.comment("Body of the else branch");
        ifStatement.getElseBlock().forEach(s -> s.accept(this, param));
        cg.labelDef(exitLabel);
        return null;
    }

    @Override
    public Void visit(ReadStatement read, Void param) {
        cg.line(read.getLine());
        cg.comment("Read");
        read.getReadExpression().accept(addressCGVisitor, param);
        cg.in(read.getReadExpression().getType());
        cg.store(read.getReadExpression().getType());
        return null;
    }

    @Override
    public Void visit(WriteStatement write, Void param) {
        cg.line(write.getLine());
        cg.comment("Write");
        write.getWriteExpression().accept(valueCGVisitor, param);
        cg.out(write.getWriteExpression().getType());
        return null;
    }

    @Override
    public Void visit(ReturnStatement returnStatement, Void param) {
        cg.line(returnStatement.getLine());
        cg.comment("Return");
        returnStatement.getReturned().accept(valueCGVisitor, param);
        cg.convert(returnStatement.getReturned().getType(), currentReturnType);
        cg.ret(currentReturnType.numberOfBytes(), currentLocalBytes, currentParamBytes);
        return null;
    }

    @Override
    public Void visit(WhileStatement whileStatement, Void param) {
        cg.line(whileStatement.getLine());
        String condLabel = cg.nextLabel();
        String exitLabel = cg.nextLabel();
        cg.comment("While");
        cg.labelDef(condLabel);
        whileStatement.getCondition().accept(valueCGVisitor, param);
        cg.jz(exitLabel);
        cg.comment("Body of the while statement");
        whileStatement.getWhileBlock().forEach(s -> s.accept(this, param));
        cg.jmp(condLabel);
        cg.labelDef(exitLabel);
        return null;
    }

    @Override
    public Void visit(AssigmentStatement assign, Void param) {
        cg.line(assign.getLine());
        assign.getLHS().accept(addressCGVisitor, param);
        assign.getRHS().accept(valueCGVisitor, param);
        cg.convert(assign.getRHS().getType(), assign.getLHS().getType());
        cg.store(assign.getLHS().getType());
        return null;
    }

    /*
     * FUNCTION INVOCATION (como sentencia)
     * execute[[FunctionInvocation : statement → ID expression*]] =
     *     for each arg: value[[arg]]
     *     <call> ID
     *     if returnType != void: <pop> returnType.suffix()
     *
     * RETURN STATEMENT
     * execute[[ReturnStatement : statement → expression]] =
     *     value[[expression]]
     *     convert expression.type → returnType
     *     <ret> returnBytes, localBytes, paramBytes
     */
    @Override
    public Void visit(FunctionInvocation funcInvocation, Void param) {
        cg.line(funcInvocation.getLine());
        funcInvocation.getParameters().forEach(p -> p.accept(valueCGVisitor, param));
        cg.call(funcInvocation.getFunction().getName());
        FunctionType ft = (FunctionType) funcInvocation.getFunction().getDefinition().getType();
        if (!(ft.getReturnType() instanceof VoidType)) {
            cg.pop(ft.getReturnType());
        }
        return null;
    }

}