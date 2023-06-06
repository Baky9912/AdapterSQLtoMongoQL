package raf.bp.adapter.extractor.concrete;

import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLExpression;
import raf.bp.model.SQL.SQLToken;
import raf.bp.adapter.extractor.SQLExtractor;

public class LimitExtractor extends SQLExtractor {
    public LimitExtractor(SQLClause clause) {
        super(clause);
        assert clause.getKeyword().equals("limit");
    }

    public Integer extractLimit(){
        SQLExpression expr = getClause().getSqlExpressions().get(0);
        SQLToken token = (SQLToken)expr;
        return Integer.parseInt(token.getWord());
    }
}
