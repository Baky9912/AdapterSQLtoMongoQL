package raf.bp.validator;

import raf.bp.model.SQL.SQLQuery;
import raf.bp.validator.rules.*;

public class SQLValidator {
    private SQLValidatorRule[] rules = {
            new EssentialKeywordsRule(),
            new NoAggregationInWhereRule(),
            new ArrayDepthRule(),
            new GroupByRule(),
            new JoinContainsConditionRule(),
            new KeywordsInOrderRule()};

    public boolean validate(SQLQuery query) {

        for (SQLValidatorRule rule : rules ) {

            if (!rule.check(query)) return false;

        }

        return true;

    }

    public void testAllRules() {

        for (SQLValidatorRule rule : rules) {
            rule.setTests();
            rule.performTests();
        }
    }

    public void main(String[] args) {
       testAllRules();

    }
}
