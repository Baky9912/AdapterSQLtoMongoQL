package raf.bp.adapter.extractor.concrete;

import java.util.ArrayList;
import java.util.List;

import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLExpression;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.model.SQL.SQLToken;
import raf.bp.model.convertableSQL.datatypes.CSQLAggregateFunction;
import raf.bp.model.convertableSQL.datatypes.CSQLSimpleDatatype;
import raf.bp.model.convertableSQL.sort.CSQLSortField;
import raf.bp.adapter.extractor.SQLExtractor;

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
            if(statement.size()>=4){  // agg func
                CSQLAggregateFunction aggFunc = new CSQLAggregateFunction(statement.get(0), statement.get(2));
                String order = "asc";
                if(statement.size()==5) 
                    order = statement.get(4);
                sortFields.add(new CSQLSortField(aggFunc, order));
            }
            else { // simple field 
                CSQLSimpleDatatype field = new CSQLSimpleDatatype(statement.get(0));
                String order = "asc";
                if(statement.size()==2) 
                    order = statement.get(1);
                sortFields.add(new CSQLSortField(field, order));
            }
        }
        return sortFields;       
    }

    public List<CSQLAggregateFunction> extractAggregateFunctions() {

        List<CSQLAggregateFunction> result = new ArrayList<>();

        boolean foundName = false, foundArg = false;
        String aggName = null, aggArg = null;
        for (SQLExpression ex : getClause().getSqlExpressions()) {

            if (ex instanceof SQLQuery) continue;

            SQLToken token = (SQLToken) ex;

            if (aggFuncs.contains(token.getWord())) {
                foundName = true;
                foundArg = false;
            }
            if (token.getWord().equals("(")) {
                foundName = false;
                foundArg = true;
                continue;
            }
            if (token.getWord().equals(")")) {
                foundName = false;
                foundArg = false;
            }
            if (token.getWord().equalsIgnoreCase("asc") || token.getWord().equalsIgnoreCase("desc")) {
                result.add(new CSQLAggregateFunction(aggName, aggArg, token.getWord()));
            }
            if (foundName) {
                aggName = token.getWord();
            } else if (foundArg) {
                aggArg = token.getWord();
            }

        }

        return result;

    }
}
