package raf.bp.sqlextractor.concrete;

import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLExpression;
import raf.bp.model.SQL.SQLToken;
import raf.bp.sqlextractor.SQLExtractor;

public class SkipExtractor extends SQLExtractor {
    public SkipExtractor(SQLClause clause) {
        super(clause);
        assert clause.getKeyword().equals("skip");
    }

    public Integer extractSkip(){
        SQLExpression expr = getClause().getSqlExpressions().get(0);
        SQLToken token = (SQLToken)expr;
        return Integer.parseInt(token.getWord());
    }
}
