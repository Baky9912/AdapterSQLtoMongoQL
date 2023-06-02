package raf.bp.validator;

import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLExpression;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.model.SQL.SQLToken;
import raf.bp.parser.SQLParser;

import java.util.ArrayList;

public abstract class SQLValidatorRule {

    protected ArrayList<String> testingQueries = new ArrayList<>();
    protected ArrayList<Boolean> expectedResults = new ArrayList<>();

    public abstract boolean check(SQLQuery query);
    public abstract void setTests();

    public void performTests() {
        ArrayList<Boolean> results = new ArrayList<>();

        SQLParser p = new SQLParser();
        for(String query : testingQueries){
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
        System.out.println(expectedResults);
        if (results.equals(expectedResults)) {
            System.out.println("All tests passed");
        }
        else {
            System.out.println("Some tests failed");
        }
    }

    public ArrayList<SQLQuery> getAllQueries(SQLQuery query) {
        ArrayList<SQLQuery> result = new ArrayList<>();

        for (SQLClause clause : query.getClauses()) {
            for (SQLExpression ex : clause.getSqlExpressions()) {
                if (ex instanceof SQLQuery q) result.add(q);
            }
        }

        return result;
    }
}
