package raf.bp.sqlextractor.concrete;

import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLExpression;
import raf.bp.model.SQL.SQLToken;
import raf.bp.sqlextractor.SQLExtractor;

public class LimitExtractor extends SQLExtractor {
    public static Integer extractLimit(SQLClause clause){
        assert clause.getKeyword().equals("limit");
        SQLExpression expr = clause.getSqlExpressions().get(0);
        SQLToken token = (SQLToken)expr;
        return Integer.parseInt(token.getWord());
    }
}
