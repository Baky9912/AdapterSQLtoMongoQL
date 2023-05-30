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
