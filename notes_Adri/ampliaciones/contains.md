# Extra: Contains

## Descripción

El operador `contains` se aplica a un array y devuelve 1 (int) si algún elemento del array es igual al valor buscado, o 0 si no lo es.

Ejemplo: `v contains 5`

## 1. Gramática (Cmm.g4)

Se añade una nueva alternativa en la regla `expression`, con la precedencia adecuada (similar a comparación):

```antlr
| e1=expression 'contains' e2=expression
    { $ast = new Contains($e1.ast.getLine(), $e1.ast.getColumn(), $e1.ast, $e2.ast); }
```

## 2. Nodo del AST

Se crea la clase `Contains` en `ast.expression`:

**Contains.java**
- Atributos: `Expression array`, `Expression value`
- Implementa `Expression`
- Extiende `AbsExpression`

## 3. Visitor de identificación

No requiere cambios — ambas subexpresiones son expresiones normales que ya se visitan.

## 4. Visitor de tipo (TypeCheckingVisitor)

```
(P) Contains: expression1 -> expression2 contains expression3
(R) expression2.type debe ser ArrayType
    expression3.type debe ser promotable al tipo base del array
    expression1.type = new IntType()
    expression1.lvalue = false
```

En código:

```java
visit(Contains contains, Void param):
    contains.getArray().accept(this, param)
    contains.getValue().accept(this, param)
    if !(contains.getArray().getType() instanceof ArrayType):
        new ErrorType(line, col, "Contains requires an array as left operand")
    else:
        ArrayType at = (ArrayType) contains.getArray().getType()
        if !contains.getValue().getType().isPromotableTo(at.getArrayType()):
            new ErrorType(line, col, "Value type not compatible with array element type")
    contains.setType(new IntType())
    contains.setLvalue(false)
```

## 5. Generación de código (ValueCGVisitor)

Se genera un bucle que recorre el array comparando cada elemento con el valor buscado:

```
value[[Contains: e1 -> e2 contains e3]] =
    String foundLabel = cg.nextLabel()
    String endLabel   = cg.nextLabel()
    ArrayType at = (ArrayType) e2.type
    int elemSize = at.getArrayType().numberOfBytes()
    int arraySize = at.getSize()

    // contador i en pila no es posible → se genera un bucle desenrollado o con variable temporal
    // Opción práctica: generar código para cada índice (unrolling) si el tamaño es conocido
    for i in 0..arraySize-1:
        address[[e2]]
        pushi i * elemSize
        addi
        load at.getArrayType().suffix()
        value[[e3]]
        convert e3.type → at.getArrayType()
        eq at.getArrayType().suffix()
        jnz foundLabel

    pushi 0          // no encontrado
    jmp endLabel
    labelDef(foundLabel)
    pushi 1          // encontrado
    labelDef(endLabel)
```

## 6. Visitor de offsets

No requiere cambios.
