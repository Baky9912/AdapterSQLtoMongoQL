package raf.bp.adapter.extractor;

import raf.bp.adapter.extractor.util.ArgumentIterator;
import raf.bp.model.SQL.SQLClause;

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
