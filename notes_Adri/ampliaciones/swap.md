# Extra: Swap

## Descripción

La sentencia `swap` intercambia los valores de dos expresiones lvalue:
`swap a, b` equivale a `tmp = a; a = b; b = tmp`

## 1. Gramática (Cmm.g4)

Se añade una nueva alternativa en la regla `statement`:

```antlr
| 'swap' e1=expression ',' e2=expression ';'
    { $ast.add(new SwapStatement($e1.ast.getLine(), $e1.ast.getColumn(), $e1.ast, $e2.ast)); }
```

## 2. Nodo del AST

Se crea la clase `SwapStatement` en `ast.statement`:

**SwapStatement.java**
- Atributos: `Expression left`, `Expression right`
- Implementa `Statement`
- Extiende `AbsStatement`

## 3. Visitor de identificación

No requiere cambios.

## 4. Visitor de tipo (TypeCheckingVisitor)

```
(P) SwapStatement: statement -> expression1 , expression2
(R) expression1.lvalue debe ser true
    expression2.lvalue debe ser true
    expression1.type debe ser igual a expression2.type
    (o al menos mutuamente asignables)
```

En código:

```java
visit(SwapStatement swap, Void param):
    swap.getLeft().accept(this, param)
    swap.getRight().accept(this, param)
    if !swap.getLeft().getLvalue():
        new ErrorType(line, col, "Lvalue required in swap (left operand)")
    if !swap.getRight().getLvalue():
        new ErrorType(line, col, "Lvalue required in swap (right operand)")
    swap.getLeft().getType().mustBeAssignable(swap.getRight().getType(), line, col)
    swap.getRight().getType().mustBeAssignable(swap.getLeft().getType(), line, col)
```

## 5. Generación de código (ExecuteCGVisitor)

El swap se implementa sin variable temporal usando la pila de MAPL: se cargan ambos valores, y se almacenan en orden inverso.

```
execute[[SwapStatement: statement -> expression1 , expression2]] =
    address[[e1]]
    value[[e2]]           // pila: [dir_e1, val_e2]
    address[[e2]]
    value[[e1]]           // pila: [dir_e1, val_e2, dir_e2, val_e1]
    store e1.type.suffix() // almacena val_e1 en dir_e2 → pila: [dir_e1, val_e2]
    store e2.type.suffix() // almacena val_e2 en dir_e1 → pila: []
```

## 6. Visitor de offsets

No requiere cambios.
