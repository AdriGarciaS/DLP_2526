# Extra: Incremento y Decremento (++i, i++, --i, i--)

## 1. Gramática (Cmm.g4)

Se añaden alternativas en `expression` para pre y post incremento/decremento:

```antlr
// Pre-incremento y pre-decremento
| '++' e1=expression
    { $ast = new PreIncrement($e1.ast.getLine(), $e1.ast.getColumn(), $e1.ast, "+"); }
| '--' e1=expression
    { $ast = new PreIncrement($e1.ast.getLine(), $e1.ast.getColumn(), $e1.ast, "-"); }

// Post-incremento y post-decremento
| e1=expression '++'
    { $ast = new PostIncrement($e1.ast.getLine(), $e1.ast.getColumn(), $e1.ast, "+"); }
| e1=expression '--'
    { $ast = new PostIncrement($e1.ast.getLine(), $e1.ast.getColumn(), $e1.ast, "-"); }
```

## 2. Nodos del AST

Se crean dos clases en `ast.expression`:

**PreIncrement.java**
- Atributos: `Expression expression`, `String operator` (`"+"` o `"-"`)
- Implementa `Expression`
- Extiende `AbsExpression`
- Semántica: modifica la variable y devuelve el valor nuevo

**PostIncrement.java**
- Atributos: `Expression expression`, `String operator` (`"+"` o `"-"`)
- Implementa `Expression`
- Extiende `AbsExpression`
- Semántica: devuelve el valor original y luego modifica la variable

## 3. Visitor de identificación

No requiere cambios — la subexpresión es una expresión normal ya visitada.

## 4. Visitor de tipo (TypeCheckingVisitor)

```
(P) PreIncrement: expression1 -> ++ expression2
(R) expression2.lvalue debe ser true
    expression2.type debe ser built-in (int o char)
    expression1.type = expression2.type
    expression1.lvalue = false

(P) PostIncrement: expression1 -> expression2 ++
(R) expression2.lvalue debe ser true
    expression2.type debe ser built-in (int o char)
    expression1.type = expression2.type
    expression1.lvalue = false
```

## 5. Generación de código (ValueCGVisitor)

**Pre-incremento (++i):** primero modifica, luego devuelve el nuevo valor.

```
value[[PreIncrement: e1 -> ++ e2]] =
    address[[e2]]
    address[[e2]]
    load e2.type.suffix()
    pushi 1
    add e2.type.suffix()
    store e2.type.suffix()    // almacena el nuevo valor
    address[[e2]]
    load e2.type.suffix()     // deja el nuevo valor en pila
```

**Post-incremento (i++):** primero devuelve el valor original, luego modifica.

```
value[[PostIncrement: e1 -> e2 ++]] =
    address[[e2]]
    load e2.type.suffix()     // deja el valor original en pila
    address[[e2]]
    address[[e2]]
    load e2.type.suffix()
    pushi 1
    add e2.type.suffix()
    store e2.type.suffix()    // almacena el nuevo valor
```

El decremento es idéntico sustituyendo `add` por `sub`.

## 6. Visitor de offsets

No requiere cambios.
