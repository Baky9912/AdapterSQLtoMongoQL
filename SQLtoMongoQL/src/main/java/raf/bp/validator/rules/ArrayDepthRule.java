package raf.bp.validator.rules;

import java.util.ArrayList;
import java.util.Arrays;

import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLExpression;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.model.SQL.SQLToken;
import raf.bp.validator.SQLValidatorRule;

public class ArrayDepthRule extends SQLValidatorRule{
    public static class OneArrayException extends RuntimeException {
        public OneArrayException(String message) {
            super(message);
        }
    }

    @Override
    public boolean check(SQLQuery query) {
        for(SQLClause clause : query.getClauses()){
            int level=0;
            for(SQLExpression expr : clause.getSqlExpressions()){
                if(expr instanceof SQLToken token){
                    if(token.getWord().equals("["))
                        level++;
                    if(token.getWord().equals("]"))
                        level--;
                }
                else if(expr instanceof SQLQuery nestedQuery){
                    if(level>0) return false;
                    if(!check(nestedQuery)) return false;
                }
                if(level<0 || level>1) return false;
            }
            if(level!=0) return false;
        }
        return true;
    }

    @Override
    public void setTests() {
        String q1 = "select x from y where x in [1, \"hello world   \"] or false";
        String q2 = "select x from y where x in [1, \"hello world   \", (select a from b)] or false";
        String q3 = "select x from y where x in [1, [ola], \"hello world   \"] or false";
        String q4 = "select x from y where x in [1, \"hello world   \", order by x] or false";
        String q5 = "SELECT       avg(salary),          department_id from hr.employees group by department_id";
        String q6 = "select department_name, department_id, location_id from hr.departments where department_id in\n"
                + "(select department_id from hr.employees group by department_id having max(salary) > 10000)";

        this.testingQueries.add(q1);
        this.testingQueries.add(q2);
        this.testingQueries.add(q3);
        this.testingQueries.add(q4);
        this.testingQueries.add(q5);
        this.testingQueries.add(q6);

        this.expectedResults = new ArrayList<>(Arrays.asList(true, false, false, false, true, true));

    }

}
