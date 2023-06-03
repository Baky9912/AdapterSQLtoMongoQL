package raf.bp.validator.rules;

import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLExpression;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.parser.SQLParser;
import raf.bp.validator.SQLValidatorRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EssentialKeywordsRule extends SQLValidatorRule {

    public class MissingEssentialKeywordException extends RuntimeException {
        public MissingEssentialKeywordException(String message) {
            super(message);
        }
    }

    @Override
    public boolean check(SQLQuery query) {
        if (!_check(query)) {
            throw new MissingEssentialKeywordException("There is an essential keyword missing. Essential keywords include 'select' and 'from'");
        }

        return true;

    }

    public boolean _check(SQLQuery query) {

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
    public void setTests() {
        this.testingQueries = new ArrayList<>(List.of((new String[]{
            "SELECT       avg(salary),          department_id from hr.employees group by department_id", // true
            "SELECT first_name WHERE first_name='asdf'", // false
            "select department_name, department_id, location_id from hr.departments where department_id in\n"
                    + "(select department_id order by hr.employees group by department_id having max(salary) > 10000)", // false, nested fails check
            "select department_name, department_id, location_id from hr.departments where department_id in\n"
                    + "(select department_id from hr.employees group by department_id having max(salary) > 10000)", // true
            "select department_name, department_id, location_id group by hr.departments where department_id in\n"
                    + "(select department_id from hr.employees group by department_id having max(salary) > 10000)" // false, outer fails the check
        })));

        this.expectedResults = new ArrayList<>(Arrays.asList(true, false, false, true, false));
    }
}
