package codegeneration;

import ast.Program;
import ast.definition.FunctionDefinition;
import ast.definition.VariableDefinition;
import ast.type.FunctionType;
import ast.type.RecordField;
import ast.type.RecordType;
import semantics.AbsVisitor;

public class OffsetVisitor extends AbsVisitor<Void, Void> {

    /*
     *
     * Globals
     * P : Program : program -> definition*
     * R : int accumulativeOffset = 0;
     *     for (Definition def : definition*){
     *         if (def instanceof VariableDefinition){
     *             def.offset = accumulativeOffset;
     *             accumulativeOffset += def.type.numberOfBytes();
     *         }
     *     }
     *
     * Locals
     * P : FunctionDefinition : definition -> type ID variableDefinition* statements*
     * R : int accumulativeOffset = 0;
     *     for (variableDefinition var : variableDefinition*){
     *         accumulativeOffset += var.type.numberOfBytes();
     *         var.offset = -accumulativeOffset;
     *     }
     *
     * Parameters
     * P : FunctionDefinition : definition -> type ID variableDefinition* statements*
     * R : int accumulativeOffset = 0;
     *     for (variableDefinition var : variableDefinition*.reversed()){
     *         var.offset = 4 + accumulativeOffset;
     *         accumulativeOffset += var.type.numberOfBytes();
     *     }
     *
     * RecordFields
     * P : RecordType : type -> recordField*
     * R : int accumulativeOffset = 0;
     *     for (RecordField rec : recordFields*){
     *         rec.offset = accumulativeOffset;
     *         accumulativeOffset += rec.type.numberOfBytes();
     *     }
     *
     */

    private int globalOffset = 0;
    private int localOffset = 0;
    private int paramOffset = 0;

    @Override
    public Void visit(Program program, Void param) {
        globalOffset = 0;
        for (ast.definition.Definition def : program.getDefinitions()) {
            if (def instanceof VariableDefinition) {
                VariableDefinition vd = (VariableDefinition) def;
                vd.setOffset(globalOffset);
                globalOffset += vd.getType().numberOfBytes();
                vd.getType().accept(this, param);
            }
            else if (def instanceof FunctionDefinition) {
                def.accept(this, param);
            }
        }
        return null;
    }

    @Override
    public Void visit(FunctionDefinition funcDefinition, Void param) {
        // 1. Procesar parámetros (orden inverso)
        paramOffset = 0;
        FunctionType funcType = (FunctionType) funcDefinition.getType();
        java.util.List<VariableDefinition> params = funcType.getParameters();
        for (int i = params.size() - 1; i >= 0; i--) {
            VariableDefinition p = params.get(i);
            p.setOffset(4 + paramOffset);
            paramOffset += p.getType().numberOfBytes();
            p.getType().accept(this, param);
        }

        // 2. Procesar variables locales
        localOffset = 0;
        java.util.List<VariableDefinition> locals = funcDefinition.getLocalVariables();
        for (VariableDefinition local : locals) {
            localOffset += local.getType().numberOfBytes();
            local.setOffset(-localOffset);
            local.getType().accept(this, param);
        }

        // 3. Visitar statements del cuerpo (no afectan offsets)
        funcDefinition.getBodyStatements().forEach(stmt -> stmt.accept(this, param));

        return null;
    }

    @Override
    public Void visit(VariableDefinition varDefinition, Void param) {
        // Este método es llamado desde FunctionDefinition para parámetros y locales,
        // pero allí ya se asigna el offset manualmente. Aquí solo visitamos el tipo.
        varDefinition.getType().accept(this, param);
        return null;
    }

    @Override
    public Void visit(RecordType recordType, Void param) {
        int fieldOffset = 0;
        for (RecordField field : recordType.getRecordFields()) {
            field.setOffset(fieldOffset);
            fieldOffset += field.getFieldType().numberOfBytes();
            field.getFieldType().accept(this, param);
        }
        return null;
    }
}