package raf.bp.parser.expression;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComplexExpression extends Expression {

    private List<Expression> expressions;
    public ComplexExpression(){
        this.expressions = new ArrayList<>();
    }

    public ComplexExpression(List<Expression> expressions){
        this.expressions = expressions;
    }

    @Override
    public boolean isNestedQuery(){
        // da li pocinje sa (select, (((((select isl

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
        return se.getWord().equals("select");
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

    public String toDebugString() {
        StringBuilder sb = new StringBuilder();
        sb.append("( ");
        for(Expression expr : expressions){
            if(expr instanceof SymbolExpression){
                sb.append("[SymbolExpression]");
            }
            else{
                sb.append("[ComplexExpression]");
            }
            sb.append(" ").append(expr.toString()).append("\n");
        }
        sb.append(") ");
        return sb.toString();
    }

    
}
