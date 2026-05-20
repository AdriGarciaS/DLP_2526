# Extra: Asignación Múltiple (a=b=0)

## Descripción

La asignación múltiple permite encadenar asignaciones de derecha a izquierda:
`a = b = 0` equivale a `b = 0; a = b`

## 1. Gramática (Cmm.g4)

Este extra **solo requiere cambiar el parser**. La regla de asignación en `statement` ya tiene `<assoc=right>`, pero actualmente solo permite una asignación. Se modifica para permitir el encadenamiento:

```antlr
| <assoc=right> e1=expression '=' e2=expression ';'
    { $ast.add(new AssigmentStatement($e1.ast.getLine(), $e1.ast.getColumn(), $e1.ast, $e2.ast)); }
```

La clave está en que `expression` ya puede ser una `Variable`, y ANTLR con `<assoc=right>` resolverá `a = b = 0` como `a = (b = 0)` gracias a que la asignación también puede aparecer como expresión.

Para ello, se añade la asignación también como alternativa en `expression`:

```antlr
| <assoc=right> e1=expression '=' e2=expression
    { $ast = new AssigmentExpression($e1.ast.getLine(), $e1.ast.getColumn(), $e1.ast, $e2.ast); }
```

## 2. Nodo del AST

Se crea la clase `AssigmentExpression` en `ast.expression`:

**AssigmentExpression.java**
- Atributos: `Expression lhs`, `Expression rhs`
- Implementa `Expression`
- Extiende `AbsExpression`
- El tipo resultante es el tipo del `lhs`

## 3. Visitor de tipo (TypeCheckingVisitor)

```
(P) AssigmentExpression: expression1 -> expression2 = expression3
(R) expression2.lvalue debe ser true
    expression2.type.mustBeAssignable(expression3.type, line, col)
    expression1.type = expression2.type
    expression1.lvalue = false
```

## 4. Generación de código (ValueCGVisitor)

```
value[[AssigmentExpression: e1 -> e2 = e3]] =
    address[[e2]]
    value[[e3]]
    convert e3.type → e2.type
    store e2.type.suffix()    // almacena en memoria
    // deja el valor en pila para encadenar
    address[[e2]]
    load e2.type.suffix()
```

## 5. Visitor de offsets

No requiere cambios.
