package raf.bp.model;

import java.beans.Expression;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Clause {
    public Expression expression;   
    public String keyword; 
    public Clause(String keyword, Expression expression){
        this.keyword = keyword;
        this.expression = expression;
    }
}
