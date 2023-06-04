package raf.bp.sqlextractor;

import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLExpression;
import raf.bp.model.SQL.SQLToken;
import raf.bp.model.convertableSQL.datatypes.CSQLAggregateFunction;
import raf.bp.model.convertableSQL.datatypes.CSQLSimpleDatatype;
import java.util.ArrayList;
import java.util.List;

abstract public class SQLExtractor {
    public static List<List<String>> findStatements(SQLClause clause){
        List<List<String>> statements = new ArrayList<>();
        List<String> currStatement = new ArrayList<>();
        for(SQLExpression ele : clause.getSqlExpressions()){
            SQLToken token = (SQLToken)ele;
            String word = token.getWord();
            if(word.equals(",")){
                statements.add(currStatement);
                currStatement = new ArrayList<>();
            }
            else{
                currStatement.add(word);
            }
        }
        statements.add(currStatement);
        return statements;
    }

}
