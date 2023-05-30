package raf.bp.model.expression;

import java.util.ArrayList;
import java.util.List;

public class ComplexExpression extends Expression {

    public List<Expression> expressions;
    public ComplexExpression(){
        this.expressions = new ArrayList<>();
    }

    public ComplexExpression(List<Expression> expressions){
        this.expressions = expressions;
    }

    public boolean isNestedQuery(){
        // da li pocinje sa (select ili (((((select
        Expression expr = this;
        ComplexExpression ce;
        while(expr instanceof ComplexExpression){
            ce = (ComplexExpression)expr;
            expr = ce.expressions.get(0);
        }
        if(!(expr instanceof SymbolExpression)){
            return false;
        }
        SymbolExpression se = (SymbolExpression)expr;
        return se.word.equals("select");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("( ");
        for(Expression expr : expressions){
            sb.append(expr.toString()).append(" ");
        }
        sb.append(") ");
        return sb.toString();
    }
}
