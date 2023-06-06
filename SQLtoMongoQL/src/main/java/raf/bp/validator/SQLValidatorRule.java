package raf.bp.validator;

import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLExpression;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.parser.SQLParser;

import java.util.ArrayList;
import java.util.List;

public abstract class SQLValidatorRule {

    protected ArrayList<String> testingQueries = new ArrayList<>();
    protected ArrayList<Boolean> expectedResults = new ArrayList<>();
    protected ArrayList<String> aggregateFunctions = new ArrayList<>(List.of((new String[] {"count", "sum", "avg", "min", "max"})));
    protected ArrayList<String> skipableTokens = new ArrayList<>(List.of(new String[]{"(", ")", ","}));
    protected ArrayList<String> keywordsInOrder = new ArrayList<>(List.of(new String[] {"select", "from", "join", "where", "group_by", "order_by", "offset", "limit"}));
    protected ArrayList<String> differentJoins = new ArrayList<>(List.of(new String[] {"inner_join", "outer_join", "left_join", "right_join", "join", "cross_join"}));

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
                System.out.println(e);
                results.add(false);
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

    /*
    * finds and returns SQLClause from SQLQuery that matches the String keyword
    * otherwise returns null
    */
    public SQLClause findClauseByKeyword(SQLQuery query, String keyword) {

        for (SQLClause clause : query.getClauses()) {
            if (clause.getKeyword().equals(keyword)) return clause;
        }

        return null;
    }

    /*
     * finds all nested queries
     * returns the query submitted as part of the array
     */
    public ArrayList<SQLQuery> getAllQueries(SQLQuery query) {
        ArrayList<SQLQuery> result = new ArrayList<>();
        result.add(query);

        for (SQLClause clause : query.getClauses()) {
            for (SQLExpression ex : clause.getSqlExpressions()) {
                if (ex instanceof SQLQuery q) result.add(q);
            }
        }

        return result;
    }
}
