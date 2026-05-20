import ast.Program;
import ast.errorhandler.ErrorHandler;
import codegeneration.*;
import introspector.model.IntrospectorModel;
import introspector.view.IntrospectorView;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import parser.CmmLexer;
import parser.CmmParser;
import semantics.IdentificationVisitor;
import semantics.TypeCheckingVisitor;
import semantics.Visitor;

public class Main {

    public static void main(String... args) throws Exception {
        if (args.length < 1) {
            System.err.println("Please, pass me the input file.");
            return;
        }

        // create a lexer that feeds off of input CharStream
        CharStream input = CharStreams.fromFileName(args[0]);
        CmmLexer lexer = new CmmLexer(input);

        // create a parser that feeds off the tokens buffer
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CmmParser parser = new CmmParser(tokens);

        // Modify your previous parser.program() line with:
        Program ast = parser.program().ast;

        Visitor<Void, Void> identification = new IdentificationVisitor();
        ast.accept(identification, null);

        Visitor<Void, Void> typeCheck = new TypeCheckingVisitor();
        ast.accept(typeCheck, null);

        if (ErrorHandler.getInstance().anyErrors()) {
            ErrorHandler.getInstance().showErrors(System.err);
        }
        else {
            // Offset calculation
            OffsetVisitor offsetVisitor = new OffsetVisitor();
            ast.accept(offsetVisitor, null);

            // Code generation
            CodeGenerator codeGenerator = new CodeGenerator(args[1]);
            codeGenerator.source(args[0]);

            AddressCGVisitor addressCG = new AddressCGVisitor(codeGenerator);
            ValueCGVisitor valueCG = new ValueCGVisitor(codeGenerator, addressCG);
            addressCG.setValueCGVisitor(valueCG);
            ExecuteCGVisitor executeCG = new ExecuteCGVisitor(codeGenerator, valueCG, addressCG);

            ast.accept(executeCG, null);
            codeGenerator.close();

            System.out.println("Code generated in: " + args[1]);

            // * The AST is shown if no errors exist
            IntrospectorModel model = new IntrospectorModel("Program", ast);
            new IntrospectorView("Introspector", model);
        }

    }


}
