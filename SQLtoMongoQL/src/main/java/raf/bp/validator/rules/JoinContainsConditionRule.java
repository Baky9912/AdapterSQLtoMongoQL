package raf.bp.validator.rules;

import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLExpression;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.validator.SQLValidatorRule;

import java.util.ArrayList;
import java.util.List;

public class JoinContainsConditionRule extends SQLValidatorRule {

    @Override
    public boolean check(SQLQuery query) {

        System.out.println("***********************************************");
        SQLQuery.printAnyQuery(query);

        for (SQLClause clause : query.getClauses()) {

            System.out.println("NOW PROCESSING " + clause.getKeyword());
            if (clause.getKeyword().equals("from")) {
                System.out.println(clause.getSqlExpressions());
            }

        }
        return false;
    }

    @Override
    public void setTests() {
        this.testingQueries = new ArrayList<>(List.of(new String[]{
                "SELECT * FROM table1 JOIN table1 USING (clm_name)",
                "select * from table1 join table2 on table1.clm_name = table2.clm_name",
                "SELECT * from table1 join table2 using (clm_name) join table3 on table2.clm_name = table3.clm_name",
                "select * from table1 join table2",
                "select * from table1 join table2 using ()",
                "select * from table1 join table2 on table1.clm_name = table2",
                "select * from table1 join table2 on table1.clm_name = table2.clm_name2"
        }));

        this.expectedResults = new ArrayList<>(List.of(new Boolean[] {true, true, true, false, false, false, true}));

    }
}
