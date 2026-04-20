package semantics.symbolTable;

import ast.definition.Definition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTable {
    private int scope = 0;

    private List<Map<String, Definition>> table;

    public SymbolTable() {
        table = new ArrayList<Map<String, Definition>>();
        table.add(new HashMap<>());
    }

    public void set() {
        scope++;
        table.add(new HashMap<>());

    }

    public void reset() {
        table.remove(scope);
        scope--;
    }

    public boolean insert(Definition def) {
        if (findInCurrentScope(def.getName()) == null) {
            table.get(scope).put(def.getName(), def);
            def.setScope(scope);
            return true;
        }
        return false;
    }

    public Definition findInCurrentScope(String id) {
        return table.get(scope).get(id);
    }

    public Definition find(String id) {
        for (int i = scope; i >= 0; i--) {
            if (table.get(i).get(id) != null) {
                return table.get(i).get(id);
            }
        }
        return null;
    }
}
