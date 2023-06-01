package raf.bp.validator.rules;

import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLExpression;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.parser.SQLParser;
import raf.bp.validator.SQLValidatorRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EssentialKeywordsRule implements SQLValidatorRule {

    public class MissingEssentialKeywordException extends RuntimeException {
        public MissingEssentialKeywordException(String message) {
            super(message);
        }
    }

    @Override
    public boolean check(SQLQuery query) throws MissingEssentialKeywordException {

       boolean select = false, from = false;

       for (SQLClause clause : query.getClauses()) {
           if (clause.getKeyword().equals("select")) select = true;
           if (clause.getKeyword().equals("from")) from = true;

           for (SQLExpression expression : clause.getSqlExpressions()) {
               /* we can do a recursive call like this because nested query can appear after keyword "from" at the earliest */
               if (expression instanceof SQLQuery ex) return check(ex) && select && from;
           }

       }

       return select && from;

    }

    @Override
    public void performTests() {
        ArrayList<String> queries = new ArrayList<>(List.of((new String[]{
                "SELECT       avg(salary),          department_id from hr.employees group by department_id", // true
                "SELECT first_name WHERE first_name='asdf'", // false
                "select department_name, department_id, location_id from hr.departments where department_id in\n"
                        + "(select department_id order by hr.employees group by department_id having max(salary) > 10000)", // false, nested fails check
                "select department_name, department_id, location_id from hr.departments where department_id in\n"
                        + "(select department_id from hr.employees group by department_id having max(salary) > 10000)", // true
                "select department_name, department_id, location_id group by hr.departments where department_id in\n"
                        + "(select department_id from hr.employees group by department_id having max(salary) > 10000)" // false, outer fails the check
        })));

        ArrayList<Boolean> expected = new ArrayList<>(Arrays.asList(true, false, false, true, false));
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
}
