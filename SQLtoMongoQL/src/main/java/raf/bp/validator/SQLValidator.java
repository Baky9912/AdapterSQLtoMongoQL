package raf.bp.validator;

import raf.bp.parser.SQLParser;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.validator.rules.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
