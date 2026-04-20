package parser;

import ast.expression.ArithmeticOperation;
import ast.expression.Expression;
import ast.expression.ModuleOperation;
import ast.type.ArrayType;
import ast.type.Type;

public class LexerHelper {

    public static char lexemeToChar(String str) {
        try {
            // Borar comillas ('a' = > a    '\n' => \n    '/123' => /123)
            String content = str.substring(1, str.length() - 1);

            // caracteres especiales , empeiza por \
            if (content.length() == 2 && content.charAt(0) == '\\') {
                switch (content.charAt(1)) {
                    case 'n':
                        return '\n';
                    case 't':
                        return '\t';
                    default:
                        return '\0';
                }
            }

            // caracteres escritos en ASCIII
            else if (content.length() > 1 && content.charAt(0) == '\\' && Character.isDigit(
                    content.charAt(1))) {
                String number = content.substring(1);
                int ascii = Integer.parseInt(number);
                return (char) ascii;
            }

            // caracter normal
            else if (content.length() == 1) {
                return content.charAt(0);
            }

            throw new IllegalArgumentException("Not vali char: " + str);
        } catch (Exception e) {
            System.out.println("Error obtaining char from " + str + ": " + e.getMessage());
        }
        return '\0';
    }

    // TODO: Implement the lexemeToChar and lexemeToReal methods

    public static double lexemeToReal(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            System.out.println("Error converting " + str + " to real number: " + e.getMessage());
        }
        return -1.0;

    }

    public static Expression operationOrModule(Expression e1, String op, Expression e2) {
        if (op.equals("%")) {
            return new ModuleOperation(e1.getLine(), e1.getColumn(), e1, op, e2);
        }
        return new ArithmeticOperation(e1.getLine(), e1.getColumn(), e1, op, e2);
    }

    public static Type arrayCreation(Type currentType, String size) {
        int newSize = lexemeToInt(size);

        // CASO 1: Tipo base (no es array)
        if (!(currentType instanceof ArrayType)) {
            // int + [2] → new Array(int, 2)
            return new ArrayType(currentType, newSize);
        }

        // CASO 2: Ya es un array
        ArrayType existingArray = (ArrayType) currentType;

        // Obtener el tipo base del array existente (el más interno)
        Type baseType = existingArray;
        while (baseType instanceof ArrayType) {
            baseType = ((ArrayType) baseType).getArrayType();
        }

        // Crear NUEVO array con el tipo base y el nuevo tamaño
        ArrayType newInnerArray = new ArrayType(baseType, newSize);

        // Recopilar TODAS las dimensiones existentes en el orden ORIGINAL (de externa a interna)
        java.util.ArrayList<Integer> dimensions = new java.util.ArrayList<>();
        Type temp = existingArray;
        while (temp instanceof ArrayType) {
            ArrayType arr = (ArrayType) temp;
            dimensions.add(arr.getSize());
            temp = arr.getArrayType();
        }
        // Para [9][5]int, dimensions = [9, 5] (de externa a interna)

        // Reconstruir aplicando las dimensiones existentes en el mismo orden
        // pero ahora el tipo base es newInnerArray ([2]int)
        Type result = newInnerArray;
        for (int i = dimensions.size() - 1; i >= 0; i--) {
            result = new ArrayType(result, dimensions.get(i));
        }

        return result;
    }

    public static int lexemeToInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            System.out.println(e);
        }
        return -1;
    }
}
