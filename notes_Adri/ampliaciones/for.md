# Extra: For Statement

## 1. Gramática (Cmm.g4)

Se añade una nueva alternativa en la regla `statement`:

```antlr
| 'for' '(' init=expression '=' initVal=expression ';'
             cond=expression ';'
             upd=expression '=' updVal=expression ')'
  b=block
    {
        $ast.add(new AssigmentStatement($init.ast.getLine(), $init.ast.getColumn(), $init.ast, $initVal.ast));
        $ast.add(new ForStatement($cond.ast.getLine(), $cond.ast.getColumn(),
                    $cond.ast, $b.ast,
                    new AssigmentStatement($upd.ast.getLine(), $upd.ast.getColumn(), $upd.ast, $updVal.ast)));
    }
```

Nótese que la inicialización se traduce directamente a un `AssigmentStatement` anterior al bucle, y el incremento se almacena dentro del `ForStatement`.

## 2. Nodo del AST

Se crea la clase `ForStatement` en `ast.statement`:

**ForStatement.java**
- Atributos: `Expression condition`, `List<Statement> body`, `Statement increment`
- Implementa `Statement`
- Extiende `AbsStatement`

## 3. Visitor de identificación

No requiere cambios — todas las subexpresiones son expresiones normales que ya se visitan.

## 4. Visitor de tipo (TypeCheckingVisitor)

```
(P) ForStatement: statement -> expression statement* statement
(R) expression.type.mustBeComparison(line, col)
    for each statement in body: visit(statement)
    visit(increment)
```

## 5. Generación de código (ExecuteCGVisitor)

El for se traduce igual que un while con el incremento al final del cuerpo:

```
execute[[ForStatement: statement -> expression statement* statement]] =
    String condLabel = cg.nextLabel()
    String exitLabel = cg.nextLabel()

    labelDef(condLabel)
    value[[condition]]
    jz exitLabel
    for each stmt in body: execute[[stmt]]
    execute[[increment]]
    jmp condLabel
    labelDef(exitLabel)
```

## 6. Visitor de offsets

No requiere cambios — el for no declara variables nuevas.
