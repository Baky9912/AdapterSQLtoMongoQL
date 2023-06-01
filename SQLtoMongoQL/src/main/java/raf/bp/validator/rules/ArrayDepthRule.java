package raf.bp.validator.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLExpression;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.model.SQL.SQLToken;
import raf.bp.parser.SQLParser;
import raf.bp.validator.SQLValidatorRule;

public class ArrayDepthRule implements SQLValidatorRule{
    public class OneArrayException extends RuntimeException {
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
    public void performTests() {
        String q1 = "select x from y where x in [1, \"hello world   \"] or false";
        String q2 = "select x from y where x in [1, \"hello world   \", (select a from b)] or false";
        String q3 = "select x from y where x in [1, [ola], \"hello world   \"] or false";
        String q4 = "select x from y where x in [1, \"hello world   \", order by x] or false";
        String q5 = "SELECT       avg(salary),          department_id from hr.employees group by department_id";
        String q6 = "select department_name, department_id, location_id from hr.departments where department_id in\n"
	+ "(select department_id from hr.employees group by department_id having max(salary) > 10000)";
        ArrayList<String> queries = new ArrayList<>(List.of((new String[]{q1, q2, q3, q4, q5, q6})));
        ArrayList<Boolean> expected = new ArrayList<>(Arrays.asList(true, false, false, false, true, true));
        ArrayList<Boolean> results = new ArrayList<>();

        SQLParser p = new SQLParser();
        for(String query : queries){
            try{
                SQLQuery parsedQuery = p.parseQuery(query);
                results.add(check(parsedQuery));
            }
            catch (RuntimeException e){
                System.out.println(e.toString());
            }
        }

        System.out.println((this.getClass()));
        System.out.println(results);
        System.out.println(expected);
        if (results.equals(expected)) {
            System.out.println("All tests passed");
        }
        else {
            System.out.println("Some tests failed");
        }
    }

    public static void main(String[] args){
        ArrayDepthRule adp = new ArrayDepthRule();
        adp.performTests();
    }

}