package raf.bp.validator.rules;

import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLExpression;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.model.SQL.SQLToken;
import raf.bp.parser.SQLParser;
import raf.bp.validator.SQLValidatorRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NoAggregationInWhereRule implements SQLValidatorRule {

    @Override
    public boolean check(SQLQuery query) {
        ArrayList<String> aggregateFunctions = new ArrayList<>(List.of((new String[] {"count", "sum", "avg", "min", "max"})));

        ArrayList<SQLQuery> queries = new ArrayList<>();
        queries.add(query);
        /* first we find all sql queries in the query */
        for (SQLClause clause : query.getClauses()) {
            for (SQLExpression ex : clause.getSqlExpressions()) {
                if (ex instanceof SQLQuery) queries.add((SQLQuery) ex);
            }
        }

        /* then we search through every query for keyword 'where' and then we check if there are any aggregate functions */
        for (SQLQuery q : queries) {
            for (SQLClause clause : q.getClauses()) {
                if (!clause.getKeyword().equals("where")) continue;

                for (SQLExpression ex : clause.getSqlExpressions()) {
                    SQLToken token = (SQLToken) ex;
                    System.out.println("***************************************************");
                    System.out.println(token.getWord());
                    System.out.println("***************************************************");

                    if (aggregateFunctions.contains(token.getWord())) return false;
                }
            }
        }

        return true;
    }

    @Override
    public void performTests() {
        ArrayList<String> queries = new ArrayList<>(List.of((new String[]{
                "SELECT       avg(salary),          department_id from hr.employees group by department_id", // true
                "SELECT first_name From employees where first_name='asdf' ", // true
                "select last_name as prezime from employees where salary = max(salary)", // false
                "select last_name as prezime from employees where salary = count(salary)", // false
                "select last_name as prezime from employees where salary = avg(salary)", // false
                "select last_name as prezime from employees where salary = min(salary)", // false
                "select last_name as prezime from employees where salary = sum(salary)", // false
        })));

        ArrayList<Boolean> expected = new ArrayList<>(Arrays.asList(true, true, false, false, false, false, false));
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