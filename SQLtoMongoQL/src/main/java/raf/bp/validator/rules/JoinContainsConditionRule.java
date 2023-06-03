package raf.bp.validator.rules;

import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLExpression;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.model.SQL.SQLToken;
import raf.bp.validator.SQLValidatorRule;

import java.util.ArrayList;
import java.util.List;

public class JoinContainsConditionRule extends SQLValidatorRule {

    @Override
    public boolean check(SQLQuery query) {

        SQLClause fromClause = findClauseByKeyword(query, "from");

        if (!checkMatchingJoinConditions(fromClause)) return false;
        if (!joinTableInConditionExists(fromClause)) return false;

        return true;
    }

    /*
    * function checks if table referenced in conditions actually exists.
    * This also takes into account aliases, e.g. table1 t1
    * Function assumes that the argument clause will be "from"
    * */
    public boolean joinTableInConditionExists(SQLClause clause) {
        ArrayList<String> validTableNames = new ArrayList<>();
        boolean lastWasJoin = true, lastWasOn = false;

        for (SQLExpression ex : clause.getSqlExpressions()) {

            SQLToken token = (SQLToken) ex;

            if (token.getWord().equals("=")) continue;

            if (token.getWord().equals("join")) {
                lastWasJoin = true;
                lastWasOn = false;
                continue;
            }
            if (token.getWord().equals("on")) {
                lastWasJoin = false;
                lastWasOn = true;
                continue;
            }
            if (token.getWord().equals("using")) {
                lastWasJoin = false;
                lastWasOn = false;
                continue;
            }
            if (lastWasJoin) {
                validTableNames.add(token.getWord());
                System.out.println("VALID TABLE NAMES:");
                System.out.println(validTableNames);
            }
            if (lastWasOn) {
                String tempTableName = token.getWord().split("\\.")[0];
                System.out.println("Comparing " + tempTableName);
                if (!validTableNames.contains(tempTableName)) return false;
            }

        }

        return true;
    }

    /*
    * checks that every join has using or on as the following keyword
    * prevents double join without condition
    * */
    public boolean checkMatchingJoinConditions(SQLClause clause) {

        int counter = 0;
        for (SQLExpression ex : clause.getSqlExpressions()) {

            SQLToken token = (SQLToken) ex;

            if (token.getWord().equals("join")) counter++;
            else if (token.getWord().equals("using")) counter--;
            else if (token.getWord().equals("on")) counter--;

            if (counter < 0) return false;

        }

        return counter == 0;

    }

    @Override
    public void setTests() {
        this.testingQueries = new ArrayList<>(List.of(new String[]{
                "SELECT * FROM table1 JOIN table1 USING (clm_name)",
                "select * from table1 join table2 on table1.clm_name = table2.clm_name",
                "SELECT * from table1 join table2 using (clm_name) join table3 on table2.clm_name = table3.clm_name",
                "SELECT * from table1 join table2 using (clm_name) join table3 on table1.clm_name = table3.clm_name",
                "select * from table1 join table2",
                "select * from table1 join table2 on table1.clm_name = table2",
                "select * from table1 join table2 on table1.clm_name = table2.clm_name2",
                "SELECT * from table1 join table2 using (clm_name) on table3 join table1.clm_name = table3.clm_name",
                "SELECT * from table1 t1 join table2 t2 using (clm_name) join table3 t3 on t1.clm_name = t3.clm_name"
        }));

        this.expectedResults = new ArrayList<>(List.of(new Boolean[] {true, true, true, true, false, true, true, false, true}));

    }
}
