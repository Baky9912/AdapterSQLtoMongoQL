package raf.bp.validator;

import raf.bp.parser.SQLParser;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.validator.rules.EssentialKeywordsRule;
import raf.bp.validator.rules.NoAggregationInWhereRule;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SQLValidator {

    public static boolean validate(SQLQuery query) {

        Rule essentialKeywordRule = new EssentialKeywordsRule();

//        System.out.println(essentialKeywordRule.check(query));
        return essentialKeywordRule.check(query);
    }

    public static void testAllRules() {
        Rule[] rules = {new EssentialKeywordsRule(), new NoAggregationInWhereRule()};

        for (Rule rule : rules) {
            rule.performTests();
        }
    }

    public static void main(String[] args) {
       testAllRules();

    }
}
