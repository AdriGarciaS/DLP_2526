package codegeneration;

import ast.type.CharType;
import ast.type.DoubleType;
import ast.type.IntType;
import ast.type.Type;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Abstrae la escritura de instrucciones MAPL en el fichero de salida.
 * Los visitors solo llaman métodos semánticos; la lógica de texto queda aquí.
 *
 * Estándar: Single Responsibility — separar la generación de texto de la
 * lógica de traversal del AST.
 *
 * El sufijo de tipo (b/i/f) se obtiene siempre mediante type.suffix(),
 * definido en la propia clase de tipo — nunca hardcodeado aquí.
 */
public class CodeGenerator {

    private final PrintWriter out;
    private int currentLine = 0;

    public CodeGenerator(String outputFile) throws IOException {
        this.out = new PrintWriter(new FileWriter(outputFile));
    }

    // -----------------------------------------------------------------------
    // Debugging info
    // -----------------------------------------------------------------------

    public void source(String sourceFile) {
        out.println("#source\t\"" + sourceFile + "\"");
        out.println();
    }

    public void line(int lineNumber) {
        if (lineNumber != currentLine) {
            out.println("#line\t" + lineNumber);
            currentLine = lineNumber;
        }
    }

    // -----------------------------------------------------------------------
    // Push instructions
    // -----------------------------------------------------------------------

    public void pushb(int asciiCode) {
        out.println("\tpushb\t" + asciiCode);
    }

    public void pushi(int value) {
        out.println("\tpushi\t" + value);
    }

    public void pushf(double value) {
        out.println("\tpushf\t" + value);
    }

    public void pusha(int address) {
        out.println("\tpusha\t" + address);
    }

    public void pushBP() {
        out.println("\tpush\tbp");
    }

    // -----------------------------------------------------------------------
    // Load / Store
    // -----------------------------------------------------------------------

    public void load(Type type) {
        out.println("\tload" + type.suffix());
    }

    public void store(Type type) {
        out.println("\tstore" + type.suffix());
    }

    // -----------------------------------------------------------------------
    // Arithmetic / Logical / Comparison
    // -----------------------------------------------------------------------

    public void arithmetic(String operator, Type type) {
        switch (operator) {
            case "+" -> out.println("\tadd" + type.suffix());
            case "-" -> out.println("\tsub" + type.suffix());
            case "*" -> out.println("\tmul" + type.suffix());
            case "/" -> out.println("\tdiv" + type.suffix());
            default  -> throw new IllegalArgumentException("Unknown arithmetic operator: " + operator);
        }
    }

    public void modulus(Type type) {
        out.println("\tmod" + type.suffix());
    }

    public void logical(String operator) {
        switch (operator) {
            case "&&" -> out.println("\tand");
            case "||" -> out.println("\tor");
            default   -> throw new IllegalArgumentException("Unknown logical operator: " + operator);
        }
    }

    public void not() {
        out.println("\tnot");
    }

    public void comparison(String operator, Type type) {
        switch (operator) {
            case "<"  -> out.println("\tlt" + type.suffix());
            case ">"  -> out.println("\tgt" + type.suffix());
            case "<=" -> out.println("\tle" + type.suffix());
            case ">=" -> out.println("\tge" + type.suffix());
            case "==" -> out.println("\teq" + type.suffix());
            case "!=" -> out.println("\tne" + type.suffix());
            default   -> throw new IllegalArgumentException("Unknown comparison operator: " + operator);
        }
    }

    // -----------------------------------------------------------------------
    // Type conversions
    // -----------------------------------------------------------------------

    public void convert(Type from, Type to) {
        if (from.getClass().equals(to.getClass())) return;

        if (from instanceof CharType   && to instanceof IntType)    { out.println("\tb2i"); return; }
        if (from instanceof IntType    && to instanceof DoubleType) { out.println("\ti2f"); return; }
        if (from instanceof DoubleType && to instanceof IntType)    { out.println("\tf2i"); return; }
        if (from instanceof IntType    && to instanceof CharType)   { out.println("\ti2b"); return; }
        if (from instanceof CharType   && to instanceof DoubleType) {
            out.println("\tb2i");
            out.println("\ti2f");
            return;
        }
        throw new IllegalArgumentException("Cannot convert " + from + " to " + to);
    }

    // -----------------------------------------------------------------------
    // I/O
    // -----------------------------------------------------------------------

    public void out(Type type) {
        out.println("\tout" + type.suffix());
    }

    public void in(Type type) {
        out.println("\tin" + type.suffix());
    }

    // -----------------------------------------------------------------------
    // Stack operations
    // -----------------------------------------------------------------------

    public void addi() {
        out.println("\taddi");
    }

    // -----------------------------------------------------------------------
    // Function support
    // -----------------------------------------------------------------------

    public void label(String name) {
        out.println();
        out.println(name + ":");
    }

    public void enter(int bytes) {
        out.println("\tenter\t" + bytes);
    }

    public void ret(int returnBytes, int localBytes, int paramBytes) {
        out.println("\tret\t" + returnBytes + ", " + localBytes + ", " + paramBytes);
    }

    public void call(String functionName) {
        out.println("\tcall\t" + functionName);
    }

    public void halt() {
        out.println("\thalt");
    }

    // -----------------------------------------------------------------------
    // Comments
    // -----------------------------------------------------------------------

    public void comment(String text) {
        out.println("\t' * " + text);
    }

    public void sectionComment(String text) {
        out.println("' " + text);
    }

    // -----------------------------------------------------------------------
    // Flush / close
    // -----------------------------------------------------------------------

    public void close() {
        out.flush();
        out.close();
    }
}