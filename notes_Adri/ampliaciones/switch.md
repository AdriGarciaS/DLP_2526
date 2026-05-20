# Extra: Switch Statement

## 1. Gramática (Cmm.g4)

Se añade una nueva alternativa en la regla `statement`:

```antlr
| 'switch' '(' e=expression ')' '{' (c=switchCase { $ast.addAll($c.ast); })* '}'
```

Y se añade la regla auxiliar `switchCase`:

```antlr
switchCase returns [List<Statement> ast = new ArrayList<Statement>()]:
    'case' v=expression ':' (s=statement { $ast.addAll($s.ast); })*
    | 'default' ':' (s=statement { $ast.addAll($s.ast); })*
    ;
```

## 2. Nodos del AST

Se crean dos clases nuevas en `ast.statement`:

**SwitchStatement.java**
- Atributos: `Expression selector`, `List<SwitchCase> cases`
- Implementa `Statement`
- Extiende `AbsStatement`

**SwitchCase.java**
- Atributos: `Expression value` (null si es default), `List<Statement> body`
- Implementa `Statement`
- Extiende `AbsStatement`

## 3. Visitor de identificación

No requiere cambios — el selector y los valores de cada case son expresiones normales que ya se visitan.

## 4. Visitor de tipo (TypeCheckingVisitor)

```
(P) SwitchStatement: statement -> expression switchCase*
(R) expression.type.mustBeComparison(line, col)
    for each case: visit(switchCase)

(P) SwitchCase: switchCase -> expression? statement*
(R) if value != null: value.type must be promotable to selector.type
    for each statement: visit(statement)
```

## 5. Generación de código (ExecuteCGVisitor)

El switch se traduce a una cadena de comparaciones con saltos condicionales:

```
execute[[Switch: statement -> expression switchCase*]] =
    String exitLabel = cg.nextLabel()
    for each case c:
        String nextLabel = cg.nextLabel()
        if c.value != null:
            value[[expression]]        // selector
            value[[c.value]]           // valor del case
            <eqi>
            jz nextLabel
        execute[[c.body]]
        jmp exitLabel
        labelDef(nextLabel)
    labelDef(exitLabel)
```

## 6. Visitor de offsets

No requiere cambios — no se declaran variables nuevas.
