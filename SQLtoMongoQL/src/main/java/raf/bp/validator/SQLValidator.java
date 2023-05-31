package raf.bp.validator;

import raf.bp.parser.SQLParser;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.validator.rules.EssentialKeywordsRule;

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

    public static void main(String[] args) {
        ArrayList<String> queries = new ArrayList<>(List.of((new String[]{
                "SELECT       avg(salary),          department_id from hr.employees group by department_id", // true
                "SELECT first_name WHERE first_name='asdf'", // false
                "select department_name, department_id, location_id from hr.departments where department_id in\n"
                        + "(select department_id order by hr.employees group by department_id having max(salary) > 10000)", // false, nested fails check
                "select department_name, department_id, location_id from hr.departments where department_id in\n"
                        + "(select department_id from hr.employees group by department_id having max(salary) > 10000)", // true
                "select department_name, department_id, location_id group by hr.departments where department_id in\n"
                        + "(select department_id from hr.employees group by department_id having max(salary) > 10000)" // false, outer fails the check
        })));

        SQLParser p = new SQLParser();
        ArrayList<Boolean> results = new ArrayList<Boolean>();

        ArrayList<Boolean> expected = new ArrayList<>(Arrays.asList(true, false, false, true, false));

        for(String query : queries){
            try{
                SQLQuery parsedQuery = p.parseQuery(query);
//                SQLQuery.printAnyQuery(parsedQuery);
                results.add(validate(parsedQuery));
            }
            catch (RuntimeException e){
                System.out.println(e.toString());
            }
        }

        System.out.println(results);
        System.out.println(expected);
        if (results.equals(expected)) {
            System.out.println("All tests passed");
        }
        else {
            System.out.println("Some tests failed");
        }
    }
}
