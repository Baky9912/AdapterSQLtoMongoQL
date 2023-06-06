package raf.bp.validator.concrete.rules;

import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLExpression;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.model.SQL.SQLToken;
import raf.bp.validator.SQLValidatorRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NoAggregationInWhereRule extends SQLValidatorRule {

    public static class AggregationInWhereException extends RuntimeException {
        public AggregationInWhereException(String message) {
            super(message);
        }
    }
    @Override
    public boolean check(SQLQuery query) {

        ArrayList<SQLQuery> queries = getAllQueries(query);

        /* then we search through every query for keyword 'where' and then we check if there are any aggregate functions */
        for (SQLQuery q : queries) {
            for (SQLClause clause : q.getClauses()) {
                if (!clause.getKeyword().equals("where")) continue;

                for (SQLExpression ex : clause.getSqlExpressions()) {
                    if (ex instanceof SQLQuery) continue;
                    SQLToken token = (SQLToken) ex;

                    if (aggregateFunctions.contains(token.getWord()))
                        throw new AggregationInWhereException("Aggregate functions can't be in WHERE. Please remove " + token.getWord());
                }
            }
        }

        return true;
    }

    @Override
    public void setTests() {
        this.testingQueries = new ArrayList<>(List.of((new String[]{
                "SELECT       avg(salary),          department_id from hr.employees group by department_id", // true
                "SELECT first_name From employees where first_name='asdf' ", // true
                "select last_name as prezime from employees where salary = max(salary)", // false
                "select last_name as prezime from employees where salary = count(salary)", // false
                "select last_name as prezime from employees where salary = avg(salary)", // false
                "select last_name as prezime from employees where salary = min(salary)", // false
                "select last_name as prezime from employees where salary = sum(salary)", // false
                "select last_name as prezime from employees where salary = (select average(salary) from employees group by employee_id)", // true
                "select last_name as prezime from employees where salary = (select average(salary) from employees where max(salary) group by employee_id)", // false
                "select last_name as prezime from employees where salary =max(salary) and salary in (select average(salary) from employees group by employee_id)", // false
        })));

        this.expectedResults = new ArrayList<>(Arrays.asList(true, true, false, false, false, false, false, true, false, false));
    }
}
