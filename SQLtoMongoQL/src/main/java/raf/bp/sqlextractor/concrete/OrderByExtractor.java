package raf.bp.sqlextractor.concrete;

import java.util.ArrayList;
import java.util.List;

import raf.bp.model.SQL.SQLClause;
import raf.bp.sqlextractor.SQLExtractor;

public class OrderByExtractor extends SQLExtractor {

    public static List<String> extractAscendingFields(SQLClause clause){
        List<String> ascendingFields = new ArrayList<>();
        List<List<String>> statements = findStatements(clause);
        for(List<String> statement : statements){
            String field = statement.get(0);
            String order = "asc";
            if(statement.size()>1){
                order = statement.get(1);
            }
            if(order.equals("asc")){
                ascendingFields.add(field);
            }
        }
        return ascendingFields;
    }

    public static List<String> extractDescendingFields(SQLClause clause){
        List<String> descendingFields = new ArrayList<>();
        List<List<String>> statements = findStatements(clause);
        for(List<String> statement : statements){
            String field = statement.get(0);
            String order = "asc";
            if(statement.size()>1){
                order = statement.get(1);
            }
            if(order.equals("desc")){
                descendingFields.add(field);
            }
        }
        return descendingFields;
    }

}
