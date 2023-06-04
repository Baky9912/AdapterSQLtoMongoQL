package raf.bp.sqlextractor.concrete;

import java.util.ArrayList;
import java.util.List;

import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLExpression;
import raf.bp.model.SQL.SQLToken;
import raf.bp.model.convertableSQL.datatypes.CSQLAggregateFunction;
import raf.bp.model.convertableSQL.datatypes.CSQLSimpleDatatype;
import raf.bp.sqlextractor.SQLExtractor;

public class SelectExtractor extends SQLExtractor {

    public static List<CSQLSimpleDatatype> extractSimpleFields(SQLClause clause){
        assert clause.getKeyword().equals("select");
        List<CSQLSimpleDatatype> fields = new ArrayList<>();
        List<List<String>> statements = findStatements(clause);
        for(List<String> statement : statements){
            if(statement.size()==1){
                CSQLSimpleDatatype field = new CSQLSimpleDatatype(statement.get(0));
                fields.add(field);
            }
        }
        return fields;
    }

    public static List<CSQLAggregateFunction> extractAggregateFunctions(SQLClause clause){
        assert clause.getKeyword().equals("select");
        List<CSQLAggregateFunction> aggFuncs = new ArrayList<>();
        List<List<String>> statements = findStatements(clause);
        for(List<String> statement : statements){
            if(statement.size()==4){
                CSQLAggregateFunction aggFunc = new CSQLAggregateFunction(statement.get(0), statement.get(2));
                aggFuncs.add(aggFunc);
            }
        }
        return aggFuncs;
    }
}
