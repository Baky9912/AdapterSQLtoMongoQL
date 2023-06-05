package raf.bp.sqlextractor.concrete;

import java.util.ArrayList;
import java.util.List;

import raf.bp.model.SQL.SQLClause;
import raf.bp.sqlextractor.SQLExtractor;

public class GroupByExtractor extends SQLExtractor{

    public GroupByExtractor(SQLClause clause) {
        super(clause);
        assert clause.getKeyword().equals("group_by");
    }

    public List<String> extractFields(){
        List<String> fields = new ArrayList<>();
        List<List<String>> statements = findStatements(getClause());
        for(List<String> statement : statements){
            fields.add(statement.get(0));
        }
        return fields;
    }
    
}
