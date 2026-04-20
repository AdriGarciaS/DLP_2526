package ast.errorhandler;

import ast.type.ErrorType;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ErrorHandler {

    private static ErrorHandler instance;
    private List<ErrorType> errors;

    private ErrorHandler() {
        errors = new ArrayList<>();
    }

    public static ErrorHandler getInstance() {
        if (instance == null) {
            instance = new ErrorHandler();
        }
        return instance;
    }

    public void addError(ErrorType error) {
        if (error != null) {
            errors.add(error);
        }
    }

    public boolean anyErrors() {
        return !errors.isEmpty();
    }

    public void showErrors(PrintStream err) {
        for (ErrorType error : errors) {
            err.append(error.toString());

        }
    }

    // Package-private method to clear errors (useful for testing)
    void clearErrors() {
        errors.clear();
    }
}