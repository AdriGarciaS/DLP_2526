# Extra: Do-While

## 1. Gramática (Cmm.g4)

Se añade una nueva alternativa en la regla `statement`:

```antlr
| 'do' b=block 'while' '(' e=expression ')' ';'
    { $ast.add(new DoWhileStatement($e.ast.getLine(), $e.ast.getColumn(), $e.ast, $b.ast)); }
```

## 2. Nodo del AST

Se crea la clase `DoWhileStatement` en `ast.statement`:

**DoWhileStatement.java**
- Atributos: `Expression condition`, `List<Statement> body`
- Implementa `Statement`
- Extiende `AbsStatement`

## 3. Visitor de identificación

No requiere cambios — el cuerpo y la condición son estructuras ya existentes.

## 4. Visitor de tipo (TypeCheckingVisitor)

```
(P) DoWhileStatement: statement -> statement* expression
(R) for each statement in body: visit(statement)
    expression.type.mustBeComparison(line, col)
```

## 5. Generación de código (ExecuteCGVisitor)

La diferencia con el while es que el cuerpo se ejecuta siempre al menos una vez, por lo que la condición se evalúa al final:

```
execute[[DoWhileStatement: statement -> statement* expression]] =
    String bodyLabel = cg.nextLabel()

    labelDef(bodyLabel)
    for each stmt in body: execute[[stmt]]
    value[[condition]]
    jnz bodyLabel
```

No se necesita `exitLabel` porque si la condición es falsa simplemente no salta y continúa.

## 6. Visitor de offsets

No requiere cambios.
