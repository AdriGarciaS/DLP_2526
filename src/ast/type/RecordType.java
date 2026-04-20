package ast.type;

import ast.definition.VariableDefinition;
import semantics.Visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RecordType extends AbsType implements Type {

    private List<RecordField> recordFields = new ArrayList<>();

    public RecordType() {
    }


    public RecordType(List<RecordField> recordFields) {

        this.recordFields = recordFields;

    }

    public List<RecordField> getRecordFields() {
        return recordFields;

    }

    public void setRecordFields(List<RecordField> recordFields) {
        this.recordFields = recordFields;
    }


    private void createNewField(VariableDefinition var) {
        if (!fieldExists(var.getName())) {
            recordFields.add(new RecordField(var.getLine(), var.getColumn(), var.getVariableType(),
                    var.getName()));
        }
        else {
            new ErrorType(var.getLine(), var.getColumn(),
                    "Repeated struct field -> " + var.getName() + " already exists");
        }
    }

    private boolean fieldExists(String name) {
        return recordFields.stream().anyMatch(f -> f.getName().equals(name));
    }

    @Override
    public int numberOfBytes() {
        int bytes = 0;
        for (RecordField r : recordFields) {
            bytes += r.getFieldType().numberOfBytes();
        }
        return bytes;
    }

    @Override
    public Type dot(String fieldName, int line, int col) {

        for (RecordField f : recordFields) {
            if (f.getName().equals(fieldName)) {
                return f.getFieldType();
            }
        }
        return new ErrorType(line, col, "Struct does not have field '" + fieldName + "'");
    }

    @Override
    public void mustBeReadable(int line, int col) {
        new ErrorType(line, col, "Cannot read a struct as a whole");
    }

    @Override
    public void mustBeWritable(int line, int col) {
        new ErrorType(line, col, "Cannot write a struct as a whole");
    }

    @Override
    public void mustBeComparison(int line, int col) {
        new ErrorType(line, col, "Cannot use a struct as a condition");
    }

    @Override
    public void mustBeReturnable(Type expectedReturnType, int line, int col) {
        new ErrorType(line, col, "Cannot return a struct directly");
    }

    @Override
    public String toString() {
        if (recordFields.isEmpty()) {
            return "struct { }";
        }
        String fields = recordFields.stream()
                .map(RecordField::toString)
                .collect(Collectors.joining("; "));
        return "struct { " + fields + " }";
    }

    @Override
    public <TP, TR> TR accept(Visitor<TP, TR> v, TP param) {
        return v.visit(this, param);
    }
}