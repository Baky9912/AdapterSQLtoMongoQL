package raf.bp.sqlextractor.concrete;

import java.util.ArrayList;
import java.util.List;

import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLExpression;
import raf.bp.model.SQL.SQLToken;
import raf.bp.model.convertableSQL.CSQLDatatype;
import raf.bp.model.convertableSQL.datatypes.CSQLAggregateFunction;
import raf.bp.model.convertableSQL.datatypes.CSQLSimpleDatatype;
import raf.bp.sqlextractor.SQLExtractor;

public class SelectExtractor extends SQLExtractor {

    public SelectExtractor(SQLClause clause){
        super(clause);
        assert getClause().getKeyword().equals("select");
    }

    public List<CSQLSimpleDatatype> extractSimpleFields(){
        List<CSQLSimpleDatatype> fields = new ArrayList<>();
        List<List<String>> statements = findStatements(getClause());
        for(List<String> statement : statements){
            if(statement.size()==1){
                CSQLSimpleDatatype field = new CSQLSimpleDatatype(statement.get(0));
                fields.add(field);
            }
        }
        return fields;
    }

    public List<CSQLAggregateFunction> extractAggregateFunctions(){
        List<CSQLAggregateFunction> aggFuncs = new ArrayList<>();
        List<List<String>> statements = findStatements(getClause());
        for(List<String> statement : statements){
            if(statement.size()==4){
                CSQLAggregateFunction aggFunc = new CSQLAggregateFunction(statement.get(0), statement.get(2));
                aggFuncs.add(aggFunc);
            }
        }
        return aggFuncs;
    }

    public List<CSQLDatatype> extractFieldsInOrder(){
        List<CSQLDatatype> orderedFields = new ArrayList<>();
        List<List<String>> statements = findStatements(getClause());
        for(List<String> statement : statements){
            if(statement.size()==4){
                CSQLAggregateFunction aggFunc = new CSQLAggregateFunction(statement.get(0), statement.get(2));
                orderedFields.add(aggFunc);
            }
            else if(statement.size()==1){
                CSQLSimpleDatatype field = new CSQLSimpleDatatype(statement.get(0));
                    orderedFields.add(field);
            }
            else{
                throw new RuntimeException("Field length problem!");
            }
        }
        return orderedFields;       
    }
    

}
