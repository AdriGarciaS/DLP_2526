# Extra: Operador Ternario

## 1. Gramática (Cmm.g4)

Se añade una nueva alternativa en la regla `expression`, con baja precedencia (justo antes de `&&`/`||`):

```antlr
| e1=expression '?' e2=expression ':' e3=expression
    { $ast = new TernaryExpression($e1.ast.getLine(), $e1.ast.getColumn(), $e1.ast, $e2.ast, $e3.ast); }
```

## 2. Nodo del AST

Se crea la clase `TernaryExpression` en `ast.expression`:

**TernaryExpression.java**
- Atributos: `Expression condition`, `Expression thenExpr`, `Expression elseExpr`
- Implementa `Expression`
- Extiende `AbsExpression`

## 3. Visitor de identificación

No requiere cambios — las tres subexpresiones son expresiones normales que ya se visitan.

## 4. Visitor de tipo (TypeCheckingVisitor)

```
(P) TernaryExpression: expression1 -> expression2 ? expression3 : expression4
(R) expression2.type.mustBeComparison(line, col)
    expression1.type = expression3.type.supertype(expression4.type)
    expression1.lvalue = false
```

En código:

```java
visit(TernaryExpression ternary, Void param):
    ternary.getCondition().accept(this, param)
    ternary.getThenExpr().accept(this, param)
    ternary.getElseExpr().accept(this, param)
    ternary.getCondition().getType().mustBeComparison(line, col)
    ternary.setType(ternary.getThenExpr().getType()
                           .supertype(ternary.getElseExpr().getType()))
    ternary.setLvalue(false)
```

## 5. Generación de código (ValueCGVisitor)

```
value[[TernaryExpression: e1 -> e2 ? e3 : e4]] =
    String elseLabel = cg.nextLabel()
    String endLabel  = cg.nextLabel()

    value[[e2]]
    jz elseLabel
    value[[e3]]
    convert e3.type → e1.type
    jmp endLabel
    labelDef(elseLabel)
    value[[e4]]
    convert e4.type → e1.type
    labelDef(endLabel)
```

## 6. Visitor de offsets

No requiere cambios.
