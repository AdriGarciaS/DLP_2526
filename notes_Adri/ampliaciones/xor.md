# Extra: Operador XOR (^)

## 1. Gramática (Cmm.g4)

Se añade una nueva alternativa en `expression`, con la misma precedencia que `&&`/`||`:

```antlr
| e1=expression '^' e2=expression
    { $ast = new XorOperation($e1.ast.getLine(), $e1.ast.getColumn(), $e1.ast, $e2.ast); }
```

## 2. Nodo del AST

Se crea la clase `XorOperation` en `ast.expression`:

**XorOperation.java**
- Atributos: `Expression leftExpression`, `Expression rightExpression`
- Implementa `Expression`
- Extiende `AbsExpression`

## 3. Visitor de identificación

No requiere cambios.

## 4. Visitor de tipo (TypeCheckingVisitor)

El XOR solo se aplica a enteros (igual que `&&` y `||`):

```
(P) XorOperation: expression1 -> expression2 ^ expression3
(R) expression2.type debe ser promotable a IntType
    expression3.type debe ser promotable a IntType
    expression1.type = new IntType()
    expression1.lvalue = false
```

En código:

```java
visit(XorOperation xor, Void param):
    xor.getLeftExpression().accept(this, param)
    xor.getRightExpression().accept(this, param)
    xor.setLvalue(false)
    xor.setType(xor.getLeftExpression().getType()
                    .logical(xor.getRightExpre~~~~ssion().getType(), line, col))
    // reutilizamos logical() ya que la restricción de tipos es la misma
```

## 5. Generación de código (ValueCGVisitor)

MAPL no tiene instrucción `xor` directa, así que se implementa con la identidad booleana:
`a XOR b = (a OR b) AND NOT (a AND b)`

```
value[[XorOperation: e1 -> e2 ^ e3]] =
    value[[e2]]
    value[[e3]]
    <or>
    value[[e2]]
    value[[e3]]
    <and>
    <not>
    <and>
```

## 6. Visitor de offsets

No requiere cambios.
