package raf.bp.sqlextractor;

import raf.bp.executor.SQLExecutor;
import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLExpression;
import raf.bp.model.SQL.SQLToken;
import raf.bp.model.convertableSQL.datatypes.CSQLAggregateFunction;
import raf.bp.model.convertableSQL.datatypes.CSQLSimpleDatatype;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
abstract public class SQLExtractor {
    private SQLClause clause;
    
    public SQLExtractor(SQLClause clause){
        setClause(clause);
    }

    public static List<List<String>> findStatements(SQLClause clause){
        List<List<String>> statements = new ArrayList<>();
        List<String> currStatement = new ArrayList<>();
        ArgumentIterator argIter = new ArgumentIterator(clause);
        while(argIter.hasNext()){
            String word = argIter.next();
            if(word.equals(",")){
                statements.add(currStatement);
                currStatement = new ArrayList<>();
            }
            else{
                currStatement.add(word);
            }
        }
        statements.add(currStatement);

        System.out.println("FIND STATEMENTS");
        System.out.println(statements);
        return statements;
    }

}
