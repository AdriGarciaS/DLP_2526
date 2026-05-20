# Extra: Operadores de Asignación Compuesta (+=, -=, *=, /=)

## 1. Gramática (Cmm.g4)

Se añaden cuatro nuevas alternativas en la regla `statement`:

```antlr
| e1=expression '+=' e2=expression ';'
    { $ast.add(new AssigmentStatement($e1.ast.getLine(), $e1.ast.getColumn(), $e1.ast,
        new ArithmeticOperation($e1.ast.getLine(), $e1.ast.getColumn(), $e1.ast, "+", $e2.ast))); }

| e1=expression '-=' e2=expression ';'
    { $ast.add(new AssigmentStatement($e1.ast.getLine(), $e1.ast.getColumn(), $e1.ast,
        new ArithmeticOperation($e1.ast.getLine(), $e1.ast.getColumn(), $e1.ast, "-", $e2.ast))); }

| e1=expression '*=' e2=expression ';'
    { $ast.add(new AssigmentStatement($e1.ast.getLine(), $e1.ast.getColumn(), $e1.ast,
        new ArithmeticOperation($e1.ast.getLine(), $e1.ast.getColumn(), $e1.ast, "*", $e2.ast))); }

| e1=expression '/=' e2=expression ';'
    { $ast.add(new AssigmentStatement($e1.ast.getLine(), $e1.ast.getColumn(), $e1.ast,
        new ArithmeticOperation($e1.ast.getLine(), $e1.ast.getColumn(), $e1.ast, "/", $e2.ast))); }
```

La clave de este extra es que **no se necesitan nodos nuevos en el AST**. Cada operador compuesto se desazucara directamente en la gramática a un `AssigmentStatement` con un `ArithmeticOperation` como valor derecho.

## 2. Nodos del AST

No se crean clases nuevas. Se reutilizan `AssigmentStatement` y `ArithmeticOperation`.

## 3. Visitor de identificación

No requiere cambios.

## 4. Visitor de tipo (TypeCheckingVisitor)

No requiere cambios. Al desazucarar en la gramática, el type checker ya recibe un `AssigmentStatement` y un `ArithmeticOperation` normales.

## 5. Generación de código

No requiere cambios por la misma razón — el código generado es el de una asignación con operación aritmética, que ya está implementado.

## 6. Visitor de offsets

No requiere cambios.
