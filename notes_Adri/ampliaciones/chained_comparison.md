# Extra: Concatenación de Comparaciones (a < 5 < b)

## Descripción

La concatenación de comparaciones permite escribir `a < 5 < b` con la semántica de Python: equivale a `a < 5 && 5 < b`. El resultado es de tipo `int`.

## 1. Gramática (Cmm.g4)

Se añade una nueva alternativa en `expression`, con la misma precedencia que las comparaciones normales:

```antlr
| e1=expression op1=('<'|'>'|'<='|'>='|'=='|'!=') e2=expression op2=('<'|'>'|'<='|'>='|'=='|'!=') e3=expression
    { $ast = new ChainedComparison($e1.ast.getLine(), $e1.ast.getColumn(), $e1.ast, $op1.text, $e2.ast, $op2.text, $e3.ast); }
```

## 2. Nodo del AST

Se crea la clase `ChainedComparison` en `ast.expression`:

**ChainedComparison.java**
- Atributos: `Expression left`, `String op1`, `Expression middle`, `String op2`, `Expression right`
- Implementa `Expression`
- Extiende `AbsExpression`

## 3. Visitor de identificación

No requiere cambios.

## 4. Visitor de tipo (TypeCheckingVisitor)

```
(P) ChainedComparison: expression1 -> expression2 op1 expression3 op2 expression4
(R) expression2.type debe ser comparable con expression3.type
    expression3.type debe ser comparable con expression4.type
    expression1.type = new IntType()
    expression1.lvalue = false
```

En código:

```java
visit(ChainedComparison cc, Void param):
    cc.getLeft().accept(this, param)
    cc.getMiddle().accept(this, param)
    cc.getRight().accept(this, param)
    cc.getLeft().getType().comparison(cc.getMiddle().getType(), line, col)
    cc.getMiddle().getType().comparison(cc.getRight().getType(), line, col)
    cc.setType(new IntType())
    cc.setLvalue(false)
```

## 5. Generación de código (ValueCGVisitor)

Se evalúa como `(left op1 middle) && (middle op2 right)`. El valor de `middle` se evalúa una sola vez usando la pila:

```
value[[ChainedComparison: e1 -> e2 op1 e3 op2 e4]] =
    supertype1 = e2.type.supertype(e3.type)
    supertype2 = e3.type.supertype(e4.type)

    value[[e2]]
    convert e2.type → supertype1
    value[[e3]]       // middle se evalúa una vez
    convert e3.type → supertype1
    // duplicar el valor de middle en pila para la segunda comparación
    // (depende de si MAPL tiene dup; si no, re-evaluar e3)
    value[[e3]]
    convert e3.type → supertype2
    value[[e4]]
    convert e4.type → supertype2

    comparison(op1, supertype1)   // deja 0 o 1
    // el resultado de op1 queda bajo el resultado de op2 en pila
    comparison(op2, supertype2)
    <and>
```

## 6. Visitor de offsets

No requiere cambios.
