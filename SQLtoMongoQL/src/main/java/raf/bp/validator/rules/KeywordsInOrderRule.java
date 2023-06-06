package raf.bp.validator.rules;

import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.validator.concrete.SQLValidatorRule;

import java.util.ArrayList;
import java.util.List;

public class KeywordsInOrderRule extends SQLValidatorRule {
    public static class KeywordsNotInOrderException extends RuntimeException {
        public KeywordsNotInOrderException(String message) {
            super(message);
        }
    }
    @Override
    public boolean check(SQLQuery query) {
        ArrayList<SQLQuery> queries = getAllQueries(query);

        for (SQLQuery q : queries) {

            if (!q.getClauses().get(0).getKeyword().equals("select"))
                throw new KeywordsNotInOrderException("Missing select at the beginning of the query.");

            int lastPointer = -2, currentPointer = -1;
            for (SQLClause clause : q.getClauses()) {

                currentPointer = keywordsInOrder.indexOf(clause.getKeyword());

                if (currentPointer < lastPointer)
                    throw new KeywordsNotInOrderException("Keywords not in order. Please swap " + clause.getKeyword() + " with the previous keyword.");

                lastPointer = currentPointer;

            }

        }
        return true;
    }

    @Override
    public void setTests() {

        this.testingQueries = new ArrayList<>(List.of("select column1, column2 from table1",
                "select * from table 1 join table2 on table1.column = table2.column where table1.column = 'value'",
                "select column1, column2 from table1 order by column1 ASC",
                "from table1 select column1, column2",
                "where column1 = 'value'",
                "join table2 on table1.column = table2.column from table1",
                "from table1 where column1 = 'value' select column1, column2",
                "SELECT column1, column2 FROM table1 WHERE column1 IN (SELECT column3 FROM table2 WHERE column4 = 'value')",
                "SELECT * FROM (SELECT column1, column2 FROM table1) AS subquery",
                "SELECT column1, (SELECT COUNT(*) FROM table2 WHERE table2.column = table1.column) AS count FROM table1",
                "SELECT column1, column2 FROM table1 JOIN (SELECT column3, column4 FROM table2) AS subquery ON table1.column = subquery.column3"
                ));

        this.expectedResults = new ArrayList<>(List.of(true, true, true, false, false, false, false, true, true, true, true));

    }
}
