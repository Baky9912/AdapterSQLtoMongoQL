package raf.bp.model.expression;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SymbolExpression extends Expression{
    private String word;
    public SymbolExpression(String word){
        this.word = word;
    }

    @Override
    public boolean isNestedQuery(){
        return false;
    }

    @Override
    public String toString() {
        return word;
    }
}
