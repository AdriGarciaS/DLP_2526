package ast;

public abstract class AbsLocalable implements Localable {
    protected int line;
    protected int column;

    public AbsLocalable(int line, int column) {
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        return "[" + getLine() + ':' + getColumn() + "]  " + localeToString();
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public int getColumn() {
        return column;
    }

    protected abstract String localeToString();
}
