package raf.bp.model.SQL;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SQLToken extends SQLExpression{
    private String word;
    public SQLToken(String word){
        this.word = word;
    }
}
