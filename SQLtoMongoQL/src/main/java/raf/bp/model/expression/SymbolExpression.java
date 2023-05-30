package raf.bp.model.expression;

public class SymbolExpression extends Expression{
    public String word;
    public SymbolExpression(String word){
        this.word = word;
    }

    @Override
    public String toString() {
        return word;
    }
}
