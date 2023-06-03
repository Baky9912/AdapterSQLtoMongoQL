package raf.bp.converter;

import raf.bp.converter.concrete.GroupByConverter;
import raf.bp.converter.concrete.SelectConverter;
import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.parser.SQLParser;
import raf.bp.validator.SQLValidator;

import java.util.ArrayList;
import java.util.List;

public class ClauseConverter extends Converter {


    @Override
    public String convert(SQLClause clause) {

        switch (clause.getKeyword()) {
            case "select" -> { return (new SelectConverter()).convert(clause); }
            case "group_by" -> { return (new GroupByConverter()).convert(clause); }
            case "where" -> {return (new ClauseConverter()).convert(clause); }
        }

        return null;
    }

    public static void main(String[] args) {
        ClauseConverter clauseConverter = new ClauseConverter();
        ArrayList<String> testingQueries = new ArrayList<>(List.of(
                "select * from employees",
                "select first_name, last_name from employees",
                "select * from employees group by salary asc",
                "select first_name, last_name from employees group by salary desc",
                "select * from employees group by salary asc, date_of_birth desc",
                "select first_name, last_name from employees group by salary asc, date_of_birth desc"
        ));

        SQLParser p = new SQLParser();
        for(String query : testingQueries){
            try{
                SQLQuery parsedQuery = p.parseQuery(query);
                System.out.println("CONVERTER:");
                for (SQLClause clause : parsedQuery.getClauses()) {
                    System.out.print(clause.getKeyword() + ": ");
                    System.out.println(clauseConverter.convert(clause));
                }
                System.out.println("*********************");
            }
            catch (RuntimeException e){
                System.out.println(e.toString());
            }
        }
    }

}
