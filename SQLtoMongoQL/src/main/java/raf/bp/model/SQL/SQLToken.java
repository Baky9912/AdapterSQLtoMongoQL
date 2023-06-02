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

    @Override
    public String toString() {
        return this.word;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof SQLToken token)) return false;

        return this.word.equals(token.getWord());
    }
}
