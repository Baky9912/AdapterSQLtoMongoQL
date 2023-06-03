package raf.bp.validator.rules;

import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLExpression;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.model.SQL.SQLToken;
import raf.bp.validator.SQLValidatorRule;

import java.util.ArrayList;
import java.util.List;

public class GroupByRule extends SQLValidatorRule {

    public class GroupByException extends RuntimeException {
        public GroupByException(String message) {
            super(message);
        }
    }

    @Override
    public boolean check(SQLQuery query) throws GroupByException {
        ArrayList<SQLQuery> queries = getAllQueries(query);

        for (SQLQuery q : queries) {
            ArrayList<SQLToken> outsideAgg = new ArrayList<>();
            ArrayList<SQLToken> insideAgg = new ArrayList<>();
            for (SQLClause clause : q.getClauses()) {

                if (clause.getKeyword().equals("select")) {

                    boolean insideAggregateFunction = false;
                    /* nested queries can only be found in where clause (per our project specifications) */
                    for (SQLExpression ex : clause.getSqlExpressions()) {
                        SQLToken token = (SQLToken) ex;
                        if (skipableTokens.contains(token.getWord())) {
                            if (token.getWord().equals(")")) insideAggregateFunction = false;
                            continue;
                        }

                        if (aggregateFunctions.contains(token.getWord())) {
                            insideAggregateFunction = true;
                            continue;
                        }

                        if (insideAggregateFunction) insideAgg.add(token);
                        else outsideAgg.add(token);
                    }

                } else if (clause.getKeyword().equals("group_by")) {

                    for (SQLExpression ex : clause.getSqlExpressions()) {
                        SQLToken token = (SQLToken) ex;
                        outsideAgg.remove(token);

                        if (insideAgg.contains(token)) {
                            throw new GroupByException("Group by contains a field that's inside aggregate function. Please remove following field(s) from your group by: " + insideAgg);
                        }
                    }

                    if (outsideAgg.size() > 0)
                        throw new GroupByException("Aggregate functions in select detected. Please include " + outsideAgg + " field(s) in your group by.");
                }


            }
        }

        return true;
    }

    @Override
    public void setTests() {

        this.testingQueries = new ArrayList<>(List.of(new String[]{
                "select first_name, last_name, avg(salary) from employees group by first_name",
                "select first_name, last_name, department_id from employees group by department_id",
                "select first_name, last_name, department_id from employees group by department_id, first_name, last_name",
                "select avg(salary) from employees group by salary"

        }));

        this.expectedResults = new ArrayList<>(List.of(new Boolean[]{false, false, true, false}));

    }

}
