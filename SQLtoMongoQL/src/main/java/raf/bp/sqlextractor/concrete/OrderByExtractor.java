package raf.bp.sqlextractor.concrete;

import java.util.ArrayList;
import java.util.List;

import raf.bp.model.SQL.SQLClause;
import raf.bp.model.convertableSQL.datatypes.CSQLAggregateFunction;
import raf.bp.model.convertableSQL.datatypes.CSQLSimpleDatatype;
import raf.bp.model.convertableSQL.sort.CSQLSortField;
import raf.bp.sqlextractor.SQLExtractor;

public class OrderByExtractor extends SQLExtractor {

    List<String> aggFuncs = new ArrayList<>(List.of("sum", "avg", "count", "min", "max"));

    public OrderByExtractor(SQLClause clause) {
        super(clause);
        assert clause.getKeyword().equals("order_by");
    }

    /*
    public List<CSQLSortField> extractSortFields(){
        List<CSQLSortField> sortFields = new ArrayList<>();
        List<List<String>> statements = findStatements(getClause());
        for(List<String> statement : statements){
            String field = statement.get(0);
            String order = "asc";
            if(statement.size()>1){
                order = statement.get(1);
            }
            CSQLSortField sortField = new CSQLSortField(field, order);
            sortFields.add(sortField);
        }
        return sortFields;
    }*/

    public List<CSQLSortField> extractFieldsInOrder(){
        // can't make market interface because field is SimpleDatatype (with strings, numbers, etc)
        // too late to change
        List<CSQLSortField> sortFields = new ArrayList<>();
        List<List<String>> statements = findStatements(getClause());
        for(List<String> statement : statements){
            if(statement.size()==5){
                CSQLAggregateFunction aggFunc = new CSQLAggregateFunction(statement.get(0), statement.get(2));
                String order = statement.get(4);
                sortFields.add(new CSQLSortField(aggFunc, order));
            }
            else if(statement.size()==2){
                CSQLSimpleDatatype field = new CSQLSimpleDatatype(statement.get(0));
                String order = statement.get(1);
                sortFields.add(new CSQLSortField(field, order));
            }
            else{
                throw new RuntimeException("Field length problem!");
            }
        }
        return sortFields;       
    }
}
