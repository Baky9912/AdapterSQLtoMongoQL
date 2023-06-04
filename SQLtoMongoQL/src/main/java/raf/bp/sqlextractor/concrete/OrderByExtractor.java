package raf.bp.sqlextractor.concrete;

import java.util.ArrayList;
import java.util.List;

import raf.bp.model.SQL.SQLClause;
import raf.bp.model.convertableSQL.sort.CSQLSortField;
import raf.bp.sqlextractor.SQLExtractor;

public class OrderByExtractor extends SQLExtractor {

    public OrderByExtractor(SQLClause clause) {
        super(clause);
        assert clause.getKeyword().equals("order_by");
    }

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
    }
}
